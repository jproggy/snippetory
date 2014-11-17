package org.jproggy.snippetory.sql;

import java.util.Objects;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.UriResolver;
import org.jproggy.snippetory.sql.impl.StatementBuilder;
import org.jproggy.snippetory.sql.impl.StatementImpl;
import org.jproggy.snippetory.sql.spi.ConnectionProvider;
import org.jproggy.snippetory.sql.spi.PostProcessor;

public class SqlContext {
  private ConnectionProvider connections;
  private TemplateContext ctx;
  private PostProcessor proc = new PostProcessor() {
    @Override
    public Statement processRepository(Statement repo) {
      return repo;
    }
  };

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

  public SqlContext postProcessor(PostProcessor proc) {
    setPostProcessor(proc);
    return this;
  }

  public void setPostProcessor(PostProcessor proc) {
    Objects.requireNonNull(proc);
    this.proc = proc;
  }

  public PostProcessor getPostProcessor() {
    return proc;
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
    return new Repository(proc.processRepository(stmt));
  }
}
