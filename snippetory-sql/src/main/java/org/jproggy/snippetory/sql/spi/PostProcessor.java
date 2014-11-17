package org.jproggy.snippetory.sql.spi;

import org.jproggy.snippetory.sql.Statement;

public interface PostProcessor {
  Statement processRepository(Statement repo);
}
