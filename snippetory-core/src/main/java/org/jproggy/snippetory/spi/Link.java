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

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.engine.LinkRegistry;

/**
 * A {@link Link}can be denoted on a named location and will turn it into a named region then.
 * Analog to a {@link Format} a Link will be denoted via attributes in most cases, but exact rules are defined
 * by the respective {@link Syntax}.
 */
public interface Link {
  LinkRegistry REGISTRY = LinkRegistry.INSTANCE;

  Template getContents(Template parentNode, Metadata nodeMetadata);
}
