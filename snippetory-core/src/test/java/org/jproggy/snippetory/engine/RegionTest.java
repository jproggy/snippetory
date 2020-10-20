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

package org.jproggy.snippetory.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jproggy.snippetory.Encodings;
import org.jproggy.snippetory.Repo;
import org.jproggy.snippetory.Syntaxes;
import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.UriResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class RegionTest {

  private static TemplateFragment tf(String v) {
    return new TemplateFragment(v);
  }

  @Test
  void charAtTest() {
    Location placeHolder = new Location(null, new Metadata("", "", Attributes.parse(null,
            Collections.emptyMap(), null)));
    List<DataSink> parts = Arrays.asList(tf(""), tf("test"), tf("yagni"));
    Region region = new Region(new DataSinks(parts, placeHolder), Collections.<String, Region>emptyMap());
    assertEquals('t', region.charAt(0));
    assertEquals('e', region.charAt(1));
    assertEquals('s', region.charAt(2));
    assertEquals('t', region.charAt(3));
    assertEquals('y', region.charAt(4));
    assertEquals('a', region.charAt(5));
    assertEquals('g', region.charAt(6));
    assertEquals('n', region.charAt(7));
    assertEquals('i', region.charAt(8));
    assertEquals('y', region.charAt(4));
    assertEquals('e', region.charAt(1));
    assertEquals(9, region.length());
    assertEquals("testyagni", region.toString());
    CharSequence child = region.subSequence(2, 6);
    assertEquals('s', child.charAt(0));
    assertEquals('t', child.charAt(1));
    assertEquals('y', child.charAt(2));
    assertEquals('a', child.charAt(3));
    assertEquals(4, child.length());
    assertEquals("stya", child.toString());
    child = child.subSequence(1, 3);
    assertEquals('t', child.charAt(0));
    assertEquals('y', child.charAt(1));
    assertEquals(2, child.length());
    assertEquals("ty", child.toString());
    assertThrows(Exception.class, () -> region.charAt(9));
    parts = Arrays.asList((DataSink) tf("test"), tf(""), tf("yagni"), tf(""), tf("jproggy"));
    Region region2 = new Region(new DataSinks(parts, placeHolder), Collections.<String, Region>emptyMap());
    assertEquals('y', region2.charAt(4));
    assertEquals('y', region2.charAt(15));
  }

  private Template template;
  private final String[] variants = {"row1", "row2", "row3", "row4",};
  private final List<Object> data = new ArrayList<>();

  @BeforeEach
  void init() {
    template = Syntaxes.FLUYT.context().uriResolver(UriResolver.resource()).locale(Locale.US).getTemplate("testTable.htm");
    for (int i = 0; i < 111; i++) {
      data.add(String.valueOf(i));
    }
  }

  @Test
  void read() throws MalformedURLException {
    TemplateContext context = new TemplateContext().uriResolver(UriResolver.directories("src/test/resources"));
    context.getTemplate("testTable.htm");
    context.uriResolver(UriResolver.directories(new File("src/test/resources")));
    context.getTemplate("testTable.htm");
    context.uriResolver(UriResolver.url(new File("src/test/resources").toURI().toURL()));
    context.getTemplate("testTable.htm");
  }

  @Test
  void calendar() throws IOException {
    TemporalField weekField = WeekFields.of(Locale.GERMANY).dayOfWeek();
    YearMonth month = YearMonth.of(1980, Month.FEBRUARY);

    LocalDate dayCounter = month.atDay(1).with(weekField, 1);
    LocalDate end = month.atEndOfMonth().with(weekField, 7).plusDays(1);

    Template calendar = Repo.readResource("calendar.html").encoding(Encodings.html).locale(Locale.GERMANY).parse();
    calendar.set("month", month);

    for (long i = 1; i <= 7; i++) {
      calendar.get("caption").set("week-day", dayCounter.with(weekField, i)).render();
    }

    while (dayCounter.isBefore(end)) {
      Template weekTpl = calendar.get("week");
      for (int i = 1; i <= 7; i++) {
        String tpl = YearMonth.from(dayCounter).equals(month) ? "day-in" : "day-out";
        weekTpl.get(tpl).set("day", dayCounter.atStartOfDay()).render("target");
        dayCounter = dayCounter.plusDays(1);
      }
      weekTpl.render();
    }
    try (FileWriter out = new FileWriter("target/calendarOut.html")) {
      calendar.render(out);
    }
  }

  @Test
  void test100() {
    testN(100);
  }

  @Test
  void test1000() {
    testN(1000);
  }

  @Test
  void test100_1000() {
    for (int i = 0; i < 1000; i++) {
      template.clear();
      testN(100);
    }
  }

  @Test
  void test10_000() {
    testN(10000);
  }

  @Test
  void test100_000() {
    testN(100000);
  }

  @Test
  @Disabled
  void test1000_000() {
    testN(1000000);
  }

  void testN(int n) {
    int i = 0;
    try {
      int count = 0;
      for (; i < n; i++) {
        Template row = template.get(variants[i % variants.length]);
        for (int x = 0; x < 10; x++) {
          row.get("x").set("val", data.get(count)).render();
          count++;
          if (count >= data.size()) count = 0;
        }
        row.render("row1");
      }
      NUL nul = new NUL();
      template.render(nul);
      nul.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    assertEquals(n, i);
  }

  private static class NUL extends Writer {

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {}

    @Override
    public void flush() throws IOException {}

    @Override
    public void close() throws IOException {}
  }
}
