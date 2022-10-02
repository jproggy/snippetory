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

package org.jproggy.snippetory.engine;

import org.jproggy.snippetory.Template;

public interface Reference {
  static Reference to(ConditionalRegion target) {
    return p -> new ConditionalRegionAdapter(target, p);
  }
  static Reference to(Region node) {
    return p -> {
      node.setParent(p);
      return node;
    };
  }

  Template resolve(Region parentNode);
}
