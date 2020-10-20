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

package org.jproggy.snippetory.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.UriResolver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LargeTemplateTest {
  private static Template template;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    template = new TemplateContext().uriResolver(UriResolver.resource()).getTemplate("large.tpl");
  }

  @Test
  void fluyt() {
    Template fluyt = template.get("FLUYT");
    assertNotNull(fluyt);
    renderAll(fluyt);
    assertEquals(-1, fluyt.toString().indexOf('$'));
  }

  @Test
  void xmlAlike() {
    Template section = template.get("XML_ALIKE");
    assertNotNull(section);
    renderAll(section);
    assertEquals(-1, section.toString().indexOf(":t"));
    assertEquals(-1, section.toString().indexOf("{:v"));
    assertEquals(17, section.toString().indexOf(","));
  }

  private void renderAll(Template t) {
    Set<String> regions = t.regionNames();
    for (String name : regions) {
      Template child = t.get(name);
      renderAll(child);
      child.render();
    }
    for (String name : t.names()) {
      if (!regions.contains(name)) t.set(name, name + ' ');
    }
  }
}
