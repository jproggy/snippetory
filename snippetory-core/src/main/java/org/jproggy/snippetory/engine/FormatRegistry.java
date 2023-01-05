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
import java.util.Map;

import org.jproggy.snippetory.SnippetoryException;
import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.Attributes.Types;
import org.jproggy.snippetory.spi.FormatConfiguration;
import org.jproggy.snippetory.spi.FormatFactory;

public final class FormatRegistry {
  private final Map<String, FormatFactory> formats = new HashMap<>();

  private FormatRegistry() {}

  public void register(String name, FormatFactory value) {
    Attributes.register(name, Types.FORMAT);
    formats.put(name, value);
  }

  public FormatConfiguration get(String name, String definition, TemplateContext ctx) {
    FormatFactory f = formats.get(name);
    if (f == null) {
      throw new SnippetoryException(name + " isn't a format.");
    }
    FormatConfiguration created = f.create(definition, ctx);
    if (created == null) {
      throw new SnippetoryException("Format " + name + " doesn't support " + definition);
    }
    return created;
  }

  public static final FormatRegistry INSTANCE = new FormatRegistry();
}
