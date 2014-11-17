package org.jproggy.snippetory.sql;

import java.util.Iterator;

public interface Cursor<T> extends Iterable<T>, AutoCloseable {
  @Override
  public Iterator<T> iterator();

  @Override
  public void close();
}
