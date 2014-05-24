package org.jproggy.snippetory.sql;

import java.sql.ResultSet;

public interface RowProcessor {
  void processRow(ResultSet rs);
}
