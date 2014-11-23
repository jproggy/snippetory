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

/**
 * The Service provider interface contains classes enabling the user to extend and configure the behavior
 * of the Snippetory engine. Those extensions can be packaged into jar files and loaded by
 * the {@link java.util.ServiceLoader} mechanism. See {@link org.jproggy.snippetory.spi.Configurer} for further
 * information.
 * <br />
 * The classes {@link org.jproggy.snippetory.spi.Format}, {@link org.jproggy.snippetory.spi.Syntax}, and
 * {@link org.jproggy.snippetory.spi.Encoding} contain a REGISTRY attribute to allow extensions as well
 * as overriding default behavior.
 */
package org.jproggy.snippetory.spi;