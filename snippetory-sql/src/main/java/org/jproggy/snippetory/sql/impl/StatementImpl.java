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

package org.jproggy.snippetory.sql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.jproggy.snippetory.engine.Location;
import org.jproggy.snippetory.engine.Region;
import org.jproggy.snippetory.engine.SnippetoryException;
import org.jproggy.snippetory.sql.Cursor;
import org.jproggy.snippetory.sql.Statement;
import org.jproggy.snippetory.sql.spi.ConnectionProvider;
import org.jproggy.snippetory.sql.spi.RowProcessor;
import org.jproggy.snippetory.sql.spi.RowTransformer;
import org.jproggy.snippetory.util.ResourceObserver;
import org.jproggy.snippetory.util.ResourceObserver.Ref;

public class StatementImpl extends Region implements Statement, StatementBinder {
  private ConnectionProvider connectionProvider;
  private static final ScheduledExecutorService runner = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 3);
  private static final ResourceObserver resources = new ResourceObserver(runner);

  public StatementImpl(SqlSinks data, Map<String, Region> children) {
    super(data, children);
  }

  protected StatementImpl(StatementImpl template, Location parent) {
    super(template, parent);
  }

  protected StatementImpl(StatementImpl template, StatementImpl parent) {
    super(template, parent);
  }

  @Override
  public StatementImpl get(String... path) {
    return (StatementImpl)super.get(path);
  }

  @Override
  public StatementImpl set(String key, Object value) {
    super.set(key, value);
    return this;
  }

  @Override
  public StatementImpl append(String key, Object value) {
    super.append(key, value);
    return this;
  }

  @Override
  public StatementImpl clear() {
    super.clear();
    return this;
  }

  @Override
  public PreparedStatement getStatement(Connection conn) throws SQLException {
    PreparedStatement stmt = conn.prepareStatement(toString());
    ((StatementBinder)data).bindTo(stmt, 1);
    return stmt;
  }

  @Override
  public void forEach(RowProcessor proc) {
    try (Connection connection = getConnection();
        PreparedStatement stmt = getStatement(connection);
        ResultSet rs = stmt.executeQuery()) {
      while (rs.next()) {
        proc.processRow(rs);
      }
    } catch (SQLException e) {
      throw new SnippetoryException(e);
    }

  }

  @Override
  public <T> List<T> list(RowTransformer<T> transformer) {
    try (Cursor<T> rows = cursor(transformer)) {
      List<T> result =  new ArrayList<>();
      rows.forEach(result::add);
      return result;
    }
  }

  @Override
  public <K, V> Map<K, V> map(RowTransformer<K> key, RowTransformer<V> value) {
    final Map<K, V> result = new LinkedHashMap<>();
    forEach(rs -> result.put(key.transformRow(rs), value.transformRow(rs)));
    return result;
  }

  @Override
  public <T> T one(RowTransformer<T> transformer) {
    List<T> list = list(transformer);
    if (list.size() == 1) return list.get(0);
    throw new ResultCountException(list.size());
  }

  @Override
  public <T> Cursor<T> cursor(RowTransformer<T> transformer) {
    DirectCursor<T> c = new DirectCursor<>(transformer);
    Ref handle = resources.observe(c, c.sql);
    c.setHandle(handle);
    return c;
  }

  @Override
  protected StatementImpl cleanCopy() {
    return new StatementImpl(this, getParent());
  }

  @Override
  protected StatementImpl cleanCopy(Location parent) {
    return new StatementImpl(this, parent);
  }

  @Override
  protected StatementImpl cleanChild(Region child) {
    return new StatementImpl((StatementImpl)child, this);
  }

  @Override
  public StatementImpl getParent() {
    return (StatementImpl) super.getParent();
  }

  private Connection getConnection() {
    if (connectionProvider != null) {
      return connectionProvider.getConnection();
    }
    if (getParent() != null) {
      return getParent().getConnection();
    }
    throw new SnippetoryException("No connection found. Please put ConnectionProvider to SqlContext.");
  }

  @Override
  public long executeUpdate() {
    try (Connection connection = getConnection();
        PreparedStatement stmt = getStatement(connection)) {
        return stmt.executeUpdate();
      } catch (SQLException e) {
      throw new SnippetoryException(e);
    }
  }

  public void setConnectionProvider(ConnectionProvider connectionProvider) {
    this.connectionProvider = connectionProvider;
  }

  @Override
  public int bindTo(PreparedStatement stmt, int offset) throws SQLException {
    return ((StatementBinder)data).bindTo(stmt, offset);
  }

  private class DirectCursor<T> implements Cursor<T>, Iterator<T> {
    private final RowTransformer<T> transformer;
    private final SqlResources sql;
    private Boolean moveResult;
    private Ref handle;

    public DirectCursor(RowTransformer<T> transformer) {
      sql = new SqlResources();
      this.transformer = transformer;
    }

    public void setHandle(Ref handle) {
      this.handle = handle;
    }

    @Override
    public void close() {
      if (handle != null) {
        handle.close();
        handle = null;
      }
      sql.close();
    }

    @Override
    public Iterator<T> iterator() {
      return this ;
    }

    @Override
    public boolean hasNext() {
      try {
        if (moveResult == null) {
          moveResult = sql.rs.next();
        }
        return moveResult;
      } catch (SQLException e) {
        throw new SnippetoryException(e);
      }
    }

    @Override
    public T next() {
      if (hasNext()) {
        try {
          moveResult = null;
          return transformer.transformRow(sql.rs);
        } catch (SQLException e) {
          throw new SnippetoryException(e);
        }
      }
      throw new NoSuchElementException();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  private class SqlResources implements AutoCloseable, Runnable {
    private final Connection con;
    private final PreparedStatement ps;
    final ResultSet rs;

    public SqlResources() {
      try {
        con = getConnection();
        ps = getStatement(con);
        rs = ps.executeQuery();
      } catch (SQLException e) {
        close();
        throw new SnippetoryException(e);
      }
    }

    @Override
    public void close() {
      Exception e = close(con, close(ps, close(rs, null)));
      if (e != null) {
        throw new SnippetoryException(e);
      }
    }

    private Exception close(AutoCloseable c, Exception e) {
      try {
        if (c != null) c.close();
      } catch (Exception e1) {
        if (e == null) return e1;
        e.addSuppressed(e1);
        return e;
      }
      return e;
    }

    @Override
    public void run() {
      close();
    }
  }
}
