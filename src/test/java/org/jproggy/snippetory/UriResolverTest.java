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
