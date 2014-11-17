package org.jproggy.snippetory.sql;

import org.jproggy.snippetory.Template;

public class Repository {
  private final Statement repo;

  public Repository(Statement repo) {
    super();
    this.repo = repo;
  }

  public Statement get(String name) {
    return repo.get(name);
  }

  public Template toTemplate() {
    return repo;
  }
}
