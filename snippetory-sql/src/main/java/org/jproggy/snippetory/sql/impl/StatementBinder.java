package org.jproggy.snippetory.sql.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementBinder {
  int bindTo(PreparedStatement stmt, int offset) throws SQLException;
}
