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
