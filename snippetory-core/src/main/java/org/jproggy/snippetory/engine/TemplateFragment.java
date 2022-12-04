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

import java.util.Collections;
import java.util.Set;

import org.jproggy.snippetory.engine.chars.CharSequences;

public class TemplateFragment extends CharSequences implements DataSink {
  private final CharSequence data;

  public TemplateFragment(CharSequence data) {
    this.data = data;
  }

  @Override
  public void set(String name, Object value) {
  }

  @Override
  public void append(String name, Object value) {}

  @Override
  public Set<String> names() {
    return Collections.emptySet();
  }

  @Override
  public Set<String> regionNames() {
    return Collections.emptySet();
  }

  @Override
  public Reference getChild(String name) {
    return null;
  }

  @Override
  public String toString() {
    return data.toString();
  }

  @Override
  protected CharSequence part(int index) {
    return data;
  }

  @Override
  protected int partCount() {
    return 1;
  }

  @Override
  public <T extends Appendable> T appendTo(T to) {
    return CharSequences.append(to, data);
  }

  public TemplateFragment start(int start) {
    return new TemplateFragment(this.subSequence(0, start));
  }

  public TemplateFragment end(int start) {
    return new TemplateFragment(this.subSequence(start, data.length()));
  }

  @Override
  public TemplateFragment cleanCopy(Location parent) {
    // cloning is not necessary. One instance is enough
    return this;
  }

  @Override
  public void clear() {
    // is immutable --> nothing to clear
  }

  @Override
  public CharSequence format() {
    return data;
  }
}
