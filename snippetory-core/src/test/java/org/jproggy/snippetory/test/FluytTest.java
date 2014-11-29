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

import static org.jproggy.snippetory.Syntaxes.FLUYT;
import static org.jproggy.snippetory.Syntaxes.FLUYT_CC;
import static org.jproggy.snippetory.Syntaxes.FLUYT_X;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jproggy.snippetory.Repo;
import org.jproggy.snippetory.Syntaxes;
import org.jproggy.snippetory.Template;
import org.junit.Test;

public class FluytTest {
  @Test
  public void basic() {
    Template t = FLUYT.parse("$test");
    assertEquals(" i++; ", t.set("test", " i++; ").toString());

    t = FLUYT.parse("$test$test$");
    assertEquals(" i++; test$", t.set("test", " i++; ").toString());

    t = FLUYT.parse("$test$$test");
    assertEquals(" i++;  i++; ", t.set("test", " i++; ").toString());

    t = FLUYT.parse("$test$$test$");
    assertEquals(" i++;  i++; ", t.set("test", " i++; ").toString());

    t = FLUYT.parse("$test$$test{}$");
    assertEquals(" i++;  i++; ", t.set("test", " i++; ").toString());

    t = FLUYT.parse("$test{}$$test");
    assertEquals(" i++;  i++; ", t.set("test", " i++; ").toString());

    t = FLUYT.parse("$test{ i++; }test$");
    assertEquals(" i++; ", t.get("test").toString());

    t = FLUYT.parse("$test{ i++; }$");
    assertEquals(" i++; ", t.get("test").toString());

    t = FLUYT.parse("${ $test i++; }$");
    assertEquals("", t.toString());
    t.set("test", null);
    assertEquals("", t.toString());
    t.set("test", "");
    assertEquals("  i++; ", t.toString());
    t.set("test", "blub");
    assertEquals(" blub i++; ", t.toString());

    t = FLUYT.parse("$test$bla");
    t.set("test", "xy");
    assertEquals("xybla", t.toString());
  }

  @Test
  public void x() {
    Template t = FLUYT_X.parse("$test");
    assertEquals(" i++; ", t.set("test", " i++; ").toString());

    t = FLUYT_X.parse("$test{ i++; }test$");
    assertEquals(" i++; ", t.get("test").toString());

    t = FLUYT_X.parse("<t:test> &$32; </t:test>");
    assertEquals(" &$32; ", t.get("test").set("32", "i++").toString());

    t = FLUYT_X.parse("$test{ i++; }$");
    assertEquals(" i++; ", t.get("test").toString());

    t = FLUYT_X.parse("<t:test> i++; </t:>");
    assertEquals(" i++; ", t.get("test").toString());

    t = FLUYT_X.parse("<t:> $test i++; </t:>");
    assertEquals("", t.toString());
    t.set("test", "");
    assertEquals("  i++; ", t.toString());
    t.set("test", "blub");
    assertEquals(" blub i++; ", t.toString());

    t = FLUYT_X.parse("${ $test i++; }$");
    assertEquals("", t.toString());
    t.set("test", "");
    assertEquals("  i++; ", t.toString());
    t.set("test", "blub");
    assertEquals(" blub i++; ", t.toString());

    t = FLUYT_X.parse("$test$bla");
    t.set("test", "xy");
    assertEquals("xybla", t.toString());
  }

  @Test
  public void cc() {
    Template t = FLUYT_CC.parse("// $test");
    assertEquals(" i++; ", t.set("test", " i++; ").toString());

    t = FLUYT_CC.parse("/*$test{*/ i++; /* }test$ */");
    assertEquals(" i++; ", t.get("test").toString());

    t = FLUYT_CC.parse("/* $test{ */ i++; /*}$*/");
    assertEquals(" i++; ", t.get("test").toString());

    t = FLUYT_CC.parse("/* $test{ */ \n i++; \n/*}$*/");
    assertEquals(" i++; \n", t.get("test").toString());

    t = FLUYT_CC.parse("/*${ $test i++; }$*/");
    assertEquals("", t.toString());
    t.set("test", "");
    assertEquals("  i++; ", t.toString());
    t.set("test", "blub");
    assertEquals(" blub i++; ", t.toString());

    t = FLUYT_CC.parse("/*\t$test\t*/bla");
    t.set("test", "xy");
    assertEquals("xybla", t.toString());

    t = FLUYT_CC.parse("/*\t$test(*/test/*)*/bla");
    t.set("test", "xy");
    assertEquals("xybla", t.toString());

    t = FLUYT_CC.parse("/*\t$test(pad='5'\t*/test/*)*/bla");
    t.set("test", "xy");
    assertEquals("xy   bla", t.toString());

    t = FLUYT_CC.parse("/*\t$test$\t*/bla");
    t.set("test", "xy");
    assertEquals("xybla", t.toString());
  }

