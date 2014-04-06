/*******************************************************************************
 * Copyright (c) 2011-2012 JProggy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * EXCEPT AS EXPRESSLY SET FORTH IN THIS AGREEMENT, THE PROGRAM IS PROVIDED ON AN
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS OF TITLE,
 * NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE
 *******************************************************************************/

package org.jproggy.snippetory.engine;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jproggy.snippetory.engine.chars.SelfAppender;
import org.jproggy.snippetory.spi.CharDataSupport;
import org.jproggy.snippetory.spi.EncodedData;
import org.jproggy.snippetory.spi.Format;
import org.jproggy.snippetory.spi.TemplateNode;
import org.jproggy.snippetory.spi.VoidFormat;

public class Location implements DataSink, TemplateNode {
  protected final Metadata md;
  private StringBuilder target;
  private final Location parent;

  // caches -> are initialized lazily -> do not access directly!
  private VoidFormat voidformat = null;
  private Format[] formats = null;

  public Location(Location parent, Metadata metadata) {
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
      if (md.suffix != null) return target.toString() + md.suffix;
      return target;
    }
    Object f = getVoidFormat().formatVoid(this);
    if (f instanceof EncodedData) {
      EncodedData data = (EncodedData)f;
      if (getEncoding().equals(data.getEncoding())) {
        return data.toCharSequence();
      }
      return md.transcode(new StringBuilder(), data.toCharSequence(), data.getEncoding());
    }
    return f.toString();
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

  private void writeToTarget(Object formated, String sourceEnc) {
    if (sourceEnc.equals(getEncoding())) {
      if (formated instanceof SelfAppender) {
        ((SelfAppender)formated).appendTo(target);
      } else {
        target.append(formated);
      }
    } else {
      md.transcode(target, CharDataSupport.toCharSequence(formated), sourceEnc);
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

  private Format[] getFormats() {
    if (formats == null) {
      formats = md.getFormats(this);
    }
    return formats;
  }

  private boolean matches(Location node, Format f) {
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
    if (getParent() != null) parent.clearFormats(node);
  }

  private Object format(Location node, Object value) {
    for (Format f : getFormats()) {
      if (f.supports(value)) value = f.format(node, value);
    }
    return value;
  }

  private VoidFormat getVoidFormat() {
    if (voidformat == null) {
      for (Format f : getFormats()) {
        if (f instanceof VoidFormat) {
          voidformat = ((VoidFormat)f);
          return voidformat;
        }
      }
      return voidformat = md;
    }
    return voidformat;
  }

  public String getName() {
    return md.name;
  }

  @Override
  public String getEncoding() {
    return md.enc.getName();
  }

  @Override
  public TemplateNode getParent() {
    return parent;
  }

  @Override
  public void set(String name, Object value) {
    if (mine(name)) set(value);
    getVoidFormat().set(name, value);
  }

  @Override
  public void append(String name, Object value) {
    if (mine(name)) append(value);
    getVoidFormat().append(name, value);
  }

  protected boolean mine(String name) {
    return name != null && name.equals(md.name);
  }

  @Override
  public Set<String> regionNames() {
    return Collections.emptySet();
  }

  @Override
  public Region getChild(String name) {
    return null;
  }

  @Override
  public Set<String> names() {
    HashSet<String> result = new HashSet<String>(getVoidFormat().names());
    if (getName() != null) result.add(getName());
    return result;
  }

  @Override
  public Location cleanCopy(Location parent) {
    return new Location(parent, md);
  }
}
