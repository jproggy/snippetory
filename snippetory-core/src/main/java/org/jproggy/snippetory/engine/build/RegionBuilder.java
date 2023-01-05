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

package org.jproggy.snippetory.engine.build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jproggy.snippetory.engine.Attributes;
import org.jproggy.snippetory.engine.DataSink;
import org.jproggy.snippetory.engine.Location;
import org.jproggy.snippetory.engine.Region;
import org.jproggy.snippetory.engine.TemplateFragment;
import org.jproggy.snippetory.util.ParseError;
import org.jproggy.snippetory.util.Token;

public class RegionBuilder {
  private static final String BACKWARD = Attributes.BACKWARD;

  final Location placeHolder;
  final List<DataSink> parts = new ArrayList<>();
  final Map<String, Region> children = new HashMap<>();

  public RegionBuilder(Location parent) {
    super();
    this.placeHolder = parent;
  }

  public void checkNameUnique(Token t) {
    if (t.getName() == null) return;
    if (children.containsKey(t.getName())) {
      throw new ParseError("Duplicate child template <" + t.getName() + ">.", t);
    }
  }

  public TemplateFragment handleBackward(Token t) {
    TemplateFragment end = null;
    if (t.getAttributes().containsKey(BACKWARD)) {
      String target = t.getAttributes().get(BACKWARD);
      TemplateFragment value = (TemplateFragment) parts.get(parts.size() - 1);
      Matcher m = Pattern.compile(target).matcher(value);
      if (!m.find()) {
        throw new ParseError("Target not found: <" + target + ">.", t);
      }
      int group = m.groupCount();
      if (group > 1) {
        throw new ParseError("Only one match group allowed: <" + target + ">.", t);
      }
      parts.set(parts.size() - 1, value.start(m.start(group)));
      end = value.end(m.end(group));
      if (m.find()) throw new ParseError("Backward target ambiguous: <" + target + ">.", t);
      t.getAttributes().remove(BACKWARD);
    }
    return end;
  }

  public void verifyName(Token t) {
    if (!empty(t.getName()) && !sameName(placeHolder, t)) {
      throw new ParseError("<" + t.getName() + "> found but <" + name(placeHolder) + "> expected.", t);
    }
  }

  private String name(Location parent) {
    return parent.getParent() == null ? "file end" : parent.getName();
  }

  private boolean sameName(Location parent, Token t) {
    if (empty(parent.getName()) && empty(t.getName())) return true;
    return Objects.equals(parent.getName(), t.getName());
  }

  private boolean empty(String val) {
    return val == null || val.isEmpty();
  }

  public void addPart(DataSink part) {
    parts.add(part);
  }

}
