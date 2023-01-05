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

package org.jproggy.snippetory.engine.spi;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.jproggy.snippetory.SnippetoryException;
import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.util.CharDataSupport;
import org.jproggy.snippetory.spi.Format;
import org.jproggy.snippetory.spi.FormatConfiguration;
import org.jproggy.snippetory.util.StateContainer;
import org.jproggy.snippetory.util.TemplateNode;
import org.jproggy.snippetory.spi.VoidFormat;

public class ValuesFormat implements VoidFormat {
    private final Template region;
    private final Predicate<Set<String>> trigger;
    private final Set<String> usedNames = new HashSet<>();

    public ValuesFormat(Template region, String definition) {
        this.region = region;
        if("all".equalsIgnoreCase(definition)) {
            trigger = names -> names.size() == region.names().size();
        } else {
            try {
                int count = Integer.parseInt(definition);
                if (region.names().size() < count) {
                    throw new SnippetoryException("Region can't ever be rendered.");
                }
                trigger = names -> names.size() >= count;
            } catch (NumberFormatException e) {
                throw new SnippetoryException("Don't understand " + definition, e);
            }
        }
    }

    @Override
    public Object formatVoid(TemplateNode node) {
        return trigger.test(usedNames) ? region : null;
    }

    @Override
    public void set(String name, Object value) {
        if (region.names().contains(name)) {
            region.set(name, value);
            if (!CharDataSupport.isNull(value)) usedNames.add(name);
        }
    }

    @Override
    public void append(String name, Object value) {
        if (region.names().contains(name)) {
            region.append(name, value);
            if (!CharDataSupport.isNull(value)) usedNames.add(name);
        }
    }

    @Override
    public Set<String> names() {
        return region.names();
    }

    @Override
    public void clear(TemplateNode location) {
        usedNames.clear();
        region.clear();
    }

    public static FormatConfiguration create(String definition, TemplateContext ctx) {
        return new Config(definition);
    }

    private static class Config extends StateContainer<ValuesFormat> implements FormatConfiguration {
        private final String definition;

        public Config(String definition) {
            super(KeyResolver.up(0));
            this.definition = definition;
        }

        @Override
        public Format getFormat(TemplateNode node) {
            return get(node);
        }

        @Override
        public boolean controlsRegion() {
            return true;
        }

        @Override
        protected ValuesFormat createValue(TemplateNode key) {
            return new ValuesFormat(key.region(), definition);
        }
    }
}
