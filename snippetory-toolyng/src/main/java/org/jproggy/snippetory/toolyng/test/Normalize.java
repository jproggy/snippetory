package org.jproggy.snippetory.toolyng.test;

import junit.framework.ComparisonFailure;

import java.util.regex.Pattern;

public class Normalize {
  private static final Pattern WS = Pattern.compile("[ \t\r\n]*");

  public static void assertNormEquals(String message, String expected, String actual) {
    if (!normalize(expected).equals(normalize(actual))) {
      String cleanMessage = message == null ? "" : message;
      throw new ComparisonFailure(cleanMessage, expected, actual);
    }
  }

  public static void assertNormEquals(String expected, String actual) {
    assertNormEquals(null, expected, actual);
  }

  private static String normalize(String val) {
    if (val == null) return "";
    return WS.matcher(val).replaceAll("");
  }
}
