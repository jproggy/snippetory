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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Locale;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.spi.FormatFactory;
import org.jproggy.snippetory.spi.SimpleFormat;
import org.jproggy.snippetory.spi.TemplateNode;

public class NumFormatter implements FormatFactory {
  private final SupportedTypes types;

  protected NumFormatter(Class<?>... types) {
    this.types = new SupportedTypes(types);
  }

  public NumFormatter() {
    types = null;
  }

  @Override
  public SimpleFormat create(String definition, TemplateContext ctx) {
    if (("".equals(definition) && isTechLocale(ctx)) || "tostring".equalsIgnoreCase(definition)) {
      return new ToStringFormat();
    }
    if (types == null) {
      return new DecimalFormatWrapper(definition, ctx.getLocale());
    } else {
      return new TypedDecimalFormatWrapper(definition, ctx.getLocale(), types);
    }

  }

  private boolean isTechLocale(TemplateContext ctx) {
    return ctx.getLocale().equals(TemplateContext.TECH);
  }

  public static class ToStringFormat extends SimpleFormat {

    public ToStringFormat() {}

    @Override
    public Object format(TemplateNode location, Object value) {
      return value.toString();
    }

    @Override
    public boolean supports(Object value) {
      return value instanceof Number;
    }
  }

  public static class DecimalFormatWrapper extends SimpleFormat {
    private final NumberFormat impl;

    public DecimalFormatWrapper(String definition, Locale l) {
      impl = toFormat(definition, l);
    }

    @Override
    public Object format(TemplateNode location, Object value) {
      synchronized (impl) {
        return impl.format(value, new StringBuffer(), new FieldPosition(0));
      }
    }

    @Override
    public boolean supports(Object value) {
      return value instanceof Number;
    }

    private static NumberFormat toFormat(String definition, Locale l) {
      if ("".equals(definition)) return NumberFormat.getNumberInstance(l);
      if ("currency".equals(definition)) return NumberFormat.getCurrencyInstance(l);
      if ("int".equals(definition)) return NumberFormat.getIntegerInstance(l);
      if ("percent".endsWith(definition)) return NumberFormat.getPercentInstance(l);
      if ("JS".equals(definition)) return NumberFormat.getNumberInstance(Locale.US);
      return new DecimalFormat(definition, DecimalFormatSymbols.getInstance(l));
    }
  }

  public static class TypedDecimalFormatWrapper extends DecimalFormatWrapper {
    private final SupportedTypes types;

    public TypedDecimalFormatWrapper(String definition, Locale l, SupportedTypes types) {
      super(definition, l);
      this.types = types;
    }

    @Override
    public boolean supports(Object value) {
      return types.isSupported(value);
    }
  }
}
