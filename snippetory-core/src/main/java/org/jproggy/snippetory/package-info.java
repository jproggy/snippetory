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
 * The classes necessary generate code with Snippetory.
 * This package contains all classes that are intended to be the interface for the
 * generating the code you need. I.e. loading templates, composing the texts,
 * binding the data and so on.
 * <p>
 * There a several ways to load a {@link org.jproggy.snippetory.Template Template} form a very short
 * <code>{@code Syntaxes.FLUYT.parse()}</code> or {@link org.jproggy.snippetory.Repo} or even setting up a full
 * blown {@link org.jproggy.snippetory.TemplateContext}
 * </p>
 * <p>
 * {@link org.jproggy.snippetory.Syntaxes} allow to <a href="https://www.jproggy.org/snippetory/intact-templates">
 *     intact templates</a>.
 * </p>
 * @see <a href="https://www.jproggy.org/snippetory/best-practice">Best practice for generating code with Snippetory</a>
 */
package org.jproggy.snippetory;