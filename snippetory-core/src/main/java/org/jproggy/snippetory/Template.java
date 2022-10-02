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
import java.util.Collections;
import java.util.Set;

import org.jproggy.snippetory.spi.EncodedData;
import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Metadata;

/**
 * <p>
 * The Template is the central interface of the Snippetory Template Engine.
 * A Template has two faces. It's a template or a snippet. It describes how
 * data is bound to create an output. However, it's a repository of snippets as
 * well. This introduces new possibilities of (re-)usage to template code. Any
 * Template can be reused as needed and combined with any other Template. As
 * simple as calling a method.
 * </p><p>
 * It abstracts from most technical issues of the output by handling several
 * typical issues when generating textual languages interpreted by other machines
 * and presented to humans all over the world. On one hand any target language needs
 * some help to maintain syntactical correctness.
 * </p><p>
 * {@link Encoding}s are designed to deliver this help by escaping characters or terms to
 * keep their original meaning instead of confusing the target parser or provide
 * possibilities for several types of attacks. As the needs differ and grow additional
 * encodings can be added and existing can be replaced.
 * </p><p>
 * To achieve a professional presentation to people of different languages, throughout
 * a mix of transmission languages and technologies you need a quite flexible and
 * robust formatting system. This is provided with a chain of type sensitive
 * formatters applied just before encoding. And as formatting is part of the look of the
 * result it is useful to have it in the template.
 * </p><p>
 * The next important aspect of the design of Snippetory is, that logic separated from view.
 * No loops, no conditions, no variable definitions and manipulations in the template.
 * (by the way, we'll talk of variables in some cases, but formally that are just
 * location marks in the template not a declaration and several usages, that have to
 * be in sequence) And freeing template from logic means freeing from context. And
 * in consequence it is easy to take a peace of template and use it where appropriate.
 * This means an abstraction from template storage and organization and allows one to
 * organize the template structure as needed.
 * </p>
 *
 * @author B. Ebertz
 * @see Repo
 */

public interface Template extends EncodedData {

  Template NONE = new Template() {

    @Override
    public CharSequence toCharSequence() {
      return "";
    }

    @Override
    public String getEncoding() {
      return Encodings.NULL.name();
    }

    @Override
    public Template set(String name, Object value) {
      return this;
    }

    @Override
    public void render(PrintStream out) {
    }

    @Override
    public void render(Writer out) {
    }

    @Override
    public void render(Template target, String name) {
    }

    @Override
    public Set<String> regionNames() {
      return Collections.emptySet();
    }

    @Override
    public Set<String> names() {
      return Collections.emptySet();
    }

    @Override
    public Template get(String... name) {
      return this;
    }

    @Override
    public Template clear() {
      return this;
    }

    @Override
    public Template append(String name, Object value) {
      return this;
    }

    @Override
    public boolean isPresent() {
      return false;
    }

    @Override
    public Template getParent() {
      return null;
    }

    @Override
    public String toString() {
      return "";
    }
  };

  /**
   * Get further elements out of this repository.
   *
   * @param name the path within the repository. This might consist of several elements.
   * @return a clean instance of the child template identified by the name. Whether
   * such a template was found, is reflected {@link Template#isPresent} method of the template.
   * Implementation must not return null, but could use {@link Template#NONE} instead.
   */
  Template get(String... name);

  /**
   * Sets all variables with given name to a String representation of the value.
   * Exact value might differ according to different metadata associated with
   * each of these variables. Eventually set or appended data is overwritten.
   * All matching formats and encodings are used. However, there is some
   * special handling for the interface {@link EncodedData}. In this case the
   * provided encoding in determined to calculate the correct transcoding.
   *
   * @return the Template itself
   */
  Template set(String name, Object value);

  /**
   * Appends a String representation of the value to all variables with given name.
   * The exact value might differ according to different metadata associated with
   * each of these variables. Eventually set or appended data is kept and new data
   * is appended behind the last character.
   * All matching formats and encodings are used. However, there is some
   * special handling for the interface {@link EncodedData}. In this case the
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
  default void render() {
    render(metadata().getName());
  }

  /**
   * <p>
   * Appends the textual representation of this Template to a sibling of the location
   * where it was created. (I.e. got from)
   * This will be used when handling several variants of presentations within a list.
   * To avoid sorting of elements by variant it has to be ensured that all variants
   * are rendered to the same target.
   * </p><p>
   * A template:
   * </p>
   * <pre>
   *  &lt;ul&gt;
   *   {v:target}
   *   &lt;t:debit&gt;  &lt;li class="debit"&gt; {v:action} : {v:value} &lt;/li&gt; &lt;/t:debit&gt;
   *   &lt;t:credit&gt; &lt;li class="credit"&gt;{v:action} : {v:value} &lt;/li&gt; &lt;/t:credit&gt;
   *  &lt;/ul&gt;
   *  </pre>
   * And the logic:
   * <pre>
   *   for (Booking b: bookings) {
   *     if (b.isCredit()) {
   *       snip.get("credit").set("action", b.getAction()).set("value", b.getValue()).render("target");
   *     } else {
   *       snip.get("debit").set("action", b.getAction()).set("value", b.getValue()).render("target");
   *     }
   *   }
   *  </pre>
   * <p>
   * using render() instead of render("target0") in the "debit" line would show all debits
   * first and all credits afterwards. But in the example it's ensured that all data is written
   * to the same location, though, the order stays as defined.
   * </p><p>
   *
   * @param siblingName names a child of the same parent. If using the name of this Snippetory
   *                    it's the same as render() without parameter.
   *                    </p>
   */
  default void render(String siblingName) {
    render(getParent(), siblingName);
  }

  /**
   * Appends the textual representation of this Template to an arbitrary location.
   * This allows even to mix data from different files. Quite similar to
   * <pre>
   * target.append(name, thisOne);
   * </pre>
   * however, an interceptor gets more information what's going on.
   *
   * @param target a Template to append to
   * @param name   the name of the place where the data should go to.
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
   * <p>
   * The names of all locations, to be accessed by the set operation. Can be used to ensure
   * to access all existing names. This method belongs to the reflective API and as such
   * is only for special use.
   * </p><p>
   * Use with care as it's a more expensive operation.
   * </p>
   */
  Set<String> names();

  /**
   * Delivers the names of the regions. Regions can be accessed by the get operation. This
   * is a subset of the names delivered by <code>names()</code>.
   */
  Set<String> regionNames();

  /**
   * The parent node of this node. If this node is absent or the root of a particular structure result might be null.
   */
  Template getParent();

  /**
   * Check whether this instance represents a real template and not the 'null' template.
   *
   * @return {@code false} if this instance isn't a real template but rather a null object, also called absent
   */
  boolean isPresent();

  /**
   * The metadata of a template region
   */
  default Metadata metadata() {
    return new Metadata() {
      @Override
      public String getName() {
        return null;
      }

      @Override
      public Annotation annotation(String name) {
        return new Annotation(name, null);
      }
    };
  }
}
