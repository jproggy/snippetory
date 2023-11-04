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

import static org.jproggy.snippetory.Syntaxes.HIDDEN_BLOCKS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.jproggy.snippetory.Template;

class HiddenBloxTest {
  @Test
  void hiddenBlox() {
    Template t1 = HIDDEN_BLOCKS.parse("/*t:test*/ i++; /*!t:test*/");
    assertEquals(" i++; ", t1.get("test").toString());
    Template t2 = HIDDEN_BLOCKS.parse("<!--t:test enc='url'*/ i++; /*!t:test -->");
    assertEquals(" i++; ", t2.get("test").toString());
    Template t3 = HIDDEN_BLOCKS.parse("/*t:test --> i++; <!--!t:test*/");
    assertEquals(" i++; ", t3.get("test").toString());
    Template t4 = HIDDEN_BLOCKS.parse("<!--t:test --> i++; <!--!t:test -->");
    assertEquals(" i++; ", t4.get("test").toString());
    Template t5 = HIDDEN_BLOCKS.parse("/*t:test pad=\"10\"*/ i++; /*!t:test*/");
    t5.get("test").render();
    assertEquals(" i++;     ", t5.toString());
    Template t6 = HIDDEN_BLOCKS.parse("<!--t:test date=''*/ i++; /*!t:test -->");
    t6.get("test").render();
    assertEquals(" i++; ", t6.toString());
  }

  @Test
  void lineRemovalHB() {
    Template t1 = HIDDEN_BLOCKS.parse("  /*t:test*/  \n i++; \n   /*!t:test*/  \n");
    t1.append("test", t1.get("test"));
    assertEquals(" i++; \n", t1.toString());
    t1 = HIDDEN_BLOCKS.parse("/*t:test*/\n i++; \n/*!t:test*/\n");
    t1.append("test", t1.get("test"));
    assertEquals(" i++; \n", t1.toString());
    t1 = HIDDEN_BLOCKS.parse("/*t:test*/  \n i++; \n   /*!t:test*/\n");
    t1.append("test", t1.get("test"));
    assertEquals(" i++; \n", t1.toString());
    t1 = HIDDEN_BLOCKS.parse("  /*t:test*/\n i++; \n/*!t:test*/  ");
    t1.append("test", t1.get("test"));
    assertEquals(" i++; \n", t1.toString());
    Template t7 = HIDDEN_BLOCKS.parse("  /*t:test crop='4' crop.mark='-' -->  \n i++; \n   <!--!t:test*/  \n");
    t7.get("test").render();
    t7.get("test").render();
    assertEquals(" i+- i+-", t7.toString());
    Template t8 = HIDDEN_BLOCKS.parse("<!--t:test -->{v:test pad='8' pad.align='right'}\n <!--!t:test -->\n");
    t8.get("test").append("test", "12345").append("test", "123").render();
    t8.get("test").set("test", "test").render();
    assertEquals("   12345     123\n    test\n", t8.toString());
  }

  @Test
  void delimiter() {
    Template t1 = HIDDEN_BLOCKS.parse("in ({v:test delimiter=', '})");
    t1.append("test", 5);
    assertEquals("in (5)", t1.toString());
    t1.append("test", 8);
    assertEquals("in (5, 8)", t1.toString());
    t1.append("test", 5);
    assertEquals("in (5, 8, 5)", t1.toString());
    Template t2 = HIDDEN_BLOCKS.parse("\"{v:test delimiter='\",\"'}\"");
    t2.append("test", 5);
    assertEquals("\"5\"", t2.toString());
    t2.append("test", "hallo");
    assertEquals("\"5\",\"hallo\"", t2.toString());
  }

  @Test
  void childTemplates() {
    Template t1 = HIDDEN_BLOCKS.parse("in/*t:test*/ and out/*!t:test*/ and around");
    assertEquals("in and around", t1.toString());
    t1.append("test", t1.get("test"));
    assertEquals("in and out and around", t1.toString());
    t1.append("test", t1.get("test"));
    assertEquals("in and out and out and around", t1.toString());
    t1.clear();
    assertEquals("in and around", t1.toString());
    Template t2 = HIDDEN_BLOCKS.parse("/*t:outer*/in/*t:test*/ and {v:test}/*!t:test*/ and around/*!t:outer*/").get(
            "outer");
    t2.get("test").append("test", "hallo").render();
    assertEquals("in and hallo and around", t2.toString());
  }
}
