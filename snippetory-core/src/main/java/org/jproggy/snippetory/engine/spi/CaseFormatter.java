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
import org.jproggy.snippetory.util.CharDataSupport;
import org.jproggy.snippetory.spi.FormatFactory;
import org.jproggy.snippetory.util.SimpleFormat;
import org.jproggy.snippetory.util.TemplateNode;

import java.util.Locale;
import java.util.regex.Pattern;

public class CaseFormatter implements FormatFactory {
  private static final Pattern SPLITTER = Pattern.compile("[-_]");

  @Override
  public StringFormat create(String definition, TemplateContext ctx) {
    if ("upper".equals(definition)) return new Upper(ctx.getLocale());
    if ("lower".equals(definition)) return new Lower(ctx.getLocale());
    if ("firstUpper".equals(definition)) return new FirstUpper(ctx.getLocale());
    if ("camelizeUpper".equals(definition)) return new Camelize(false, ctx.getLocale());
    if ("camelizeLower".equals(definition)) return new Camelize(true, ctx.getLocale());
    throw new IllegalArgumentException("definition " + definition + " unknown.");
  }

  public abstract static class StringFormat extends SimpleFormat {
    @Override
    public boolean supports(Object value) {
      return CharDataSupport.isCharData(value);
    }
  }

  public static class Upper extends StringFormat {
    private final Locale locale;

    private Upper(Locale locale) {
      this.locale = locale;
    }

    @Override
    public Object format(TemplateNode location, Object value) {
      return value.toString().toUpperCase(locale);
    }
  }

  public static class Lower extends StringFormat {
    private final Locale locale;

    private Lower(Locale locale) {
      this.locale = locale;
    }

    @Override
    public Object format(TemplateNode location, Object value) {
      return value.toString().toLowerCase(locale);
    }
  }

  public static class FirstUpper extends StringFormat {
    private final Locale locale;

    private FirstUpper(Locale locale) {
      this.locale = locale;
    }

    @Override
    public Object format(TemplateNode location, Object value) {
      String s = value.toString();
      return s.substring(0, 1).toUpperCase(locale) + s.substring(1);
    }
  }

  public static class Camelize extends StringFormat {
    private final boolean lower;
    private final Locale locale;

    private Camelize(boolean lower, Locale locale) {
      this.lower = lower;
      this.locale = locale;
    }

    @Override
    public Object format(TemplateNode location, Object value) {
      StringBuilder result = new StringBuilder();
      for (String val : SPLITTER.split(value.toString())) {
        if (val.length() == 0) continue;
        if (result.length() == 0 && lower) {
          result.append(val.substring(0, 1).toLowerCase(locale));
        } else {
          result.append(val.substring(0, 1).toUpperCase(locale));
        }
        if (val.length() > 1) result.append(val.substring(1).toLowerCase(locale));
      }
      return result;
    }
  }
}
