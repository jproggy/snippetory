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

import org.jproggy.snippetory.engine.DataSink;
import org.jproggy.snippetory.engine.DataSinks;
import org.jproggy.snippetory.engine.Location;

import java.util.List;
import java.util.Map;

public class CypherSinks extends DataSinks implements StatementBinder {

  public CypherSinks(List<DataSink> parts, Location placeHolder) {
    super(parts, placeHolder);
  }

  protected CypherSinks(DataSinks template, Location parent) {
    super(template, parent);
  }

  @Override
  public DataSinks cleanCopy(Location parent) {
    return new CypherSinks(this, parent);
  }

  @Override
  public void bindTo(Map<String, Object> params) {
    for (DataSink v : parts) {
      ((StatementBinder)v).bindTo(params);
    }
  }
}
