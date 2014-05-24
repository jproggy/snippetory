package org.jproggy.snippetory.sql;

import org.jproggy.snippetory.Repo;
import org.jproggy.snippetory.Template;

public class TestResults {
  private static Template data = Repo.readResource("org/jproggy/snippetory/sql/TestResults.txt").parse();

  public static String test1() {
    return data.get("test1").toString();
  }

  public static String test2() {
    Template template = data.get("test1");
    template.get("test2").render();
    return template.toString();
  }

  public static String test3() {
    Template template = data.get("test1");
    template.get("test2").render();
    template.get("test3").render();
    return template.toString();
  }
}
