package org.jproggy.snippetory.engine.spi;

import java.util.Objects;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.spi.FormatFactory;
import org.jproggy.snippetory.spi.SimpleFormat;
import org.jproggy.snippetory.spi.TemplateNode;
import org.jproggy.snippetory.spi.VoidFormat;

public class EnvFormatter implements FormatFactory {

    @Override
    public EnvFormat create(String name, TemplateContext ctx) {
        return new EnvFormat(name);
    }

    public static class EnvFormat extends SimpleFormat implements VoidFormat {
        private final String value;

        private EnvFormat(String name) {
            this.value = Objects.toString(System.getenv(name), "");
        }

        @Override
        public Object formatVoid(TemplateNode node) {
            return value;
        }
    }
}