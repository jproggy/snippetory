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

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.util.SimpleFormat;

public class IntFormatter extends NumFormatter {

  public IntFormatter() {
    super(Long.class, Integer.class, Short.class, Byte.class, BigInteger.class, AtomicInteger.class, AtomicLong.class,
            LongAdder.class, LongAccumulator.class);
  }

  @Override
  public SimpleFormat create(String definition, TemplateContext ctx) {
    if (definition.isEmpty()) return super.create("tostring", ctx);
    return super.create(definition, ctx);
  }
}
