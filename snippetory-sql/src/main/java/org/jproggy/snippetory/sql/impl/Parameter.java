package org.jproggy.snippetory.sql.impl;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.jproggy.snippetory.engine.Location;
import org.jproggy.snippetory.engine.Metadata;
import org.jproggy.snippetory.spi.EncodedData;

public class Parameter extends Location implements StatementBinder {
  private List<Object> values = new ArrayList<Object>();

  public Parameter(Location parent, Metadata metadata) {
    super(parent, metadata);
  }

  @Override
  public Location cleanCopy(Location parent) {
     return new Parameter(parent, md);
  }

  @Override
  public void set(String name, Object value) {
    value = handleValue(name, value);
    super.set(name, value);
  }

  @Override
  public void append(String name, Object value) {
    value = handleValue(name, value);
    super.append(name, value);
  }

  protected Object handleValue(String name, Object value) {
    if (mine(name) && !(value instanceof EncodedData)) {
      values.add(value);
      value = "?";
    } else if (mine(name) && value instanceof StatementBinder) {
      values.add(value);
    }
    return value;
  }

  @Override
  public int bindTo(PreparedStatement stmt, int offset) throws SQLException {
    int i = 0;
    for (Object val: values) {
      if (val instanceof StatementBinder) {
        offset = ((StatementBinder)val).bindTo(stmt, offset + i);
        i = 0;
        continue;
      } else if (val instanceof String) {
        stmt.setString(offset + i, (String)val);
      } else if (val instanceof BigDecimal) {
        stmt.setBigDecimal(offset + i, (BigDecimal)val);
      } else if (val instanceof Byte) {
        stmt.setByte(offset + i, ((Number)val).byteValue());
      } else if (val instanceof Double) {
        stmt.setDouble(offset + i, ((Number)val).doubleValue());
      } else if (val instanceof Float) {
        stmt.setFloat(offset + i, ((Number)val).floatValue());
      } else if (val instanceof Long) {
        stmt.setLong(offset + i, ((Number)val).longValue());
      } else if (val instanceof Integer) {
        stmt.setInt(offset + i, ((Number)val).intValue());
      } else if (val instanceof Short) {
        stmt.setShort(offset + i, ((Number)val).shortValue());
      } else if (val instanceof Date) {
        stmt.setDate(offset + i, (Date)val);
      } else if (val instanceof Time) {
        stmt.setTime(offset + i, (Time)val);
      } else if (val instanceof Timestamp) {
        stmt.setTimestamp(offset + i, (Timestamp)val);
      } else if (val instanceof java.util.Date) {
        stmt.setTimestamp(offset + i, new Timestamp(((java.util.Date)val).getTime()));
      } else if (val instanceof URL) {
        stmt.setURL(offset + i, (URL)val);
      } else if (val instanceof Array) {
        stmt.setArray(offset + i, (Array)val);
      } else if (val instanceof Blob) {
        stmt.setBlob(offset + i, (Blob)val);
      } else if (val instanceof Clob) {
        stmt.setClob(offset + i, (Clob)val);
      } else if (val instanceof NClob) {
        stmt.setNClob(offset + i, (NClob)val);
      } else if (val instanceof Ref) {
        stmt.setRef(offset + i, (Ref)val);
      } else if (val instanceof SQLXML) {
        stmt.setSQLXML(offset + i, (SQLXML)val);
      } else if (val instanceof byte[]) {
        stmt.setBytes(offset + i, (byte[])val);
      }
      i++;
    }
    return offset + i;
  }
}
