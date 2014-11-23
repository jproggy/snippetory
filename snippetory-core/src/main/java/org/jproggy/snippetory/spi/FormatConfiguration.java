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
 * The FormatConfiguration allows Snippetory to support complex state handling approaches
 * like counting invokations on several nodes. However, most Formats are state less and such
 * don't need any state handling at all. In those cases SimpleFormat is a great alternative.
 * No matter wether implemented directly or via SimpleFormat it allows one to implement
 * sub-attributes by providing setter-methods or by implementing DynamicAttributes.
 * The types supported by those setter methods are  numbers, boolean, enums and String.
 * This can be extended by registering {@link  java.beans.ProperyEditor PropertyEditors} on
 * {@link java.beans.PropertyEditorManager PropertyEditorManager}
 *
 * @author B. Ebertz
 */
public interface FormatConfiguration {

	/**
	 * Allows the Configuration to control the instanciation of Format.
	 * @see StateContainer
	 */
	Format getFormat(TemplateNode node);
}
