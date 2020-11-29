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

import java.util.regex.Pattern;

public interface Metadata {
    String getName();

    Attribute attrib(String name);

    @FunctionalInterface
    interface Attribute {
        default boolean exists() {
            return value() == null;
        }

        String value();

        default boolean is(String value) {
            if (!exists()) {
                return value == null;
            }
            return value().equals(value);
        }

        default boolean matches(String regEx) {
            return matches(Pattern.compile(regEx));
        }

        default boolean matches(Pattern regEx) {
            if (!exists()) return false;
            return regEx.matcher(value()).matches();
        }
    }
}
