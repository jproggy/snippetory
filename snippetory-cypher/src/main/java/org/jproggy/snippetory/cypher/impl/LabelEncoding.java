package org.jproggy.snippetory.cypher.impl;

import static org.jproggy.snippetory.Encodings.NULL;
import static org.jproggy.snippetory.Encodings.plain;

import java.io.IOException;

import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.util.CharSequences;
import org.jproggy.snippetory.util.IncompatibleEncodingException;

public class LabelEncoding implements Encoding {

    @Override
    public void transcode(Appendable target, CharSequence value, String sourceEncoding) throws IOException, IncompatibleEncodingException {
        if ((this.is(sourceEncoding) || plain.is(sourceEncoding) || NULL.is(sourceEncoding))
                && value != null && value.length() > 0) {
            if (value.charAt(0) != ':') {
                target.append(':');
            }
            CharSequences.append(target, value);
        } else {
            throw new IncompatibleEncodingException("can't convert encoding " + sourceEncoding + " into " + getName());
        }
    }

    @Override
    public String getName() {
        return "CypherLabel";
    }
}
