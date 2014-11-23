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
 * The interface can be implemented by {@link FormatConfiguration FormatConfigurations} that
 * do not know their exact attributes at compile time.
 *
 * @author B. Ebertz
 *
 */
public interface DynamicAttributes {
	/**
	 * Attributes,that are defined in template code but not defined as setter methods,
	 * will be provided over this method.
	 * <p>
	 * Example:<br />
	 * <pre>
	 *   $(button='Test' button.id='btn_id' button.onclick='buttonClicked()')
	 * </pre>
	 * If there is a setter method <code>setId(String val)</code> this will be called
	 * with parameter <code>"btn_id"</code> and <code>setAttribute</code> will
	 * be called with <code>("onclick", "buttonClicked()")</code>.
	 */
	void setAttribute(String name, String value);
}
