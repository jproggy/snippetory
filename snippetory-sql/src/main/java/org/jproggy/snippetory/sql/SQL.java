package org.jproggy.snippetory.sql;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.jproggy.snippetory.engine.IncompatibleEncodingException;
import org.jproggy.snippetory.engine.chars.EncodedContainer;
import org.jproggy.snippetory.spi.Configurer;
import org.jproggy.snippetory.spi.EncodedData;
import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Syntax;
import org.jproggy.snippetory.spi.SyntaxID;
import org.jproggy.snippetory.sql.impl.SqlSyntax;
import org.jproggy.snippetory.sql.spi.RowTransformer;

public class SQL implements Encoding, Configurer {
  public static final SyntaxID SYNTAX;
  static {
    SqlSyntax s = new SqlSyntax();
    SYNTAX = s;
    Syntax.REGISTRY.register(SYNTAX, s);
  }

  public static final SQL ENCODING = new SQL();
  static {
    Encoding.REGISTRY.register(ENCODING);
  }

  public SQL () {
    super();
  }

  public static EncodedData markAsSql(CharSequence value) {
    return new EncodedContainer(value, "sql");
  }

  @Override
  public void transcode(Appendable target, CharSequence value, String encodingName) throws IOException,
      IncompatibleEncodingException {
    if (value.length() == 1 && value.charAt(0) == '?') {
      target.append('?');
    } else {
      throw new IncompatibleEncodingException("can't convert encoding " + encodingName + " into " + getName());
    }
  }

  @Override
  public String getName() {
    return "sql";
  }

  public static RowTransformer<Boolean> asBoolean() {
    return asBoolean(1);
  }

  public static RowTransformer<Boolean> asBoolean(final int colIndex) {
    return new RowTransformer<Boolean>() {
      @Override
      public Boolean transformRow(ResultSet rs) throws SQLException {
        return rs.getBoolean(colIndex);
      }
    };
  }

  public static RowTransformer<Boolean> asBoolean(final String colName) {
    return new RowTransformer<Boolean>() {
      @Override
      public Boolean transformRow(ResultSet rs) throws SQLException {
        return rs.getBoolean(colName);
      }
    };
  }

  public static RowTransformer<Integer> asInteger() {
    return asInteger(1);
  }

  public static RowTransformer<Integer> asInteger(final int colIndex) {
    return new RowTransformer<Integer>() {
      @Override
      public Integer transformRow(ResultSet rs) throws SQLException {
        return rs.getInt(colIndex);
      }
    };
  }

  public static RowTransformer<Integer> asInteger(final String colName) {
    return new RowTransformer<Integer>() {
      @Override
      public Integer transformRow(ResultSet rs) throws SQLException {
        return rs.getInt(colName);
      }
    };
  }

  public static RowTransformer<Long> asLong() {
    return asLong(1);
  }

  public static RowTransformer<Long> asLong(final int colIndex) {
    return new RowTransformer<Long>() {
      @Override
      public Long transformRow(ResultSet rs) throws SQLException {
        return rs.getLong(colIndex);
      }
    };
  }

  public static RowTransformer<Long> asLong(final String colName) {
    return new RowTransformer<Long>() {
      @Override
      public Long transformRow(ResultSet rs) throws SQLException {
        return rs.getLong(colName);
      }
    };
  }

  public static RowTransformer<Double> asDouble() {
    return asDouble(1);
  }

  public static RowTransformer<Double> asDouble(final int colIndex) {
    return new RowTransformer<Double>() {
      @Override
      public Double transformRow(ResultSet rs) throws SQLException {
        return rs.getDouble(colIndex);
      }
    };
  }

  public static RowTransformer<Double> asDouble(final String colName) {
    return new RowTransformer<Double>() {
      @Override
      public Double transformRow(ResultSet rs) throws SQLException {
        return rs.getDouble(colName);
      }
    };
  }

  public static RowTransformer<BigDecimal> asBigDecimal() {
    return asBigDecimal(1);
  }

  public static RowTransformer<BigDecimal> asBigDecimal(final int colIndex) {
    return new RowTransformer<BigDecimal>() {
      @Override
      public BigDecimal transformRow(ResultSet rs) throws SQLException {
        return rs.getBigDecimal(colIndex);
      }
    };
  }

