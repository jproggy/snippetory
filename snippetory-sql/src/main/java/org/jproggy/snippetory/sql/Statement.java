package org.jproggy.snippetory.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jproggy.snippetory.Template;

public interface Statement extends Template {
  @Override
  Statement get(String... name);
  @Override
  Statement set(String name, Object value);
  @Override
  Statement append(String name, Object value);
  @Override
  Statement clear();

  PreparedStatement getStatement(Connection conn) throws SQLException;
  void processRows(RowProcessor proc);

  long executeUpdate();
}
