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

package org.jproggy.snippetory.util;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Set;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.spi.Metadata;

/**
 * A TemplateWrapper is a utility class for conveniently implementing decorators
 * for templates.
 * It makes sure, that all templates returned from its methods are wrapped, too.
 * The only exception is {@link #getImplementation()}.
 */
public abstract class TemplateWrapper implements Template {
  protected final Template wrapped;

  public TemplateWrapper(Template template) {
    this.wrapped = template;
  }

  @Override
  public String getEncoding() {
    return wrapped.getEncoding();
  }

  @Override
  public CharSequence toCharSequence() {
    return wrapped.toCharSequence();
  }

  @Override
  public Template get(String... name) {
    if (name.length == 0) return wrap(wrapped.get());
    if (name.length == 1) return wrap(wrapped.get(name));
    Template t = this;
    for (String part : name) {
      t = t.get(part);
      if (t == null) return Template.NONE;
    }
    return t;
  }

  protected abstract Template wrap(Template toBeWrapped);

  @Override
  public Template set(String name, Object value) {
    wrapped.set(name, value);
    return this;
  }

  @Override
  public Template append(String name, Object value) {
    wrapped.append(name, value);
    return this;
  }

  @Override
  public Template clear() {
    wrapped.clear();
    return this;
  }

  @Override
  public void render() {
    wrapped.render();
  }

  @Override
  public void render(String siblingName) {
    wrapped.render(siblingName);
  }

  @Override
  public void render(Template target, String name) {
    wrapped.render(target, name);
  }

  @Override
  public void render(Writer out) throws IOException {
    wrapped.render(out);
  }

  @Override
  public void render(PrintStream out) throws IOException {
    wrapped.render(out);
  }

  @Override
  public Set<String> names() {
    return wrapped.names();
  }

  @Override
  public Set<String> regionNames() {
    return wrapped.regionNames();
  }

  @Override
  public Template getParent() {
    return wrap(wrapped.getParent());
  }

  @Override
  public boolean isPresent() {
    return wrapped.isPresent();
  }

  @Override
  public Metadata metadata() {
    return wrapped.metadata();
  }

  @Override
  public String toString() {
    return wrapped.toString();
  }

  /**
   * Unwraps all layers of TemplateWrapper around the template.
   * Be aware, that there might be wrappers, that aren't derived
   * from TemplateWrapper. Those won't be unwrapped by this implementation.
   *
   * @return the unwrapped template
   */
  public Template getImplementation() {
    if (wrapped instanceof TemplateWrapper) {
      return ((TemplateWrapper) wrapped).getImplementation();
    }
    return wrapped;
  }
}
