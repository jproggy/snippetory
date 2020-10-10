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

package org.jproggy.snippetory.engine;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Format;
import org.jproggy.snippetory.spi.FormatConfiguration;
import org.jproggy.snippetory.spi.Link;
import org.jproggy.snippetory.spi.TemplateNode;
import org.jproggy.snippetory.spi.Transcoding;
import org.jproggy.snippetory.spi.VoidFormat;

public class Metadata implements VoidFormat {

  public Metadata(String name, String fragment, Attributes attribs) {
    super();
    this.name = name;
    this.formats = attribs.formats.values().toArray(new FormatConfiguration[0]);
    this.enc = attribs.enc;
    this.fragment = fragment;
    this.delimiter = attribs.delimiter;
    this.prefix = attribs.prefix;
    this.suffix = attribs.suffix;
    this.link = attribs.link;
  }

  final String name;
  final FormatConfiguration[] formats;
  final Encoding enc;
  final String fragment;
  final String delimiter;
  final String prefix;
  final String suffix;
  final Link link;

  public CharSequence getFallback() {
    if (prefix != null || suffix != null) return "";
    return fragment;
  }

  public <T extends Appendable> T transcode(T target, CharSequence value, String sourceEnc) {
    try {
      for (Transcoding overwrite : EncodingRegistry.INSTANCE.getOverwrites(enc)) {
        if (overwrite.supports(sourceEnc, enc.getName())) {
          overwrite.transcode(target, value, sourceEnc, enc.getName());
          return target;
        }
      }
      enc.transcode(target, value, sourceEnc);
      return target;
    } catch (IOException e) {
      throw new SnippetoryException(e);
    }
  }

  Format[] getFormats(TemplateNode location) {
    Format[] result = new Format[formats.length];
    for (int i = 0; i < formats.length; i++) {
      result[i] = formats[i].getFormat(location);
    }
    return result;
  }

  @Override
  public Object format(TemplateNode location, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean supports(Object value) {
    return false;
  }

  @Override
  public void clear(TemplateNode location) {}

  @Override
  public Object formatVoid(TemplateNode node) {
    return getFallback();
  }

  @Override
  public void set(String name, Object value) {}

  @Override
  public void append(String name, Object value) {}

  @Override
  public Set<String> names() {
    return Collections.emptySet();
  }
}
