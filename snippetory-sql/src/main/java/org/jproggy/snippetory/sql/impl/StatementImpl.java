package org.jproggy.snippetory.sql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.jproggy.snippetory.engine.Location;
import org.jproggy.snippetory.engine.Region;
import org.jproggy.snippetory.engine.SnippetoryException;
import org.jproggy.snippetory.sql.RowProcessor;
import org.jproggy.snippetory.sql.Statement;
import org.jproggy.snippetory.sql.spi.ConnectionProvider;

public class StatementImpl extends Region implements Statement, StatementBinder {
  private ConnectionProvider connectionProvider;

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
  public void processRows(RowProcessor proc) {
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
}
