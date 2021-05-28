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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.Attributes;
import org.jproggy.snippetory.engine.ConditionalRegion;
import org.jproggy.snippetory.engine.DataSink;
import org.jproggy.snippetory.engine.DataSinks;
import org.jproggy.snippetory.engine.Location;
import org.jproggy.snippetory.engine.MetaDescriptor;
import org.jproggy.snippetory.engine.ParseError;
import org.jproggy.snippetory.engine.Region;
import org.jproggy.snippetory.engine.SnippetoryException;
import org.jproggy.snippetory.engine.TemplateFragment;
import org.jproggy.snippetory.engine.Token;
import org.jproggy.snippetory.spi.Syntax;

/** Builds a template tree from the token stream provided by the tokenizer.
 *
 * @author B. Ebertz */
public class TemplateBuilder {
  private Syntax tempSyntax;
  private Syntax.Tokenizer parser;
  private final TemplateContext ctx;

  protected TemplateBuilder(TemplateContext ctx, CharSequence data) {
    this.ctx = ctx;
    tempSyntax = ctx.getSyntax();
    parser = getSyntax().parse(data, ctx);
  }

  /** Initialize Snippetory templating platform as a whole. Includes initialization of plug-ins. */
  public static void init() {
    Attributes.init();
  }

  public static Template parse(TemplateContext ctx, CharSequence data) {
    TemplateBuilder builder = new TemplateBuilder(ctx.clone(), data);
    Location root = new Location(null, new MetaDescriptor(null, "", Attributes.parse(null, ctx.getBaseAttribs(), ctx)));
    return builder.parse(root);
  }

  protected Region parse(Location parent) {
    RegionBuilder reg = new RegionBuilder(parent);
    Deque<RegionBuilder> regionStack = new ArrayDeque<>();
    Token t = null;
    while (parser.hasNext()) {
      t = parser.next();

      try {
        switch (t.getType()) {
        case BlockStart: {
          reg.checkNameUnique(t);
          TemplateFragment end = reg.handleBackward(t);
          Location placeHolder = placeHolder(reg.placeHolder, t);
          if (t.getName() == null) {
            regionStack.push(reg);
            reg = new RegionBuilder(placeHolder);
          } else {
            reg.addPart(placeHolder);
            Region template = parse(placeHolder);
            reg.children.put(placeHolder.getName(), template);
          }
          if (end != null) reg.addPart(end);
          break;
        }
        case BlockEnd:
          if (t.getName() == null && !regionStack.isEmpty()) {
            ConditionalRegion r = buildConditional(reg.placeHolder, reg.parts, reg.children);
            if (r.names().isEmpty()) {
              throw new ParseError(
                  "Conditional region needs to contain at least one named location, or will never be rendered", t);
            }
            reg = regionStack.pop();
            reg.addPart(r);
            break;
          }
          if (!regionStack.isEmpty()) {
            throw new ParseError(regionStack.size() + " unclosed conditional regions detected", t);
          }
          reg.verifyName(t);
          return build(reg.placeHolder, reg.parts, reg.children);
        case Field:
          TemplateFragment end = reg.handleBackward(t);
          reg.addPart(location(reg.placeHolder, t));
          if (end != null) reg.addPart(end);
          break;
        case TemplateData:
          reg.addPart(buildFragment(t));
          break;
        case Syntax:
          setSyntax(Syntax.REGISTRY.byName(t.getName()));
          parser = getSyntax().takeOver(parser);
          break;
        case Comment:
          // comments are simply ignored.
          break;
        default:
          throw new SnippetoryException("Unknown token type: " + t.getType());
        }
      } catch (ParseError e) {
        throw e;
      } catch (RuntimeException e) {
        throw new ParseError(e, t);
      }
    }
    if (!regionStack.isEmpty()) {
      throw new ParseError(regionStack.size() + " unclosed conditional regions detected", t);
    }
    verifyRootNode(parent, t);
    return build(reg.placeHolder, reg.parts, reg.children);
  }

  protected ConditionalRegion
      buildConditional(Location placeHolder, List<DataSink> parts, Map<String, Region> children) {
    return new ConditionalRegion(placeHolder, parts, children);
  }

  protected TemplateFragment buildFragment(Token t) {
    return new TemplateFragment(t.getContent());
  }

  protected Region build(Location placeHolder, List<DataSink> parts, Map<String, Region> children) {
    return new Region(new DataSinks(parts, placeHolder), children);
  }

  private void verifyRootNode(Location parent, Token t) {
    if (parent.getName() != null) throw new ParseError("No end element for " + parent.getName(), t);
  }

  protected Location location(Location parent, Token t) {
    return new Location(parent, new MetaDescriptor(t.getName(), t.getContent(), Attributes.parse(parent, t.getAttributes(),
            ctx)));
  }

  protected Location placeHolder(Location parent, Token t) {
    return new Location(parent, new MetaDescriptor(t.getName(), "", Attributes.parse(parent, t.getAttributes(), ctx)));
  }

  private void setSyntax(Syntax s) {
    if (s == null) throw new NullPointerException();
    tempSyntax = s;
  }

  private Syntax getSyntax() {
    if (tempSyntax == null) return Syntax.REGISTRY.getDefault();
    return tempSyntax;
  }
}
