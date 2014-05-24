package org.jproggy.snippetory.sql;

public class Repository {
  private final Statement repo;

  public Repository(Statement repo) {
    super();
    this.repo = repo;
  }

  public Statement get(String name) {
    return repo.get(name);
  }
}
