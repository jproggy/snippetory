package org.jproggy.snippetory.toolyng.letter;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.jproggy.snippetory.TemplateContext.TECH;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public enum CaseFormat {
    LOWER_CAMEL {
        @Override
        public List<String> segments(String val) {
            return splitOnUpper(val);
        }

        @Override
        public String join(List<String> parts) {
            return camelize(parts, true);
        }
    }
    , UPPER_CAMEL {
        @Override
        public List<String> segments(String val) {
            return splitOnUpper(val);
        }

        @Override
        public String join(List<String> parts) {
            return camelize(parts, false);
        }
    }, UPPER_UNDERSCORE(String::toUpperCase, "_"),
    LOWER_HYPHEN(String::toLowerCase, "-") ,
    LOWER_UNDERSCORE(String::toLowerCase, "_");

    private final Function<String, String> adaptCase;
    private final String separator;

    CaseFormat() {
        this.adaptCase = null;
        this.separator = null;
    }

    CaseFormat(Function<String, String> adaptCase, String separator) {
        this.adaptCase = adaptCase;
        this.separator = separator;
    }

    private static String camelize(List<String> parts, boolean lower) {
        StringBuilder result = new StringBuilder();
        for (String val : parts) {
            if (val.length() == 0) continue;
            if (result.length() == 0 && lower) {
                result.append(val.substring(0, 1).toLowerCase(TECH));
            } else {
                result.append(val.substring(0, 1).toUpperCase(TECH));
            }
            if (val.length() > 1) result.append(val.substring(1).toLowerCase(TECH));
        }
        return result.toString();
    }

    private static List<String> splitOnUpper(String val) {
        if (val == null) return Collections.emptyList();
        List<String> segments = new ArrayList<>();
        char[] chars = val.toCharArray();
        int wordStart = 0;
        for (int i = 1; i < chars.length; i++) {
            if (Character.isUpperCase(chars[i])) {
                segments.add(new String(chars, wordStart, i - wordStart));
                wordStart = i;
            }
        }
        if (wordStart < chars.length) {
            segments.add(new String(chars, wordStart, chars.length - wordStart));
        }
        return segments;
    }


    public String to(CaseFormat to, String val) {
        return to.join(segments(val));
    }
    public List<String> segments(String val) {
        return asList(val.split(separator));
    }
    public String join(List<String> val) {
        return val.stream().map(adaptCase).collect(joining(separator));
    }
}
