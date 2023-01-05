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
import org.jproggy.snippetory.util.EncodedContainer;
import org.jproggy.snippetory.util.CharDataSupport;
import org.jproggy.snippetory.util.SimpleFormat;
import org.jproggy.snippetory.util.TemplateNode;

public class NullFormat extends SimpleFormat {
  private final String value;

  public NullFormat(String value) {
    this.value = value;
  }

  @Override
  public Object format(TemplateNode location, Object value) {
    return new EncodedContainer(this.value, location.getEncoding());
  }

  @Override
  public boolean supports(Object value) {
    return CharDataSupport.isNull(value);
  }

  public static NullFormat create(String definition, TemplateContext ctx) {
    return new NullFormat(definition);
  }
}
