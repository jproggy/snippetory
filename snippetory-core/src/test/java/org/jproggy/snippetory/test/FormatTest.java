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

import static org.jproggy.snippetory.Syntaxes.XML_ALIKE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jproggy.snippetory.Template;
import org.junit.Test;

public class FormatTest {
  @Test
  public void formatPad() {
    Template pad = XML_ALIKE.parse("{v:test pad='5' pad.align='right'}");
    pad.set("test", "x");
    assertEquals("    x", pad.toString());
    pad.set("test", "123456");
    assertEquals("123456", pad.toString());
  }

  @Test
  public void formatCrop() {
    Template crop = XML_ALIKE.parse("{v:test crop='5' crop.mark='...'}");
    crop.set("test", "x");
    assertEquals("x", crop.toString());
    crop.set("test", "123456");
    assertEquals("12...", crop.toString());
    crop.set("test", "12345");
    assertEquals("12345", crop.toString());
  }

  @Test
  public void formatDefaultLocaion() {
    Template t = XML_ALIKE.parse("{v:test default='not set'}");
    assertEquals("not set", t.toString());
    t.set("test", "test");
    assertEquals("test", t.toString());
  }

  @Test
  public void formatDefaultRegion() {
    Template t = XML_ALIKE.parse("<t:test default='not set'></t:test>");
    assertEquals("not set", t.toString());
    t.set("test", "test");
    assertEquals("test", t.toString());
  }

  @Test
  public void formatDefaultCondRegion() {
    Template t = XML_ALIKE.parse("<t: default='not set'>{v:test}</t:>");
    assertEquals("not set", t.toString());
    t.set("test", "test");
    assertEquals("test", t.toString());
  }

  @Test
  public void formatCase() {
    Template t = XML_ALIKE.parse("{v:test case='upper'}");
    t.set("test", "test");
    assertEquals("TEST", t.toString());
    t.set("test", "TEST");
    assertEquals("TEST", t.toString());
    t = XML_ALIKE.parse("{v:test case='lower'}");
    t.set("test", "test");
    assertEquals("test", t.toString());
    t.set("test", "TEST");
    assertEquals("test", t.toString());
    t = XML_ALIKE.parse("{v:test case='firstUpper'}");
    t.set("test", "test");
    assertEquals("Test", t.toString());
    t.set("test", "TEST");
    assertEquals("TEST", t.toString());
    t = XML_ALIKE.parse("{v:test case='camelizeLower'}");
    t.set("test", "test");
    assertEquals("test", t.toString());
    t.set("test", "test-test");
    assertEquals("testTest", t.toString());
    t.set("test", "TEST_TEST");
    assertEquals("testTest", t.toString());
    t.set("test", "TEST__TEST");
    assertEquals("testTest", t.toString());
    t.set("test", "test-x-test");
    assertEquals("testXTest", t.toString());
    t = XML_ALIKE.parse("{v:test case='camelizeUpper'}");
    t.set("test", "test");
    assertEquals("Test", t.toString());
    t.set("test", "test-test");
    assertEquals("TestTest", t.toString());
    t.set("test", "TEST_TEST");
    assertEquals("TestTest", t.toString());
  }

  @Test
  public void toggle() {
    Template t = XML_ALIKE.parse("<t:test>{v:toggle='1;2;3'}. {v: toggle='unpair;pair'}\n</t:test>");
    bindPLain(t);
    assertEquals("1. unpair\n2. pair\n3. unpair\n", t.toString());
    t.clear();
    bindPLain(t);
    assertEquals("1. unpair\n2. pair\n3. unpair\n", t.toString());

    t = XML_ALIKE.parse("<t:test>{v:x toggle='1;2;3'}. {v:x toggle='unpair;pair'}\n</t:test>");
    bindX(t);
    assertEquals("1. unpair\n2. pair\n3. unpair\n", t.toString());
    t.clear();
    bindX(t);
    assertEquals("1. unpair\n2. pair\n3. unpair\n", t.toString());
    t.clear();
    t.get("test").set("x", 1).render();
    t.get("test").set("x", 0).render();
    t.get("test").set("x", -1).render();
    assertEquals("1. unpair\n2. pair\n3. unpair\n", t.toString());

    t = XML_ALIKE.parse("<t:test><t: toggle='1. ;2. ;3. ;4. '>von {v:von} nach {v:nach}\n</t:></t:test>");
    bindTrack(t);
    assertEquals("1. von Hergersweiler nach Winden\n2. von 6 nach {v:nach}\n", t.toString());
    t.clear();
    bindTrack(t);
    assertEquals("1. von Hergersweiler nach Winden\n2. von 6 nach {v:nach}\n", t.toString());
    t = t.get();
    bindTrack(t);
    assertEquals("1. von Hergersweiler nach Winden\n2. von 6 nach {v:nach}\n", t.toString());
  }

  private void bindX(Template t) {
    t.get("test").set("x", 1).render();
    t.get("test").set("x", 2).render();
    t.get("test").set("x", 3).render();
  }

  private void bindPLain(Template t) {
    t.get("test").render();
    t.get("test").render();
    t.get("test").render();
  }

  private void bindTrack(Template t) {
    t.get("test").render();
    t.get("test").set("von", "Hergersweiler").set("nach", "Winden").render();
    t.get("test").render();
    t.get("test").set("von", 6).render();
  }

  @Test
  public void unkown() {
    try {
      XML_ALIKE.parse("{v: bal='blupp'}");
      fail();
    } catch (RuntimeException e) {
      assertEquals("Can't understand attribute bal='blupp'", e.getCause().getMessage());
    }
    try {
      XML_ALIKE.parse("{v: crop='20' crop.blub='xx'}");
      fail();
    } catch (RuntimeException e) {
      assertEquals("Can't understand attribute crop.blub='xx'", e.getCause().getMessage());
    }
    try {
      XML_ALIKE.parse("{v: crop='20' crap.mark='xx'}");
      fail();
    } catch (RuntimeException e) {
      assertEquals("Missing parent crap for sub-attribute mark='xx'", e.getCause().getMessage());
    }
    try {
      XML_ALIKE.parse("{v: crop.mark='xx'}");
      fail();
    } catch (RuntimeException e) {
      assertEquals("Missing parent crop for sub-attribute mark='xx'", e.getCause().getMessage());
    }
  }
}
