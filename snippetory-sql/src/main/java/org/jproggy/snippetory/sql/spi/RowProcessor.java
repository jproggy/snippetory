package org.jproggy.snippetory.sql.spi;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowProcessor {
  void processRow(ResultSet rs) throws SQLException;
}
