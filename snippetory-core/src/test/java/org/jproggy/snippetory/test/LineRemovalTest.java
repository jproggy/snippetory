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

import org.jproggy.snippetory.Template;
import org.junit.Test;

public class LineRemovalTest {
  @Test
  public void test1() {
    Template t1 = XML_ALIKE.parse(" \n <t:test>  \n  i++; \r\n  </t:test>  \n ");
    assertEquals("  i++; \r\n", t1.get("test").toString());
    t1.get("test").render();
    assertEquals(" \n  i++; \r\n ", t1.toString());
  }

  @Test
  public void test2() {
    Template t1 = XML_ALIKE.parse(" <t:test>  \n  i++; \r\n  </t:test> ");
    assertEquals("  i++; \r\n", t1.get("test").toString());
    t1.get("test").render();
    assertEquals("  i++; \r\n", t1.toString());
  }

  @Test
  public void test3() {
    Template t1 = XML_ALIKE.parse(" <t:test>  x\n  i++; \r\n  </t:test> ");
    assertEquals("  x\n  i++; \r\n", t1.get("test").toString());
    t1.get("test").render();
    assertEquals("   x\n  i++; \r\n", t1.toString());
  }

  @Test
  public void test4() {
    Template t1 = XML_ALIKE.parse(" \r <t:test>  \r  i++; \r  </t:test>  \r ");
    assertEquals("  i++; \r", t1.get("test").toString());
    t1.get("test").render();
    assertEquals(" \r  i++; \r ", t1.toString());
  }

  @Test
  public void test5() {
    Template t1 = XML_ALIKE.parse(" \r<t:test>\r  i++; \r</t:test> \r ");
    assertEquals("  i++; \r", t1.get("test").toString());
    t1.get("test").render();
    assertEquals(" \r  i++; \r ", t1.toString());
  }

  @Test
  public void test6() {
    Template t1 = XML_ALIKE.parse(" \r<t:test>  \r  i++; \r  </t:test> \r ");
    assertEquals("  i++; \r", t1.get("test").toString());
    t1.get("test").render();
    assertEquals(" \r  i++; \r ", t1.toString());
  }

  @Test
  public void test7() {
    Template t1 = XML_ALIKE.parse(" \r <t:test>\r  i++; \r</t:test>  \r ");
    assertEquals("  i++; \r", t1.get("test").toString());
    t1.get("test").render();
    assertEquals(" \r  i++; \r ", t1.toString());
  }

  @Test
  public void test8() {
    Template t1 = XML_ALIKE.parse(" \r\n <t:test>  \r\n  i++; \r\n  </t:test>  \r\n ");
    assertEquals("  i++; \r\n", t1.get("test").toString());
    t1.get("test").render();
    assertEquals(" \r\n  i++; \r\n ", t1.toString());
  }

  @Test
  public void test9() {
    Template t1 = XML_ALIKE.parse(" \r\n  <t:test>\r\n  i++; \r\n  </t:test>\r\n ");
    assertEquals("  i++; \r\n", t1.get("test").toString());
    t1.get("test").render();
    assertEquals(" \r\n  i++; \r\n ", t1.toString());
  }

  @Test
  public void test10() {
    Template t1 = XML_ALIKE.parse(" \r\n\t<t:test>\t\r\n  i++; \r\n\t</t:test>\r\n ");
    assertEquals("  i++; \r\n", t1.get("test").toString());
    t1.get("test").render();
    assertEquals(" \r\n  i++; \r\n ", t1.toString());
  }

  @Test
  public void test11() {
    Template t1 = XML_ALIKE.parse(" \r\n<t:test>  \r\n  i++; \r\n</t:test>  \r\n ");
    assertEquals("  i++; \r\n", t1.get("test").toString());
    t1.get("test").render();
    assertEquals(" \r\n  i++; \r\n ", t1.toString());
  }

  @Test
  public void test12() {
    Template t1 = XML_ALIKE.parse(" \r\n<t:test>\r\n  i++; \r\n</t:test>\r\n ");
    assertEquals("  i++; \r\n", t1.get("test").toString());
    t1.get("test").render();
    assertEquals(" \r\n  i++; \r\n ", t1.toString());
  }
}
