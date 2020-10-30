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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.jproggy.snippetory.Encodings;
import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.spi.AliasLink;
import org.jproggy.snippetory.engine.spi.CaseFormatter;
import org.jproggy.snippetory.engine.spi.CropFormatter;
import org.jproggy.snippetory.engine.spi.DateFormatter;
import org.jproggy.snippetory.engine.spi.DecimalFormatter;
import org.jproggy.snippetory.engine.spi.DefaultFormatter;
import org.jproggy.snippetory.engine.spi.IntFormatter;
import org.jproggy.snippetory.engine.spi.NullFormatter;
import org.jproggy.snippetory.engine.spi.NumFormatter;
import org.jproggy.snippetory.engine.spi.PadFormatter;
import org.jproggy.snippetory.engine.spi.ToggleFormatter;
import org.jproggy.snippetory.spi.Configurer;
import org.jproggy.snippetory.spi.DynamicAttributes;
import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.FormatConfiguration;
import org.jproggy.snippetory.spi.Link;

public class Attributes {
  public static final String BACKWARD = "backward";
  static final Registry REGISTRY = new Registry();

  public static Attributes parse(Location parent, Map<String, String> attribs, TemplateContext ctx) {
    Attributes result = new Attributes(parent, ctx);
    for (Map.Entry<String, String> attr : attribs.entrySet()) {
      Types type = Attributes.REGISTRY.type(attr.getKey());
      type.handle(result, attr.getKey(), attr.getValue());
    }
    return result;
  }

  Map<String, FormatConfiguration> formats = new LinkedHashMap<>();
  Encoding enc;
  Link link;
  String linkName;
  String delimiter;
  String prefix;
  String suffix;
  private final TemplateContext ctx;

  Attributes(Location parent, TemplateContext ctx) {
    enc = parent == null ? Encodings.NULL : parent.md.enc;
    this.ctx = ctx;
  }

  void unregisteredAttribute(String key, String value) {
    String[] parts = key.split("\\.");
    if (parts.length == 2) {
      subAttribute(parts[0], parts[1], value);
    } else {
      throw new SnippetoryException("Can't understand attribute " + key + "='" + value + "'");
    }

  }

  private void subAttribute(String parent, String attrib, String value) {
    Object base = formats.get(parent);
    if (base == null) {
      if (!parent.equals(linkName)) {
        throw new SnippetoryException("Missing parent " + parent + " for sub-attribute " + attrib + "='" + value + '\'');
      }
      base = link;
    }
    try {
      BeanInfo desc = Introspector.getBeanInfo(base.getClass());
      for (PropertyDescriptor prop : desc.getPropertyDescriptors()) {
        if (prop.getName().equals(attrib)) {
          setProperty(base, prop, value);
          return;
        }
      }
    } catch (IntrospectionException e) {
      throw new SnippetoryException(e);
    }
    if (base instanceof DynamicAttributes) {
      ((DynamicAttributes) base).setAttribute(attrib, value);
      return;
    }
    throw new SnippetoryException("Can't understand attribute " + parent + '.' + attrib + "='" + value + "'");
  }

  private void setProperty(Object format, PropertyDescriptor prop, String value) {
    try {
      PropertyEditor editor = toEditor(prop);
      editor.setAsText(value);
      prop.getWriteMethod().invoke(format, editor.getValue());
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new SnippetoryException(e);
    }
  }

  private PropertyEditor toEditor(PropertyDescriptor prop) throws InstantiationException, IllegalAccessException {
    Class<?> editorType = prop.getPropertyEditorClass();
    if (editorType != null) {
      try {
        return (PropertyEditor) editorType.getConstructor().newInstance();
      } catch (InvocationTargetException | NoSuchMethodException e) {
        throw new SnippetoryException(e);
      }
    }
    Class<?> type = prop.getPropertyType();
    if (Enum.class.isAssignableFrom(type)) {
      return new EnumEditor((Class<Enum<?>>)type);
    }
    return PropertyEditorManager.findEditor(type);
  }

  enum Types {
    FORMAT {
      @Override
      void handle(Attributes target, String key, String value) {
        FormatConfiguration format = FormatRegistry.INSTANCE.get(key, value, target.ctx);
        target.formats.put(key, format);
      }
    },
    LINK {
      @Override
      void handle(Attributes target, String key, String value) {
        Link link = LinkRegistry.INSTANCE.get(key, value, target.ctx);
        if (target.link != null) {
          throw new SnippetoryException("Only one link per node possible");
        }
        target.link = link;
        target.linkName = key;
      }
    },
    ENCODING {
      @Override
      void handle(Attributes target, String key, String value) {
        target.enc = EncodingRegistry.INSTANCE.get(value);
        if (target.enc == null) {
          throw new SnippetoryException("Encoding " + value + " not found.");
        }
      }
    },
    DELIMITER {
      @Override
      void handle(Attributes target, String key, String value) {
        target.delimiter = value;
      }
    },
    PREFIX {
      @Override
      void handle(Attributes target, String key, String value) {
        target.prefix = value;
      }
    },
    SUFFIX {
      @Override
      void handle(Attributes target, String key, String value) {
        target.suffix = value;
      }
    },
    BACKWARD {
      @Override
      void handle(Attributes target, String key, String value) {
        throw new SnippetoryException("Internal error: BACKWARD is not expected here");
      }
    },
    UNREGISTERED {
      @Override
      void handle(Attributes target, String key, String value) {
        target.unregisteredAttribute(key, value);
      }
    };
    abstract void handle(Attributes target, String key, String value);
  }

  public static void init() {
    // noop -> just called to ensure initializer is called.
  }

  static {
    REGISTRY.register("enc", Types.ENCODING);
    REGISTRY.register("delimiter", Types.DELIMITER);
    REGISTRY.register("prefix", Types.PREFIX);
    REGISTRY.register("suffix", Types.SUFFIX);
    REGISTRY.register(BACKWARD, Types.BACKWARD);
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

  public static class Registry {
    private final Map<String, Types> attribs = new HashMap<>();

    private Registry() {}

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
  }
}
