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

package org.jproggy.snippetory;

import static junit.framework.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class UriResolverTest {

  @Test
  public void fileTestString() {
    TemplateContext ctx = new TemplateContext();
    UriResolver resolver = UriResolver.directories("src/test/resources");
    assertEquals("mini", resolver.resolve("mini.txt", ctx));
  }

  @Test
  public void fileTestStrings() {
    TemplateContext ctx = new TemplateContext();
    UriResolver resolver = UriResolver.directories("bla", "src/test/resources");
    assertEquals("mini", resolver.resolve("mini.txt", ctx));
  }

  @Test
  public void fileTestFile() {
    TemplateContext ctx = new TemplateContext();
    UriResolver resolver = UriResolver.directories(new File("src/test/resources"));
    assertEquals("mini", resolver.resolve("mini.txt", ctx));
  }

  @Test
  public void fileTestFiles() {
    TemplateContext ctx = new TemplateContext();
    UriResolver resolver = UriResolver.directories(new File("src/test/resources/org/jproggy"), new File("src/test/resources"));
    assertEquals("org.jproggy.mini", resolver.resolve("mini.txt", ctx));
  }

  @Test
  public void ressourceTestPackage() {
    TemplateContext ctx = new TemplateContext();
    UriResolver resolver = UriResolver.resource("org/jproggy");
    assertEquals("org.jproggy.mini", resolver.resolve("mini.txt", ctx));
  }

  @Test
  public void ressourceTest() {
    TemplateContext ctx = new TemplateContext();
    UriResolver resolver = UriResolver.resource();
    assertEquals("mini", resolver.resolve("mini.txt", ctx));
  }
}
