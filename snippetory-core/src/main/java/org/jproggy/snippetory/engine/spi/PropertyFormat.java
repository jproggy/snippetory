package org.jproggy.snippetory.engine.spi;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.chars.EncodedContainer;
import org.jproggy.snippetory.spi.SimpleFormat;
import org.jproggy.snippetory.spi.TemplateNode;
import org.jproggy.snippetory.spi.VoidFormat;

public class PropertyFormat extends SimpleFormat implements VoidFormat {
    private final String value;

    private PropertyFormat(String name) {
        String val = System.getenv(name);
        if (val == null) {
            val = System.getProperty(name);
        }
        value = val;
    }

    @Override
    public Object formatVoid(TemplateNode node) {
        if (value == null) {
            return null;
        }
        return new EncodedContainer(value, node.getEncoding());
    }
    public static PropertyFormat create(String name, TemplateContext ctx) {
        return new PropertyFormat(name);
    }
}