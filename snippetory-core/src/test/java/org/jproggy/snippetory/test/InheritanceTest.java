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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jproggy.snippetory.Syntaxes.XML_ALIKE;

import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.TemplateContext;

class InheritanceTest {
  static {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
  }

  private static final ZonedDateTime D1 = ZonedDateTime.parse("2011-10-15T00:00:00Z[GMT]");
  private static final ZonedDateTime D2 = ZonedDateTime.parse("2011-10-06T00:00:00Z[GMT]");

  private static final ZonedDateTime D1_TIME = ZonedDateTime.parse("2011-10-15T01:05:15Z[GMT]");

  @Nested
  class FromRoot {

    @Test
    void dateToRegion() {
      TemplateContext ctx = XML_ALIKE.context().attrib("date", "medium_long");
      Template date = ctx.parse("<t:test>Date: {v:d1}<t:> Other date: {v:d2}</t:>\n</t:test>");
      date.get("test").set("d1", D1).set("d2", D1_TIME).render();
      date.get("test").set("d1", D2).render();
      assertThat(date.toString(), is(
              "Date: Oct 15, 2011, 12:00:00 AM GMT Other date: Oct 15, 2011, 1:05:15 AM GMT\nDate: Oct 6, 2011, 12:00:00 AM GMT\n"
      ));
      Template test = date.get("test");
      assertThat(test.toString(), is("Date: {v:d1}\n"));
      test.set("d2", D2).set("d1", D2);
      assertThat(test.toString(), is(
              "Date: Oct 6, 2011, 12:00:00 AM GMT Other date: Oct 6, 2011, 12:00:00 AM GMT\n"
      ));
      assertThat(date.toString(), is(
              "Date: Oct 15, 2011, 12:00:00 AM GMT Other date: Oct 15, 2011, 1:05:15 AM GMT\nDate: Oct 6, 2011, 12:00:00 AM GMT\n"
      ));
      date.set("test", D2);
      assertThat(date.toString(), is("Oct 6, 2011, 12:00:00 AM GMT"));

      date = date.get();
      assertThat(date.toString(), is(""));
      date.get("test").set("d1", D1).set("d2", D1_TIME).render();
      date.get("test").set("d1", D2).render();
      assertThat(date.toString(), is(
              "Date: Oct 15, 2011, 12:00:00 AM GMT Other date: Oct 15, 2011, 1:05:15 AM GMT\nDate: Oct 6, 2011, 12:00:00 AM GMT\n"
      ));
    }

    @Test
    void dateToCond() {
      TemplateContext ctx = XML_ALIKE.context().attrib("date", "short_medium");
      Template date = ctx.parse("<t:>Date: {v:d1}<t:test> Other date: {v:d2}</t:test></t:>");
      Template test1 = date.get("test");
      test1.set("d2", D1_TIME).render();
      date.set("d1", D1);
      assertThat(date.toString(), is("Date: 10/15/11, 12:00:00 AM Other date: 10/15/11, 1:05:15 AM"));
      date.set("test", D2);
      assertThat(date.toString(), is("Date: 10/15/11, 12:00:00 AM10/6/11, 12:00:00 AM"));

      Template test2 = date.get("test");
      assertThat(test2.toString(), is(" Other date: {v:d2}"));
      test2.set("d2", D2);
      assertThat(test1.toString(), is(" Other date: 10/15/11, 1:05:15 AM"));
      assertThat(test2.toString(), is(" Other date: 10/6/11, 12:00:00 AM"));

      date = date.get();
      assertThat(date.toString(), is(""));
      date.set("d1", D2).get("test").set("d2", D2).render();
      date.set("d1", D1);
      assertThat(date.toString(), is("Date: 10/15/11, 12:00:00 AM Other date: 10/6/11, 12:00:00 AM"));
    }

