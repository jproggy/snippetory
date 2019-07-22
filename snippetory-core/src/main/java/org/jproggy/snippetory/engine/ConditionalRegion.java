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

package org.jproggy.snippetory.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jproggy.snippetory.spi.EncodedData;

public class ConditionalRegion extends DataSinks implements EncodedData {
  private final Set<String> names;
  private final Map<String, Region> children;
  private boolean appendMe;

  public ConditionalRegion(Location formatter, List<DataSink> parts, Map<String, Region> children) {
    super(parts, formatter);
    names = names();
    this.children = children;
  }

  protected ConditionalRegion(ConditionalRegion template, Location parent) {
    super(template, template.getPlaceholder().cleanCopy(parent));
    names = names();
    this.children = new HashMap<String, Region>();
    for (Map.Entry<String, Region> entry : template.children.entrySet()) {
      this.children.put(entry.getKey(), entry.getValue().cleanCopy(super.getPlaceholder()));
    }
    appendMe = false;
  }

  @Override
  public void set(String name, Object value) {
    if (names.contains(name)) {
      super.set(name, value);
      if (value != null) appendMe = true;
    }
  }

  @Override
  public void append(String name, Object value) {
    if (names.contains(name)) {
      super.append(name, value);
      if (value != null) appendMe = true;
    }
  }

  @Override
  public Set<String> regionNames() {
    return children.keySet();
  }

  @Override
  public Region getChild(String name) {
    Region child = children.get(name);
    if (child != null) child = child.cleanCopy(getPlaceholder());
    return child;
  }

  @Override
  public void clear() {
    super.clear();
    getPlaceholder().clear();
    appendMe = false;
  }

  @Override
  public ConditionalRegion cleanCopy(Location parent) {
    return new ConditionalRegion(this, parent);
  }

  @Override
  public CharSequence format() {
    Location placeholder = getPlaceholder();
    if (appendMe()) {
      placeholder.set(this);
    }
    return placeholder.format();
  }

  protected boolean appendMe() {
    return appendMe;
  }

  @Override
  public CharSequence toCharSequence() {
    return this;
  }

  @Override
  public String getEncoding() {
    return getPlaceholder().getEncoding();
  }

}
