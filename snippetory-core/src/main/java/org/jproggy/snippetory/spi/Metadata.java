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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.jproggy.snippetory.engine.AnnotationRegistry;
import org.jproggy.snippetory.engine.SnippetoryException;

/**
 * @author B.Ebertz
 */
public interface Metadata {
    static void registerAnnotation(String name) {
        AnnotationRegistry.INSTANCE.registerAnnotation(name);
    }

    String getName();

    /**
     * Get a specific annotation of a template element. Annotations represents arbitrary value, that can be placed in
     * templates to tag regions for further processing. Annotations can also be denoted to locations, but then only
     * being read via Formats or Links.
     * Typical analysis pattern are:
     * <pre>{@code
     * if (template.metadata().annotation("type").is("embedded")) {
     *   ...
     * }
     *
     * String type = template.metadata().annotation("type").orElse("standAlone");
     *
     * Type type = template.metadata().annotation("type")
     *     .map(Type::valueOf)
     *     .orElse(Type.standAlone);
     *
     * int size = template.metadata().annotation("size")
     *     .defaultTo("12")
     *     .verify(StringUtils::isNumeric)
     *     .map(Integer::valueOf)
     *     .get();
     * }</pre>
     */
    Annotation annotation(String name);


    class Annotation {
        private final String value;
        private final String name;

        public Annotation(String name, String value) {
            this.value = value;
            this.name = name;
        }

        /**
         * Is a value for this annotation defined?
         *
         * @return true if no definition of the annotation is available, false if found in file or as default value
         */
        public boolean isAbsent() {
            return value == null;
        }

        /**
         * converts value of this {@code Annotation} to an {@code Optional}
         */
        public Optional<String> value() {
            return Optional.ofNullable(value);
        }

        /**
         * the value of the annotation as defined in template file a default value was defined,
         * null if not defined at all
         */
        public String get() {
            return value;
        }

        /**
         * If the {@code Annotation} is present, returns the value, otherwise returns
         * {@code defaultValue}.
         *
         * @param defaultValue the value to be returned, if no value is present.
         *                     May be {@code null}.
         * @return the value, if present, otherwise {@code defaultValue}
         */
        public String orElse(String defaultValue) {
            return value().orElse(defaultValue);
        }

        /**
         * If the {@code Annotation} is present, compares the value to other,
         * otherwise it compares {@code null} to other.
         *
         * @param other the value to be compared with.
         * @return whether the comparison succeeds
         */
        public boolean is(String other) {
            return Objects.equals(value, other);
        }

        public boolean matches(String regEx) {
            return matches(Pattern.compile(regEx));
        }

        /**
         * Attempts to match the entire value of the {@code Annotation} against the pattern {@code regEx}.
         *
         * @param regEx the {@link Pattern} to match against
         * @return {@code true} if, and only if, the value
         * matches this given pattern
         */
        public boolean matches(Pattern regEx) {
            Objects.requireNonNull(regEx);
            if (isAbsent()) return false;
            return regEx.matcher(value).matches();
        }


        /**
         * If the {@code Annotation} is present, returns the result of applying the given
         * mapping function to the value, otherwise returns an empty {@code Optional}.
         *
         * <p>If the mapping function returns a {@code null} result then this method
         * returns an empty {@code Optional}.
         *
         * @param mapper the mapping function to apply to a value, if present
         * @param <T>    The type of the value returned from the mapping function
         * @return an {@code Optional} describing the result of applying a mapping
         * function to the value of this {@code Annotation}, if it is
         * present, otherwise an empty {@code Optional}
         * @throws NullPointerException if the mapping function is {@code null}
         * @apiNote This method supports post-processing on {@code Annotation} values, without
         * the need to explicitly check for a return status. For example, the
         * following code converts the value of the {@code Annotation} to an enum value:
         *
         * <pre>{@code
         * Type p = annotation.map(Type::valueOf).orElse(Type.embedded);
         * }</pre>
         * <p>
         * Here, {@code valueOf}  is only executed if a value to v´convert is available.
         */
        public <T> Optional<T> map(Function<String, T> mapper) {
            return value().map(mapper);
        }

        /**
         * Fail, if annotation is not valid. The validator can either return false and relay on default messages
         * or throw a {@link RuntimeException}.
         *
         * @throws SnippetoryException if predicate is false.
         */
        public Annotation verify(Predicate<String> validator) {
            if (!validator.test(value)) {
                if (isAbsent()) throw new SnippetoryException("The mandatory annotation " +
                        name + " is not provided");
                throw new SnippetoryException(value + " is not supported for annotation " + name);
            }
            return this;
        }

        /**
         * Declares a default value for this annotation
         */
        public Annotation defaultTo(String other) {
            if (isAbsent()) {
                return new Annotation(name, other);
            }
            return this;
        }
    }
}
