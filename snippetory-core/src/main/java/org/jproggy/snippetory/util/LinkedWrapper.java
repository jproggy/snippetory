package org.jproggy.snippetory.util;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.spi.Metadata;

public class LinkedWrapper extends TemplateWrapper {
    private final Template parent;
    private final Metadata node;

    public LinkedWrapper(Template linked, Template parent, Metadata node) {
        super(linked);
        this.parent = parent;
        this.node = node;
    }

    @Override
    protected Template wrap(Template toBeWrapped) {
        throw new UnsupportedOperationException("use wrap method with parent parameter instead");
    }

    protected Template wrap(Template toBeWrapped, Template parent) {
        return new LinkedWrapper(toBeWrapped, parent, toBeWrapped.metadata());
    }

    @Override
    public Template get(String... name) {
        if (name.length == 0) return wrap(wrapped.get(), parent);
        if (name.length == 1) return wrap(wrapped.get(name), this);
        return super.get(name);
    }

    @Override
    public void render() {
        render(node.getName());
    }

    @Override
    public void render(String siblingName) {
        render(getParent(), siblingName);
    }

    @Override
    public Template getParent() {
        return parent;
    }

    @Override
    public Metadata metadata() {
        return new Metadata() {
            @Override
            public String getName() {
                return node.getName();
            }

            @Override
            public Annotation annotation(String name) {
                return node.annotation(name).defaultTo(wrapped.metadata().annotation(name).get());
            }
        };
    }
}
