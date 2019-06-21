package org.jproggy.snippetory.sql.spi;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.jproggy.snippetory.sql.SQL;
import org.jproggy.snippetory.sql.Statement;

/**
 * Represents an operation that works on a {@link ResultSet} and transforms one or more rows
 * into an object. {@link SQL} provides a number of basic implementations
 *
 * <p>This is a functional interface.

 * @author B. Ebertz
 *
 * @param <T> type of the objects created by this class
 * @see Statement 
 * @see SQL  
 */
public interface RowTransformer<T> {

  /**
   * Process a single row of a result set.
   * <p>The caller will call this method repeatedly for each row of the result set, so iterating within the
   * method is not necessary.
   */
  public T transformRow(ResultSet rs) throws SQLException;
}
