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
import org.jproggy.snippetory.spi.Format;

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
      values.add(format(this, value));
      value = "?";
    } else if (mine(name) && value instanceof StatementBinder) {
      values.add(value);
    }
    return value;
  }

  private Object format(Location node, Object value) {
    if (isSupported(value)) return value;
    for (Format f : getFormats()) {
      if (matches(node, f) && f.supports(value)) {
        value = f.format(node, value);
        if (isSupported(value)) return value;
      }
    }
    if (getParent() != null) return ((Parameter)getParent()).format(node, value);
    return String.valueOf(value);
  }

  private boolean isSupported(Object val) {
    if (val instanceof String) {
      return true;
    } else if (val instanceof BigDecimal) {
      return true;
    } else if (val instanceof Byte) {
      return true;
    } else if (val instanceof Double) {
      return true;
    } else if (val instanceof Float) {
      return true;
    } else if (val instanceof Long) {
      return true;
    } else if (val instanceof Integer) {
      return true;
    } else if (val instanceof Short) {
      return true;
    } else if (val instanceof Date) {
      return true;
    } else if (val instanceof Time) {
      return true;
    } else if (val instanceof Timestamp) {
      return true;
    } else if (val instanceof java.util.Date) {
      return true;
    } else if (val instanceof URL) {
      return true;
    } else if (val instanceof Array) {
      return true;
    } else if (val instanceof Blob) {
      return true;
    } else if (val instanceof Clob) {
      return true;
    } else if (val instanceof NClob) {
      return true;
    } else if (val instanceof Ref) {
      return true;
    } else if (val instanceof SQLXML) {
      return true;
    } else if (val instanceof byte[]) {
      return true;
    }
    return false;
  }

  @Override
  public int bindTo(PreparedStatement stmt, int offset) throws SQLException {
    for (Object val: values) {
      if (val instanceof StatementBinder) {
        offset = ((StatementBinder)val).bindTo(stmt, offset);
        continue;
      } else if (val instanceof String) {
        stmt.setString(offset, (String)val);
      } else if (val instanceof BigDecimal) {
        stmt.setBigDecimal(offset, (BigDecimal)val);
      } else if (val instanceof Byte) {
        stmt.setByte(offset, ((Number)val).byteValue());
      } else if (val instanceof Double) {
        stmt.setDouble(offset, ((Number)val).doubleValue());
      } else if (val instanceof Float) {
        stmt.setFloat(offset, ((Number)val).floatValue());
      } else if (val instanceof Long) {
        stmt.setLong(offset, ((Number)val).longValue());
      } else if (val instanceof Integer) {
        stmt.setInt(offset, ((Number)val).intValue());
      } else if (val instanceof Short) {
        stmt.setShort(offset, ((Number)val).shortValue());
      } else if (val instanceof Date) {
        stmt.setDate(offset, (Date)val);
      } else if (val instanceof Time) {
        stmt.setTime(offset, (Time)val);
      } else if (val instanceof Timestamp) {
        stmt.setTimestamp(offset, (Timestamp)val);
      } else if (val instanceof java.util.Date) {
        stmt.setTimestamp(offset, new Timestamp(((java.util.Date)val).getTime()));
      } else if (val instanceof URL) {
        stmt.setURL(offset, (URL)val);
      } else if (val instanceof Array) {
        stmt.setArray(offset, (Array)val);
      } else if (val instanceof Blob) {
        stmt.setBlob(offset, (Blob)val);
      } else if (val instanceof Clob) {
        stmt.setClob(offset, (Clob)val);
      } else if (val instanceof NClob) {
        stmt.setNClob(offset, (NClob)val);
      } else if (val instanceof Ref) {
        stmt.setRef(offset, (Ref)val);
      } else if (val instanceof SQLXML) {
        stmt.setSQLXML(offset, (SQLXML)val);
      } else if (val instanceof byte[]) {
        stmt.setBytes(offset, (byte[])val);
      }
      offset++;
    }
    return offset;
  }
}