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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jproggy.snippetory.UriResolver;
import org.jproggy.snippetory.SnippetoryException;
import org.jproggy.snippetory.sql.spi.ConnectionProvider;
import org.jproggy.snippetory.sql.spi.VariantResolver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DBAccessTest {
  static Repository repo;
  static Map<String, String> dbNames = new HashMap<>();
  String dbType;

  static Iterable<ConnectionFab> dbUrls() {
    new File("target/test/db/sqlite").mkdirs();
    List<ConnectionFab> result = new ArrayList<>();
    if (System.getProperty("snippetory.test.dbUser") != null) {
      result.add(new ConnectionFab(
              "jdbc:mysql://localhost:3306/snippetory_test",
              System.getProperty("snippetory.test.dbUser"),
              System.getProperty("snippetory.test.dbPassword")));
      result.add(new ConnectionFab(
              "jdbc:postgresql://localhost:5432/snippetory_test",
              System.getProperty("snippetory.test.dbUser"),
              System.getProperty("snippetory.test.dbPassword")));
    }
    result.addAll(Arrays.asList(
            new ConnectionFab("jdbc:derby:target/test/db/derby/snippetory_test;create=true", null, null),
            new ConnectionFab("jdbc:derby:memory:snippetory_test;create=true", null, null),
            new ConnectionFab("jdbc:sqlite:target/test/db/sqlite/snippetory_test.db", null, null),
            new ConnectionFab("jdbc:hsqldb:mem:snippetory_test", null, null),
            new ConnectionFab("jdbc:hsqldb:file:target/test/db/hsql/snippetory_test", null, null)
    ));
    return result;
  }

  @BeforeAll
  static void prepare() throws Exception {
    dbNames.put("HSQL Database Engine", "hsql");
    dbNames.put("Microsoft SQL Server Database", "mssql");
    dbNames.put("Microsoft SQL Server", "mssql");
    dbNames.put("MySQL", "mysql");
    dbNames.put("Oracle", "oracle");
    dbNames.put("PostgreSQL", "postgres");
    dbNames.put("Apache Derby", "derby");
    dbNames.put("SQLite", "sqlite");
  }

  void init(ConnectionProvider cons) throws Exception {
    SqlContext ctx = new SqlContext().connections(cons);
    String prodName = cons.getConnection().getMetaData().getDatabaseProductName();
    dbType = dbNames.get(prodName);
    ctx.postProcessor(VariantResolver.wrap(dbType));
    ctx.uriResolver(UriResolver.resource("org/jproggy/snippetory/sql"));
    repo = ctx.getRepository("DbAccessRepo.sql");
    if (!hasSimpleTable(ctx, cons)) {
      repo.get("createSimpleTable").executeUpdate();
      repo.get("fillSimpleTable").executeUpdate();
    }
  }

  boolean hasSimpleTable(SqlContext ctx, ConnectionProvider cons) throws Exception {
    try (ResultSet rs = cons.getConnection().getMetaData().getTables(null, "%", "%", null)) {
      while (rs.next()) {
        if ("simple".equalsIgnoreCase(rs.getString("TABLE_NAME"))) return true;
      }
    }
    return false;
  }

  @ParameterizedTest
  @MethodSource("dbUrls")
  void testInsDel(ConnectionProvider cons) throws Exception {
    init(cons);
    Statement insert = repo.get("insertSimpleTable");
    LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
    insert.get("values").set("name", "test1").set("price", 100.3).set("ext_id", "testinsert").set("xx", now).render();
    insert.get("values").set("name", "test3").set("price", null).set("ext_id", "testinsert").set("xx", LocalTime.of(0, 0)).render();
    insert.get("values").set("name", null).set("price", 103.3).set("ext_id", "testinsert").set("xx", LocalTime.MIDNIGHT).render();
    assertEquals(3, insert.executeUpdate());
    List<Time> data = repo.get("selectSimpleTable").set("ext_id", "testinsert").list(rs -> rs.getTime("xx"));
    assertEquals(Time.valueOf(now), data.get(0), data.get(0).getClass().getName());
    assertEquals(Time.valueOf(LocalTime.of(0, 0)), data.get(1));
    assertEquals(Time.valueOf(LocalTime.MIDNIGHT), data.get(2));
    assertEquals(3, repo.get("deleteSimpleTable").set("ext_id", "testinsert").executeUpdate());
  }

  @ParameterizedTest
  @MethodSource("dbUrls")
  void testCursor(ConnectionProvider cons) throws Exception {
    init(cons);
    int count = 0;
    try (Cursor<Map<String, Object>> data = repo.get("selectSimpleTable").cursor(SQL.asMap())) {
      for (Map<String, Object> item : data) {
        count++;
        assertEquals(5, item.size());
        assertEquals(Integer.class, item.get("simple_id").getClass());
      }
    }
    assertEquals(5, count);
  }

  @ParameterizedTest
  @MethodSource("dbUrls")
  void testName(ConnectionProvider cons) throws Exception {
    init(cons);
    Statement stmt = repo.get("selectSimpleTable");
    stmt.get("name").set("name", null).render();
    assertEquals(0, stmt.list(SQL.asInteger()).size());
  }

  @ParameterizedTest
  @MethodSource("dbUrls")
  void testList(ConnectionProvider cons) throws Exception {
    init(cons);
    List<String> data = repo.get("selectSimpleTable").set("ext_id", "test").list(SQL.asString("name"));
    assertEquals(3, data.size());
    assertEquals("[Kuno, Karl, Egon]", data.toString());
  }

  @ParameterizedTest
  @MethodSource("dbUrls")
  void testMap(ConnectionProvider cons) throws Exception {
    init(cons);
    Statement stmt = repo.get("selectSimpleTable").set("ext_id", "test");
    Map<String, Map<String, Object>> data = stmt.map(SQL.asString("name"), SQL.asMap("ext_id", "price"));
    assertEquals(3, data.size());
    assertEquals("[Kuno, Karl, Egon]", data.keySet().toString());
    Iterator<Map<String, Object>> i = data.values().iterator();
    if ("sqlite".equals(dbType)) return; //sqlite has problems with scale of bigdecimal
    Map<String, Object> row1 = i.next();
    assertEquals(new BigDecimal("22.000"), row1.get("price"));
    assertEquals("test2", row1.get("ext_id"));
    Map<String, Object> row2 = i.next();
    assertEquals(new BigDecimal("101.300"), row2.get("price"));
    assertEquals("test4", row2.get("ext_id"));
    Map<String, Object> row3 = i.next();
    assertEquals(new BigDecimal("123.450"), row3.get("price"));
    assertEquals("test6", row3.get("ext_id"));
  }

  @ParameterizedTest
  @MethodSource("dbUrls")
  void testOne(ConnectionProvider cons) throws Exception {
    init(cons);
    Statement stmt = repo.get("selectSimpleTable");
    stmt.get("name").set("name", "Karl").render();
    Double data = stmt.one(SQL.asDouble("price"));
    assertEquals(101.300, data);
  }

  @ParameterizedTest
  @MethodSource("dbUrls")
  void testForEach(ConnectionProvider cons) throws Exception {
    init(cons);
    int[] count = {0};
    Set<Integer> ids = new HashSet<>();
    repo.get("selectSimpleTable").forEach(rs -> {
      count[0]++;
      assertTrue(ids.add(rs.getInt("simple_id")));
    });

    assertEquals(5, count[0]);
  }

  @ParameterizedTest
  @MethodSource("dbUrls")
  void nullhandling(ConnectionProvider cons) throws Exception {
    init(cons);
    assertEquals("[x]", repo.get("nulls").list(SQL.asString("colNullStr").orElse("x")).toString());
    assertEquals("something", repo.get("nulls").one(SQL.asString("colStr").orElse("x")));
    assertEquals("1", repo.get("nulls").one(SQL.asString("colNullStr").orElse(SQL.asInteger("colNum").asString())));
    assertEquals("<none>", repo.get("nulls").one(SQL.asString("colNullStr").orElse(SQL.asInteger("colNullInt").asString().orElse("<none>"))));
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

    @Override
    public String toString() {
      return url.substring(5);
    }
  }
}
