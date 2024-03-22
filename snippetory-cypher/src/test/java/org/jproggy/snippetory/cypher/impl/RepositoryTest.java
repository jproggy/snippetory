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

package org.jproggy.snippetory.cypher.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.jproggy.snippetory.UriResolver;
import org.jproggy.snippetory.cypher.CypherContext;
import org.jproggy.snippetory.cypher.Repository;
import org.jproggy.snippetory.cypher.Statement;

public class RepositoryTest {
  CypherContext ctx;
  Repository repo;


  @BeforeEach
  public void init() {
    ctx = new CypherContext().uriResolver(UriResolver.resource("org/jproggy/snippetory/cypher"));
    repo = ctx.getRepository("TestRepo.cypher");
    Parameter.reset();
  }

  @Test
  public void test1() throws Exception {
    Statement statement = repo.get("test1");
    statement.set("value1", "test");
    assertEquals(TestResults.test1(), statement.toString());
  }

  @Test
  public void test2() throws Exception {
    Statement statement = repo.get("test2");
    statement.set("value1", "test1");
    statement.set("value2", "test2");
    assertEquals(TestResults.test2(), statement.toString());
  }

  @Test
  public void test3() throws Exception {
    Statement statement = repo.get("test3");
    statement.set("value1", (byte)1);
    statement.set("value2", 2);
    statement.set("value3", 3.0);
    assertEquals(TestResults.test3(), statement.toString());
  }

  @Test
  public void test4() throws Exception {
    Statement statement = repo.get("test4");
    statement.set("value1", (byte)1);
    statement.set("value2", 3.0);
    assertEquals(TestResults.test2(), statement.toString());
  }
}
