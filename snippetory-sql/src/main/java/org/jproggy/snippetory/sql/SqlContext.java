package org.jproggy.snippetory.sql;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.UriResolver;
import org.jproggy.snippetory.sql.impl.StatementBuilder;
import org.jproggy.snippetory.sql.impl.StatementImpl;
import org.jproggy.snippetory.sql.spi.ConnectionProvider;

public class SqlContext {
  private ConnectionProvider connections;
  private TemplateContext ctx;

  public SqlContext() {
    super();
    ctx = new TemplateContext().syntax(SQL.SYNTAX).encoding(SQL.ENCODING);
  }

  public SqlContext uriResolver(UriResolver uriResolver) {
    ctx.uriResolver(uriResolver);
    return this;
  }

  public void setUriResolver(UriResolver uriResolver) {
    ctx.setUriResolver(uriResolver);
  }

  public UriResolver getUriResolver() {
    return ctx.getUriResolver();
  }

  public SqlContext conntecions(ConnectionProvider conntions) {
    this.connections = conntions;
    return this;
  }

  public ConnectionProvider getConnections() {
    return connections;
  }

  public void setConnections(ConnectionProvider connections) {
    this.connections = connections;
  }

  public Repository getRepository(String uri) {
    if (ctx.getUriResolver() == null) {
      throw new IllegalStateException("Need UrlResolver to find repository description. Please set one");
    }
    StatementImpl stmt = StatementBuilder.parse(ctx, ctx.getUriResolver().resolve(uri, ctx));
    stmt.setConnectionProvider(connections);
    return new Repository(stmt);
  }
}
