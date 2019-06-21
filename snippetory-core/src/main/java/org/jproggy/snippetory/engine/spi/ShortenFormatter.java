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
import org.jproggy.snippetory.engine.spi.CropFormatter.CropFormat;
import org.jproggy.snippetory.spi.FormatFactory;

public class ShortenFormatter implements FormatFactory {
  @Override
  public CropFormat create(String definition, TemplateContext ctx) {
    int length = 0;
    String mark = "";
    boolean num = true;
    for (char c : definition.toCharArray()) {
      if (num) {
        if (c >= '0' && c <= '9') {
          length = (10 * length) + (c - '0');
          continue;
        }
        num = false;
      }
      mark += c;
    }
    if (length == 0) {
      throw new IllegalArgumentException("no length defined");
    }
    if (length < mark.length()) {
      throw new IllegalArgumentException("Suffix too long");
    }
    CropFormat cropFormat = new CropFormat(length);
    if (mark.length() > 0) cropFormat.setMark(mark);
    return cropFormat;
  }
}
