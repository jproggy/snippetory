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

package org.jproggy.snippetory.sql;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.jproggy.snippetory.Repo;
import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.TemplateContext;

class SqlSyntaxTest {
  private static final TemplateContext SYNTAX = new TemplateContext().syntax(SQL.SYNTAX);
  @Test
  void basic() {
    
    Template t = SYNTAX.parse("$test");
    assertEquals(" i++; ", t.set("test", " i++; ").toString());

    t = SYNTAX.parse("$test$()");
    assertEquals(" i++; ()", t.set("test", " i++; ").toString());

    t = SYNTAX.parse("$test${");
    assertEquals(" i++; {", t.set("test", " i++; ").toString());

    t = SYNTAX.parse("$test$(){");
    assertEquals(" i++; (){", t.set("test", " i++; ").toString());

    t = SYNTAX.parse("$test$test$");
    assertEquals(" i++; test$", t.set("test", " i++; ").toString());

    t = SYNTAX.parse("$test$$test");
    assertEquals(" i++;  i++; ", t.set("test", " i++; ").toString());

    t = SYNTAX.parse("$test$$test()$");
    assertEquals(" i++; $test()$", t.set("test", " i++; ").toString());

    t = SYNTAX.parse("$test()$test$");
    assertEquals("$test() i++; ", t.set("test", " i++; ").toString());

    t = SYNTAX.parse("$test$$test$");
    assertEquals(" i++;  i++; ", t.set("test", " i++; ").toString());

    t = SYNTAX.parse("$test$$test{}$");
    assertEquals(" i++;  i++; ", t.set("test", " i++; ").toString());

    t = SYNTAX.parse("$test{}$$test");
    assertEquals(" i++;  i++; ", t.set("test", " i++; ").toString());

    t = SYNTAX.parse("$test{ i++; }test$");
    assertEquals(" i++; ", t.get("test").toString());

    t = SYNTAX.parse("$test{ i++; }$");
    assertEquals(" i++; ", t.get("test").toString());

    t = SYNTAX.parse("${ $test i++; }$");
    assertEquals("", t.toString());
    t.set("test", null);
    assertEquals("", t.toString());
    t.set("test", "");
    assertEquals("  i++; ", t.toString());
    t.set("test", "blub");
    assertEquals(" blub i++; ", t.toString());

    t = SYNTAX.parse("$test$bla");
    t.set("test", "xy");
    assertEquals("xybla", t.toString());
  }

  @Test
  void sql() {
    Template t = SYNTAX.parse(":test");
    assertEquals(" i++; ", t.set("test", " i++; ").toString());

    t = SYNTAX.parse("-- $test{\n i++; \n-- }test$");
    assertEquals(" i++; \n", t.get("test").toString());

    t = SYNTAX.parse(":test/*default=' xx '*/");
    assertEquals(" xx ", t.toString());
    assertThat(t.names(), contains("test"));
    assertThat(t.regionNames(), is(empty()));
  }

  @Test
  void cc() {
    Template t = SYNTAX.parse("/* $test */");
    assertEquals(" i++; ", t.set("test", " i++; ").toString());

    t = SYNTAX.parse("/*$test{*/ i++; /* }test$ */");
    assertEquals(" i++; ", t.get("test").toString());

    t = SYNTAX.parse("/* $test{ */ i++; /*}$*/");
    assertEquals(" i++; ", t.get("test").toString());

    t = SYNTAX.parse("/* $test{ */ \n i++; \n/*}$*/");
    assertEquals(" i++; \n", t.get("test").toString());

    t = SYNTAX.parse("/*${ $test i++; }$*/");
    assertEquals("", t.toString());
    t.set("test", "");
    assertEquals("  i++; ", t.toString());
    t.set("test", "blub");
    assertEquals(" blub i++; ", t.toString());

    t = SYNTAX.parse("/*\t$test\t*/bla");
    t.set("test", "xy");
    assertEquals("xybla", t.toString());

    t = SYNTAX.parse("/*\t$test(*/test/*)*/bla");
    t.set("test", "xy");
    assertEquals("xybla", t.toString());

    t = SYNTAX.parse("/*\t$test(pad='5'\t*/test/*)*/bla");
    t.set("test", "xy");
    assertEquals("xy   bla", t.toString());

    t = SYNTAX.parse("/*\t$test$\t*/bla");
    t.set("test", "xy");
    assertEquals("xybla", t.toString());
  }

