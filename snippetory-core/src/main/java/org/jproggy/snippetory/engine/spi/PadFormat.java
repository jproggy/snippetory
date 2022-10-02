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

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.chars.EncodedContainer;
import org.jproggy.snippetory.spi.CharDataSupport;
import org.jproggy.snippetory.spi.FormatConfiguration;
import org.jproggy.snippetory.spi.SimpleFormat;
import org.jproggy.snippetory.spi.TemplateNode;

public class PadFormat extends SimpleFormat {

  public enum Alignment {
    left, right
  }

  private final int length;
  private Alignment align = Alignment.left;
  private String fill = "                       ";

  public PadFormat(int width) {
    super();
    this.length = width;
  }

  public void setAlign(Alignment val) {
    if (val == null) throw new NullPointerException();
    align = val;
  }

  public void setFill(String fill) {
    this.fill = fill;
  }

  @Override
  public Object format(TemplateNode location, Object value) {
    if (CharDataSupport.length(value) >= length) {
      return value;
    }
    String encoding = CharDataSupport.getEncoding(value);
    String v = value.toString();
    String b = fill(length - v.length());
    return new EncodedContainer((align == Alignment.right) ? (b + v) : (v + b), encoding);
  }

  private String fill(int i) {
    while (fill.length() < i) {
      fill += fill;
    }
    return fill.substring(0, i);
  }

  @Override
  public boolean supports(Object value) {
    return CharDataSupport.isCharData(value);
  }

  public static FormatConfiguration create(String definition, TemplateContext ctx) {
    int width = Integer.parseInt(definition);
    return new PadFormat(width);
  }
}
