package org.jproggy.snippetory.sql.impl;

import java.util.List;
import java.util.Map;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.Attributes;
import org.jproggy.snippetory.engine.ConditionalRegion;
import org.jproggy.snippetory.engine.DataSink;
import org.jproggy.snippetory.engine.Location;
import org.jproggy.snippetory.engine.Metadata;
import org.jproggy.snippetory.engine.Region;
import org.jproggy.snippetory.engine.TemplateFragment;
import org.jproggy.snippetory.engine.Token;
import org.jproggy.snippetory.engine.build.TemplateBuilder;

public class StatementBuilder extends TemplateBuilder {
  private final TemplateContext ctx;
  protected StatementBuilder(TemplateContext ctx, CharSequence data) {
    super(ctx, data);
    this.ctx = ctx;
  }

  public static StatementImpl parse(TemplateContext ctx, CharSequence data) {
    StatementBuilder builder = new StatementBuilder(ctx.clone(), data);
    Parameter root = new Parameter(null, new Metadata(null, "", Attributes.parse(null, ctx.getBaseAttribs(), ctx)));
    return (StatementImpl)builder.parse(root);
  }

  @Override
  protected Location location(Location parent, Token t) {
    return new Parameter(parent, new Metadata(t.getName(), t.getContent(), Attributes.parse(parent, t.getAttributes(),
        ctx)));
  }

  @Override
  protected Location placeHolder(Location parent, Token t) {
    return new Parameter(parent, new Metadata(t.getName(), "", Attributes.parse(parent, t.getAttributes(), ctx)));
  }

  @Override
  protected StatementImpl build(Location parent, List<DataSink> parts, Map<String, Region> children) {
    return new StatementImpl(new SqlSinks(parts, parent), children);
  }

  @Override
  protected TemplateFragment buildFragment(Token t) {
    return new StatementFragment(t.getContent());
  }

  @Override
  protected ConditionalRegion
      buildConditional(Location placeHolder, List<DataSink> parts, Map<String, Region> children) {
    return new ConditionalSqlRegion(placeHolder, parts, children);
  }
}
