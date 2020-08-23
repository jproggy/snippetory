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

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.TemplateContext;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class InheritanceTest {
  static {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
  }

  private static final java.sql.Date D2 = java.sql.Date.valueOf("2011-10-06");
  private static final java.sql.Date D1 = java.sql.Date.valueOf("2011-10-15");
  private static final Date D1_TIME = new Date(D1.getTime() + 3915000l);

  public static class FormRoot {

    @Test
    public void dateToRegion() {
      TemplateContext ctx = XML_ALIKE.context().attrib("date", "medium_long");
      Template date = ctx.parse("<t:test>Date: {v:d1}<t:> Other date: {v:d2}</t:>\n</t:test>");
      date.get("test").set("d1", D1).set("d2", D1_TIME).render();
      date.get("test").set("d1", D2).render();
      assertEquals(
          "Date: Oct 15, 2011, 12:00:00 AM GMT Other date: Oct 15, 2011, 1:05:15 AM GMT\nDate: Oct 6, 2011, 12:00:00 AM GMT\n",
          date.toString());

      Template test = date.get("test");
      assertEquals("Date: {v:d1}\n", test.toString());
      test.set("d2", D2).set("d1", D2);
      assertEquals("Date: Oct 6, 2011, 12:00:00 AM GMT Other date: Oct 6, 2011, 12:00:00 AM GMT\n", test.toString());
      assertEquals(
          "Date: Oct 15, 2011, 12:00:00 AM GMT Other date: Oct 15, 2011, 1:05:15 AM GMT\nDate: Oct 6, 2011, 12:00:00 AM GMT\n",
          date.toString());

      date.set("test", D2);
      assertEquals("Oct 6, 2011, 12:00:00 AM GMT", date.toString());

      date = date.get();
      assertEquals("", date.toString());
      date.get("test").set("d1", D1).set("d2", D1_TIME).render();
      date.get("test").set("d1", D2).render();
      assertEquals(
          "Date: Oct 15, 2011, 12:00:00 AM GMT Other date: Oct 15, 2011, 1:05:15 AM GMT\nDate: Oct 6, 2011, 12:00:00 AM GMT\n",
          date.toString());
    }

    @Test
    public void dateToCond() {
      TemplateContext ctx = XML_ALIKE.context().attrib("date", "short_medium");
      Template date = ctx.parse("<t:>Date: {v:d1}<t:test> Other date: {v:d2}</t:test></t:>");
      Template test1 = date.get("test");
      test1.set("d2", D1_TIME).render();
      date.set("d1", D1);
      assertEquals("Date: 10/15/11, 12:00:00 AM Other date: 10/15/11, 1:05:15 AM", date.toString());
      date.set("test", D2);
      assertEquals("Date: 10/15/11, 12:00:00 AM10/6/11, 12:00:00 AM", date.toString());

      Template test2 = date.get("test");
      assertEquals(" Other date: {v:d2}", test2.toString());
      test2.set("d2", D2);
      assertEquals(" Other date: 10/15/11, 1:05:15 AM", test1.toString());
      assertEquals(" Other date: 10/6/11, 12:00:00 AM", test2.toString());

      date = date.get();
      assertEquals("", date.toString());
      date.set("d1", D2).get("test").set("d2", D2).render();
      date.set("d1", D1);
      assertEquals("Date: 10/15/11, 12:00:00 AM Other date: 10/6/11, 12:00:00 AM", date.toString());
    }

    @Test
    public void dateToCondAndRegion() {
      TemplateContext ctx = XML_ALIKE.context().attrib("date", "short_medium");
      Template date = ctx.parse("<t:test><t:>Date: {v:d1}<t:test><t:> Other date: {v:d2}</t:></t:test></t:></t:test>")
          .get("test");
      Template test1 = date.get("test");
      test1.set("d2", D1_TIME).render();
      date.set("d1", D1);
      assertEquals("Date: 10/15/11, 12:00:00 AM Other date: 10/15/11, 1:05:15 AM", date.toString());
      date.set("test", D2);
      assertEquals("Date: 10/15/11, 12:00:00 AM10/6/11, 12:00:00 AM", date.toString());

      Template test2 = date.get("test");
      assertEquals("", test2.toString());
      test2.set("d2", D2);
      assertEquals(" Other date: 10/15/11, 1:05:15 AM", test1.toString());
      assertEquals(" Other date: 10/6/11, 12:00:00 AM", test2.toString());

      date = date.get();
      assertEquals("", date.toString());
      date.set("d1", D2).get("test").set("d2", D2).render();
      date.set("d1", D1);
      assertEquals("Date: 10/15/11, 12:00:00 AM Other date: 10/6/11, 12:00:00 AM", date.toString());
    }
  }

  public static class FromRCond {
    @Test
    public void simple() {
      Template date = XML_ALIKE.parse("before<t: date='sql'>->{v:test}<-</t:>after");
      assertEquals("beforeafter", date.toString());
      date.set("test", D1);
      assertEquals("before->2011-10-15<-after", date.toString());

      date = date.get();
      assertEquals("beforeafter", date.toString());
      date.set("test", D1);
      assertEquals("before->2011-10-15<-after", date.toString());
    }

    @Test
    public void nestedCond() {
      Template date = XML_ALIKE.parse("before<t: date='sql'>-><t:>{v:test}</t:><-</t:>after");
      assertEquals("beforeafter", date.toString());
      date.set("test", D1);
      assertEquals("before->2011-10-15<-after", date.toString());

      date = date.get();
      assertEquals("beforeafter", date.toString());
      date.set("test", D1);
      assertEquals("before->2011-10-15<-after", date.toString());
    }

    @Test
    public void nestedRegion() {
      Template date = XML_ALIKE.parse("before<t: date='sql'>-><t:v1>{v:test}</t:v1><-</t:>after");
      assertEquals("beforeafter", date.toString());
      date.get("v1").set("test", D1).render();
      assertEquals("before->2011-10-15<-after", date.toString());

      Template v1 = date.get("v1");
      assertEquals("{v:test}", v1.toString());
      v1.append("test", D2);
      assertEquals("2011-10-06", v1.toString());

      date = date.get();
      assertEquals("beforeafter", date.toString());
      date.get("v1").set("test", D1).render();
      assertEquals("before->2011-10-15<-after", date.toString());
    }
  }

  public static class FormRegion {
    static {
      TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
    }

    @Test
    public void toRegion() {
      Template date = XML_ALIKE.parse(
          "<t:test date='short'>Date 1: {v:d1}<t:test date='short'> Date 2: {v:d1}</t:test></t:test>").get("test");
      date.set("d1", D1);
      date.get("test").set("d1", D2).render();
      assertEquals("Date 1: 10/15/11 Date 2: 10/6/11", date.toString());

    }

    @Test
    public void toCond() {
      Template date = XML_ALIKE.parse(
          "<t:test date=\"short_full\">Date 1: {v:d1 date='sql'}<t:> Date 2: {v:d2} </t:></t:test>", Locale.GERMAN)
          .get("test");
      date.set("d1", D1);
      date.set("d2", D2);
      assertEquals("Date 1: 2011-10-15 Date 2: 06.10.11, 00:00:00 Mittlere Greenwich-Zeit ", date.toString());

    }

    @Test
    public void nestedRegion() {
      Template date = XML_ALIKE
          .read("<t:test date='_medium'><t:>Date 1: {v:d1}</t:><t:test><t:> Date 2: {v:d2} </t:></t:test></t:test>")
          .locale(Locale.GERMAN).attrib("date", "long_short").parse().get("test");
      date.set("d1", D1_TIME);
      date.get("test").set("d2", D2).render();
      assertEquals("Date 1: 01:05:15 Date 2: 00:00:00 ", date.toString());
    }

  }

}
