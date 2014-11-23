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

package org.jproggy.snippetory;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Set;

import org.jproggy.snippetory.spi.EncodedData;
import org.jproggy.snippetory.spi.Encoding;



/**
 * The Template is the central interface of the Snippetory Template Engine.
 * A Template has two faces. It's a template or a snippet. It describes how
 * data is bound to create an output. However, it's a repository of snippets as
 * well. This introduces new possibilities of (re-)usage to template code. Any
 * Template can be reused as needed and combined with any other Template. As
 * simple as calling a method.<br />
 * <br />
 * It abstracts from most technical issues of the output by handling several
 * typical issues when generating textual languages interpreted by other machines
 * and presented to humans all over the world. On one hand any target language needs
 * some help to maintain syntactical correctness.<br />
 * {@link Encoding}s are designed to deliver this help by escaping characters or terms to
 * keep their original meaning instead of confusing the target parser or provide
 * possibilities for several types of attacks. As the needs differ and grow additional
 * encodings can be added and existing can be replaced.<br />
 * To achieve a professional presentation to people of different languages, throughout
 * a mix of transmission languages and technologies you need a quite flexible and
 * robust formatting system. This is provided with a chain of type sensitive
 * formatters applied just before encoding. And as formatting is part of the look of the
 * result it is useful to have it in the template.<br />
 * <br />
 * The next important aspect of the design of Snippetory is, that logic separated from view.
 * No loops, no conditions, no variable definitions and manipulations in the template.
 * (by the way, we'll talk of variables in some cases, but formally that are just
 * location marks in the template not a declaration and several usages, that have to
 * be in sequence) And freeing template from logic means freeing from context. And
 * in consequence it is easy to take a peace of template and use it where appropriate.
 * This means an abstraction from template storage and organization and allows one to
 * organize the template structure as needed.
 *
 * @see Repo
 *
 * @author B. Ebertz
 */

public interface Template extends EncodedData  {

	/**
	 * Get further elements out of this repository.
	 *
	 * @param name the path within the repository. This might consist of several elements.
	 *
	 * @return a clean instance of the child template identified by the name  or null if
	 * there is no child template with this name. It's undefined if this is a new copy or if only a
	 * single instance exists. Though subsequent calls an get on the same instance with
	 * the same name might clear the instances returned by previous call or not.
	 */
	Template get(String... name);

	/**
	 * Sets all variables with given name to a String representation of the value.
	 * Exact value might differ according to different meta data associated with
	 * each of these variables. Eventually set or appended data is overwritten.
	 * All matching formats and encodings are used. However, there is some
	 * special handling for the interface (@link EncodedData). In this case the
	 * provided encoding in determined to calculate the correct transcoding.
	 *
	 * @return the Template itself
	 */
	Template set(String name, Object value);

	/**
	 * Appends a String representation of the value to all variables with given name.
	 * The exact value might differ according to different meta data associated with
	 * each of these variables. Eventually set or appended data is kept and new data
	 * is appended behind the last character.
	 * All matching formats and encodings are used. However, there is some
	 * special handling for the interface (@link EncodedData). In this case the
	 * provided encoding in determined to calculate the correct transcoding.
	 *
	 * @return the Template itself
	 */
	Template append(String name, Object value);

	/**
	 * removes all data already bound to this instance.
	 *
	 * @return the Template itself
	 */
	Template clear();

	/**
	 * Appends the textual representation of this Template to the location where
	 * it was created. (I.e. got from)
	 * This works pretty fine for the really simple cases.
	 */
	void render();

	/**
	 * Appends the textual representation of this Template to a sibling of the location
	 * where it was created. (I.e. got from)
	 * This will be used when handling several variants of presentations within a list.
	 * To avoid sorting of elements by variant it has to be ensured that all variants
	 * are rendered to the same target.<br />
	 * <br />
	 *  A template:
	 *  <pre>
	 *  &lt;ul>
	 *   {v:target}
	 *   &lt;t:debit>  &lt;li class="debit"> {v:action} : {v:value} &lt;/li> &lt;/t:debit>
	 *   &lt;t:credit> &lt;li class="credit">{v:action} : {v:value} &lt;/li> &lt;/t:credit>
	 *  &lt;/ul>
	 *  </pre>
	 *  And the logic:
	 *  <pre>
	 *   for (Booking b: bookings) {
	 *     if (b.isCredit()) {
	 *       snip.get("credit").set("action", b.getAction()).set("value", b.getValue()).render("target");
	 *     } else {
	 *       snip.get("debit").set("action", b.getAction()).set("value", b.getValue()).render("target");
	 *     }
	 *   }
	 *  </pre>
	 * using render() instead of render("target0") in the "debit" line would show all debits
	 * first and all credits afterwards. But in the example it's ensured that all data is written
	 * to the same location, though, the order stays as defined.
	 *
	 * @param siblingName names a child of the same parent. If using the name of this Snippetory
	 * it's the same as render() without parameter.
	 */
	void render(String siblingName);

	/**
	 * Appends the textual representation of this Template to an arbitrary location.
	 * This allows even to mix data from different files. Quite similar to
	 * <pre>
	 * target.append(name, thisOne);
	 * </pre>
	 * however an interceptor gets more information what's going on.
	 *
	 * @param target a Template to append to
	 * @param name the name of the place where the data should go to.
	 */
	void render(Template target, String name);

	/**
	 * A convenience method to write to a Writer
	 */
	void render(Writer out) throws IOException;

	/**
	 * A convenience method to write to a PrintStream
	 */
	void render(PrintStream out) throws IOException;

	/**
	 * The names of all locations, to be accessed by the set operation. Can be used to ensure
	 * to access all existing names. This method belongs to the reflective API and as such
	 * is only for special use. <br />
	 * Use with care as it's a more expensive operation.
	 */
	Set<String> names();

	/**
	 * Delivers the names of the regions. Regions can be accessed by the get operation. This
	 * is a subset of the names delivered by <code>names()</code>.
	 */
	Set<String> regionNames();
}
