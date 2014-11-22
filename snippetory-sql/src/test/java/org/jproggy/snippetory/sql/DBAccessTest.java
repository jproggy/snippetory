/// Copyright JProggy
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.

package org.jproggy.snippetory.sql;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jproggy.snippetory.UriResolver;
import org.jproggy.snippetory.engine.SnippetoryException;
import org.jproggy.snippetory.sql.spi.ConnectionProvider;
import org.jproggy.snippetory.sql.spi.RowProcessor;
import org.jproggy.snippetory.util.VariantResolver;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DBAccessTest {
  static ConnectionProvider cons;
  static Repository repo;
  static Map<String, String> dbNames = new HashMap<>();

  @Parameters
  public static Iterable<String[]> dbUrls() {
    new File("target/test/db/sqlite").mkdirs();
    return Arrays.asList(
        new String[]{
            "jdbc:mysql://localhost:3306/snippetory_test",
            System.getProperty("snippetory.test.dbUser"),
            System.getProperty("snippetory.test.dbPassword")},
            new String[]{"jdbc:derby:target/test/db/derby/snippetory_test;create=true", null, null},
            new String[]{"jdbc:derby:memory:snippetory_test;create=true", null, null},
            new String[]{"jdbc:sqlite:target/test/db/sqlite/snippetory_test.db", null, null},
            new String[]{"jdbc:hsqldb:mem:snippetory_test", null, null},
            new String[]{"jdbc:hsqldb:file:target/test/db/hsql/snippetory_test", null, null}
        );
  }

  public DBAccessTest(String url, String user, String pw) throws Exception {
    cons = new ConnectionFab(url, user, pw);
  }


  @BeforeClass
  public static void prepare() throws Exception {
    dbNames.put("HSQL Database Engine", "hsql");
    dbNames.put("Microsoft SQL Server Database", "mssql");
    dbNames.put("Microsoft SQL Server", "mssql");
    dbNames.put("MySQL", "mysql");
    dbNames.put("Oracle", "oracle");
    dbNames.put("PostgreSQL", "postgre");
    dbNames.put("Apache Derby", "derby");
    dbNames.put("SQLite", "sqlite");
  }

  @Before
  public void init() throws Exception {
    SqlContext ctx = new SqlContext().conntecions(cons);
    String prodName = cons.getConnection().getMetaData().getDatabaseProductName();
    String dbType = dbNames.get(prodName);
    ctx.postProcessor(VariantResolver.wrap(dbType));
    ctx.uriResolver(UriResolver.resource("org/jproggy/snippetory/sql"));
    repo = ctx.getRepository("DbAccessRepo.sql");
    if (!hasSimpleTable(ctx)) {
      repo.get("createSimpleTable").executeUpdate();
      repo.get("fillSimpleTable").executeUpdate();
    }
  }

  boolean hasSimpleTable(SqlContext ctx) throws Exception {
    try (ResultSet rs = cons.getConnection().getMetaData().getTables(null, "%", "SIMPLE", null)) {
      return rs.next();
    }
  }

  @Test
  public void testInsDel() throws Exception {
    Statement insert = repo.get("insertSimpleTable");
    insert.get("values").set("name", "test1").set("price", 100.3).set("ext_id", "testinsert").render();
    insert.get("values").set("name", "test3").set("price", 101.3).set("ext_id", "testinsert").render();
    insert.get("values").set("name", "test5").set("price", 103.3).set("ext_id", "testinsert").render();
    assertEquals(3, insert.executeUpdate());
    assertEquals(3, repo.get("deleteSimpleTable").set("ext_id", "testinsert").executeUpdate());
  }

  @Test
  public void testDirectCursor() throws Exception {
    int count = 0;
    try ( Cursor<Map<String, Object>> data = repo.get("selectSimpleTable").directCursor(SQL.asMap());) {
      for (Map<String, Object> item: data) {
        count++;
        assertEquals(4, item.size());
        assertEquals(Integer.class, item.get("simple_id").getClass());
      }
    }
    assertEquals(5, count);
  }

  @Test
  public void testReadaheadCursor() throws Exception {
    int count = 0;
    try ( Cursor<Object[]> data = repo.get("selectSimpleTable").readAheadCursor(SQL.asObjects());) {
      for (Object[] item: data) {
        count++;
        assertEquals(4, item.length);
        assertEquals(Integer.class, item[0].getClass());
      }
    }
    assertEquals(5, count);
  }

  @Test
  public void testList() throws Exception {
    List<String> data = repo.get("selectSimpleTable").set("ext_id", "test").list(SQL.asString("name"));
    assertEquals(3, data.size());
    assertEquals("[Kuno, Karl, Egon]", data.toString());
  }

  @Test
  public void testMap() throws Exception {
    Statement stmt = repo.get("selectSimpleTable").set("ext_id", "test");
    Map<String, Map<String, Object>> data = stmt.map(SQL.asString("name"), SQL.asMap("ext_id", "price"));
    assertEquals(3, data.size());
    assertEquals("[Kuno, Karl, Egon]", data.keySet().toString());
    Iterator<Map<String, Object>> i = data.values().iterator();
    Map<String, Object> row1 = i.next();
    assertEquals(new BigDecimal("22.000").doubleValue(), ((Number)row1.get("price")).doubleValue());
    assertEquals("test2", row1.get("ext_id"));
    Map<String, Object> row2 = i.next();
    assertEquals(new BigDecimal("101.300").doubleValue(), ((Number)row2.get("price")).doubleValue());
    assertEquals("test4", row2.get("ext_id"));
    Map<String, Object> row3 = i.next();
    assertEquals(new BigDecimal("123.450").doubleValue(), ((Number)row3.get("price")).doubleValue());
    assertEquals("test6", row3.get("ext_id"));
  }

  @Test
  public void testOne() throws Exception {
    Double data = repo.get("selectSimpleTable").set("name", "Karl").one(SQL.asDouble("price"));
    assertEquals(101.300, data);
  }

  @Test
  public void testForEach() throws Exception {
    final int[] count = {0};
    final Set<Integer> ids = new HashSet<>();
    repo.get("selectSimpleTable").forEach(new RowProcessor() {
      @Override
      public void processRow(ResultSet rs) throws SQLException {
        count[0]++;
        assertTrue(ids.add(rs.getInt("simple_id")));
      }
    });

    assertEquals(5, count[0]);
  }

  static class ConnectionFab implements ConnectionProvider {
    final String password;
    final String user;
    final String url;

    public ConnectionFab(String url, String user, String pw) {
      super();
      this.password = pw;
      this.user = user;
      this.url = url;
    }

    @Override
    public Connection getConnection() {
      try {
        if (user == null || password == null) return DriverManager.getConnection(url);
        return DriverManager.getConnection(url, user, password);
      } catch (SQLException e) {
        throw new SnippetoryException(e);
      }
    }
  }
}
