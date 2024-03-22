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

package org.jproggy.snippetory.cypher;

import org.jproggy.snippetory.cypher.impl.CypherBuilder;
import org.jproggy.snippetory.cypher.impl.StatementImpl;
import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.UriResolver;

public class CypherContext {
  private TemplateContext ctx;

  public CypherContext() {
    super();
    ctx = new TemplateContext().syntax(Cypher.SYNTAX).encoding(Cypher.ENCODING);
  }

  public CypherContext uriResolver(UriResolver uriResolver) {
    ctx.uriResolver(uriResolver);
    return this;
  }

  public void setUriResolver(UriResolver uriResolver) {
    ctx.setUriResolver(uriResolver);
  }

  public UriResolver getUriResolver() {
    return ctx.getUriResolver();
  }

  public Repository getRepository(String uri) {
    if (ctx.getUriResolver() == null) {
      throw new IllegalStateException("Need UriResolver to find repository description. Please set one");
    }
    StatementImpl stmt = CypherBuilder.parse(ctx, ctx.getUriResolver().resolve(uri, ctx));
    return new Repository(stmt);
  }
}