  @Test
  void attribs() {
    Template t = SYNTAX.parse("$test(enc='url')");
    t.set("test", "i++");
    assertEquals("i%2B%2B", t.toString());

    t = SYNTAX.parse("$test( enc='url'){ i++; }test$");
    t.get("test").render();
    assertEquals(" i++; ", t.toString());
    t.set("test", "i++");
    assertEquals("i%2B%2B", t.toString());

    t = SYNTAX.parse("$test(case='camelizeUpper' ){test-test}$");
    t.get("test").render();
    assertEquals("TestTest", t.toString());

    t = SYNTAX.parse("$(\tpad=\"10\" pad.fill=\"->\"\t){$test{ i++; }$}$");
    assertEquals("", t.toString());
    t.get("test").render();
    assertEquals(" i++; ->->", t.toString());
  }

  @Test
  void comments() {
    Template t1 = SYNTAX.parse("/// comment on start  \n  $test{  \n i++; \n   ///another comment  \n   }test$  \n");
    assertEquals("", t1.toString());
    t1.append("test", t1.get("test"));
    assertEquals(" i++; \n", t1.toString());

    t1 = SYNTAX.parse("$test{\n i++; \n}test$\n  /// comment at the end");
    t1.append("test", t1.get("test"));
    assertEquals(" i++; \n", t1.toString());

    t1 = SYNTAX.parse("\t /// comment after tab \r $test{  \n i++; \n   }test$\n");
    t1.append("test", t1.get("test"));
    assertEquals(" i++; \n", t1.toString());
  }

  @Test
  void lineRemoval() {
    Template t = SYNTAX.parse("  $test{  \n i++; \n   }test$  \n");
    t.append("test", t.get("test"));
    assertEquals(" i++; \n", t.toString());
    t = SYNTAX.parse("$test{\n i++; \n}test$");
    t.append("test", t.get("test"));
    assertEquals(" i++; \n", t.toString());
    t = SYNTAX.parse("$test{  \n i++; \n   }test$\n");
    t.append("test", t.get("test"));
    assertEquals(" i++; \n", t.toString());
    t = SYNTAX.parse("  $test{\n i++; \n}test$  ");
    t.append("test", t.get("test"));
    assertEquals(" i++; \n", t.toString());
    t = SYNTAX.parse("  $test(crop='4' crop.mark='weg'){  \n i++; \n   }test$  \n");
    t.get("test").render();
    t.get("test").render();
    assertEquals(" weg weg", t.toString());
    t = SYNTAX.parse("$test(crop=\"15\" crop.mark='...'){$test(pad='8')\n }test$\n");
    t.get("test").append("test", "12345").append("test", "123").render();
    t.get("test").set("test", "test").render();
    assertEquals("12345   123 ...test    \n", t.toString());
  }

  @Test
  void lineRemovalCond() {
    Template t = SYNTAX.parse("  ${  \n $test \n   }$  \n");
    t.append("test", "i++;");
    assertEquals(" i++; \n", t.toString());
    t = SYNTAX.parse("${\n $test \n}$");
    t.set("test", "i++;");
    assertEquals(" i++; \n", t.toString());
    t = SYNTAX.parse("${  \n $test \n   }$\n");
    t.append("test", "i++;");
    assertEquals(" i++; \n", t.toString());
    t = SYNTAX.parse("  ${\n $test \n}$  ");
    t.append("test", "i++;");
    assertEquals(" i++; \n", t.toString());
    t = SYNTAX.parse("  $(pad='14' pad.fill='_'){  \n $test \n   }$  \n");
    t.append("test", " i++;");
    t.append("test", " i++;");
    assertEquals("  i++; i++; \n_", t.toString());
    t = SYNTAX.parse("$(crop=\"15\" crop.mark='...'){$test(pad='8')\n }$\n");
    t.append("test", "12345").append("test", "123");
    assertEquals("12345   123 ...", t.toString());
  }

  @Test
  void delimiter() {
    Template t = SYNTAX.parse("in ($test(delimiter=', '))");
    t.append("test", 5);
    assertEquals("in (5)", t.toString());
    t.append("test", 8);
    assertEquals("in (5, 8)", t.toString());
    t.append("test", 5);
    assertEquals("in (5, 8, 5)", t.toString());
    t = SYNTAX.parse("\"$test(delimiter='\",\"')\"");
    t.append("test", 5);
    assertEquals("\"5\"", t.toString());
    t.append("test", "hallo");
    assertEquals("\"5\",\"hallo\"", t.toString());
  }

  @Test
  void childTemplates() {
    Template t = SYNTAX.parse("in$test{ and out}test$ and around");
    assertEquals("in and around", t.toString());
    t.append("test", t.get("test"));
    assertEquals("in and out and around", t.toString());
    t.append("test", t.get("test"));
    assertEquals("in and out and out and around", t.toString());
    t.clear();
    assertEquals("in and around", t.toString());
    t = SYNTAX.parse("$outer{in$test{ and $test}test$ and around}outer$").get("outer");
    t.get("test").append("test", "hallo").render();
    assertEquals("in and hallo and around", t.toString());
  }

