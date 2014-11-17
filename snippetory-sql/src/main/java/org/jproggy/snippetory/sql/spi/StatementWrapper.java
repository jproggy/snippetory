package org.jproggy.snippetory.sql.spi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.spi.TemplateWrapper;
import org.jproggy.snippetory.sql.Cursor;
import org.jproggy.snippetory.sql.Statement;

public abstract class StatementWrapper extends TemplateWrapper implements Statement {

  public StatementWrapper(Statement template) {
    super(template);
  }

  protected Statement wrapped() {
    return (Statement)wrapped;
  }

  @Override
  public Statement get(String... name) {
    return (Statement)super.get(name);
  }
  @Override
  public Statement set(String name, Object value) {
    return wrapped().set(name, value);
  }
  @Override
  public Statement append(String name, Object value) {
    return wrapped().append(name, value);
  }
  @Override
  public Statement clear() {
    return wrapped().clear();
  }

  @Override
  public PreparedStatement getStatement(Connection conn) throws SQLException {
    return wrapped().getStatement(conn);
  }

  @Override
  public void forEach(RowProcessor proc) {
    wrapped().forEach(proc);
  }

  @Override
  public <T> List<T> list(RowTransformer<T> transformer) {
    return wrapped().list(transformer);
  }

  @Override
  public <K, V> Map<K, V> map(RowTransformer<K> key, RowTransformer<V> value) {
    return wrapped().map(key, value);
  }

  @Override
  public <T> T one(RowTransformer<T> transformer) {
    return wrapped().one(transformer);
  }

  @Override
  public <T> Cursor<T> directCursor(RowTransformer<T> transformer) {
    return wrapped().directCursor(transformer);
  }

  @Override
  public <T> Cursor<T> readAheadCursor(RowTransformer<T> transformer) {
    return wrapped().readAheadCursor(transformer);
  }

  @Override
  public long executeUpdate() {
    return wrapped().executeUpdate();
  }

  @Override
  protected abstract Statement wrap(Template toBeWrapped);
}