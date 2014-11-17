package org.jproggy.snippetory.util.concurrent;

public class QueueCloseException extends RuntimeException {
  private static final long serialVersionUID = -1717965808724704393L;

  public QueueCloseException() {
    super("Queue already closed");
  }
}
