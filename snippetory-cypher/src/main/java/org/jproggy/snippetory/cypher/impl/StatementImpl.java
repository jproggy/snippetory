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

package org.jproggy.snippetory.cypher.impl;

import org.jproggy.snippetory.cypher.Statement;
import org.jproggy.snippetory.engine.Location;
import org.jproggy.snippetory.engine.Region;
import org.neo4j.driver.Query;

import java.util.HashMap;
import java.util.Map;

public class StatementImpl extends Region implements Statement, StatementBinder {

  public StatementImpl(CypherSinks data, Map<String, Region> children) {
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
    return (StatementImpl)super.getParent();
  }

  @Override
  public void bindTo(Map<String, Object> params) {
    ((StatementBinder)data).bindTo(params);
  }

  @Override
  public Query toQuery() {
    Map<String, Object> params = new HashMap<>();
    bindTo(params);
    return new Query(toString(), params);
  }
}
