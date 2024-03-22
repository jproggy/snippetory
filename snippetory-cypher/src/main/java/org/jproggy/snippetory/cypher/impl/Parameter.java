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

package org.jproggy.snippetory.cypher.impl;

import org.jproggy.snippetory.cypher.Cypher;
import org.jproggy.snippetory.engine.Location;
import org.jproggy.snippetory.engine.MetaDescriptor;
import org.jproggy.snippetory.spi.EncodedData;
import org.jproggy.snippetory.spi.Format;
import org.jproggy.snippetory.util.TemplateWrapper;

import org.neo4j.driver.Value;
import org.neo4j.driver.internal.AsValue;
import org.neo4j.driver.types.IsoDuration;
import org.neo4j.driver.types.Point;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Parameter extends Location implements StatementBinder {
  private Map<String, Object> values = new HashMap<>();
  private List<StatementBinder> binders = new ArrayList<>();
  private static final AtomicInteger counter = new AtomicInteger();

  public Parameter(Location parent, MetaDescriptor metadata) {
    super(parent, metadata);
  }

    public static void reset() {
      counter.set(0);
    }

    @Override
  public Location cleanCopy(Location parent) {
     return new Parameter(parent, md);
  }

  @Override
  public void set(String name, Object value) {
    value = handleValue(name, value);
    super.set(name, value);
  }

  @Override
  public void append(String name, Object value) {
    value = handleValue(name, value);
    super.append(name, value);
  }

  protected Object handleValue(String name, Object value) {
    if (!mine(name)) return value;
    // binding null to statement requires knowledge of sql type.
    // As that's not available just use literal instead
    // stmt.getParameterMetaData().getParameterType(offset) failed on first try
    if (value == null) return Cypher.markAsCypher("NULL");
    if (!(value instanceof EncodedData)) {
      String varName = name + '_' + counter.incrementAndGet();
      values.put(varName, format(this, value));
      value = Cypher.markAsCypher('$' + varName);
    } else if (value instanceof StatementBinder) {
      binders.add((StatementBinder) value);
    } else if (value instanceof TemplateWrapper) {
      value = ((TemplateWrapper)value).getImplementation();
      if (value instanceof StatementBinder) {
        binders.add((StatementBinder) value);
      }
    }
    return value;
  }

  private Object format(Location node, Object value) {
    if (isSupported(value)) return value;
    for (Format f : getFormats()) {
      if (matches(node, f) && f.supports(value)) {
        value = f.format(node, value);
        if (isSupported(value)) return value;
      }
    }
    if (getParent() != null) return ((Parameter)getParent()).format(node, value);
    return value;
  }

  private boolean isSupported(Object value) {
    if ( value == null ) { return true; }

    if ( value instanceof AsValue) { return true; }
    if ( value instanceof Value) { return true; }
    if ( value instanceof Boolean ) { return true; }
    if ( value instanceof String ) { return true; }
    if ( value instanceof Character ) { return true; }
    if ( value instanceof Long ) { return true; }
    if ( value instanceof Short ) { return true; }
    if ( value instanceof Byte ) { return true; }
    if ( value instanceof Integer ) { return true; }
    if ( value instanceof Double ) { return true; }
    if ( value instanceof Float ) { return true; }
    if ( value instanceof LocalDate) { return true; }
    if ( value instanceof OffsetTime) { return true; }
    if ( value instanceof LocalTime ) { return true; }
    if ( value instanceof LocalDateTime) { return true; }
    if ( value instanceof OffsetDateTime) { return true; }
    if ( value instanceof ZonedDateTime) { return true; }
    if ( value instanceof IsoDuration) { return true; }
    if ( value instanceof Period) { return true; }
    if ( value instanceof Duration) { return true; }
    if ( value instanceof Point) { return true; }

    if ( value instanceof List<?> ) { return true; }
    if ( value instanceof Map<?,?> ) { return true; }
    if ( value instanceof Iterable<?> ) { return true; }
    if ( value instanceof Iterator<?>) { return true; }
    if ( value instanceof Stream<?>) { return true; }

    if ( value instanceof byte[] ) { return true; }
    if ( value instanceof boolean[] ) { return true; }
    if ( value instanceof String[] ) { return true; }
    if ( value instanceof long[] ) { return true; }
    if ( value instanceof int[] ) { return true; }
    if ( value instanceof double[] ) { return true; }
    if ( value instanceof float[] ) { return true; }
    if ( value instanceof Value[] ) { return true; }
    if ( value instanceof Object[] ) { return true; }
    return false;
  }

  @Override
  public void bindTo(Map<String, Object> params) {
    params.putAll(values);
    binders.forEach(b -> b.bindTo(params));
  }
}
