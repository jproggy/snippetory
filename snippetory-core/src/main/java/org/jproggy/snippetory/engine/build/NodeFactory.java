package org.jproggy.snippetory.engine.build;

import java.util.List;
import java.util.Map;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.Attributes;
import org.jproggy.snippetory.engine.ConditionalRegion;
import org.jproggy.snippetory.engine.DataSink;
import org.jproggy.snippetory.engine.DataSinks;
import org.jproggy.snippetory.engine.Location;
import org.jproggy.snippetory.engine.MetaDescriptor;
import org.jproggy.snippetory.engine.Region;
import org.jproggy.snippetory.engine.TemplateFragment;
import org.jproggy.snippetory.util.Token;

public class NodeFactory {
    protected final TemplateContext ctx;

    public NodeFactory(TemplateContext ctx) {
        this.ctx = ctx;
    }

    protected ConditionalRegion buildConditional(Location placeHolder, List<DataSink> parts,
                                                 Map<String, Region> children) {
        ConditionalRegion region = new ConditionalRegion(placeHolder, parts, children);
        placeHolder.metadata().linkConditionalRegion(region);
        return region;
    }

    protected TemplateFragment buildFragment(Token t) {
        return new TemplateFragment(t.getContent());
    }

    protected Region buildRegion(Location placeHolder, List<DataSink> parts, Map<String, Region> children) {
        Region region = new Region(new DataSinks(parts, placeHolder), children);
        placeHolder.metadata().linkRegion(region);
        return region;
    }

    protected Location location(Location parent, Token t) {
        return new Location(parent, new MetaDescriptor(t.getName(), t.getContent(), Attributes.parse(parent, t.getAttributes(),
                ctx)));
    }

    protected Location placeHolder(Location parent, Token t) {
        String fragment = t.isOverwritten() ? t.getContent() : "";
        return new Location(parent, new MetaDescriptor(t.getName(), fragment, Attributes.parse(parent, t.getAttributes(), ctx)));
    }

}
