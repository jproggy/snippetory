package org.jproggy.snippetory.spi;

import org.jproggy.snippetory.Template;

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
        return toBeWrapped;
    }

    @Override
    public void render() {
        render(node.getName());
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
