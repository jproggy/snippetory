package org.jproggy.snippetory.toolyng.test;

import static java.util.Collections.emptySet;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jproggy.snippetory.Template;

public class TestRecorder implements Template {
  Template repo;
  Template region;
  Map<String, Template> appends = new HashMap<>();

  public TestRecorder(Template repo, Template region) {
    super();
    this.repo = repo;
    this.region = region;
  }

  @Override
  public String getEncoding() {
    return repo.getEncoding();
  }

  @Override
  public CharSequence toCharSequence() {
    return repo.toCharSequence();
  }

  @Override
  public Template set(String name, Object val) {
    repo.get("set").set("name", name).set("data", "value").render();
    return this;
  }

  @Override
  public Template append(String name, Object val) {
    appends.computeIfAbsent(name, (k) -> repo.get("append"))
            .get("value")
            .set("name", name)
            .set("data", "value")
            .render();
    return this;
  }

  @Override
  public Template clear() {
    return this;
  }

  @Override
  public Template get(String... name) {
    if (name.length == 0) return this;
    Template child = new TestRecorder(repo, region.get(name[0]));
    if (name.length > 1) {
      String[] tempNames = new String[name.length - 1];
      System.arraycopy(name, 1, tempNames, 0, tempNames.length);
      return child.get(tempNames);
    }
    return child;
  }

  @Override
  public Set<String> names() {
    return emptySet();
  }

  @Override
  public Set<String> regionNames() {
    return emptySet();
  }

  @Override
  public Template getParent() {
    return null;
  }

  @Override
  public void render(Writer out) throws IOException {
  }

  @Override
  public boolean isPresent() {
    return false;
  }

  @Override
  public void render(PrintStream out) throws IOException {
  }

  @Override
  public void render(Template target, String name) {
  }

}
