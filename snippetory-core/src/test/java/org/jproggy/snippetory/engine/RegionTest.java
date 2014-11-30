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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jproggy.snippetory.Encodings;
import org.jproggy.snippetory.Repo;
import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.UriResolver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class RegionTest {

  private static TemplateFragment tf(String v) {
    return new TemplateFragment(v);
  }

  @Test
  public void charAtTest() {
    Location placeHolder = new Location(null, new Metadata("", "", Attributes.parse(null,
        Collections.<String, String> emptyMap(), null)));
    List<DataSink> parts = Arrays.asList((DataSink)tf(""), tf("test"), tf("yagni"));
    Region region = new Region(new DataSinks(parts, placeHolder), Collections.<String, Region> emptyMap());
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
    try {
      region.charAt(9);
      fail();
    } catch (Exception e) {
      // ignore --> expected
    }
    parts = Arrays.asList((DataSink)tf("test"), tf(""), tf("yagni"), tf(""), tf("jproggy"));
    region = new Region(new DataSinks(parts, placeHolder), Collections.<String, Region> emptyMap());
    assertEquals('y', region.charAt(4));
    assertEquals('y', region.charAt(15));
  }

  private Template template;
  private String[] variants = { "row1", "row2", "row3", };
  private List<Object> data = new ArrayList<Object>();

  @Before
  public void init() {
    template = Repo.readResource("testTable.htm").locale(Locale.US).parse();
    for (int i = 0; i < 111; i++) {
      data.add(String.valueOf(i));
    }
  }

  @Test
  public void read() throws MalformedURLException {
    TemplateContext context = new TemplateContext().uriResolver(UriResolver.directories("src/test/resources"));
    context.getTemplate("testTable.htm");
    context.uriResolver(UriResolver.directories(new File("src/test/resources")));
    context.getTemplate("testTable.htm");
    context.uriResolver(UriResolver.url(new File("src/test/resources").toURI().toURL()));
    context.getTemplate("testTable.htm");
  }

  @Test
  public void calendar() throws IOException {
    Date day = Date.valueOf("1979-02-02");
    Template month = Repo.readResource("calendar.html").encoding(Encodings.html).locale(Locale.GERMAN).parse();
    month.set("day", day);
    Calendar dayCounter = getStart(day);
    Calendar end = getEnd(day);
    while (dayCounter.before(end)) {
      Template week = month.get("week");
      for (int i = 1; i <= 7; i++) {
        week.get("day").set("day", dayCounter).render();
        dayCounter.add(Calendar.DAY_OF_YEAR, 1);
      }
      week.render();
    }
    month.render(new FileWriter("target/calendarOut.html"));
  }

  private Calendar getStart(Date day) {
    Calendar result = Calendar.getInstance();
    result.setTime(day);
    int daysOfMonth = result.get(Calendar.DAY_OF_MONTH);
    result.add(Calendar.DAY_OF_YEAR, daysOfMonth * -1);
    int daysOfWeek = result.get(Calendar.DAY_OF_WEEK) - result.getFirstDayOfWeek();
    if (daysOfWeek < 0) daysOfWeek += 7;
    result.add(Calendar.DAY_OF_YEAR, daysOfWeek * -1);
    return result;
  }

  private Calendar getEnd(Date day) {
    Calendar result = Calendar.getInstance();
    result.setTime(day);
    int days = result.getActualMaximum(Calendar.DAY_OF_MONTH) - result.get(Calendar.DAY_OF_MONTH);
    result.add(Calendar.DAY_OF_YEAR, days);
    days = result.getActualMaximum(Calendar.DAY_OF_WEEK) - result.get(Calendar.DAY_OF_WEEK);
    result.add(Calendar.DAY_OF_YEAR, days);
    return result;
  }

  @Test
  public void test100() {
    testN(100);
  }

  @Test
  public void test1000() {
    testN(1000);
  }

  @Test
  public void test100_1000() {
    for (int i = 0; i < 1000; i++) {
      template.clear();
      testN(100);
    }
  }

  @Test
  public void test10000() {
    testN(10000);
  }

  @Test
  public void test100_000() {
    testN(100000);
  }

  @Test
  @Ignore
  public void test1000000() {
    testN(1000000);
  }

  public void testN(int n) {
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
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      Assert.assertEquals(n, i);
    }
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
