package org.jproggy.snippetory.engine.spi;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.SnippetoryException;
import org.jproggy.snippetory.spi.Link;
import org.jproggy.snippetory.spi.LinkedWrapper;
import org.jproggy.snippetory.spi.Metadata;

public class AliasLink implements Link {
    private String file;
    private Template external;
    private final String definition;
    private final TemplateContext ctx;
    private final boolean absolute;

    public AliasLink(String definition, TemplateContext ctx) {
        absolute = definition.startsWith("/");
        if (absolute) {
            definition = definition.substring(1);
        }
        this.definition = definition;
        this.ctx = ctx;
    }

    private Template rootNode(Template org) {
        if (external != null) return external;
        if (file != null) {
            external = ctx.getTemplate(file);
            return external;
        }
        while (absolute && org.getParent() != null) {
            org = org.getParent();
        }
        return org;
    }


    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public Template getContents(Template parent, Metadata node) {
        return new LinkedWrapper(navigate(rootNode(parent), definition.split("/")), parent, node);
    }

    private Template navigate(Template node, String[] levels) {
        for (String level : levels) {
           if ("..".equals(level)) {
               node = node.getParent();
           } else if(!".".equals(level)) {
               node = node.get(level);
           }
           if (node == null) {
               throw new SnippetoryException("Can't navigate out of file via '..'. Use alias.file instead.");
           }
           if (!node.isPresent()) {
               throw  new SnippetoryException("Can't create alias for absent node: " + level);
           }
        }
        return node;
    }
}