    @Test
    void dateToCondAndRegion() {
      TemplateContext ctx = XML_ALIKE.context().attrib("date", "short_medium");
      Template date = ctx.parse(
              "<t:test><t:>Date: {v:d1}<t:test><t:> Other date: {v:d2}</t:></t:test></t:></t:test>"
      ).get("test");
      Template test1 = date.get("test");
      test1.set("d2", D1_TIME).render();
      date.set("d1", D1);
      assertThat(date.toString(), is("Date: 10/15/11, 12:00:00 AM Other date: 10/15/11, 1:05:15 AM"));
      date.set("test", D2);
      assertThat(date.toString(), is("Date: 10/15/11, 12:00:00 AM10/6/11, 12:00:00 AM"));

      Template test2 = date.get("test");
      assertThat(test2.toString(), is(""));
      test2.set("d2", D2);
      assertThat(test1.toString(), is(" Other date: 10/15/11, 1:05:15 AM"));
      assertThat(test2.toString(), is(" Other date: 10/6/11, 12:00:00 AM"));

      date = date.get();
      assertThat(date.toString(), is(""));
      date.set("d1", D2).get("test").set("d2", D2).render();
      date.set("d1", D1);
      assertThat(date.toString(), is("Date: 10/15/11, 12:00:00 AM Other date: 10/6/11, 12:00:00 AM"));
    }
  }

  @Nested
  class FromRCond {
    @Test
    void simple() {
      Template date = XML_ALIKE.parse("before<t: date='sql'>->{v:test}<-</t:>after");
      assertThat(date.toString(), is("beforeafter"));
      date.set("test", D1);
      assertThat(date.toString(), is("before->2011-10-15<-after"));

      date = date.get();
      assertThat(date.toString(), is("beforeafter"));
      date.set("test", D1);
      assertThat(date.toString(), is("before->2011-10-15<-after"));
    }

    @Test
    void nestedCond() {
      Template date = XML_ALIKE.parse("before<t: date='sql'>-><t:>{v:test}</t:><-</t:>after");
      assertThat(date.toString(), is("beforeafter"));
      date.set("test", D1);
      assertThat(date.toString(), is("before->2011-10-15<-after"));

      date = date.get();
      assertThat(date.toString(), is("beforeafter"));
      date.set("test", D1);
      assertThat(date.toString(), is("before->2011-10-15<-after"));
    }

    @Test
    void nestedRegion() {
      Template date = XML_ALIKE.parse("before<t: date='sql'>-><t:v1>{v:test}</t:v1><-</t:>after");
      assertThat(date.toString(), is("beforeafter"));
      date.get("v1").set("test", D1).render();
      assertThat(date.toString(), is("before->2011-10-15<-after"));

      Template v1 = date.get("v1");
      assertThat(v1.toString(), is("{v:test}"));
      v1.append("test", D2);
      assertThat(v1.toString(), is("2011-10-06"));

      date = date.get();
      assertThat(date.toString(), is("beforeafter"));
      date.get("v1").set("test", D1).render();
      assertThat(date.toString(), is("before->2011-10-15<-after"));
    }
  }

  @Nested
  class FromRegion {
    @Test
    void toRegion() {
      Template date = XML_ALIKE.parse(
              "<t:test date='short'>Date 1: {v:d1}<t:test date='short'> Date 2: {v:d1}</t:test></t:test>"
      ).get("test");
      date.set("d1", D1);
      date.get("test").set("d1", D2).render();
      assertThat(date.toString(), is("Date 1: 10/15/11 Date 2: 10/6/11"));

    }

    @Test
    void toCond() {
      Template date = XML_ALIKE.parse(
              "<t:test date=\"short_full\">Date 1: {v:d1 date='sql'}<t:> Date 2: {v:d2} </t:></t:test>",
              Locale.GERMAN
      ).get("test");
      date.set("d1", D1);
      date.set("d2", D2);
      assertThat(date.toString(), is("Date 1: 2011-10-15 Date 2: 06.10.11, 00:00:00 Mittlere Greenwich-Zeit "));
    }

    @Test
    void nestedRegion() {
      Template date = XML_ALIKE
              .read("<t:test date='_medium'><t:>Date 1: {v:d1}</t:><t:test><t:> Date 2: {v:d2} </t:></t:test></t:test>")
              .locale(Locale.GERMAN)
              .attrib("date", "long_short")
              .parse()
              .get("test");
      date.set("d1", D1_TIME);
      date.get("test").set("d2", D2).render();
      assertThat(date.toString(), is("Date 1: 01:05:15 Date 2: 00:00:00 "));
    }
  }
}
