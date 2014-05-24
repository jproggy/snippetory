package org.jproggy.snippetory.sql.spi;

import java.sql.Connection;

public interface ConnectionProvider {
  Connection getConnection();
}
