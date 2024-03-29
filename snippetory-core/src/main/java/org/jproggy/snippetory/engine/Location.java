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

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.spi.EncodedData;
import org.jproggy.snippetory.spi.Format;
import org.jproggy.snippetory.spi.VoidFormat;
import org.jproggy.snippetory.util.CharDataSupport;
import org.jproggy.snippetory.util.CharSequences;
import org.jproggy.snippetory.util.TemplateNode;

public class Location implements DataSink, TemplateNode {
  protected final MetaDescriptor md;
  private StringBuilder target;
  private final Location parent;

  // caches -> are initialized lazily -> do not access directly!
  private Format[] formats = null;

  public Location(Location parent, MetaDescriptor metadata) {
    this.parent = parent;
    this.md = metadata;
  }

  @Override
  public String toString() {
    return format().toString();
  }

  @Override
  public CharSequence format() {
    if (target != null) {
      if (md.suffix != null) return target + md.suffix;
      return target;
    }

    Object value = null;
    VoidFormat v = null;
    for (Format f : getFormats()) {
      if (f instanceof VoidFormat) {
        v = (VoidFormat) f;
        value = ((VoidFormat) f).formatVoid(this);
        if (!CharDataSupport.isNull(value)) break;
      }
    }

    if (v == null) {
      return md.getFallback();
    }
    value = toCharData(this, value);
    for (Format format : getFormats()) {
      if (format != v && format.supports(value)) {
        value = format.format(this, value);
      }
    }
    String encoding = CharDataSupport.getEncoding(value);
    CharSequence data = CharDataSupport.toCharSequence(value);
    if (getEncoding().equals(encoding)) {
      return data;
    }
    return md.transcode(new StringBuilder(), data, encoding);
  }

  protected void set(Object value) {
    target = null;
    append(value);
  }

  private void append(Object value) {
    prepareTarget();
    Object formatted = format(this, toCharData(this, value));
    String sourceEncoding = getEncoding(value, formatted);
    writeToTarget(formatted, sourceEncoding);
  }

  private void prepareTarget() {
    if (target == null) {
      target = md.prefix == null ? new StringBuilder() : new StringBuilder(md.prefix);
    } else {
      if (md.delimiter != null) target.append(md.delimiter);
    }
  }

  private void writeToTarget(Object formatted, String sourceEnc) {
    if (sourceEnc.equals(getEncoding())) {
      CharSequences.append(target, formatted);
    } else {
      md.transcode(target, CharDataSupport.toCharSequence(formatted), sourceEnc);
    }
  }

  private String getEncoding(Object value, Object formatted) {
    if (formatted instanceof EncodedData) return ((EncodedData)formatted).getEncoding();
    return CharDataSupport.getEncoding(value);
  }

  @Override
  public void clear() {
    target = null;
    clearFormats(this);
  }

  private Object toCharData(Location node, Object value) {
    if (isCharData(value)) return value;
    for (Format f : getFormats()) {
      if (matches(node, f) && f.supports(value)) {
        value = f.format(node, value);
        if (isCharData(value)) return value;
      }
    }
    if (parent != null) return parent.toCharData(node, value);
    if (value == null) return "";
    return String.valueOf(value);
  }

  protected Format[] getFormats() {
    if (formats == null) {
      formats = md.getFormats(this);
    }
    return formats;
  }

  protected boolean matches(Location node, Format f) {
    if (this.equals(node)) return true;
    return !(f instanceof VoidFormat);
  }

  private boolean isCharData(Object value) {
    return CharDataSupport.isCharData(value);
  }

  private void clearFormats(Location node) {
    for (Format f : getFormats()) {
      f.clear(node);
    }
  }

  private Object format(Location node, Object value) {
    for (Format f : getFormats()) {
      if (f.supports(value)) value = f.format(node, value);
    }
    return value;
  }

  public void withVoidFormats(Consumer<VoidFormat> converter) {
    for (Format f : getFormats()) {
      if (f instanceof VoidFormat) {
        converter.accept((VoidFormat)f);
      }
    }
  }

  public String getName() {
    return md.name;
  }

  @Override
  public String getEncoding() {
    return md.enc.getName();
  }

  @Override
  public Location getParent() {
    return parent;
  }

  @Override
  public void set(String name, Object value) {
    if (mine(name)) set(value);
    withVoidFormats(v -> v.set(name, value));
  }

  @Override
  public void append(String name, Object value) {
    if (mine(name)) append(value);
    withVoidFormats(v -> v.append(name, value));
  }

  protected boolean mine(String name) {
    return name != null && name.equals(md.name);
  }

  @Override
  public Set<String> regionNames() {
    if (md.link != null && !md.controlsRegion()) return singleton(md.name);
    return emptySet();
  }

  @Override
  public Reference getChild(String name) {
    if (Objects.equals(name, md.name) && md.link != null) return md.link;
    return null;
  }

  @Override
  public Set<String> names() {
    Set<String> result = new HashSet<>();
    withVoidFormats(v -> result.addAll(v.names()));
    if (getName() != null) result.add(getName());
    return result;
  }

  @Override
  public Location cleanCopy(Location parent) {
    return new Location(parent, md);
  }

  @Override
  public MetaDescriptor metadata() {
    return md;
  }

  @Override
  public Template region() {
    if (md.link == null || !md.controlsRegion()) {
      return Template.NONE;
    }
    return md.link.resolve(null).get();
  }
}
