/*******************************************************************************
 * Copyright (c) 2011-2012 JProggy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * EXCEPT AS EXPRESSLY SET FORTH IN THIS AGREEMENT, THE PROGRAM IS PROVIDED ON AN
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS OF TITLE,
 * NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE
 *******************************************************************************/
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
