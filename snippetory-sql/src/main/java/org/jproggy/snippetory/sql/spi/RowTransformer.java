package org.jproggy.snippetory.sql.spi;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowTransformer<T> {
  public T transformRow(ResultSet rs) throws SQLException;
}
