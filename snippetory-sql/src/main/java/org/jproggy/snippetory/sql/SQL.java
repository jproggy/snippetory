/// Copyright JProggy
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.

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

import org.jproggy.snippetory.spi.Configurer;
import org.jproggy.snippetory.spi.EncodedData;
import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Syntax;
import org.jproggy.snippetory.spi.SyntaxID;
import org.jproggy.snippetory.sql.impl.SqlSyntax;
import org.jproggy.snippetory.sql.spi.RowTransformer;
import org.jproggy.snippetory.util.EncodedContainer;
import org.jproggy.snippetory.util.IncompatibleEncodingException;

/**
 * Small helper class to execute very simple stuff like
 * <pre>
 * {@code
 * long productCount = repository.get("countProducts").one(SQL.asLong());}
 * </pre>
 * or
 * <pre>
 * {@code
 * Map<String, Long> productCount = repository.get("countProductsByCategory").map(
 *   SQL.asString("catId"),
 *   SQL.asLong("count")
 *  );}
 * </pre>
 */
public class SQL implements Encoding, Configurer {
  public static final SyntaxID SYNTAX;
  static {
    SqlSyntax s = new SqlSyntax();
    SYNTAX = s;
    Syntax.register(SYNTAX, s);
  }

  public static final SQL ENCODING = new SQL();
  static {
    Encoding.register(ENCODING);
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

  public static ColumnTransformer<Boolean> asBoolean() {
    return asBoolean(1);
  }

  public static ColumnTransformer<Boolean> asBoolean(int colIndex) {
    return new ColumnTransformer<>(colIndex) {
      @Override
      public Boolean transformRow(ResultSet rs) throws SQLException {
        return rs.getBoolean(colIndex);
      }
    };
  }

  public static ColumnTransformer<Boolean> asBoolean(String colName) {
    return new ColumnTransformer<>(colName) {
      @Override
      public Boolean transformRow(ResultSet rs) throws SQLException {
        return rs.getBoolean(colName);
      }
    };
  }

  public static ColumnTransformer<Integer> asInteger() {
    return asInteger(1);
  }

  public static ColumnTransformer<Integer> asInteger(int colIndex) {
    return new ColumnTransformer<>(colIndex) {
      @Override
      public Integer transformRow(ResultSet rs) throws SQLException {
        return rs.getInt(colIndex);
      }
    };
  }

  public static ColumnTransformer<Integer> asInteger(String colName) {
    return new ColumnTransformer<>(colName) {
      @Override
      public Integer transformRow(ResultSet rs) throws SQLException {
        return rs.getInt(colName);
      }
    };
  }

  public static ColumnTransformer<Long> asLong() {
    return asLong(1);
  }

  public static ColumnTransformer<Long> asLong(int colIndex) {
    return new ColumnTransformer<>(colIndex) {
      @Override
      public Long transformRow(ResultSet rs) throws SQLException {
        return rs.getLong(colIndex);
      }
    };
  }

  public static ColumnTransformer<Long> asLong(String colName) {
    return new ColumnTransformer<>(colName) {
      @Override
      public Long transformRow(ResultSet rs) throws SQLException {
        return rs.getLong(colName);
      }
    };
  }

  public static RowTransformer<Double> asDouble() {
    return asDouble(1);
  }

  public static ColumnTransformer<Double> asDouble(int colIndex) {
    return new ColumnTransformer<>(colIndex) {
      @Override
      public Double transformRow(ResultSet rs) throws SQLException {
        return rs.getDouble(colIndex);
      }
    };
  }

  public static ColumnTransformer<Double> asDouble(String colName) {
    return new ColumnTransformer<>(colName) {
      @Override
      public Double transformRow(ResultSet rs) throws SQLException {
        return rs.getDouble(colName);
      }
    };
  }

  public static ColumnTransformer<BigDecimal> asBigDecimal() {
    return asBigDecimal(1);
  }

  public static ColumnTransformer<BigDecimal> asBigDecimal(int colIndex) {
    return new ColumnTransformer<>(colIndex) {
      @Override
      public BigDecimal transformRow(ResultSet rs) throws SQLException {
        return rs.getBigDecimal(colIndex);
      }
    };
  }

  public static ColumnTransformer<BigDecimal> asBigDecimal(String colName) {
    return new ColumnTransformer<>(colName) {
      @Override
      public BigDecimal transformRow(ResultSet rs) throws SQLException {
        return rs.getBigDecimal(colName);
      }
    };
  }

  public static ColumnTransformer<String> asString() {
    return asString(1);
  }

  public static ColumnTransformer<String> asString(int colIndex) {
    return new ColumnTransformer<>(colIndex) {
      @Override
      public String transformRow(ResultSet rs) throws SQLException {
        return rs.getString(colIndex);
      }
    };
  }

  public static ColumnTransformer<String> asString(String colName) {
    return new ColumnTransformer<>(colName) {
      @Override
      public String transformRow(ResultSet rs) throws SQLException {
        return rs.getString(colName);
      }
    };
  }

  public static ColumnTransformer<Date> asSqlDate() {
    return asSqlDate(1);
  }

  public static ColumnTransformer<Date> asSqlDate(int colIndex) {
    return new ColumnTransformer<>(colIndex) {
      @Override
      public Date transformRow(ResultSet rs) throws SQLException {
        return rs.getDate(colIndex);
      }
    };
  }

  public static RowTransformer<Date> asSqlDate(String colName) {
    return new RowTransformer<>() {
      @Override
      public Date transformRow(ResultSet rs) throws SQLException {
        return rs.getDate(colName);
      }
    };
  }

  public static ColumnTransformer<Time> asSqlTime() {
    return asSqlTime(1);
  }

  public static ColumnTransformer<Time> asSqlTime(int colIndex) {
    return new ColumnTransformer<>(colIndex) {
      @Override
      public Time transformRow(ResultSet rs) throws SQLException {
        return rs.getTime(colIndex);
      }
    };
  }

  public static ColumnTransformer<Time> asSqlTime(String colName) {
    return new ColumnTransformer<>(colName) {
      @Override
      public Time transformRow(ResultSet rs) throws SQLException {
        return rs.getTime(colName);
      }
    };
  }

  public static ColumnTransformer<Timestamp> asSqlTimestamp() {
    return asSqlTimestamp(1);
  }

  public static ColumnTransformer<Timestamp> asSqlTimestamp(int colIndex) {
    return new ColumnTransformer<>(colIndex) {
      @Override
      public Timestamp transformRow(ResultSet rs) throws SQLException {
        return rs.getTimestamp(colIndex);
      }
    };
  }

  public static ColumnTransformer<Timestamp> asSqlTimestamp(String colName) {
    return new ColumnTransformer<>(colName) {
      @Override
      public Timestamp transformRow(ResultSet rs) throws SQLException {
        return rs.getTimestamp(colName);
      }
    };
  }

  public static RowTransformer<Object[]> asObjects() {
    return asObjects(-1);
  }

  public static RowTransformer<Object[]> asObjects(int numColums) {
    return new RowTransformer<>() {
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

  public static RowTransformer<Map<String, Object>> asMap(String... colNames) {
    return new RowTransformer<>() {
      @Override
      public Map<String, Object> transformRow(ResultSet rs) throws SQLException {
        Map<String, Object> result = new HashMap<>(colNames.length);
        for (String colName : colNames) {
          result.put(colName, rs.getObject(colName));
        }
        return result;
      }
    };
  }

  public static RowTransformer<Map<String, Object>> asMap() {
    return new RowTransformer<>() {
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

  public abstract static class ColumnTransformer<T> implements RowTransformer<T> {
    private final int index;
    private final String colName;

    private ColumnTransformer(ColumnTransformer<?> parent) {
      this.index = parent.index;
      this.colName = parent.colName;
    }

    private ColumnTransformer(String colName) {
      this.index = 0;
      this.colName = colName;
    }

    private ColumnTransformer(int index) {
      this.index = index;
      this.colName = null;
    }

    public ColumnTransformer<T> orElse(ColumnTransformer<T> other) {
      return new ColumnTransformer<>(this) {
        @Override
        public T transformRow(ResultSet rs) throws SQLException {
          T val = ColumnTransformer.this.transformRow(rs);
          if (rs.wasNull()) return other.transformRow(rs);
          return val;
        }
      };
    }

    public ColumnTransformer<T> orElse(T defaultVal) {
      return new ColumnTransformer<>(this) {
        @Override
        public T transformRow(ResultSet rs) throws SQLException {
          T val = ColumnTransformer.this.transformRow(rs);
          if (rs.wasNull()) return defaultVal;
          return val;
        }
      };
    }

    public ColumnTransformer<T> orFail() {
      return new ColumnTransformer<>(this) {
        @Override
        public T transformRow(ResultSet rs) throws SQLException {
          T val = ColumnTransformer.this.transformRow(rs);
          if (rs.wasNull()) throw new NullPointerException("No value found in " + col());
          return val;
        }
      };
    }

    public ColumnTransformer<String> asString() {
      return new ColumnTransformer<>(this) {
        @Override
        public String transformRow(ResultSet rs) throws SQLException {
          T val = ColumnTransformer.this.transformRow(rs);
          if (rs.wasNull()) return null;
          return val.toString();
        }
      };
    }

    private String col() {
      if (colName == null) return "No. " + index;
      return colName;
    }
  }

}
