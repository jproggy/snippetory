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

package org.jproggy.snippetory.spi;

/**
 * Simplifies creation of new Formats by unifying the concepts of {@link Format}
 * and {@link FormatConfiguration}. This only works as long as no sophisticated
 * state management is needed. So it's fine for all state less Formats.
 *
 * @author B. Ebertz
 */
public abstract class SimpleFormat implements Format, FormatConfiguration {

  @Override
  public Format getFormat(TemplateNode node) {
    return this;
  }

  @Override
  public void clear(TemplateNode location) {}

}
