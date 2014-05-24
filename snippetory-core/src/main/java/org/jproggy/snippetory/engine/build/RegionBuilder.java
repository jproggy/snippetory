package org.jproggy.snippetory.engine.build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jproggy.snippetory.engine.Attributes;
import org.jproggy.snippetory.engine.DataSink;
import org.jproggy.snippetory.engine.Location;
import org.jproggy.snippetory.engine.ParseError;
import org.jproggy.snippetory.engine.Region;
import org.jproggy.snippetory.engine.TemplateFragment;
import org.jproggy.snippetory.engine.Token;

public class RegionBuilder {
  private static final String BACKWARD = Attributes.BACKWARD;

  final Location placeHolder;
  final List<DataSink> parts = new ArrayList<DataSink>();
  final Map<String, Region> children = new HashMap<String, Region>();

  public RegionBuilder(Location parent) {
    super();
    this.placeHolder = parent;
  }

  public void checkNameUnique(Token t) {
    if (t.getName() == null) return;
    if (children.containsKey(t.getName())) {
      throw new ParseError("duplicate child template " + t.getName(), t);
    }
  }

  public TemplateFragment handleBackward(Token t) {
    TemplateFragment end = null;
    if (t.getAttributes().containsKey(BACKWARD)) {
      String target = t.getAttributes().get(BACKWARD);
      TemplateFragment value = (TemplateFragment)parts.get(parts.size() - 1);
      Matcher m = Pattern.compile(target).matcher(value);
      if (m.find()) {
        int group = 0;
        if (m.groupCount() == 1) {
          group = 1;
        } else if (m.groupCount() > 1) {
          throw new ParseError("only one match group allowed: " + target, t);
        }
        parts.set(parts.size() - 1, value.start(m.start(group)));
        end = value.end(m.end(group));
        if (m.find()) throw new ParseError("backward target ambigous: " + target, t);
      } else {
        throw new ParseError("target not found: " + target, t);
      }
      t.getAttributes().remove(BACKWARD);
    }
    return end;
  }

  public void verifyName(Token t) {
    if (placeHolder.getName() == null || !(empty(t.getName()) || sameName(placeHolder, t))) {
      throw new ParseError(t.getName() + " found but " + name(placeHolder) + " expected", t);
    }
  }

  private String name(Location parent) {
    return parent.getName() == null ? "file end" : parent.getName();
  }

  private boolean sameName(Location parent, Token t) {
    return parent.getName().equals(t.getName());
  }

  private boolean empty(String val) {
    return val == null || val.isEmpty();
  }

  public void addPart(DataSink part) {
    parts.add(part);
  }

}
