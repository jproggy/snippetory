package org.jproggy.snippetory.util;

import org.jproggy.snippetory.engine.SnippetoryException;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ResourceObserver {
  private final ReferenceQueue<Object> queue = new ReferenceQueue<>();

  public ResourceObserver(final ScheduledExecutorService cleaner) {
    cleaner.scheduleAtFixedRate(this::cleanup, 1, 1, TimeUnit.SECONDS);
  }

  private void cleanup() {
    try {
      while (queue.poll() != null) {
        queue.remove().clear();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      // interrupt not expected, as item is checked to be there, and tasks should not be interrupted.
      throw new SnippetoryException(e);
    }
  }

  public Ref observe(Object gard, Runnable action) {
    return new Ref(gard, action);
  }

  public final class Ref extends PhantomReference<Object> {
    private Runnable action;

    public Ref(Object referent, Runnable action) {
      super(referent, queue);
      this.action =  action;
    }

    public void close() {
      super.clear();
      action = null;
    }

    @Override
    public void clear() {
      try {
        if (action != null) action.run();
      } catch (Exception e) {
        throw new SnippetoryException(e);
      } finally {
        close();
      }
    }
  }
}
