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

package org.jproggy.snippetory.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.jproggy.snippetory.Encodings;
import org.jproggy.snippetory.engine.Attributes.Types;
import org.jproggy.snippetory.engine.spi.AliasLink;
import org.jproggy.snippetory.engine.spi.CaseFormatter;
import org.jproggy.snippetory.engine.spi.CropFormatter;
import org.jproggy.snippetory.engine.spi.DateFormatter;
import org.jproggy.snippetory.engine.spi.DecimalFormatter;
import org.jproggy.snippetory.engine.spi.DefaultFormatter;
import org.jproggy.snippetory.engine.spi.EnvFormatter;
import org.jproggy.snippetory.engine.spi.IntFormatter;
import org.jproggy.snippetory.engine.spi.NullFormatter;
import org.jproggy.snippetory.engine.spi.NumFormatter;
import org.jproggy.snippetory.engine.spi.PadFormatter;
import org.jproggy.snippetory.engine.spi.ToggleFormatter;
import org.jproggy.snippetory.spi.Configurer;

public class AttributesRegistry {
    static final AttributesRegistry INSTANCE = new AttributesRegistry();
    private final Map<String, Types> attribs = new HashMap<>();

    AttributesRegistry() {
    }

    public void register(String name, Types value) {
        Types old = attribs.get(name);
        if (old != null && old != value) {
            throw new SnippetoryException("attribute " + name + " already defined otherwise.");
        }
        attribs.put(name, value);
    }

    public Types type(String name) {
        if (!attribs.containsKey(name)) return Types.UNREGISTERED;
        return attribs.get(name);
    }

    static {
        INSTANCE.register("enc", Types.ENCODING);
        INSTANCE.register("delimiter", Types.DELIMITER);
        INSTANCE.register("prefix", Types.PREFIX);
        INSTANCE.register("suffix", Types.SUFFIX);
        INSTANCE.register(Attributes.BACKWARD, Types.BACKWARD);
        FormatRegistry.INSTANCE.register("pad", new PadFormatter());
        FormatRegistry.INSTANCE.register("crop", new CropFormatter());
        FormatRegistry.INSTANCE.register("number", new NumFormatter());
        FormatRegistry.INSTANCE.register("int", new IntFormatter());
        FormatRegistry.INSTANCE.register("decimal", new DecimalFormatter());
        FormatRegistry.INSTANCE.register("date", new DateFormatter());
        FormatRegistry.INSTANCE.register("toggle", new ToggleFormatter());
        FormatRegistry.INSTANCE.register("case", new CaseFormatter());
        FormatRegistry.INSTANCE.register("default", new DefaultFormatter());
        FormatRegistry.INSTANCE.register("null", new NullFormatter());
        FormatRegistry.INSTANCE.register("env", new EnvFormatter());
        LinkRegistry.INSTANCE.register("alias", AliasLink::new);
        for (Encodings e : Encodings.values()) {
            EncodingRegistry.INSTANCE.register(e);
        }
        for (Configurer c : ServiceLoader.load(Configurer.class)) {
            // avoid optimize this loop, as iterating is necessary to load the classes
            // i.e. to initialize the extensions
            c.getClass();
        }
    }
}
