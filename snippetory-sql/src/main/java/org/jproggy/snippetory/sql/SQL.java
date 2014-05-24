package org.jproggy.snippetory.sql;

import java.io.IOException;

import org.jproggy.snippetory.engine.IncompatibleEncodingException;
import org.jproggy.snippetory.engine.chars.EncodedContainer;
import org.jproggy.snippetory.spi.Configurer;
import org.jproggy.snippetory.spi.EncodedData;
import org.jproggy.snippetory.spi.Encoding;
import org.jproggy.snippetory.spi.Syntax;
import org.jproggy.snippetory.spi.SyntaxID;
import org.jproggy.snippetory.sql.impl.SqlSyntax;

public class SQL implements Encoding, Configurer {
  public static final SyntaxID SYNTAX;
  static {
    SqlSyntax s = new SqlSyntax();
    SYNTAX = s;
    Syntax.REGISTRY.register(SYNTAX, s);
  }

  public static final SQL ENCODING = new SQL();
  static {
    Encoding.REGISTRY.register(ENCODING);
  }

  public SQL () {
    super();
  }

  public static EncodedData wrap(CharSequence value) {
    return new EncodedContainer(value, "sql");
  }

  @Override
  public void transcode(Appendable target, CharSequence value, String encodingName) throws IOException,
      IncompatibleEncodingException {
    if (value.length() == 1 && value.charAt(0) == '?') {
      target.append('?');
    } else {
      throw new IncompatibleEncodingException("can't convert encoding " + encodingName + " into " + getName());
    }
  }

  @Override
  public String getName() {
    return "sql";
  }

}
