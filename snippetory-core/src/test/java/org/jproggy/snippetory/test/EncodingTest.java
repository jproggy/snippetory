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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.jproggy.snippetory.Encodings;
import org.jproggy.snippetory.Syntaxes;
import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.IncompatibleEncodingException;
import org.junit.jupiter.api.Test;

public class EncodingTest {

  @Test
  void encodingXML() throws Exception {
    TemplateContext ctx = new TemplateContext().encoding(Encodings.xml).syntax(Syntaxes.FLUYT);
    Template t = ctx.parse("$test");
    t.set("test", "<test>\n&amp;\n</test>");
    assertEquals("&lt;test>\n&amp;amp;\n&lt;/test>", t.toString());
    t.set("test", Encodings.xml.wrap("<test>&amp;</test>"));
    assertEquals("<test>&amp;</test>", t.toString());
    t.set("test", Encodings.html.wrap("<test>&amp;</test>"));
    assertEquals("<test>&amp;</test>", t.toString());
    assertThrows(IncompatibleEncodingException.class, () -> t.set("test", Encodings.string.wrap("<test>&amp;</test>")));
    t.set("test", Encodings.NULL.wrap("<test>\n&amp;\n</test>"));
    assertEquals("<test>\n&amp;\n</test>", t.toString());
  }

  @Test
  void encodingHTML() throws Exception {
    TemplateContext ctx = new TemplateContext().encoding(Encodings.html).syntax(Syntaxes.FLUYT);
    Template t = ctx.parse("$test");
    t.set("test", "<test>&amp;</test>\n\rfoo\rbar");
    assertEquals("&lt;test>&amp;amp;&lt;/test><br />foo<br />bar", t.toString());
    t.set("test", Encodings.xml.wrap("<test>&amp;</test>"));
    assertEquals("<test>&amp;</test>", t.toString());
    t.set("test", Encodings.html.wrap("<test>&amp;</test>"));
    assertEquals("<test>&amp;</test>", t.toString());
    assertThrows(IncompatibleEncodingException.class, () -> t.set("test", Encodings.string.wrap("<test>&amp;</test>")));
    t.set("test", Encodings.NULL.wrap("<test>\n&amp;\n</test>"));
    assertEquals("<test>\n&amp;\n</test>", t.toString());
  }

  @Test
  void encodingString() throws Exception {
    TemplateContext ctx = new TemplateContext().encoding(Encodings.string).syntax(Syntaxes.FLUYT);
    Template t = ctx.parse("$test");
    t.set("test", "<test>&amp;</test>\n\rfoo\rbar\u0000");
    assertEquals("<test>&amp;</test>\\n\\rfoo\\rbar\\u0000", t.toString());
    t.set("test", Encodings.xml.wrap("<test>&amp;</test>"));
    assertEquals("<test>&amp;</test>", t.toString());
    t.set("test", Encodings.html.wrap("<test>&amp;</test>"));
    assertEquals("<test>&amp;</test>", t.toString());
    t.set("test", Encodings.string.wrap("<test>&amp;</test>"));
    assertEquals("<test>&amp;</test>", t.toString());
    t.set("test", Encodings.html_string.wrap("<test>&amp;</test>"));
    assertEquals("<test>&amp;</test>", t.toString());
    t.set("test", Encodings.NULL.wrap("<test>\n&amp;\n</test>"));
    assertEquals("<test>\n&amp;\n</test>", t.toString());
  }

  @Test
  void encodingPlain() throws Exception {
    TemplateContext ctx = new TemplateContext().encoding(Encodings.plain).syntax(Syntaxes.FLUYT);
    Template t = ctx.parse("$test");
    t.set("test", "<test>&amp;</test>\n\rfoo\rbar");
    assertEquals("<test>&amp;</test>\n\rfoo\rbar", t.toString());
    assertThrows(IncompatibleEncodingException.class, () -> t.set("test", Encodings.xml.wrap("<test>&amp;</test>")));
    assertThrows(IncompatibleEncodingException.class, () -> t.set("test", Encodings.html.wrap("<test>&amp;</test>")));
    assertThrows(IncompatibleEncodingException.class, () -> t.set("test", Encodings.string.wrap("<test>&amp;</test>")));
    assertThrows(IncompatibleEncodingException.class, () -> t.set("test", Encodings.html_string.wrap("<test>&amp;</test>")));
    t.set("test", Encodings.NULL.wrap("<test>\n&amp;\n</test>"));
    assertEquals("<test>\n&amp;\n</test>", t.toString());
  }

  @Test
  void encodingURL() throws Exception {
    TemplateContext ctx = new TemplateContext().encoding(Encodings.url).syntax(Syntaxes.FLUYT);
    Template t = ctx.parse("$test");
    t.set("test", "a.b cä+");
    assertEquals("a.b+c%C3%A4%2B", t.toString());
  }

  @Test
  void encodingHtmlString() throws Exception {
    TemplateContext ctx = new TemplateContext().encoding(Encodings.html_string).syntax(Syntaxes.FLUYT);
    Template t = ctx.parse("$test");
    t.set("test", "<test>&amp;</test>\n\rfoo\rbar");
    assertEquals("&lt;test>&amp;amp;&lt;/test><br />foo<br />bar", t.toString());
    t.set("test", Encodings.xml.wrap("<test>\n&amp;\n</test>"));
    assertEquals("<test>\\n&amp;\\n</test>", t.toString());
    t.set("test", Encodings.html.wrap("<test>\n&amp;\n</test>"));
    assertEquals("<test>\\n&amp;\\n</test>", t.toString());
    t.set("test", Encodings.NULL.wrap("<test>\n&amp;\n</test>"));
    assertEquals("<test>\n&amp;\n</test>", t.toString());
    assertThrows(IncompatibleEncodingException.class, () -> t.set("test", Encodings.string.wrap("<test>&amp;</test>")));
  }

  @Test
  void unkown() {
    RuntimeException e = assertThrows(RuntimeException.class, () ->
            Syntaxes.FLUYT.parse("$test(enc='test')")
    );
    assertEquals("Encoding test not found.", e.getCause().getMessage());
  }
}
