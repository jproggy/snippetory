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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.sql.impl.ResultCountException;
import org.jproggy.snippetory.sql.spi.RowProcessor;
import org.jproggy.snippetory.sql.spi.RowTransformer;

/**
 * Represents the template of a statement and methods to bind data as well as to tailor this statement
 * to the current needs. This means all the possibilities of a template can be used here. However,
 * there are a view specialties to mention. In general data is intended to be bound to the parameters of
 * a prepared statement. The type that's used to provide this data is used to figure out the binding
 * method. So if a {@code String} is bound {@link PreparedStatement#setString}, {@link PreparedStatement#setInt} for
 * an {@code Integer} and so on. To extend the statement itself instead of binding parameters the {@link SQL#markAsSql}
 * method can be used, if those parts are represented by plain {@code String}s. Parts, that are taken from other
 * statements will extend the statement as well.
 *
 * @author B.Ebertz
 */
public interface Statement extends Template {
  @Override
  Statement set(String name, Object value);
  @Override
  Statement append(String name, Object value);
  @Override
  Statement clear();

  /**
   * Create a prepared statement from the provided connection and bind the data, provided via the statements
   * {@code set} method to this prepared statement. This is the most basic way to use.
   */
  PreparedStatement getStatement(Connection conn) throws SQLException;

  /**
   * The RowProcessor is called again until there are no further rows
   * in the result set. This means, if RowProcessor doesn't navigate
   * the result set, it will be called once per row of the result
   * until it throws an exception.
   *
   * @param proc Every runtime exception thrown in RowProcessor.process is
   *         re-thrown.
   * @throws NullPointerException if proc is null
   *
   */
  void forEach(RowProcessor proc);
  
  /**
   * The RowTransformer is called again until there are no further rows
   * in the result set and all results are collected in a list. This means, 
   * if RowProcessor doesn't navigate 
   * the result set, it will be called once per row of the result
   * until it throws an exception.
   *
   * @param transformer The RowProcessor will be called as long as ResultSet.next
   *        returns true.
   */
  default <T> List<T> list(RowTransformer<T> transformer) {
    try (Cursor<T> rows = cursor(transformer)) {
      List<T> result =  new ArrayList<>();
      rows.forEach(result::add);
      return result;
    }
  }
  /**
   * As long a the key is unique, the order of the query result is preserved.
   */
  <K, V> Map<K, V> map(RowTransformer<K> key, RowTransformer<V> value);
  /**
   * @throws ResultCountException if the conversion to a list doesn't result in a list with size == 1
   */
  <T> T one(RowTransformer<T> transformer) throws ResultCountException;
  /**
   * In most cases it's preferable to use {@link #forEach(RowProcessor)}. The Cursor is a bit more flexible in some
   * cases, but also more complicated in usage.
   */
  <T> Cursor<T> cursor(RowTransformer<T> transformer);
  /**
   * Executes the query as an update.
   * @return number of rows affected by the update.
   */
  long executeUpdate();
}
