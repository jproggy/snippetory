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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jproggy.snippetory.engine.Location;
import org.jproggy.snippetory.engine.Region;
import org.jproggy.snippetory.engine.SnippetoryException;
import org.jproggy.snippetory.sql.Cursor;
import org.jproggy.snippetory.sql.OpenCursor;
import org.jproggy.snippetory.sql.Statement;
import org.jproggy.snippetory.sql.spi.ConnectionProvider;
import org.jproggy.snippetory.sql.spi.RowProcessor;
import org.jproggy.snippetory.sql.spi.RowTransformer;
import org.jproggy.snippetory.util.concurrent.ArrayBlockingQueue;
import org.jproggy.snippetory.util.concurrent.BlockingQueue;
import org.jproggy.snippetory.util.concurrent.QueueCloseException;
import org.jproggy.snippetory.util.concurrent.Sink;
import org.jproggy.snippetory.util.concurrent.Source;

public class StatementImpl extends Region implements Statement, StatementBinder {
  private ConnectionProvider connectionProvider;
  private static final ExecutorService runner = Executors.newCachedThreadPool();

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
        ResultSet rs = stmt.executeQuery();) {
      while (rs.next()) {
        proc.processRow(rs);
      }
    } catch (SQLException e) {
      throw new SnippetoryException(e);
    }

  }

  @Override
  public <T> List<T> list(RowTransformer<T> transformer) {
    try (Cursor<T> rows = directCursor(transformer)) {
      List<T> result =  new ArrayList<>();
      for (T row: rows) {
        result.add(row);
      }
      return result;
    }
  }

  @Override
  public <K, V> Map<K, V> map(final RowTransformer<K> key, final RowTransformer<V> value) {
    final Map<K,V> result =  new LinkedHashMap<>();

    forEach(new RowProcessor() {
      @Override
      public void processRow(ResultSet rs) throws SQLException {
        result.put(key.transformRow(rs), value.transformRow(rs));
      }
    });
    return result;
  }

  @Override
  public <T> T one(RowTransformer<T> transformer) {
    List<T> list = list(transformer);
    if (list.size() == 1) return list.get(0);
    throw new ResultCountException(list.size());
  }

  @Override
  public <T> OpenCursor<T> directCursor(RowTransformer<T> transformer) {
     return new DirectCursor<>(transformer);
  }

  @Override
  public <T> Cursor<T> readAheadCursor(RowTransformer<T> transformer) {
    Task<T> task = new Task<>(transformer, new ArrayBlockingQueue<T>(200));
    runner.execute(task);
    return new ReadAheadCursor<>(task);
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
  protected StatementImpl getParent() {
    return (StatementImpl)super.getParent();
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
        PreparedStatement stmt = getStatement(connection);) {
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

  private class DirectCursor<T> implements OpenCursor<T>, OpenCursor.OpenIterator<T> {
    private final Connection con;
    private final PreparedStatement ps;
    private final ResultSet rs;
    private final RowTransformer<T> transformer;
    private Boolean moveResult;

    public DirectCursor(RowTransformer<T> transformer) {
      try {
        con = getConnection();
        ps = getStatement(con);
        rs = ps.executeQuery();
        this.transformer = transformer;
      } catch (SQLException e) {
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
        c.close();
      } catch (Exception e1) {
        if (e == null) return e1;
        e.addSuppressed(e1);
        return e;
      }
      return null;
    }

    @Override
    public OpenIterator<T> iterator() {
      return this ;
    }

    @Override
    public boolean hasNext() {
      try {
        if (moveResult == null) moveResult = rs.next();
        return moveResult;
      } catch (SQLException e) {
        throw new SnippetoryException(e);
      }
    }

    @Override
    public T next() {
      try {
        moveResult = null;
        return transformer.transformRow(rs);
      } catch (SQLException e) {
        throw new SnippetoryException(e);
      }
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void processRow(RowProcessor proc) {
      try {
        proc.processRow(rs);
      } catch (SQLException e) {
        throw new SnippetoryException(e);
      }
    }

    @Override
    public void updateRow() {
      try {
        rs.updateRow();
      } catch (SQLException e) {
        throw new SnippetoryException(e);
      }
    }
  }
  private static class ReadAheadCursor<T> implements Cursor<T> {
    private final Task<T> task;
    private final Source<T> source;

    public ReadAheadCursor(Task<T> t) {
      this.task = t;
      this.source = task.queue.source();
    }

    @Override
    public void close() {
      task.queue.close();
      if (task.throwable != null) {
        throw new SnippetoryException(task.throwable);
      }
    }

    @Override
    public Iterator<T> iterator() {
      return source.iterator();
    }
  }

  private class Task<T> implements Runnable {
    private final RowTransformer<T> transformer;
    final BlockingQueue<T> queue;
    volatile Throwable throwable;

    public Task(RowTransformer<T> transformer, BlockingQueue<T> queue) {
      this.queue = queue;
      this.transformer = transformer;
    }

    @Override
    public void run() {
      try (Sink<T> s = queue.sink();
          Connection con = getConnection();
          PreparedStatement ps = getStatement(con);
          ResultSet rs = ps.executeQuery(); ) {
        while (rs.next()) {
          s.put(transformer.transformRow(rs));
        }
      } catch (QueueCloseException e) {
        // ignored on the assumption that the client closed the queue
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (Throwable e) {
        throwable = e;
      }
    }
  }
}
