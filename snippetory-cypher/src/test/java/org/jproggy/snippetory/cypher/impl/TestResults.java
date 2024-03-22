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

package org.jproggy.snippetory.cypher.impl;

import org.jproggy.snippetory.Repo;
import org.jproggy.snippetory.Syntaxes;
import org.jproggy.snippetory.Template;

public class TestResults {
  private static final Template data = Repo.readResource("org/jproggy/snippetory/cypher/TestResults.txt")
          .syntax(Syntaxes.FLUYT_X)
          .parse();

  public static String test1() {
    return data.get("test1").set("p1", "$value1_1").toString();
  }

  public static String test2() {
    Template template = data.get("test1").set("p1", "$value1_1");
    template.get("test2").set("p2", "$value2_2").render();
    return template.toString();
  }

  public static String test3() {
    Template template = data.get("test1").set("p1", "$value1_1");
    template.get("test2").set("p2", "$value2_2").render();
    template.get("test3").set("p3", "$value3_3").render();
    return template.toString();
  }

  public static String testHtml() {
    return data.get("html").toString();
  }
}
