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

package org.jproggy.snippetory.cypher.spi;

import org.jproggy.snippetory.cypher.Statement;
import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.util.TemplateWrapper;

import org.neo4j.driver.Query;

public abstract class StatementWrapper extends TemplateWrapper implements Statement {

  public StatementWrapper(Statement template) {
    super(template);
  }

  protected Statement wrapped() {
    return (Statement)wrapped;
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
  public boolean isPresent() {
    return wrapped().isPresent();
  }

  @Override
  public Query toQuery() {
    return wrapped().toQuery();
  }

  @Override
  protected abstract Statement wrap(Template toBeWrapped);
}
