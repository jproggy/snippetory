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
import org.jproggy.snippetory.spi.CharDataSupport;
import org.jproggy.snippetory.spi.FormatFactory;
import org.jproggy.snippetory.spi.SimpleFormat;
import org.jproggy.snippetory.spi.TemplateNode;

public class CropFormatter implements FormatFactory {

  @Override
  public CropFormat create(String definition, TemplateContext ctx) {
    int width = Integer.parseInt(definition);
    return new CropFormat(width);
  }

  public static class CropFormat extends SimpleFormat {
    private final int length;
    private String mark = "";

    public CropFormat(int length) {
      super();
      this.length = length;
    }

    public void setMark(String mark) {
      this.mark = mark;
    }

    @Override
    public Object format(TemplateNode location, Object value) {
      CharSequence s = CharDataSupport.toCharSequence(value);
      if (s.length() <= length) return value;
      return new StringBuilder(s.subSequence(0, length - mark.length())).append(mark);
    }

    @Override
    public boolean supports(Object value) {
      return CharDataSupport.isCharData(value);
    }
  }
}
