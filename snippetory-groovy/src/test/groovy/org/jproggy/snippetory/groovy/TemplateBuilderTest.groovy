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

package org.jproggy.snippetory.groovy;

import static org.junit.Assert.*
import groovy.xml.MarkupBuilder

import org.jproggy.snippetory.Encodings
import org.jproggy.snippetory.Syntaxes
import org.junit.Test

class TemplateBuilderTest {

  @Test
  public void test1() {
    def t = new TemplateBuilder('$region(delimiter=" "){text$x}$');
    t.region(x:"test");
    t.region{x "other"};
    assertEquals("texttest textother", t.toString());
  }

  @Test
  public void test2() {
    def t = new TemplateBuilder('$region(delimiter=" "){text$x}$');
    t.region("test").region("other");
    assertEquals("test other", t.toString());
  }

  @Test
  public void test3() {
    def t = new TemplateBuilder('$region(delimiter=" "){text$x}$');
    t {
      region "test"
      region("other");
    }
    assertEquals("test other", t.toString());
  }

  @Test
  public void test4() {
    def t = new TemplateBuilder('$region{text$x(delimiter=" ")}$');
    t.region {
      x "test"
      x "other"
    }
    assertEquals("texttest other", t.toString());
  }

  @Test
  public void test5() {
    def t = new TemplateBuilder('$region{text$x(delimiter=" ")}$');
    t.region {
      x ("test", "other")
    }
    assertEquals("texttest other", t.toString());
  }

  @Test
  public void addTpl() {
    def t = new TemplateBuilder('$region(delimiter=" "){text$x}$');
    def tpl = Syntaxes.FLUYT.parse('$greeting $greeted')
    t.region {
      x "test"
    }
    t.region(tpl) {
      greeting "hello"
      greeted "world"
    }
    t.region("test", "other") {
      x(it)
    }
    assertEquals("texttest hello world texttest textother", t.toString());
  }

  @Test
  public void testJson() {
    def t = new TemplateBuilder('{numbers:[$i(delimiter=", ")] }', Syntaxes.FLUYT, Encodings.string);
    t.i((2..10) + 5)
    assertEquals("{numbers:[2, 3, 4, 5, 6, 7, 8, 9, 10, 5] }", t.toString());
  }

  @Test
  public void testPlain() {
    def t = new TemplateBuilder('$attrib{$value(pad="20" pad.fill="."): $data\n}$', Syntaxes.FLUYT, Encodings.plain);
    t.attrib(new Entry(label:"Age", value:45), new Entry(label:"Name", value:"John"), new Entry(label:"First name", value:"Karl") ) {
      value it.label; data it.value
    }
    assertEquals(
"""Age.................: 45
Name................: John
First name..........: Karl
""", t.toString());
  }

  @Test
  public void testHtml1() {
    def t = new TemplateBuilder(
'''<table>
<t:row>
  <tr><t:elem><td>$value</td></t:elem></tr>
</t:row>
</table>''', Syntaxes.FLUYT_X, Encodings.html);
    t {
      row {
        elem (value:"v1.1<")
        elem {value "v1.2&"}
        elem (value:"v1.3")
        elem (value:"v1.4")
      }
      row {
        elem("v2.1", "v2.2", "v2.3", "v2.4") {value it}
      }
      row([["v3.1", "v3.2", "v3.3", "v3.4"]]) {
        elem (it){value it}
      }
    }
    assertEquals(
"""<table>
  <tr><td>v1.1&lt;</td><td>v1.2&amp;</td><td>v1.3</td><td>v1.4</td></tr>
  <tr><td>v2.1</td><td>v2.2</td><td>v2.3</td><td>v2.4</td></tr>
  <tr><td>v3.1</td><td>v3.2</td><td>v3.3</td><td>v3.4</td></tr>
</table>"""      , t.toString());
  }

  @Test
  public void testHtml1_1() {
    def t = new TemplateBuilder(
'''<table>
  <tr><th>$h1</th><th>$h2</th><th>$h3</th><th>$h4</th></tr>
<t:row>
  <tr><t:elem><td>$value</td></t:elem></tr>
</t:row>
</table>''', Syntaxes.FLUYT_X, Encodings.html);
    t (h1: "heading 1", h2: "heading 2", h3: "heading 3", h4: "heading 4") {
      row {
        elem("v1.1", "v1.2", "v1.3", "v1.4") {value it}
      }
      row {
        elem("v2.1", "v2.2", "v2.3", "v2.4") {value it}
      }
    }
    assertEquals(
"""<table>
  <tr><th>heading 1</th><th>heading 2</th><th>heading 3</th><th>heading 4</th></tr>
  <tr><td>v1.1</td><td>v1.2</td><td>v1.3</td><td>v1.4</td></tr>
  <tr><td>v2.1</td><td>v2.2</td><td>v2.3</td><td>v2.4</td></tr>
</table>"""      , t.toString());
  }

  @Test
  public void testHtml1_2() {
    def t = new TemplateBuilder(
'''<table>
  <tr><th>$h1</th><th>$h2</th><th>$h3</th><th>$h4</th></tr>
</table>''', Syntaxes.FLUYT_X, Encodings.html);
    t (h1: "heading 1", h2: "heading 2", h3: "heading 3", h4: "heading 4")
    assertEquals(
"""<table>
  <tr><th>heading 1</th><th>heading 2</th><th>heading 3</th><th>heading 4</th></tr>
</table>"""      , t.toString());
  }

  @Test
  public void testHtml2() {
    def t = new TemplateBuilder(
'''<table>
<t:row>
  <tr><t:elem><td>$value</td></t:elem></tr>
</t:row>
</table>''', Syntaxes.FLUYT_X, Encodings.html);
    t.row {
      elem (value:"v1.1<")
      elem (value:"v1.2&")
      elem (value:"v1.3")
      elem (value:"v1.4")
    }.row {
      elem (value:"v2.1")
      elem (value:"v2.2")
      elem (value:"v2.3")
      elem (value:"v2.4")
    }
    assertEquals(
"""<table>
  <tr><td>v1.1&lt;</td><td>v1.2&amp;</td><td>v1.3</td><td>v1.4</td></tr>
  <tr><td>v2.1</td><td>v2.2</td><td>v2.3</td><td>v2.4</td></tr>
</table>"""      , t.toString());
  }

  @Test
  public void testHtml3() {
    def t = new TemplateBuilder(
'''<table>
<t:row>
  <tr><t:elem><td>$value</td></t:elem></tr>
</t:row>
</table>''', Syntaxes.FLUYT_X, Encodings.html);
    t.row {
      elem (value:"v1.1<")
      elem (value:"v1.2&")
      elem (value:"v1.3")
      elem (value:"v1.4")
    }
    t.row {
      elem (value:"v2.1")
      elem (value:"v2.2")
      elem (value:"v2.3")
      elem (value:"v2.4")
    }
    assertEquals(
"""<table>
  <tr><td>v1.1&lt;</td><td>v1.2&amp;</td><td>v1.3</td><td>v1.4</td></tr>
  <tr><td>v2.1</td><td>v2.2</td><td>v2.3</td><td>v2.4</td></tr>
</table>"""      , t.toString());
  }

  public void html1() {
    def h = new MarkupBuilder()
    println h.html {
      head {
        title "testdoc";
        script()
      }
    }
  }
  class Entry {
    String label;
    def value;
  }
}
