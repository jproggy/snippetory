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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.spi.FormatFactory;
import org.jproggy.snippetory.spi.SimpleFormat;
import org.jproggy.snippetory.spi.TemplateNode;

public class DateFormatter implements FormatFactory {
  private static final Map<String, FormatStyle> LENGTHS = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

  static {
    LENGTHS.put("short", FormatStyle.SHORT);
    LENGTHS.put("medium", FormatStyle.MEDIUM);
    LENGTHS.put("long", FormatStyle.LONG);
    LENGTHS.put("full", FormatStyle.FULL);
  }

  @Override
  public DateFormatWrapper create(String definition, TemplateContext ctx) {
    return new DateFormatWrapper(toFormat(definition, ctx.getLocale()));
  }

  private DateTimeFormatter toFormat(String definition, Locale l) {
    if ("".equals(definition) && isTechLocale(l)) {
      definition = "sql_sql";
    }

    if ("".equals(definition)) return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(l);
    // sql
    if ("iso".equals(definition)) return DateTimeFormatter.ISO_DATE;
    if ("_iso".equals(definition)) return DateTimeFormatter.ISO_TIME;
    if ("iso_iso".equals(definition)) return DateTimeFormatter.ISO_DATE_TIME;

    // iso
    if ("sql".equals(definition)) return DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
    if ("_sql".equals(definition)) return DateTimeFormatter.ofPattern("HH:mm:ss", Locale.US);
    if ("sql_sql".equals(definition)) return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);

    // JS
    if ("JS".equals(definition)) return DateTimeFormatter.ofPattern("'new Date('yyyy', 'MM', 'dd')'", Locale.US);
    if ("_JS".equals(definition)) return DateTimeFormatter.ofPattern("'new Date(0, 0, 0, 'HH', 'mm', 'ss')'", Locale.US);
    if ("JS_JS".equals(definition))
      return DateTimeFormatter.ofPattern("'new Date('yyyy', 'MM', 'dd', 'HH', 'mm', 'ss')'", Locale.US);

    return evaluateLengths(definition, l);
  }

  private boolean isTechLocale(Locale l) {
    return l.equals(TemplateContext.TECH);
  }

  private DateTimeFormatter evaluateLengths(String definition, Locale l) {
    // data by length
    FormatStyle f = LENGTHS.get(definition);
    if (f != null) {
      return DateTimeFormatter.ofLocalizedDate(f).withLocale(l);
    }
    // time by length
    if (definition.charAt(0) == '_') {
      f = LENGTHS.get(definition.substring(1));
    }
    if (f != null) {
      return DateTimeFormatter.ofLocalizedTime(f).withLocale(l);
    }
    // date time by length
    String[] both = definition.split("_");
    if (both.length == 2) {
      f = LENGTHS.get(both[0]);
      FormatStyle t = LENGTHS.get(both[1]);
      if (f != null && t != null) {
        return DateTimeFormatter.ofLocalizedDateTime(f, t).withLocale(l);
      }
    }
    return DateTimeFormatter.ofPattern(definition, l);
  }

  public static class DateFormatWrapper extends SimpleFormat {
    private final DateTimeFormatter impl;

    public DateFormatWrapper(DateTimeFormatter d) {
      impl = d;
    }

    @Override
    public Object format(TemplateNode location, Object value) {
      if (value instanceof Date) {
        return format(location, Instant.ofEpochMilli(((Date) value).getTime()));
      }
      if (value instanceof Calendar) {
        return format(location, Instant.ofEpochMilli(((Calendar)value).getTime().getTime()));
      }
      if (value instanceof ChronoLocalDate) {
        return ((LocalDate) value).format(impl);
      }
      if (value instanceof ChronoLocalDateTime) {
        return ((LocalDateTime) value).format(impl);
      }
      if (value instanceof OffsetDateTime) {
        return ((OffsetDateTime) value).format(impl);
      }
      if (value instanceof OffsetTime) {
        return ((OffsetTime) value).format(impl);
      }
      if (value instanceof ChronoZonedDateTime) {
        return ((ZonedDateTime) value).format(impl);
      }
      if (value instanceof YearMonth) {
        return ((YearMonth) value).format(impl);
      }
      if (value instanceof Year) {
        return ((Year) value).format(impl);
      }
      if (value instanceof Instant) {
        return ((Instant) value).atZone(ZoneId.systemDefault()).format(impl);
      }
      throw new IllegalArgumentException("Unsupported type" + value);
    }

    @Override
    public boolean supports(Object value) {
      if (value instanceof Date) return true;
      if (value instanceof ChronoLocalDate) return true;
      if (value instanceof LocalTime) return true;
      if (value instanceof ChronoLocalDateTime) return true;
      if (value instanceof ChronoZonedDateTime) return true;
      if (value instanceof OffsetTime) return true;
      if (value instanceof OffsetDateTime) return true;
      if (value instanceof YearMonth) return true;
      if (value instanceof Year) return true;
      if (value instanceof Calendar) return true;
      return false;
    }
  }
}
