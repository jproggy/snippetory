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
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.Test;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.TemplateContext;

class NumberFormatTest {

  @Test
  void formatNumber() {
    Template number = XML_ALIKE.parse("{v:test}", Locale.GERMAN);
    number.set("test", "x");
    assertEquals("x", number.toString());
    number.set("test", "123456");
    assertEquals("123456", number.toString());
    number.set("test", 1.6);
    assertEquals("1,6", number.toString());
    number.set("test", 1001.6333);
    assertEquals("1.001,633", number.toString());

    number = XML_ALIKE.parse("{v:test}", Locale.US);
    number.set("test", "x");
    assertEquals("x", number.toString());
    number.set("test", "123456");
    assertEquals("123456", number.toString());
    number.set("test", 123456);
    assertEquals("123456", number.toString());
    number.set("test", 1.6);
    assertEquals("1.6", number.toString());
    number.set("test", 1001.6333);
    assertEquals("1,001.633", number.toString());

    number = XML_ALIKE.parse("{v:test number='0.00#'}", Locale.GERMANY);
    number.set("test", 1.55);
    assertEquals("1,55", number.toString());
    number.set("test", 1.55555);
    assertEquals("1,556", number.toString());
    number = XML_ALIKE.parse("{v:test number='000'}", Locale.GERMANY);
    number.set("test", 1.55);
    assertEquals("002", number.toString());
    number.set("test", 1000);
    assertEquals("1000", number.toString());
    number = XML_ALIKE.parse("{v:test number='tostring'}", Locale.GERMANY);
    number.set("test", 1.55);
    assertEquals("1.55", number.toString());
    number.set("test", 10000);
    assertEquals("10000", number.toString());
    number = XML_ALIKE.parse("{v:test}", Locale.GERMANY);
    number.set("test", 1.55);
    assertEquals("1,55", number.toString());

    number = XML_ALIKE.parse("{v:test}");
    number.set("test", 1.55);
    assertEquals("1.55", number.toString());
    number = XML_ALIKE.parse("{v:test}", Locale.US);
    number.set("test", 1.55);
    assertEquals("1.55", number.toString());
  }

  @Test
  void formatInt() {
    Template number = XML_ALIKE.parse("{v:test int=\"0.00#\"}", Locale.GERMAN);
    number.set("test", "x");
    assertEquals("x", number.toString());
    number.set("test", "123456");
    assertEquals("123456", number.toString());
    number.set("test", 1);
    assertEquals("1,00", number.toString());
    number.set("test", 1001.6);
    assertEquals("1.001,6", number.toString());
    number.set("test", 1.6333);
    assertEquals("1,633", number.toString());

    number = XML_ALIKE.parse("{v:test}");
    number.set("test", 1000);
    assertEquals("1000", number.toString());

    number = XML_ALIKE.parse("{v:test}", Locale.US);
    number.set("test", 1000);
    assertEquals("1000", number.toString());

    number = XML_ALIKE.parse("{v:test int='tostring'}", Locale.GERMANY);
    number.set("test", 1000);
    assertEquals("1000", number.toString());
    number.set("test", 10000);
    assertEquals("10000", number.toString());

    number = XML_ALIKE.parse("{v:test}", Locale.GERMANY);
    number.set("test", 10000);
    assertEquals("10000", number.toString());

    number = XML_ALIKE.parse("{v:test}");
    number.set("test", 10000);
    assertEquals("10000", number.toString());
  }

  @Test
  void formatNumberInheritance() {
    Template t = XML_ALIKE.parse("before<t: number='000'>->{v:test}<-</t:>after", Locale.GERMAN);
    assertEquals(t.names(), Set.of("test"));
    assertEquals("beforeafter", t.toString());
    t.set("test", 5.1);
    assertEquals("before->005<-after", t.toString());
    t = XML_ALIKE.parse("before<t: number='000'>-><t:>{v:test}</t:><-</t:>after");
    assertEquals("beforeafter", t.toString());
    t.set("test", 5);
    assertEquals("before->005<-after", t.toString());
    t = XML_ALIKE.context()
            .attrib("number", "000")
            .locale(Locale.US)
            .parse("before<t:>-><t:>{v:test}</t:><-</t:>after");
    assertEquals(t.names(), Set.of("test"));
    assertEquals("beforeafter", t.toString());
    t.set("test", 5);
    assertEquals("before->005<-after", t.toString());
  }
  @Test
  void decimalFormatOverridesContextNumberFormat() {
    // Set up a TemplateContext with a number format attribute
    TemplateContext context = XML_ALIKE.context()
            .attrib("number", "000")
            .locale(Locale.US);

    // Parse a template without any format attributes - should inherit from context
    Template template = context.parse("{v:test}");
    template.set("test", 5);
    assertEquals("005", template.toString());
    template.set("test", 5.11);
    assertEquals("005", template.toString());

    // Parse a template with decimal format - should override the context's number format
    Template templateWithDecimal = context.parse("{v:test decimal='#.0#'}");
    templateWithDecimal.set("test", 5.678);
    assertEquals("5.68", templateWithDecimal.toString());
    templateWithDecimal.set("test", BigDecimal.valueOf(5.0));
    assertEquals("5.0", templateWithDecimal.toString());
    templateWithDecimal.set("test", BigInteger.valueOf(5));
    assertEquals("005", templateWithDecimal.toString());

    // Test with integer value in the template with decimal format
    templateWithDecimal.set("test", 5);
    assertEquals("005", templateWithDecimal.toString());

    // Test with a template that explicitly overrides the number format too
    Template templateWithBoth = context.parse("{v:test decimal='#.0#'}");
    templateWithBoth.set("test", 5);
    assertEquals("005", templateWithBoth.toString());
    templateWithBoth.set("test", 5.678);
    assertEquals("5.68", templateWithBoth.toString());
    templateWithBoth.set("test", BigDecimal.valueOf(5.0));
    assertEquals("5.0", templateWithBoth.toString());

    // Test with German locale
    TemplateContext contextDE = XML_ALIKE.context()
            .attrib("decimal", "#.##")
            .attrib("number", "000")
            .locale(Locale.GERMAN);

    Template templateDE = contextDE.parse("{v:test}");
    templateDE.set("test", 5);
    assertEquals("005", templateDE.toString());
    templateDE.set("test", 5.678);
    assertEquals("5,68", templateDE.toString());
  }
}
