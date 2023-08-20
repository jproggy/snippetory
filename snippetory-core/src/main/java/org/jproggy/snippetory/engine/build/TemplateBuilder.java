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

import org.jproggy.snippetory.SnippetoryException;
import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.Attributes;
import org.jproggy.snippetory.engine.AttributesRegistry;
import org.jproggy.snippetory.engine.ConditionalRegion;
import org.jproggy.snippetory.engine.Location;
import org.jproggy.snippetory.engine.MetaDescriptor;
import org.jproggy.snippetory.engine.Region;
import org.jproggy.snippetory.engine.SyntaxRegistry;
import org.jproggy.snippetory.engine.TemplateFragment;
import org.jproggy.snippetory.spi.Syntax;
import org.jproggy.snippetory.util.ParseError;
import org.jproggy.snippetory.util.Token;

/**
 * Builds a template tree from the token stream provided by the tokenizer.
 **/
public class TemplateBuilder {
  private Syntax tempSyntax;
  private Syntax.Tokenizer parser;
  private  final NodeFactory nodes;

  protected TemplateBuilder(TemplateContext ctx, CharSequence data, NodeFactory nodes) {
    tempSyntax = ctx.getSyntax();
    parser = getSyntax().parse(data, ctx);
    this.nodes = nodes;
  }

  /** Initialize Snippetory templating platform as a whole. Includes initialization of plug-ins. */
  public static void init() {
    AttributesRegistry.init();
  }

  public static Template parse(TemplateContext ctx, CharSequence data) {
    TemplateContext cloned = ctx.clone();
    TemplateBuilder builder = new TemplateBuilder(cloned, data, new NodeFactory(cloned));
    Location root = new Location(null, new MetaDescriptor(null, "", Attributes.parse(null, cloned.getBaseAttribs(), cloned)));
    return builder.parse(root);
  }

  protected Region parse(Location parent) {
    RegionBuilder reg = new RegionBuilder(parent);
    Deque<RegionBuilder> regionStack = new ArrayDeque<>();
    Token last = null;
    while (parser.hasNext()) {
      Token t = parser.next();
      last = t;

      try {
        switch (t.getType()) {
        case BlockStart: {
          reg.checkNameUnique(t);
          TemplateFragment end = reg.handleBackward(t);
          Location placeHolder = nodes.placeHolder(reg.placeHolder, t);
          if (t.getName() == null || placeHolder.metadata().controlsRegion()) {
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
          reg.verifyName(t);
          if (!regionStack.isEmpty()) {
            ConditionalRegion r = nodes.buildConditional(reg.placeHolder, reg.parts, reg.children);
            if (r.names().isEmpty() && !reg.placeHolder.metadata().controlsRegion()) {
              throw new ParseError(
                  "Conditional region needs to contain at least one named location, or will never be rendered.", t);
            }
            reg = regionStack.pop();
            reg.addPart(r);
            break;
          }
          return nodes.buildRegion(reg.placeHolder, reg.parts, reg.children);
        case Field:
          TemplateFragment end = reg.handleBackward(t);
          reg.addPart(nodes.location(reg.placeHolder, t));
          if (end != null) reg.addPart(end);
          break;
        case TemplateData:
          reg.addPart(nodes.buildFragment(t));
          break;
        case Syntax:
          setSyntax(SyntaxRegistry.INSTANCE.byName(t.getName()));
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
      throw new ParseError(regionStack.size() + " unclosed conditional regions detected", last);
    }
    verifyRootNode(parent, last);
    return nodes.buildRegion(reg.placeHolder, reg.parts, reg.children);
  }

  private void verifyRootNode(Location parent, Token t) {
    if (parent.getName() != null) throw new ParseError("No end element for <" + parent.getName() + ">.", t);
  }

  private void setSyntax(Syntax s) {
    if (s == null) throw new NullPointerException();
    tempSyntax = s;
  }

  private Syntax getSyntax() {
    if (tempSyntax == null) return SyntaxRegistry.INSTANCE.getDefault();
    return tempSyntax;
  }
}
