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

import java.util.Set;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.engine.spi.DefaultFormatter;
import org.jproggy.snippetory.engine.spi.ToggleFormatter;

/**
 * <p>
 * Extends the format to support the special case, that no value has been
 * provided to a location via {@link Template#set} or {@link Template#append}
 * method. (When rendering to a template append will be used internally.) Just
 * like the default format does. Other implementations can provide more
 * sophisticated algorithms to evaluate the rendered value.
 * </p>
 * <p>
 * VoidFormats are often used
 * </p>
 * <p>
 * <b>Be aware that only the first VoidFormat per location will be executed.</b>
 * </p>
 * <p>
 * <b>Be aware that VoidFormats are not inherited through the template tree.</b>
 * </p>
 *
 * @author B. Ebertz
 *
 * @see Format
 * @see DefaultFormatter
 * @see ToggleFormatter
 *
 */
public interface VoidFormat extends Format {

	/**
	 * Provide a representation
	 * @param node
	 * @return
	 */
	Object formatVoid(TemplateNode node);

	/**
	 * <p>
	 * Offers a value to the callee. The callee is responsible for filtering out
	 * the relevant names. Thus it's expected to ignore all names, that aren't
	 * listed in it's names(). However, the caller may do this filtering, too,
	 * as far as it provides a names declared by the names() method. The set
	 * method is intended to keep only a single value.
	 * </p>
	 */
	void set(String name, Object value);

	/**
	 * Appends a String representation of the value to all variables with given
	 * name. The exact value might differ according to different meta data
	 * associated with each of these variables. Eventually set or appended data
	 * is kept and new data is appended behind the last character. All matching
	 * formats and encodings are used. However, there is some special handling
	 * for the interface (@link EncodedData). In this case the provided encoding
	 * in determined to calculate the correct transcoding.
	 */
	void append(String name, Object value);

	/**
	 * Declares the names supported by this Format. The returned Set must not
	 * change over time. It may return different instances, but it must be
	 * reliable to cache the result.
	 */
	Set<String> names();
}