  @Test
  public void attribs() {
    Template t = FLUYT.parse("$test(enc='url')");
    t.set("test", "i++");
    assertEquals("i%2B%2B", t.toString());

    t = FLUYT.parse("$test( enc='url'){ i++; }test$");
    t.get("test").render();
    assertEquals(" i++; ", t.toString());
    t.set("test", "i++");
    assertEquals("i%2B%2B", t.toString());

    t = FLUYT.parse("$test(case='camelizeUpper' ){test-test}$");
    t.get("test").render();
    assertEquals("TestTest", t.toString());

    t = FLUYT.parse("$(\tpad=\"10\" pad.fill=\"->\"\t){$test{ i++; }$}$");
    assertEquals("", t.toString());
    t.get("test").render();
    assertEquals(" i++; ->->", t.toString());
  }

  @Test
  public void comments() {
    Template t1 = FLUYT.parse("/// comment on start  \n  $test{  \n i++; \n   ///another comment  \n   }test$  \n");
    assertEquals("", t1.toString());
    t1.append("test", t1.get("test"));
    assertEquals(" i++; \n", t1.toString());

    t1 = FLUYT.parse("$test{\n i++; \n}test$\n  /// comment at the end");
    t1.append("test", t1.get("test"));
    assertEquals(" i++; \n", t1.toString());

    t1 = FLUYT.parse("\t /// comment after tab \r $test{  \n i++; \n   }test$\n");
    t1.append("test", t1.get("test"));
    assertEquals(" i++; \n", t1.toString());
  }

  @Test
  public void lineRemoval() {
    Template t = FLUYT.parse("  $test{  \n i++; \n   }test$  \n");
    t.append("test", t.get("test"));
    assertEquals(" i++; \n", t.toString());
    t = FLUYT.parse("$test{\n i++; \n}test$");
    t.append("test", t.get("test"));
    assertEquals(" i++; \n", t.toString());
    t = FLUYT.parse("$test{  \n i++; \n   }test$\n");
    t.append("test", t.get("test"));
    assertEquals(" i++; \n", t.toString());
    t = FLUYT.parse("  $test{\n i++; \n}test$  ");
    t.append("test", t.get("test"));
    assertEquals(" i++; \n", t.toString());
    t = FLUYT.parse("  $test(crop='4' crop.mark='weg'){  \n i++; \n   }test$  \n");
    t.get("test").render();
    t.get("test").render();
    assertEquals(" weg weg", t.toString());
    t = FLUYT.parse("$test(crop=\"15\" crop.mark='...'){$test(pad='8')\n }test$\n");
    t.get("test").append("test", "12345").append("test", "123").render();
    t.get("test").set("test", "test").render();
    assertEquals("12345   123 ...test    \n", t.toString());
  }

  @Test
  public void lineRemovalCond() {
    Template t = FLUYT.parse("  ${  \n $test \n   }$  \n");
    t.append("test", "i++;");
    assertEquals(" i++; \n", t.toString());
    t = FLUYT.parse("${\n $test \n}$");
    t.set("test", "i++;");
    assertEquals(" i++; \n", t.toString());
    t = FLUYT.parse("${  \n $test \n   }$\n");
    t.append("test", "i++;");
    assertEquals(" i++; \n", t.toString());
    t = FLUYT.parse("  ${\n $test \n}$  ");
    t.append("test", "i++;");
    assertEquals(" i++; \n", t.toString());
    t = FLUYT.parse("  $(pad='14' pad.fill='_'){  \n $test \n   }$  \n");
    t.append("test", " i++;");
    t.append("test", " i++;");
    assertEquals("  i++; i++; \n_", t.toString());
    t = FLUYT.parse("$(crop=\"15\" crop.mark='...'){$test(pad='8')\n }$\n");
    t.append("test", "12345").append("test", "123");
    assertEquals("12345   123 ...", t.toString());
  }

