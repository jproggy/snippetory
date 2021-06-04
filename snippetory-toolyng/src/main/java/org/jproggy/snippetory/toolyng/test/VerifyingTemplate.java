package org.jproggy.snippetory.toolyng.test;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jproggy.snippetory.Template;
import org.junit.Assert;

public class VerifyingTemplate implements Template {
  private final String encoding;
  private Map<String, Object> data;
  private Map<String, Iterator<Object>> iterators = new HashMap<>();

  public VerifyingTemplate(String encoding, Map<String, Object> data) {
    super();
    this.encoding = encoding;
    this.data = data;
  }

  @Override
  public Template set(String name, Object value) {
    Object val = data.get(name);
    if (val instanceof Iterable) {
      val = iterator(name).next();
    }
    Assert.assertEquals(val, value);
    return this;
  }

  @Override
  public Template append(String name, Object value) {
    Assert.assertEquals(data.get(name), value);
    return this;
  }

  @SuppressWarnings("unchecked")
  protected Iterator<Object> iterator(String name) {
    return iterators.computeIfAbsent(name, k -> ((Iterable<Object>) data.get(name)).iterator());
  }

  @Override
  @SuppressWarnings("unchecked")
  public Template get(String... name) {
    if (name.length == 0) return this;
    Map<String, Object> childData = (Map<String, Object>) data.get(name[0]);
    Template child = new VerifyingTemplate(encoding, childData);
    if (name.length > 1) {
      String[] tempNames = new String[name.length - 1];
      System.arraycopy(name, 1, tempNames, 0, tempNames.length);
      return child.get(tempNames);
    }
    return child;
  }

  @Override
  public Set<String> names() {
    return data.keySet();
  }

  @Override
  public Set<String> regionNames() {
    Set<String> result = new HashSet<>();
    for (Map.Entry<String, Object> e : data.entrySet()) {
      if (e.getValue() instanceof Map) result.add(e.getKey());
    }
    return result;
  }

  @Override
  public Template getParent() {
    return null;
  }

  @Override
  public boolean isPresent() {
    return true;
  }

  @Override
  public void render(Writer out) throws IOException {
    // nothing to do
  }

  @Override
  public void render(PrintStream out) throws IOException {
    // nothing to do
  }

  @Override
  public void render(Template target, String name) {
    // nothing to do
  }

  @Override
  public String getEncoding() {
    return encoding;
  }

  @Override
  public CharSequence toCharSequence() {
    throw new UnsupportedOperationException("No template, no character representation avaialble");
  }

  @Override
  public Template clear() {
    return this; // immutable
  }
}
