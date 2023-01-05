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

package org.jproggy.snippetory.util;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.spi.Metadata;

/**
 * Helper class for formatting, in case the format might need some orientation, where within the whole template
 * the formatted node is found and some other metadata.
 */
public interface TemplateNode {

  TemplateNode getParent();

  String getEncoding();

  Metadata metadata();

  /**
   * If the node represents neither a region nor a link, the returned region will be absent.
   * If the format doesn't control the region, the returned region ill be absent.
   * This template will be clean and detached, i.e. it has no parent template.
   * That means {@link Template#render()} and {@link Template#render(String)} will do nothing.
   */
  Template region();
}
