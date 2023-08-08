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
import org.jproggy.snippetory.SnippetoryException;
import org.jproggy.snippetory.engine.Attributes.Types;
import org.jproggy.snippetory.engine.spi.AliasLink;
import org.jproggy.snippetory.engine.spi.CaseFormatter;
import org.jproggy.snippetory.engine.spi.CropFormat;
import org.jproggy.snippetory.engine.spi.DateFormatter;
import org.jproggy.snippetory.engine.spi.DecimalFormatter;
import org.jproggy.snippetory.engine.spi.DefaultFormat;
import org.jproggy.snippetory.engine.spi.IntFormatter;
import org.jproggy.snippetory.engine.spi.NullFormat;
import org.jproggy.snippetory.engine.spi.NumFormatter;
import org.jproggy.snippetory.engine.spi.PadFormat;
import org.jproggy.snippetory.engine.spi.PropertyFormat;
import org.jproggy.snippetory.engine.spi.ToggleFormatter;
import org.jproggy.snippetory.engine.spi.ValuesFormat;
import org.jproggy.snippetory.spi.Configurer;
import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Format;
import org.jproggy.snippetory.spi.Link;

public class AttributesRegistry {
    public static final AttributesRegistry INSTANCE = new AttributesRegistry();
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
        Format.register("pad", PadFormat::create);
        Format.register("crop", CropFormat::create);
        Format.register("number", new NumFormatter());
        Format.register("int", new IntFormatter());
        Format.register("decimal", new DecimalFormatter());
        Format.register("date", new DateFormatter());
        Format.register("toggle", new ToggleFormatter());
        Format.register("case", new CaseFormatter());
        Format.register("default", DefaultFormat::create);
        Format.register("null", NullFormat::create);
        Format.register("property", PropertyFormat::create);
        Format.register("values", ValuesFormat::create);
        Link.register("alias", AliasLink::new);
        for (Encodings e : Encodings.values()) {
            Encoding.register(e);
        }
        for (Configurer c : ServiceLoader.load(Configurer.class)) {
            // avoid optimize this loop, as iterating is necessary to load the classes
            // i.e. to initialize the extensions
            c.getClass();
        }
    }

    public static void init() {
        // noop -> just called to ensure initializer is called.
    }
}
