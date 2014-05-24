package org.jproggy.snippetory.sql.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.jproggy.snippetory.engine.ConditionalRegion;
import org.jproggy.snippetory.engine.DataSink;
import org.jproggy.snippetory.engine.Location;
import org.jproggy.snippetory.engine.Region;

public class ConditionalSqlRegion extends ConditionalRegion implements StatementBinder {

  public ConditionalSqlRegion(Location formatter, List<DataSink> parts, Map<String, Region> children) {
    super(formatter, parts, children);
  }

  protected ConditionalSqlRegion(ConditionalRegion template, Location parent) {
    super(template, parent);
  }

  @Override
  public ConditionalSqlRegion cleanCopy(Location parent) {
    return new ConditionalSqlRegion(this, parent);
  }

  @Override
  public int bindTo(PreparedStatement stmt, int offset) throws SQLException {
    if (!appendMe()) return offset;
    for (DataSink v : parts) {
      offset = ((StatementBinder)v).bindTo(stmt, offset);
    }
    return offset;
  }
}
