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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jproggy.snippetory.Syntaxes.FLUYT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import org.jproggy.snippetory.Encodings;
import org.jproggy.snippetory.Repo;
import org.jproggy.snippetory.Syntaxes;
import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.engine.SnippetoryException;

public class ConditionalRegionTest {

    @Test
    void simple() {
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
    void valuesTrigger() {
        Template t = FLUYT.parse("before$(values='0'){->$test<-}$after");
        assertEquals("before->$test<-after", t.toString());
        assertEquals("before->11<-after", t.set("test", 11).toString());
        assertEquals("before->$test<-after", t.clear().toString());
        t = FLUYT.parse("before$(values='1'){->$test<-}$after");
        assertEquals("beforeafter", t.toString());
        t.set("test", "blub");
        assertEquals("before->blub<-after", t.toString());
        assertEquals("beforeafter", t.clear().toString());
    }

    @Test
    void valuesNamed() {
        Template t = FLUYT.parse("before$x(values='0'){->$test<-}$after");
        assertEquals("before->$test<-after", t.toString());
        assertEquals("before->11<-after", t.set("test", 11).toString());
        assertEquals("before22after", t.set("x", 22).toString());
        assertEquals("before->$test<-after", t.clear().toString());
        t = FLUYT.parse("before$x(values='1'){->$test<-}$after");
        assertEquals("beforeafter", t.toString());
        t.set("test", "blub");
        assertEquals("before->blub<-after", t.toString());
        assertEquals("before22after", t.set("x", 22).toString());
        assertEquals("beforeafter", t.clear().toString());
    }

    @Test
    void valuesAll() {
        Template t = FLUYT.parse("before$(values='all'){->$test<-}$after");
        assertEquals("beforeafter", t.toString());
        t.set("test", Encodings.html.wrap("blub"));
        assertEquals("before->blub<-after", t.toString());
        t = FLUYT.parse("before$(values='all'){->test<-}$after");
        assertEquals("before->test<-after", t.toString());
        t = FLUYT.parse("before$(values='all'){->$test{blub}$<-}$after");
        assertEquals("beforeafter", t.toString());
        t.get("test").render();
        assertEquals("before->blub<-after", t.toString());
    }

    @Test
    void valuesError() {
        Template t = FLUYT.parse("before$(values='ball'){->$test<-}$after");
        SnippetoryException e = assertThrows(SnippetoryException.class, t::toString);
        assertThat(e.getMessage(), containsString("ball"));
        t = FLUYT.parse("before$(values='1.0'){->$test<-}$after");
        e = assertThrows(SnippetoryException.class, t::toString);
        assertThat(e.getMessage(), containsString("1.0"));
        FLUYT.parse("before$x(values='0'){->$x<-}$after");
    }

    @Test
    void conditionalRegions() {
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
}
