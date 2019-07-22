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
  private PostProcessor proc = r -> r;

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
      throw new IllegalStateException("Need UriResolver to find repository description. Please set one");
    }
    StatementImpl stmt = StatementBuilder.parse(ctx, ctx.getUriResolver().resolve(uri, ctx));
    stmt.setConnectionProvider(connections);
    return new Repository(proc.processRepository(stmt));
  }
}