  @Test
  void conditionalRegionsSimple() {
    Template t = SYNTAX.parse("before$t1{${->${$test}$<-}$}t1$after");
    t.get("t1").render();
    assertEquals("beforeafter", t.toString());
    t.get("t1").set("test", "blub").render();
    assertEquals("before->blub<-after", t.toString());
    t = SYNTAX.parse("before$(default='-'){->$test<-}$after");
    assertEquals("before-after", t.toString());
    t.set("test", "blub");
    assertEquals("before->blub<-after", t.toString());
    t = SYNTAX.parse("before$(number='000'){->${$test(number='000')}$<-}$after");
    assertEquals("beforeafter", t.toString());
    t.set("test", 5);
    assertEquals("before->005<-after", t.toString());
    t = SYNTAX.parse("$test(number='000'){before${->${$test$}$<-}$after}$");
    assertEquals("", t.toString());
    t.get("test").append("test", 5).render();
    assertEquals("before->005<-after", t.toString());
    t = SYNTAX.parse("before${->$test(null='null' delimiter=' ')<-}$after");
    assertEquals("beforeafter", t.toString());
    t.set("test", "blub");
    assertEquals("before->blub<-after", t.toString());
  }

  @Test
  void conditionalRegions() {
    Template t = Repo.read("before$(default='nothing'){$test{ start$(pad='10'){$tust$middle}$$test$end }$}$after")
            .syntax(SQL.SYNTAX).parse();
    assertEquals("beforenothingafter", t.toString());
    Template test = t.get("test");
    assertEquals(" start$test$end ", test.toString());
    test.set("test", "<value>");
    assertEquals(" start<value>end ", test.toString());
    test.render();
    assertEquals("before start<value>end after", t.toString());
    test.clear();
    assertEquals(" start$test$end ", test.toString());
    assertEquals("before start<value>end after", t.toString());
    test.set("test", "xxx");
    assertEquals(" startxxxend ", test.toString());
    Template test2 = t.get("test");
    assertEquals(" start$test$end ", test2.toString());
    test2.set("tust", "222");
    assertEquals(" start222middle $test$end ", test2.toString());
    assertEquals(" startxxxend ", test.toString());
    test2.append("tust", "s");
    assertEquals(" start222smiddle$test$end ", test2.toString());
    assertEquals(" startxxxend ", test.toString());
    test2.append("test", ".s.");
    assertEquals(" start222smiddle.s.end ", test2.toString());
    test2.set("tust", "s");
    assertEquals(" startsmiddle   .s.end ", test2.toString());
    test2.render();
    assertEquals("before start<value>end  startsmiddle   .s.end after", t.toString());
  }

  @Test
  void noParse() {
    Template t = SYNTAX.parse("$test(");
    t.set("test", "hallo");
    assertEquals("$test(", t.toString());
    assertTrue(t.names().isEmpty());

    t = SYNTAX.parse("$test$(");
    t.set("test", "hallo");
    assertEquals("hallo(", t.toString());
    assertEquals(1, t.names().size());

    t = SYNTAX.parse("$test$(){");
    t.set("test", "hallo");
    assertEquals("hallo(){", t.toString());
    assertEquals(1, t.names().size());

    t = SYNTAX.parse("$test(crop)");
    t.set("test", "hallo");
    assertEquals("$test(crop)", t.toString());
    assertTrue(t.names().isEmpty());

    t = SYNTAX.parse("$test(crop='c)");
    t.set("test", "hallo");
    assertEquals("$test(crop='c)", t.toString());
    assertTrue(t.names().isEmpty());

    t = SYNTAX.parse("$test(*/ test /)");
    t.set("test", "hallo");
    assertEquals("$test(*/ test /)", t.toString());
    assertTrue(t.names().isEmpty());

    t = SYNTAX.parse("$test(* test /*)");
    t.set("test", "hallo");
    assertEquals("$test(* test /*)", t.toString());
    assertTrue(t.names().isEmpty());

    t = SYNTAX.parse("$test(crop='2'*/ test /)");
    t.set("test", "hallo");
    assertEquals("$test(crop='2'*/ test /)", t.toString());
    assertTrue(t.names().isEmpty());

    t = SYNTAX.parse("$test(pad=\"5\" * test /*)");
    t.set("test", "hallo");
    assertEquals("$test(pad=\"5\" * test /*)", t.toString());
    assertTrue(t.names().isEmpty());
  }
}
