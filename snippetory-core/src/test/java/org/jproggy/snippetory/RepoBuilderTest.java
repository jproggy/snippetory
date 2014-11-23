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
import static junit.framework.Assert.fail;

import java.net.URL;

import org.jproggy.snippetory.engine.SnippetoryException;
import org.junit.Test;

public class RepoBuilderTest {

  public void fileResourceTest() {
    TemplateContext ctx = new TemplateContext();
    UriResolver resolver = UriResolver.combine().addDirectories("src/test/resources").addResource("org/jproggy");
    assertEquals("mini", resolver.resolve("mini.txt", ctx));
    assertEquals("org.jproggy.mini2", resolver.resolve("mini2.txt", ctx));
  }

  @Test(expected=SnippetoryException.class)
  public void fileResourceTestNotFound() {
    TemplateContext ctx = new TemplateContext();
    UriResolver resolver = UriResolver.combine().addDirectories("src/test/resources").addResource("org/jproggy");
    resolver.resolve("mini3.txt", ctx);
    fail();
  }

  @Test
  public void resourceFileTest() {
    TemplateContext ctx = new TemplateContext();
    UriResolver resolver = UriResolver.combine().addResource("org/jproggy").addDirectories("src/test/resources");
    assertEquals("org.jproggy.mini", resolver.resolve("mini.txt", ctx));
  }

  @Test
  public void urlTest() {
    TemplateContext ctx = new TemplateContext();
    URL url = this.getClass().getResource("/mini.txt");
    UriResolver resolver = UriResolver.combine().addUrl(url);
    assertEquals("mini", resolver.resolve("mini.txt", ctx));
  }
}