  public static RowTransformer<BigDecimal> asBigDecimal(final String colName) {
    return new RowTransformer<BigDecimal>() {
      @Override
      public BigDecimal transformRow(ResultSet rs) throws SQLException {
        return rs.getBigDecimal(colName);
      }
    };
  }

  public static RowTransformer<String> asString() {
    return asString(1);
  }

  public static RowTransformer<String> asString(final int colIndex) {
    return new RowTransformer<String>() {
      @Override
      public String transformRow(ResultSet rs) throws SQLException {
        return rs.getString(colIndex);
      }
    };
  }

  public static RowTransformer<String> asString(final String colName) {
    return new RowTransformer<String>() {
      @Override
      public String transformRow(ResultSet rs) throws SQLException {
        return rs.getString(colName);
      }
    };
  }

  public static RowTransformer<Date> asSqlDate() {
    return asSqlDate(1);
  }

  public static RowTransformer<Date> asSqlDate(final int colIndex) {
    return new RowTransformer<Date>() {
      @Override
      public Date transformRow(ResultSet rs) throws SQLException {
        return rs.getDate(colIndex);
      }
    };
  }

  public static RowTransformer<Date> asSqlDate(final String colName) {
    return new RowTransformer<Date>() {
      @Override
      public Date transformRow(ResultSet rs) throws SQLException {
        return rs.getDate(colName);
      }
    };
  }

  public static RowTransformer<Time> asSqlTime() {
    return asSqlTime(1);
  }

  public static RowTransformer<Time> asSqlTime(final int colIndex) {
    return new RowTransformer<Time>() {
      @Override
      public Time transformRow(ResultSet rs) throws SQLException {
        return rs.getTime(colIndex);
      }
    };
  }

  public static RowTransformer<Time> asSqlTime(final String colName) {
    return new RowTransformer<Time>() {
      @Override
      public Time transformRow(ResultSet rs) throws SQLException {
        return rs.getTime(colName);
      }
    };
  }

  public static RowTransformer<Timestamp> asSqlTimestamp() {
    return asSqlTimestamp(1);
  }

  public static RowTransformer<Timestamp> asSqlTimestamp(final int colIndex) {
    return new RowTransformer<Timestamp>() {
      @Override
      public Timestamp transformRow(ResultSet rs) throws SQLException {
        return rs.getTimestamp(colIndex);
      }
    };
  }

  public static RowTransformer<Timestamp> asSqlTimestamp(final String colName) {
    return new RowTransformer<Timestamp>() {
      @Override
      public Timestamp transformRow(ResultSet rs) throws SQLException {
        return rs.getTimestamp(colName);
      }
    };
  }

  public static RowTransformer<Object[]> asObjects() {
    return asObjects(-1);
  }

  public static RowTransformer<Object[]> asObjects(final int numColums) {
    return new RowTransformer<Object[]>() {
      private int numberOfCols = numColums;
      @Override
      public Object[] transformRow(ResultSet rs) throws SQLException {
        if (numberOfCols < 0) {
          numberOfCols = rs.getMetaData().getColumnCount();
        }
        Object[] result = new Object[numberOfCols];
        for (int i = 0; i < numberOfCols; i++) {
          result[i] = rs.getObject(i + 1);
        }
        return result;
      }
    };
  }

  public static RowTransformer<Map<String, Object>> asMap(final String... colNames) {
    return new RowTransformer<Map<String, Object>>() {
      @Override
      public Map<String, Object> transformRow(ResultSet rs) throws SQLException {
        Map<String, Object> result = new HashMap<String, Object>(colNames.length);
        for (String colName: colNames) {
          result.put(colName, rs.getObject(colName));
        }
        return result;
      }
    };
  }

  public static RowTransformer<Map<String, Object>> asMap() {
    return new RowTransformer<Map<String, Object>>() {
      private String[] colNames;
      @Override
      public Map<String, Object> transformRow(ResultSet rs) throws SQLException {
        if (colNames == null) {
          ResultSetMetaData metaData = rs.getMetaData();
          int numCols = metaData.getColumnCount();
          colNames = new String[numCols];
          for (int i = 0; i < numCols; i++) {
            colNames[i] = metaData.getColumnLabel(i + 1);
          }
        }

        Map<String, Object> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < colNames.length; i++) {
          result.put(colNames[i], rs.getObject(i + 1));
        }
        return result;
      }
    };
  }

}
