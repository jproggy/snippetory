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

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jproggy.snippetory.util.CharSequences;

public class DataSinks extends CharSequences implements DataSink {
  protected final DataSink[] parts;
  private final Location placeHolder;

  public DataSinks(List<DataSink> parts, Location placeHolder) {
    super();
    this.parts = parts.toArray(new DataSink[0]);
    this.placeHolder = placeHolder;
  }

  public DataSinks(DataSinks template, Location parent) {
    super();
    this.placeHolder = template.placeHolder.cleanCopy(parent);
    this.parts = new DataSink[template.parts.length];
    for (int i = 0; i < parts.length; i++) {
      this.parts[i] = template.parts[i].cleanCopy(this.placeHolder);
    }
  }

  @Override
  public void set(String name, Object value) {
    for (DataSink v : parts) {
      v.set(name, value);
    }
    placeHolder.set(name, value);
  }

  @Override
  public void append(String name, Object value) {
    for (DataSink v : parts) {
      v.append(name, value);
    }
    placeHolder.append(name, value);
  }

  @Override
  public Set<String> names() {
    Set<String> result = new TreeSet<>();
    for (DataSink part : parts) {
      result.addAll(part.names());
    }
    return result;
  }

  @Override
  public Set<String> regionNames() {
    Set<String> result = new TreeSet<>();
    for (DataSink part : parts) {
      result.addAll(part.regionNames());
    }
    return result;
  }

  @Override
  public void clear() {
    for (DataSink v : parts) {
      v.clear();
    }
  }

  @Override
  protected int partCount() {
    return parts.length;
  }

  @Override
  protected CharSequence part(int index) {
    return parts[index].format();
  }

  @Override
  public <T extends Appendable> T appendTo(T to) {
    for (DataSink part : parts) {
      CharSequences.append(to, part.format());
    }
    return to;
  }

  @Override
  public DataSinks cleanCopy(Location parent) {
    return new DataSinks(this, parent);
  }

  @Override
  public CharSequence format() {
    return this;
  }

  public final Location getPlaceholder() {
    return placeHolder;
  }

  @Override
  public Reference getChild(String name) {
    for (DataSink v : parts) {
      if (v.regionNames().contains(name)) return v.getChild(name);
    }
    return null;
  }
}
