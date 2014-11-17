package org.jproggy.snippetory.sql;

import java.util.Iterator;

import org.jproggy.snippetory.sql.spi.RowProcessor;

public interface OpenCursor<T> extends Cursor<T> {
  @Override
  public OpenIterator<T> iterator();

  public void processRow(RowProcessor proc);
  public void updateRow();

  public interface OpenIterator<T> extends Iterator<T> {
    public void processRow(RowProcessor proc);
    public void updateRow();
  }
}
