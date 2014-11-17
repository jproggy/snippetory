package org.jproggy.snippetory.sql.impl;

import org.jproggy.snippetory.engine.SnippetoryException;

public class ResultCountException extends SnippetoryException {
  private static final long serialVersionUID = 1L;

  public ResultCountException(int numRows) {
    super(text(numRows));
  }

  private static String text(int numRows) {
    if (numRows == 0) {
      return "No rows found";
    }
    return numRows + " rows found";
  }

}
