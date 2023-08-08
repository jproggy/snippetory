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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.jproggy.snippetory.UriResolver;
import org.jproggy.snippetory.sql.spi.ConnectionProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RepositoryTest {
  PreparedStatement stmt;
  SqlContext ctx;
  Repository repo;

  @BeforeEach
  public void init() {
    ctx = new SqlContext().uriResolver(UriResolver.resource("org/jproggy/snippetory/sql"));
  }

  @Test
  public void test1() throws Exception {
    ConnectionProvider cp = connectionProvider();
    repo = ctx.connections(cp).getRepository("TestRepo.sql");
    Statement statement = repo.get("test1");
    statement.set("value1", "test");
    assertEquals(TestResults.test1(), statement.toString());
    statement.forEach(null);
    verify(stmt).setString(1, "test");
  }

  @Test
  public void test2() throws Exception {
    ConnectionProvider cp = connectionProvider();
    repo = ctx.connections(cp).getRepository("TestRepo.sql");
    Statement statement = repo.get("test2");
    statement.set("value1", "test1");
    statement.set("value2", "test2");
    assertEquals(TestResults.test2(), statement.toString());
    statement.forEach(null);
    verify(stmt).setString(1, "test1");
    verify(stmt).setString(2, "test2");
  }

  @Test
  public void test2_1() throws Exception {
    ConnectionProvider cp = connectionProvider();
    repo = ctx.connections(cp).getRepository("TestRepo.sql");
    Statement statement = repo.get("test2");
    statement.set("value2", 2f);
    statement.set("value1", Date.valueOf("2014-01-01"));
    assertEquals(TestResults.test2(), statement.toString());
    statement.forEach(null);
    verify(stmt).setDate(1, Date.valueOf("2014-01-01"));
    verify(stmt).setFloat(2, 2);
  }

  @Test
  public void test3() throws Exception {
    ConnectionProvider cp = connectionProvider();
    repo = ctx.connections(cp).getRepository("TestRepo.sql");
    Statement statement = repo.get("test3");
    statement.set("value1", (byte)1);
    statement.set("value2", 2);
    statement.set("value3", 3.0);
    assertEquals(TestResults.test3(), statement.toString());
    statement.forEach(null);
    verify(stmt).setByte(1, (byte)1);
    verify(stmt).setInt(2, 2);
    verify(stmt).setDouble(3, 3);
  }

  @Test
  public void test4() throws Exception {
    ConnectionProvider cp = connectionProvider();
    repo = ctx.connections(cp).getRepository("TestRepo.sql");
    Statement statement = repo.get("test4");
    statement.set("value1", (byte)1);
    statement.set("value2", 2);
    statement.get("t1").set("value", 3.0).render();
    assertEquals(TestResults.test3(), statement.toString());
    statement.forEach(null);
    verify(stmt).setByte(1, (byte)1);
    verify(stmt).setDouble(2, 3);
    verify(stmt).setInt(3, 2);
  }

  @Test
  public void test4_1() throws Exception {
    ConnectionProvider cp = connectionProvider();
    repo = ctx.connections(cp).getRepository("TestRepo.sql");
    Statement statement = repo.get("test4");
    statement.set("value1", (byte)1);
    statement.get("t1").set("value", 3.0).render();
    assertEquals(TestResults.test2(), statement.toString());
    statement.forEach(null);
    verify(stmt).setByte(1, (byte)1);
    verify(stmt).setDouble(2, 3);
  }

  @Test
  public void test4_2() throws Exception {
    ConnectionProvider cp = connectionProvider();
    repo = ctx.connections(cp).getRepository("TestRepo.sql");
    Statement statement = repo.get("test4");
    statement.set("value1", (byte)1);
    statement.set("value2", 3.0);
    assertEquals(TestResults.test2(), statement.toString());
    statement.forEach(null);
    verify(stmt).setByte(1, (byte)1);
    verify(stmt).setDouble(2, 3);
  }

  @Test
  public void test4_3() throws Exception {
    ConnectionProvider cp = connectionProvider();
    repo = ctx.connections(cp).getRepository("TestRepo.sql");
    Statement statement = repo.get("test4");
    statement.set("value1", (byte)1);
    statement.get("t1").set("value", 3.0).render();
    statement.get("t1").set("value", 4.0).render();
    assertEquals(TestResults.test3(), statement.toString());
    statement.forEach(null);
    verify(stmt).setByte(1, (byte)1);
    verify(stmt).setDouble(2, 3);
    verify(stmt).setDouble(3, 4);
  }

  @Test
  public void test4_34() throws Exception {
    ConnectionProvider cp = connectionProvider();
    repo = ctx.connections(cp).getRepository("TestRepo.sql");
    Statement statement = repo.get("test4");
    statement.set("value1", (byte)1);
    assertEquals(TestResults.test1(), statement.toString());
    statement.forEach(null);
    verify(stmt).setByte(1, (byte)1);
  }

  @Test
  public void test5() throws Exception {
    ConnectionProvider cp = connectionProvider();
    repo = ctx.connections(cp).getRepository("TestRepo.sql");
    Statement statement = repo.get("test5");
    statement.set("value1", (byte)1);
    statement.set("value2", 2);
    statement.get("t1").set("value", 3.0).render();
    assertEquals(TestResults.test3(), statement.toString());
    statement.forEach(null);
    verify(stmt).setByte(1, (byte)1);
    verify(stmt).setDouble(3, 3);
    verify(stmt).setInt(2, 2);
  }

  protected ConnectionProvider connectionProvider() throws Exception {
    ResultSet rs = mock(ResultSet.class);
    stmt = mock(PreparedStatement.class);
    when(stmt.executeQuery()).thenReturn(rs);

    Connection con = mock(Connection.class);
    when(con.prepareStatement(any(String.class))).thenReturn(stmt);
    ConnectionProvider cp = mock(ConnectionProvider.class);
    when(cp.getConnection()).thenReturn(con);
    return cp;
  }
}
