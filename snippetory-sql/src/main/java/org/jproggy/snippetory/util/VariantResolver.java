package org.jproggy.snippetory.util;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.sql.Statement;
import org.jproggy.snippetory.sql.spi.PostProcessor;
import org.jproggy.snippetory.sql.spi.StatementWrapper;

public class VariantResolver extends StatementWrapper {
  private final String variant;

  public VariantResolver(Statement template, String variant) {
    super(template);
    this.variant = variant;
  }

  @Override
  protected Statement wrap(Template toBeWrapped) {
    return new VariantResolver(((Statement)toBeWrapped), variant);
  }

  @Override
  public Statement get(String... names) {
    if (names.length == 1) {
      Statement statement = super.get(names);
      if (statement.regionNames().contains(variant)) {
        statement = statement.get(variant);
      }
      return statement;
    }
    return super.get(names);
  }

  public static Wrapper wrap(String variant) {
    return new Wrapper(variant);
  }

  public static class Wrapper implements PostProcessor {
    private final String variant;

    public Wrapper(String variant) {
      this.variant = variant;
    }

    @Override
    public Statement processRepository(Statement repo) {
      return new VariantResolver(repo, variant);
    }

  }

}
