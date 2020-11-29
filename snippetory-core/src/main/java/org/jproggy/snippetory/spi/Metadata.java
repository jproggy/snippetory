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
