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

package org.jproggy.snippetory.engine.spi;

import java.util.Collections;
import java.util.Set;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.spi.FormatFactory;
import org.jproggy.snippetory.spi.SimpleFormat;
import org.jproggy.snippetory.spi.TemplateNode;
import org.jproggy.snippetory.spi.VoidFormat;

public class DefaultFormatter implements FormatFactory {

  @Override
  public DefaultFormat create(String definition, TemplateContext ctx) {
    return new DefaultFormat(definition);
  }

  public static class DefaultFormat extends SimpleFormat implements VoidFormat {
    private final String value;

    private DefaultFormat(String value) {
      this.value = value;
    }

    @Override
    public Object format(TemplateNode location, Object value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean supports(Object value) {
      return false;
    }

    @Override
    public Object formatVoid(TemplateNode node) {
      return value;
    }

    @Override
    public void set(String name, Object value) {}

    @Override
    public void append(String name, Object value) {}

    @Override
    public Set<String> names() {
      return Collections.emptySet();
    }
  }
}
