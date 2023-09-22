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
import java.util.Deque;
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
  private final NodeFactory nodes;

  public RegionBuilder(Location parent, NodeFactory nodes) {
    super();
    this.placeHolder = parent;
    this.nodes = nodes;
  }

  public void checkNameUnique(Token t) {
    if (t.getName() == null) return;
    if (children.containsKey(t.getName())) {
      throw new ParseError("Duplicate child template <" + t.getName() + ">.", t);
    }
  }

  public RegionBuilder handleBlockStart(Token t, Deque<RegionBuilder> regionStack, TemplateBuilder parser) {
    checkNameUnique(t);
    Delocation hit = handleBackward(t);
    Location blockLocation = nodes.placeHolder(this.placeHolder, t);
    RegionBuilder reg = this;
    if (t.getName() == null || blockLocation.metadata().controlsRegion()) {
      if (hit != null) {
        throw new ParseError("Backward not supported here", t);
      }
      regionStack.push(reg);
      reg = new RegionBuilder(blockLocation, nodes);
    } else {
      addPart(hit, blockLocation);
      Region template = parser.parse(blockLocation);
      reg.children.put(blockLocation.getName(), template);
    }
    return reg;
  }

  public Delocation handleBackward(Token t) {
      if (!t.getAttributes().containsKey(BACKWARD)) {
          return null;
      }
      String target = t.getAttributes().get(BACKWARD);
      t.getAttributes().remove(BACKWARD);
      for (int pos = parts.size() - 1; pos >= 0; pos--) {
        if (!(parts.get(pos) instanceof  TemplateFragment)) continue;
        TemplateFragment value = (TemplateFragment) parts.get(pos);
        Pattern pattern = Pattern.compile(target);
        Matcher m = pattern.matcher(value);
        if (!m.find()) continue;

        int group = m.groupCount();
        if (group > 1) {
          throw new ParseError("Only one match group allowed: <" + target + ">.", t);
        }

        parts.set(pos, value.start(m.start(group)));
        TemplateFragment end = value.end(m.end(group));
        Delocation hit = new Delocation(pos, end);
        String overwrite = value.subSequence(m.start(group), m.end(group));
        if (m.find()) throw new ParseError("Backward target ambiguous: <" + target + ">.", t);
        checkTail(pos, pattern, t);
        t.overwriteContent(overwrite);
        return hit;
      }
      throw new ParseError("Target not found: <" + target + ">.", t);
  }

  private void checkTail(int pos, Pattern pattern, Token t) {
    for (int i = pos - 1; i >= 0; i--) {
      DataSink part = parts.get(i);
      if (!(part instanceof TemplateFragment)) continue;
      if (pattern.matcher((TemplateFragment) part).find()) {
        throw new ParseError("Backward target ambiguous: <" + pattern + ">.", t);
      }
    }
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

  public void addPart(Delocation hit, DataSink part) {
    if (hit != null) {
      parts.add(hit.pos + 1, part);
      parts.add(hit.pos + 2, hit.end);
    } else {
      parts.add(part);
    }
  }

  public static class Delocation {
    final int pos;
    final TemplateFragment end;

    private Delocation(int pos, TemplateFragment end) {
      this.pos = pos;
      this.end = end;
    }
  }
}
