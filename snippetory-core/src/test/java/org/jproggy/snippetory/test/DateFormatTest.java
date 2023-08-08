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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;

import org.jproggy.snippetory.Template;

class DateFormatTest {
  static {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
  }

  private static final ZonedDateTime D1 = LocalDate.parse("2011-10-15").atStartOfDay(ZoneId.of("GMT"));
  private static final ZonedDateTime D1_TIME = ZonedDateTime.parse("2011-10-15T01:05:15Z[GMT]");

  @Test
  void jsDate() {
    Template date = XML_ALIKE.parse("{v:test date='JS'}");
    date.set("test", D1);
    assertEquals("new Date(2011, 10, 15)", date.toString());
  }

  @Test
  void jsTime() {
    Template date = XML_ALIKE.parse("{v:test date='_JS'}");
    date.set("test", D1_TIME);
    assertEquals("new Date(0, 0, 0, 01, 05, 15)", date.toString());
  }

  @Test
  void js() {
    Template date = XML_ALIKE.parse("{v:test date='JS_JS'}", Locale.GERMAN);
    date.set("test", D1_TIME);
    assertEquals("new Date(2011, 10, 15, 01, 05, 15)", date.toString());
  }

  @Test
  void sqlDate() {
    Template date = XML_ALIKE.parse("{v:test date='sql'}");
    date.set("test", D1);
    assertEquals("2011-10-15", date.toString());
  }

  @Test
  void sql() {
    Template date = XML_ALIKE.parse("{v:test date='sql_sql'}", Locale.GERMAN);
    date.set("test", D1_TIME);
    assertEquals("2011-10-15 01:05:15", date.toString());
  }

  @Test
  void sqlTime() {
    Template date = XML_ALIKE.parse("{v:test date='_sql'}");
    date.set("test", D1_TIME);
    assertEquals("01:05:15", date.toString());
  }

  @Test
  void germanLong() {
    Template date = XML_ALIKE.parse("{v:test date='long'}", Locale.GERMAN);
    date.set("test", D1);
    assertEquals("15. Oktober 2011", date.toString());
  }

  @Test
  void germanFullShort() {
    Template date = XML_ALIKE.parse("{v:test date='full_short'}", Locale.GERMAN);
    date.set("test", D1);
    assertEquals("Samstag, 15. Oktober 2011, 00:00", date.toString());
  }

  @Test
  void germanFullFull() {
    Template date = XML_ALIKE.parse("{v:test date=\"full_full\"}", Locale.GERMAN);
    date.set("test", D1);
    assertEquals("Samstag, 15. Oktober 2011 um 00:00:00 Mittlere Greenwich-Zeit", date.toString());
  }

  @Test
  void germanShortLong() {
    Template date = XML_ALIKE.parse("<t:test date='short_long'></t:test>", Locale.GERMAN);
    date.set("test", D1);
    assertEquals("15.10.11, 00:00:00 GMT", date.toString());
  }

  @Test
  void germanMask() {
    Template date = XML_ALIKE.parse("<t:test date=\"w/MM yyyy\"></t:test>", Locale.GERMAN);
    date.set("test", D1);
    assertEquals("42/10 2011", date.toString());
  }

  @Test
  void chinaLong() {
    Template date = XML_ALIKE.parse("{v:test date='long'}", Locale.SIMPLIFIED_CHINESE);
    date.set("test", D1);
    assertEquals("2011年10月15日", date.toString());
  }

  @Test
  void chinaShortFull() {
    Template date = XML_ALIKE.parse("{v:test date='short_full'}", Locale.SIMPLIFIED_CHINESE);
    date.set("test", D1_TIME);
    assertEquals("2011/10/15 格林尼治标准时间 上午1:05:15", date.toString());
  }

  @Test
  void china_medium() {
    Template date = XML_ALIKE.parse("{v:test date='_medium'}", Locale.SIMPLIFIED_CHINESE);
    date.set("test", D1_TIME);
    assertEquals("上午1:05:15", date.toString());
  }

  @Test
  void usLong() {
    Template date = XML_ALIKE.parse("{v:test date='long'}", Locale.US);
    date.set("test", D1);
    assertEquals("October 15, 2011", date.toString());
  }

  @Test
  void usShortFull() {
    Template date = XML_ALIKE.parse("{v:test date='short_full'}", Locale.US);
    date.set("test", D1_TIME);
    assertEquals("10/15/11, 1:05:15 AM Greenwich Mean Time", date.toString());
  }

  @Test
  void us_medium() {
    Template date = XML_ALIKE.parse("{v:test date='_medium'}", Locale.US);
    date.set("test", D1_TIME);
    assertEquals("1:05:15 AM", date.toString());
  }

  @Test
  void germanShortFull() {
    Template date = XML_ALIKE.parse("{v:test date='short_full'}", Locale.GERMAN);
    date.set("test", D1_TIME);
    assertEquals("15.10.11, 01:05:15 Mittlere Greenwich-Zeit", date.toString());
  }

  @Test
  void german_medium() {
    Template date = XML_ALIKE.parse("{v:test date='_medium'}", Locale.GERMAN);
    date.set("test", D1_TIME);
    assertEquals("01:05:15", date.toString());
  }

  @Test
  void jpLongLong() {
    Template date = XML_ALIKE.parse("{v:test date='long_long'}", Locale.JAPANESE);
    date.set("test", D1);
    assertEquals("2011年10月15日 0:00:00 GMT", date.toString());
  }

  @Test
  void frFullLong() {
    Template date = XML_ALIKE.read("{v:test}").locale(Locale.FRENCH).attrib("date", "full_long").parse();
    date.set("test", D1);
    assertEquals("samedi 15 octobre 2011 à 00:00:00 GMT", date.toString());
  }

  @Test
  void korean() {
    Template date = XML_ALIKE.parse("{v:test date=\"\"}", Locale.KOREAN);
    date.set("test", D1);
    assertEquals("2011. 10. 15.", date.toString());
  }

  @Test
  void defaultGerman1() {
    Template date = XML_ALIKE.parse("{v:test}", Locale.GERMAN);
    date.set("test", D1);
    assertEquals("15.10.2011", date.toString());
  }

  @Test
  void defaultGerman2() {
    Template date = XML_ALIKE.parse("{v:test date=\"\"}", Locale.GERMAN);
    date.set("test", D1);
    assertEquals("15.10.2011", date.toString());
  }

  @Test
  void defaultGerman3() {
    Template date = XML_ALIKE.parse("<t:test date=''></t:test>", Locale.GERMAN);
    date.set("test", D1);
    assertEquals("15.10.2011", date.toString());
  }

  @Test
  void defaultGerman4() {
    Template date = XML_ALIKE.parse("<t:test date=\"\"></t:test>", Locale.GERMAN);
    date.set("test", D1);
    assertEquals("15.10.2011", date.toString());
  }
}
