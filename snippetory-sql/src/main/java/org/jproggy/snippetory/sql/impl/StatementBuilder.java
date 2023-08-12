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

package org.jproggy.snippetory.sql.impl;

import java.util.List;
import java.util.Map;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.Attributes;
import org.jproggy.snippetory.engine.ConditionalRegion;
import org.jproggy.snippetory.engine.DataSink;
import org.jproggy.snippetory.engine.Location;
import org.jproggy.snippetory.engine.MetaDescriptor;
import org.jproggy.snippetory.engine.Region;
import org.jproggy.snippetory.engine.TemplateFragment;
import org.jproggy.snippetory.engine.build.NodeFactory;
import org.jproggy.snippetory.engine.build.TemplateBuilder;
import org.jproggy.snippetory.util.Token;

public class StatementBuilder extends TemplateBuilder{
  protected StatementBuilder(TemplateContext ctx, CharSequence data) {
    super(ctx, data, new SqlNodeFactory(ctx));
  }
  public static StatementImpl parse(TemplateContext ctx, CharSequence data) {
    TemplateContext cloned = ctx.clone();
    StatementBuilder builder = new StatementBuilder(cloned, data);
    Parameter root = new Parameter(null, new MetaDescriptor(null, "", Attributes.parse(null, ctx.getBaseAttribs(), ctx)));
    return (StatementImpl) builder.parse(root);
  }

  private static class SqlNodeFactory extends NodeFactory {
    private SqlNodeFactory(TemplateContext ctx) {
      super(ctx);
    }

    @Override
    protected Location location(Location parent, Token t) {
      return new Parameter(parent, new MetaDescriptor(t.getName(), t.getContent(),
              Attributes.parse(parent, t.getAttributes(), ctx)));
    }

    @Override
    protected Location placeHolder(Location parent, Token t) {
      String fragment = t.isOverwritten() ? t.getContent() : "";
      return new Parameter(parent, new MetaDescriptor(t.getName(), fragment,
              Attributes.parse(parent, t.getAttributes(), ctx)));
    }

    @Override
    protected StatementImpl buildRegion(Location placeHolder, List<DataSink> parts, Map<String, Region> children) {
      StatementImpl statement = new StatementImpl(new SqlSinks(parts, placeHolder), children);
      placeHolder.metadata().linkRegion(statement);
      return statement;
    }

    @Override
    protected TemplateFragment buildFragment(Token t) {
      return new StatementFragment(t.getContent());
    }

    @Override
    protected ConditionalRegion
    buildConditional(Location placeHolder, List<DataSink> parts, Map<String, Region> children) {
      ConditionalSqlRegion region = new ConditionalSqlRegion(placeHolder, parts, children);
      placeHolder.metadata().linkConditionalRegion(region);
      return region;
    }
  }
}