  @Test
  public void delimiter() {
    Template t = FLUYT.parse("in ($test(delimiter=', '))");
    t.append("test", 5);
    assertEquals("in (5)", t.toString());
    t.append("test", 8);
    assertEquals("in (5, 8)", t.toString());
    t.append("test", 5);
    assertEquals("in (5, 8, 5)", t.toString());
    t = FLUYT.parse("\"$test(delimiter='\",\"')\"");
    t.append("test", 5);
    assertEquals("\"5\"", t.toString());
    t.append("test", "hallo");
    assertEquals("\"5\",\"hallo\"", t.toString());
  }

  @Test
  public void childTempates() {
    Template t = FLUYT.parse("in$test{ and out}test$ and around");
    assertEquals("in and around", t.toString());
    t.append("test", t.get("test"));
    assertEquals("in and out and around", t.toString());
    t.append("test", t.get("test"));
    assertEquals("in and out and out and around", t.toString());
    t.clear();
    assertEquals("in and around", t.toString());
    t = FLUYT.parse("$outer{in$test{ and $test}test$ and around}outer$").get("outer");
    t.get("test").append("test", "hallo").render();
    assertEquals("in and hallo and around", t.toString());
  }

  @Test
  public void conditionalRegionsSimple() {
    Template t = FLUYT.parse("before$t1{${->${$test}$<-}$}t1$after");
    t.get("t1").render();
    assertEquals("beforeafter", t.toString());
    t.get("t1").set("test", "blub").render();
    assertEquals("before->blub<-after", t.toString());
    t = FLUYT.parse("before$(default='-'){->$test<-}$after");
    assertEquals("before-after", t.toString());
    t.set("test", "blub");
    assertEquals("before->blub<-after", t.toString());
    t = FLUYT.parse("before$(number='000'){->${$test(number='000')}$<-}$after");
    assertEquals("beforeafter", t.toString());
    t.set("test", 5);
    assertEquals("before->005<-after", t.toString());
    t = FLUYT.parse("$test(number='000'){before${->${$test$}$<-}$after}$");
    assertEquals("", t.toString());
    t.get("test").append("test", 5).render();
    assertEquals("before->005<-after", t.toString());
    t = FLUYT.parse("before${->$test(null='null' delimiter=' ')<-}$after");
    assertEquals("beforeafter", t.toString());
    t.set("test", "blub");
    assertEquals("before->blub<-after", t.toString());
  }

  @Test
  public void conditionalRegions() {
    Template t = Repo.read("before$(default='nothing'){$test{ start$(pad='10'){$tust$middle}$$test$end }$}$after")
        .syntax(Syntaxes.FLUYT).parse();
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
  public void noParse() {
    Template t = FLUYT.parse("$test(");
    t.set("test", "hallo");
    assertEquals("$test(", t.toString());
    assertTrue(t.names().isEmpty());

    t = FLUYT.parse("$test(crop)");
    t.set("test", "hallo");
    assertEquals("$test(crop)", t.toString());
    assertTrue(t.names().isEmpty());

    t = FLUYT.parse("$test(crop='c)");
    t.set("test", "hallo");
    assertEquals("$test(crop='c)", t.toString());
    assertTrue(t.names().isEmpty());

    t = FLUYT.parse("$test(*/ test /)");
    t.set("test", "hallo");
    assertEquals("$test(*/ test /)", t.toString());
    assertTrue(t.names().isEmpty());

    t = FLUYT.parse("$test(* test /*)");
    t.set("test", "hallo");
    assertEquals("$test(* test /*)", t.toString());
    assertTrue(t.names().isEmpty());

    t = FLUYT.parse("$test(crop='2'*/ test /)");
    t.set("test", "hallo");
    assertEquals("$test(crop='2'*/ test /)", t.toString());
    assertTrue(t.names().isEmpty());

    t = FLUYT.parse("$test(pad=\"5\" * test /*)");
    t.set("test", "hallo");
    assertEquals("$test(pad=\"5\" * test /*)", t.toString());
    assertTrue(t.names().isEmpty());
  }
}
