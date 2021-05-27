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

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.Attributes.Types;
import org.jproggy.snippetory.spi.Link;
import org.jproggy.snippetory.spi.LinkFactory;

public final class LinkRegistry {
  private Map<String, LinkFactory> links = new HashMap<>();

  private LinkRegistry() {
  }

  public void register(String name, LinkFactory value) {
    Attributes.register(name, Types.LINK);
    links.put(name, value);
  }

  public Link get(String name, String definition, TemplateContext ctx) {
    LinkFactory f = links.get(name);
    if (f == null) {
      return null;
    }
    Link created = f.create(definition, ctx);
    if (created == null) {
      throw new SnippetoryException("Link " + name + " doesn't support " + definition);
    }
    return created;
  }

  public static final LinkRegistry INSTANCE = new LinkRegistry();
}
