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

import static org.jproggy.snippetory.Syntaxes.FLUYT_X;
import static org.jproggy.snippetory.Syntaxes.XML_ALIKE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import org.jproggy.snippetory.Template;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ArgumentsSources;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class DateFormatTest {
  static {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
  }

  private static final ZonedDateTime D1 = LocalDate.parse("2011-10-15").atStartOfDay(ZoneId.of("GMT"));
  private static final ZonedDateTime D1_TIME = ZonedDateTime.parse("2011-10-15T01:05:15Z[GMT]");

  @ParameterizedTest
  @CsvSource(value = {
      "{v:test date='JS'}|Asia/Kolkata|new Date(2011, 10, 15)|new Date(2011, 10, 15)",
      "{v:test date='JS_JS'}|Europe/Paris|new Date(2011, 10, 15, 00, 00, 00)|new Date(2011, 10, 15, 01, 05, 15)",
      "{v:test date='_JS'}|America/New_York|new Date(0, 0, 0, 00, 00, 00)|new Date(0, 0, 0, 01, 05, 15)",
  }, delimiter = '|')
  void jsDate(String template, ZoneId z, String result, String result_time) {
    Template date = XML_ALIKE.parse(template);
    assertEquals(result, date.set("test", D1.withZoneSameLocal(z)).toString());
    assertEquals(result_time, date.set("test", D1_TIME.withZoneSameLocal(z)).toString());
  }

  public static Stream<Temporal> yearRepresentations() {
    Year y = Year.of(2011);
    return Stream.of(y, y.atMonth(10), y.atDay(200), y.atMonth(10).atDay(10), D1, D1_TIME);
  }
  @ParameterizedTest
  @MethodSource("yearRepresentations")
  void year(Temporal y) {
    Template date = XML_ALIKE.parse("{v:test date='yy'}", Locale.GERMAN);
    assertEquals("11", date.set("test", y).toString());
  }

  @ParameterizedTest
  @CsvSource(value = {
      "$test(date='sql')|2011-10-15|2011-10-15",
      "$test(date='sql_sql')|2011-10-15 00:00:00|2011-10-15 01:05:15",
      "$test(date='_sql')|00:00:00|01:05:15",
  }, delimiter = '|')
  void sqlDate(String template, String result, String result_time) {
    Template date = FLUYT_X.parse(template);
    assertEquals(result, date.set("test", D1).toString());
    assertEquals(result_time, date.set("test", D1_TIME).toString());
  }

  @ParameterizedTest
  @CsvSource(value = {
      "$test(date='iso')|Asia/Kolkata|2011-10-15+05:30|2011-10-15+05:30",
      "$test(date='iso')|UTC|2011-10-15Z|2011-10-15Z",
      "$test(date='iso_iso')|Etc/GMT+10|2011-10-15T00:00:00-10:00|2011-10-15T01:05:15-10:00",
      "$test(date='_iso')|+01:00|00:00:00+01:00|01:05:15+01:00",
  }, delimiter = '|')
  void isoDate(String template, ZoneId z, String result, String result_time) {
    Template date = FLUYT_X.context().parse(template);
    assertEquals(result, date.set("test", D1.withZoneSameLocal(z)).toString());
    assertEquals(result_time, date.set("test", D1_TIME.withZoneSameLocal(z)).toString());
  }

  @Test
  void germanLong() {
    Template date = XML_ALIKE.parse("{v:test date='long'}", Locale.GERMAN);
    date.set("test", D1);
    assertEquals("15. Oktober 2011", date.toString());
  }

  @ParameterizedTest
  @CsvSource(value = { "de|Samstag, 15. Oktober 2011, 00:00", "en_GB|2011 Oct 15, Sat 00:00",
      "en_US|2011 Oct 15, Sat 00:00", "ko|2011년 10월 15일 토요일 오전 12:00", "fr_CH|2011 Oct 15, Sat 00:00" },
      delimiter = '|')
  void localFullShort(Locale l, String result) {
    Template date = XML_ALIKE.parse("{v:test date='full_short'}", l);
    date.set("test", D1);
    assertEquals(result, date.toString());
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

  @ParameterizedTest
  @CsvSource({"{v:test}", "{v:test date=\"\"}", "<t:test date=''></t:test>", "<t:test date=\"\"></t:test>"} )
  void defaultGerman(String template) {
    Template date = XML_ALIKE.parse(template, Locale.GERMAN);
    date.set("test", D1);
    assertEquals("15.10.2011", date.toString());
  }
}
