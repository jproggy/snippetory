package org.jproggy.snippetory.sql.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.jproggy.snippetory.engine.DataSink;
import org.jproggy.snippetory.engine.DataSinks;
import org.jproggy.snippetory.engine.Location;

public class SqlSinks extends DataSinks implements StatementBinder {

  public SqlSinks(List<DataSink> parts, Location placeHolder) {
    super(parts, placeHolder);
  }

  protected SqlSinks(DataSinks template, Location parent) {
    super(template, parent);
  }

  @Override
  public DataSinks cleanCopy(Location parent) {
    return new SqlSinks(this, parent);
  }

  @Override
  public int bindTo(PreparedStatement stmt, int offset) throws SQLException {
    for (DataSink v : parts) {
      offset = ((StatementBinder)v).bindTo(stmt, offset);
    }
    return offset;
  }

}
