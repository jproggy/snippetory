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

import java.util.ServiceLoader;

/**
 * This is just an interface to tag classes that are loaded on startup to register additional
 * elements on startup. Additional to tagging there must be a file named
 * org.jproggy.snippetory.spi.Configurer containing the name of your implementation class.
 * Snippetory will use the {@link ServiceLoader} mechanism to load those classes to enable
 * you to have your additions available when you need them. Be aware that Snippetory can only
 * load services if it's {@link ClassLoader} can find them.
 *
 * @see <a href="https://www.jproggy.org/snippetory/tutorial/ExtensionExample.html">Extending the platform</a>
 */
public interface Configurer {

}
