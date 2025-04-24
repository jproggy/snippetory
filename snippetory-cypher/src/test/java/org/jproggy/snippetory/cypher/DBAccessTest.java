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

package org.jproggy.snippetory.cypher;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.jproggy.snippetory.UriResolver;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class DBAccessTest {
  static Driver driver;
  static Repository repo;
  NeoConnection con;

  @BeforeAll
  public static void prepare() throws Exception {
    try {
      driver = GraphDatabase.driver(System.getProperty("NEO4J_URL"),
              AuthTokens.basic("neo4j", System.getProperty("DB_PASSWORD")));
    } catch (Exception e) {
      // ignore
      e.printStackTrace();
    }
  }

  @BeforeEach
  public void init() throws Exception {
    assumeTrue(driver !=  null);
    con = new NeoConnection(driver.session());
    CypherContext ctx = new CypherContext();
    ctx.uriResolver(UriResolver.resource("org/jproggy/snippetory/cypher"));
    repo = ctx.getRepository("DbAccessRepo.cypher");
    if (!hasSimpleTable(ctx)) {
      con.execute(repo.get("fillSimpleTable"));
    }
  }

  boolean hasSimpleTable(CypherContext ctx) throws Exception {
    return con.list(repo.get("selectSimpleTable")).size() > 0;
  }

  @Test
  public void testInsDel() throws Exception {
    Statement insert = repo.get("insertSimpleTable");
    LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
    insert.get("values").set("name", "test3").set("price", null).set("ext_id", "testinsert").set("xx", LocalTime.of(0,0)).render();
    insert.get("values").set("name", "test1").set("price", 100.3).set("ext_id", "testinsert").set("xx", now).render();
    insert.get("values").set("name", null).set("price", 103.3).set("ext_id", "testinsert").set("xx", LocalTime.MAX).render();
    assertEquals(3, con.execute(insert));
    List<LocalTime> data = con.list(
            repo.get("selectSimpleTable")
                    .set("ext_id", "testinsert")
                    .set("order", Cypher.markAsCypher("n.xx")),
            rs -> rs.get("n.xx").asLocalTime()
    );
    assertEquals(LocalTime.of(0,0), data.get(0));
    assertEquals(now, data.get(1), data.get(1).getClass().getName());
    assertEquals(LocalTime.MAX, data.get(2));
    assertEquals(3, con.execute(repo.get("deleteSimpleTable").set("ext_id", "testinsert")));
  }

  @Test
  public void testName() throws Exception {
      Statement stmt = repo.get("selectSimpleTable");
      stmt.get("name").set("name", null).render();
      assertEquals(0, con.list(stmt).size());
  }

  @Test
  public void testList() throws Exception {
    List<String> data = con.list(
            repo.get("selectSimpleTable")
                    .set("ext_id", "test")
                    .set("order", Cypher.markAsCypher("n.name DESC")),
            r -> r.get("n.name").asString()
    );
    assertEquals("[Kuno, Karl, Egon]", data.toString());
  }

  @Test
  public void testOne() throws Exception {
    Statement stmt = repo.get("selectSimpleTable");
    stmt.get("name").set("name", "Karl").render();
    double data = con.one(stmt).get("n.price").asDouble();
    assertEquals(101.300, data, 0.0);
  }

  @Test
  public void nullhandling() throws Exception {
    assertEquals(null, con.list(repo.get("nulls"), r -> r.get("colNullStr").asString(null)).get(0));
    assertEquals("something", con.one(repo.get("nulls")).get("colStr").asString("x"));
  }
}
