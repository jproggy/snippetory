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
import java.util.List;
import java.util.Map;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.sql.spi.RowProcessor;
import org.jproggy.snippetory.sql.spi.RowTransformer;

public interface Statement extends Template {
  @Override
  Statement get(String... name);
  @Override
  Statement set(String name, Object value);
  @Override
  Statement append(String name, Object value);
  @Override
  Statement clear();

  PreparedStatement getStatement(Connection conn) throws SQLException;

  void forEach(RowProcessor proc);
  <T> List<T> list(RowTransformer<T> transformer);
  <K, V> Map<K, V> map(RowTransformer<K> key, RowTransformer<V> value);
  <T> T one(RowTransformer<T> transformer);
  <T> Cursor<T> directCursor(RowTransformer<T> transformer);
  <T> Cursor<T> readAheadCursor(RowTransformer<T> transformer);
  long executeUpdate();
}
