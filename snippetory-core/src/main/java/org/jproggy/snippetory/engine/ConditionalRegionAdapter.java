package org.jproggy.snippetory.engine;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Set;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.spi.Link;

public class ConditionalRegionAdapter implements Template {
    private final ConditionalRegion target;
    private final Template parentNode;

    public ConditionalRegionAdapter(ConditionalRegion target, Template parentNode) {
        this.target = target;
        this.parentNode = parentNode;
    }

    @Override
    public Template get(String... path) {
        if (path.length == 0) {
            return new ConditionalRegionAdapter(target.cleanCopy(
                    target.getPlaceholder().getParent()), parentNode);
        }
        Link child = target.getChild(path[0]);
        if (child == null) return Template.NONE;
        Template childTpl = child.getContents(parentNode, path[0]);
        if (childTpl == null) return Template.NONE;
        for (int i = 1; i < path.length; i++) {
            childTpl = childTpl.get(path[i]);
            if (childTpl == null) return Template.NONE;
        }
        return childTpl;
    }

    @Override
    public Template set(String name, Object value) {
        target.set(name, value);
        return this;
    }

    @Override
    public Template append(String name, Object value) {
        target.append(name, value);
        return this;
    }

    @Override
    public Template clear() {
        target.clear();
        return this;
    }

    @Override
    public void render(Template parent, String name) {
        if (target.appendMe()) {
            parent.append(name, target.format());
        }
    }

    @Override
    public void render(Writer out) throws IOException {
        if (target.appendMe()) {
            target.appendTo(out);
        }
        out.flush();
    }

    @Override
    public void render(PrintStream out) {
        if (target.appendMe()) {
            target.appendTo(out);
        }
        out.flush();
    }

    @Override
    public Set<String> names() {
        return target.names();
    }

    @Override
    public Set<String> regionNames() {
        return target.regionNames();
    }

    @Override
    public Template getParent() {
        return parentNode;
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public String getEncoding() {
        return target.getEncoding();
    }

    @Override
    public CharSequence toCharSequence() {
        return target.toCharSequence();
    }
}