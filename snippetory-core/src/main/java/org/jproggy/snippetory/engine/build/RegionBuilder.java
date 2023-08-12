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
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jproggy.snippetory.SnippetoryException;
import org.jproggy.snippetory.engine.Attributes;
import org.jproggy.snippetory.engine.DataSink;
import org.jproggy.snippetory.engine.Location;
import org.jproggy.snippetory.engine.Region;
import org.jproggy.snippetory.engine.TemplateFragment;
import org.jproggy.snippetory.util.ParseError;
import org.jproggy.snippetory.util.Token;

public class RegionBuilder {
  private static final String BACKWARD = Attributes.BACKWARD;
  private static final String FORWARD = Attributes.FORWARD;

  final Location placeHolder;
  final List<DataSink> parts = new ArrayList<>();
  final Map<String, Region> children = new HashMap<>();
  final List<ForwardAction> forwardActions = new ArrayList<>();

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
      String overwrite = value.subSequence(m.start(group), m.end(group));
      if (m.find()) throw new ParseError("Backward target ambiguous: <" + target + ">.", t);
      t.overwriteContent(overwrite);
      t.getAttributes().remove(BACKWARD);
    }
    return end;
  }
  public void handleDislocations(Token t, Function<RegionBuilder, Location> loc) {
    TemplateFragment end = null;
    if (t.getAttributes().containsKey(BACKWARD)) {
      if (t.getAttributes().containsKey(FORWARD)) {
        throw new ParseError("Only one dislocation allowed. Either forward or backward.", t);
      }
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
      String overwrite = value.subSequence(m.start(group), m.end(group));
      if (m.find()) throw new ParseError("Backward target ambiguous: <" + target + ">.", t);
      t.overwriteContent(overwrite);
      t.getAttributes().remove(BACKWARD);
      addPart(loc.apply(this));
      addPart(end);
    } else if (t.getAttributes().containsKey(FORWARD)) {
      forwardActions.add(new ForwardAction(t, loc));

    } else {
      parts.add(loc.apply(this));
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

  public void addFragment(TemplateFragment part) {
    for (ForwardAction a : forwardActions) {
      part = a.process(part);
    }
    addPart(part);
  }

  public void addPart(DataSink part) {
    parts.add(part);
  }

  public void close() {
    for (ForwardAction action : forwardActions) {
      if (!action.hit) {
        String target = action.t.getAttributes().get(FORWARD);
        throw new ParseError("Target not found: <" + target + ">.", action.t);
      }
    }
  }

  private class ForwardAction {
    boolean hit;
    final Pattern target;
    final int group;
    final Token t;
    final Function<RegionBuilder, Location> loc;
    ForwardAction(Token t, Function<RegionBuilder, Location> loc) {
      String regex = t.getAttributes().get(FORWARD);
      target = Pattern.compile(regex);
      Matcher m = target.matcher("test");
      group = m.groupCount();
      if (group > 1) {
        throw new ParseError("Only one match group allowed: <" + target + ">.", t);
      }
      this.t = t;
      this.loc = loc;
    }
    TemplateFragment process(TemplateFragment portion) {
      Matcher m = target.matcher(portion);
      if (m.find()) {
        if (hit) {
          throw new SnippetoryException("Forward target ambiguous: <" + target + ">.");
        } else {
          hit = true;
          t.getAttributes().remove(FORWARD);
          t.overwriteContent(m.group(group));
          addPart(portion.start(m.start(group)));
          addPart(loc.apply(RegionBuilder.this));
          TemplateFragment end = portion.end(m.end(group));
          if (m.find()) throw new SnippetoryException("Forward target ambiguous: <" + target + ">.");
          return end;
        }
      }
      return portion;
    }
  }

}
