package org.jproggy.snippetory.toolyng.beanery;

import java.util.Set;

import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;

public class CaseHelper {
  private static final Set<String> RESERVED_WORDS = Set.of("abstract", "continue", "for", "new", "switch",
      "assert", "default", "goto", "package", "synchronized", "boolean", "do", "if", "private", "this", 
      "break", "double", "implements", "protected", "throw", "byte", "else", "import", "public", "throws", 
      "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", 
      "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", 
      "const", "float", "native", "super", "while");
      
  public static CaseFormat guessFormat(String val) {
    if (noLowerCase(val)) {
      return CaseFormat.UPPER_UNDERSCORE;
    }
    if (noUpperCase(val)) {
      if (val.contains("-")) return CaseFormat.LOWER_HYPHEN;
      return CaseFormat.LOWER_UNDERSCORE;
    }
    if (Character.isLowerCase(val.charAt(0))) {
      return CaseFormat.LOWER_CAMEL;
    }
    return CaseFormat.UPPER_CAMEL;
  }
  
  public static String convert(CaseFormat to, String val) {
    String result = guessFormat(val).to(to, val);
    if (RESERVED_WORDS.contains(result)) {
      return '_' + result;
    }
    return result;
  }

  protected static boolean noLowerCase(String val) {
    return CharMatcher.javaLowerCase().matchesNoneOf(val);
  }

  protected static boolean noUpperCase(String val) {
    return CharMatcher.javaUpperCase().matchesNoneOf(val);
  }
}
