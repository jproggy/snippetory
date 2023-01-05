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

import java.io.IOException;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.engine.EncodingRegistry;
import org.jproggy.snippetory.util.IncompatibleEncodingException;

/**
 * <p>
 * The purpose of an encoding is to ensure the syntactical correctness of an output by
 * escaping terms or characters with special meaning in the syntax of the output file.
 * For example the ampersand is illegal within XML as it's used to mark an entity.
 * It has to be replaced by &amp;amp;.
 * </p>
 * <p>
 * By handling those technical issues of the output file within the template definition
 * the handling logic gets more reusable. And simpler to implement. This is a simple but
 * very efficient abstraction layer.
 * </p>
 * <p>
 * As the encoding is inherited throughout the tree of snippets within a {@link Template}
 * a single encoding definition is sufficient for many cases. However, combination of encodings
 * is common as well. This is why it can be overwritten as often as needed, for entire subtrees
 * or just for single leaf nodes.
 * </p>
 * <p>
 * In addition to encodings there is another concept in Snippetory that looks similar at first sight:
 * The {@link Format} serves two purposes, conversion and decoration. While the distinction to
 * conversion is pretty obvious decoration is a string to string operation, too. But decoration formats
 * are less technical and rather work in the problem domain of the template.
 * </p>
 *
 * @see <a href="https://www.jproggy.org/snippetory/encodings/">Official documentation on encodings</a>
 * @see <a href="https://www.jproggy.org/snippetory/tutorial/ExtensionExample.html">Extending the platform</a>
 */
public interface Encoding {

	static void register(Encoding value) {
		EncodingRegistry.INSTANCE.register(value);
	}

	static void registerOverwite(Encoding target, Transcoding overwrite) {
		EncodingRegistry.INSTANCE.registerOverwite(target, overwrite);
	}

	/**
	 * <p>
	 * Sometimes it's possible to combine data encoded in different ways after
	 * applying a special action to one of the strings. This action might be a
	 * translation like wiki syntax to HTML or simply apply default escaping to
	 * the data and mix encodings that way. I.e. when adding HTML to a
	 * string-encoded location would be possible. (Since invention of
	 * html_string it`s forbidden anyway.) However, line breaks or quotation
	 * marks would have to be escaped.
	 * </p>
	 * <p>
	 * In other cases no action will be needed. String encoded data can be added
	 * to HTML as this is a container format and is able to carry string
	 * definition within script-section for instance.
	 * </p>
	 * <p>
	 * However, some combinations of encodings are illegal. Especially the plain
	 * encoding can't be combined with others. In those cases an
	 * {@link IncompatibleEncodingException} is thrown.
	 * </p>
	 *
	 * @param target
	 *            result of the action has to be appended to target.
	 * @param value
	 *            has be transcoded
	 * @param sourceEncoding
	 *            Value is already encoded with this encoding
	 * @throws IncompatibleEncodingException
	 *             if the encoding can't be taken as is and can't be decoded.
	 */
  void transcode(Appendable target, CharSequence value, String sourceEncoding) throws IOException,
      IncompatibleEncodingException;

  /**
   * The identifier for registering and retrieval of this Encoding
   */
  String getName();
}
