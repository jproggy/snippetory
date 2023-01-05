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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.jproggy.snippetory.spi.EncodedData;
import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.util.CharSequences;
import org.jproggy.snippetory.util.EncodedContainer;
import org.jproggy.snippetory.util.IncompatibleEncodingException;

/**
 * <p>
 * Provides direct access to the predefined <a href="http://www.jproggy.org/snippetory/encodings/">encodings</a>.
 * Even though the functionality of identifying an format and the default implementation for
 * this format are done nearby, Snippetory always uses the implementation,
 * that's registered. This allows to overwrite to default
 * implementation and still use this enum to identify an encoding. </p>
 * All default implementations defined here respect <code>NULL</code> as a wild
 * card that never has to be trans-coded.
 *
 * @author B. Ebertz
 */
public enum Encodings implements Encoding {
  /**
   * It's assumed that Snippetory is used in a modern Unicode based
   * environment. Only a minimal escaping is done:
   * <table>
   *     <caption>list of encoding actions</caption>
   * <tr>
   * <td>&lt;</td>
   * <td>--&gt;</td>
   * <td>&amp;lt;</td>
   * </tr>
   * <tr>
   * <td>&amp;</td>
   * <td>--&gt;</td>
   * <td>&amp;amp;</td>
   * </tr>
   * </table>
   * <p>
   * As XML is a compound format, i.e. it can contain other formats, almost
   * each other will be placed within without any transcoding. Only on plain
   * text the normal escaping is applied.
   * </p>
   */
  xml {
    @Override
    public void escape(Appendable target, CharSequence val) throws IOException {
      for (int i = 0; i < val.length(); i++) {
        char c = val.charAt(i);
        if (c == '<') {
          target.append("&lt;");
        } else if (c == '&') {
          target.append("&amp;");
        } else {
          target.append(c);
        }
      }
    }

    @Override
    public void transcode(Appendable target, CharSequence value, String encodingName) throws IOException {
      if (in(encodingName, xml, html, html_string)) {
        CharSequences.append(target, value);
      } else {
        super.transcode(target, value, encodingName);
      }
    }
  },

  /**
   * html is derived from xml. It just converts line breaks to {@code <br />}-tags
   * to enable transporting of simple formatting within the data bound. Be
   * aware: this applies to data bound, not to some kind of source code like
   * in HTML pages, so we do not break with the good practice of separating
   * the layout of source code, and it's resulting appearance.
   */
  html {
    @Override
    public void escape(Appendable target, CharSequence val) throws IOException {
      for (int i = 0; i < val.length(); i++) {
        char c = val.charAt(i);
        if (c == '<') {
          target.append("&lt;");
        } else if (c == '&') {
          target.append("&amp;");
        } else if (c == 10) {
          target.append("<br />");
          if (i + 1 < val.length() && val.charAt(i + 1) == 13) i++;
        } else if (c == 13) {
          target.append("<br />");
          if (i + 1 < val.length() && val.charAt(i + 1) == 10) i++;
        } else {
          target.append(c);
        }
      }
    }

    @Override
    public void transcode(Appendable target, CharSequence value, String encodingName) throws IOException {
      if (in(encodingName, xml, html, html_string)) {
        CharSequences.append(target, value);
      } else {
        super.transcode(target, value, encodingName);
      }
    }
  },
  /**
   * Applies url encoding to the data. The character encoding is utf-8.
   */
  url {
    @Override
    public void escape(Appendable target, CharSequence val) throws IOException {
      target.append(URLEncoder.encode(val.toString(), StandardCharsets.UTF_8));
    }
  },
  /**
   * Most C-based languages have almost the same rules. This implementation
   * fits at least Java and JavaScript.
   *
   */
  string {
    @Override
    public void escape(Appendable target, CharSequence val) throws IOException {
      for (int i = 0; i < val.length(); i++) {
        char ch = val.charAt(i);
        if (ch < 32) {
          switch (ch) {
          case '\b':
            target.append('\\');
            target.append('b');
            break;
          case '\n':
            target.append('\\');
            target.append('n');
            break;
          case '\t':
            target.append('\\');
            target.append('t');
            break;
          case '\f':
            target.append('\\');
            target.append('f');
            break;
          case '\r':
            target.append('\\');
            target.append('r');
            break;
          default:
            target.append("\\u").append(hex(ch, 4));
            break;
          }
        } else {
          switch (ch) {
          case '\'':
          case '"':
          case '\\':
            target.append('\\');
            break;
          default:
            // will be appended afterwards
            break;
          }
          target.append(ch);
        }
      }
    }

    @Override
    public void transcode(Appendable target, CharSequence value, String encodingName) throws IOException {
      if (in(encodingName, html_string, NULL)) {
        CharSequences.append(target, value);
      } else {
        escape(target, value);
      }
    }
  },
  /**
   * In JavaScript, I've sometimes data that is transported in a string before
   * it's displayed as HTML.
   */
  html_string {
    @Override
    public void escape(Appendable target, CharSequence val) throws IOException {
      StringBuilder tmp = new StringBuilder();
      html.escape(tmp, val);
      string.escape(target, tmp);
    }

    @Override
    public void transcode(Appendable target, CharSequence value, String encodingName) throws IOException {
      if (in(encodingName, xml, html)) {
        string.escape(target, value);
      } else if (in(encodingName, string)) {
        // I don't expect this to have practical use. But it adds
        // additional risk
        // so breaking seems right.
        throw new IncompatibleEncodingException("Can't check if content might be html");
      } else {
        super.transcode(target, value, encodingName);
      }

    }
  },
  /**
   * Plain text. You will get an IllegalEncodingException if you try to bind
   * encoded data to it.
   */
  plain {
    @Override
    public void escape(Appendable target, CharSequence val) {
      CharSequences.append(target, val);
    }
  },
  /**
   * The wild card encoding. Fits to any other, any other fits to this.
   * Sometimes it's necessary to work around the checks. It' for compatibility
   * with legacy code to ease the conversion to the Snippetory template engine,
   * but once on Snippetory it's better to get rid of it.
   */
  NULL {
    @Override
    public void escape(Appendable target, CharSequence val) {
      CharSequences.append(target, val);
    }

    @Override
    public void transcode(Appendable target, CharSequence value, String encodingName) {
      escape(target, value);
    }
  };

  private static String hex(char ch, int length) {
    String hex = Integer.toHexString(ch);
    return "0".repeat(length - hex.length()) + hex;
  }

  protected abstract void escape(Appendable target, CharSequence val) throws IOException;

  @Override
  public void transcode(Appendable target, CharSequence value, String sourceEncoding) throws IOException {
    if (in(sourceEncoding, NULL)) {
      CharSequences.append(target, value);
    } else if (in(sourceEncoding, plain)) {
      escape(target, value);
    } else {
      throw new IncompatibleEncodingException("can't convert encoding " + sourceEncoding + " into " + name());
    }
  }

  /**
   * the name is used to identify a format. There may exist different
   * implementations for the same format.
   */
  @Override
  public String getName() {
    return name();
  }

  /**
   * Marks the data to be encoded according to specified encoding.
   */
  public EncodedData wrap(CharSequence data) {
    return new EncodedContainer(data, getName());
  }

  private static boolean in(String encoding, Encodings... other) {
    for (Encodings enc : other) {
      if (enc.name().equals(encoding)) return true;
    }
    return false;
  }
}
