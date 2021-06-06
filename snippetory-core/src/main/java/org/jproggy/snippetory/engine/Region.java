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

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.engine.chars.SelfAppender;
import org.jproggy.snippetory.spi.Link;
import org.jproggy.snippetory.spi.Metadata;

public class Region implements Template, CharSequence, SelfAppender {
  private final Map<String, Region> children;
  private Region parent;
  protected final DataSinks data;

  public Region(DataSinks data, Map<String, Region> children) {
    super();
    this.data = data;
    this.children = children;
    for (Region child : children.values()) {
      child.setParent(this);
    }
  }

  protected Region(Region template, Location parent) {
    super();
    setParent(null);
    this.children = template.children;
    this.data = template.data.cleanCopy(parent);
  }

  protected Region(Region template, Region parent) {
    super();
    setParent(parent);
    this.children = template.children;
    this.data = template.data.cleanCopy(getParentLocation());
  }

  private Location getParentLocation() {
    if (parent == null) return null;
    return parent.data.getPlaceholder();
  }

  @Override
  public Template get(String... path) {
    if (path.length == 0) return cleanCopy();
    Template t = getChild(path[0]);
    if (t == null) return Template.NONE;
    for (int i = 1; i < path.length; i++) {
      t = t.get(path[i]);
      if (t == null) return Template.NONE;
    }
    return t;
  }

  protected Region cleanCopy() {
    return new Region(this, parent);
  }

  protected Template getChild(String name) {
    if (children.containsKey(name)) {
      Region child = children.get(name);
      return cleanChild(child);
    }
    Link child = data.getChild(name);
    if (child == null) return null;
    return child.getContents(this, name);
  }

  protected Region cleanChild(Region child) {
    return new Region(child, this);
  }

  @Override
  public Region set(String key, Object value) {
    data.set(key, value);
    return this;
  }

  @Override
  public Region append(String key, Object value) {
    data.append(key, value);
    return this;
  }

  @Override
  public Region clear() {
    data.clear();
    for (Region r : children.values()) {
      r.clear();
    }
    return this;
  }

  @Override
  public CharSequence toCharSequence() {
    return this;
  }

  @Override
  public <T extends Appendable> T appendTo(T result) {
    return data.appendTo(result);
  }

  @Override
  public String toString() {
    return appendTo(new StringBuilder()).toString();
  }

  @Override
  public String getEncoding() {
    return data.getPlaceholder().md.enc.getName();
  }

  @Override
  public void render() {
    // ignore render calls on root node as they don't make any sense.
    if (isRoot()) return;
    render(metadata().getName());
  }

  private boolean isRoot() {
    return getParent() == null;
  }

  @Override
  public void render(Template target, String key) {
    target.append(key, this);
  }

  @Override
  public void render(Writer out) throws IOException {
    appendTo(out);
    out.flush();
  }

  @Override
  public void render(PrintStream out) {
    appendTo(out);
    out.flush();
  }

  @Override
  public Set<String> names() {
    return data.names();
  }

  @Override
  public Set<String> regionNames() {
    Set<String> result = data.regionNames();
    result.addAll(children.keySet());
    return result;
  }
  
  @Override
  public boolean isPresent() {
    return true;
  }

  @Override
  public Template getParent() {
    return parent;
  }

  final void setParent(Region parent) {
    this.parent = parent;
  }

  @Override
  public Metadata metadata() {
    return data.getPlaceholder().metadata();
  }

  @Override
  public int length() {
    return data.length();
  }

  @Override
  public char charAt(int index) {
    return data.charAt(index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return data.subSequence(start, end);
  }

  protected Region cleanCopy(Location parent) {
    return new Region(this, parent);
  }
}
