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

import java.util.Locale;

import org.jproggy.snippetory.Template;
import org.junit.jupiter.api.Test;

class NumberFormatTest {

  @Test
  void formatNumber() {
    Template number = XML_ALIKE.parse("{v:test number=\"0.00#\"}", Locale.GERMAN);
    number.set("test", "x");
    assertEquals("x", number.toString());
    number.set("test", "123456");
    assertEquals("123456", number.toString());
    number.set("test", 1.6);
    assertEquals("1,60", number.toString());
    number.set("test", 1.6333);
    assertEquals("1,633", number.toString());

    number = XML_ALIKE.parse("{v:test}", Locale.US);
    number.set("test", "x");
    assertEquals("x", number.toString());
    number.set("test", "123456");
    assertEquals("123456", number.toString());
    number.set("test", 1.6);
    assertEquals("1.6", number.toString());
    number.set("test", 1.6333);
    assertEquals("1.633", number.toString());

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
    number.set("test", 1.6);
    assertEquals("1,6", number.toString());
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
    assertEquals("beforeafter", t.toString());
    t.set("test", 5.1);
    assertEquals("before->005<-after", t.toString());
    t = XML_ALIKE.parse("before<t: number='000'>-><t:>{v:test}</t:><-</t:>after");
    assertEquals("beforeafter", t.toString());
    t.set("test", 5);
    assertEquals("before->005<-after", t.toString());
  }
}
