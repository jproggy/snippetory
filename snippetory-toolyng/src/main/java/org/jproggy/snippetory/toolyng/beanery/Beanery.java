package org.jproggy.snippetory.toolyng.beanery;

import java.io.IOException;

import org.jproggy.snippetory.Repo;
import org.jproggy.snippetory.Template;

import com.google.common.base.CaseFormat;

public class Beanery {
  private Template beanTpl = Repo.readStream(getClass().getResourceAsStream("Bean.java")).parse();
  private String indent;

  public static void main(String[] args) throws IOException {
    if (args.length != 3) {
      System.out.println("Usage: templatePath targetDir qualifiedClassName");
      return;
    }
    Template subject = Repo.readFile(args[0]).parse();
    String targetDir = args[1];
    Type type = new Type(args[2]);
    new Beanery().boil(subject, type.getPackage(), type.getName()).render(type.getTarget(targetDir));
  }

  public Beanery() {
    indent = beanTpl.get("class", "i").toString();
  }

  /** 
   * Build an Object-oriented Interface Layer. This layer consists of a get-method for each
   * region, a class for each region used as return type of the getter, and a set method
   * for each location. This construct allows convenient navigation of templates by
   * the syntax completion feature available in most IDEs.
   * 
   * @param subject
   *          The template that shall get the interface layer
   * @param packageName The package where the resulting class of the interface layer is declared
   * @param className The simple name of the class declared 
   * @return A template containing the generated class including necessary inner classes
   */
  public Template boil(Template subject, String packageName, String className) {
    Template target = beanTpl.get().set("package", packageName);
    bindRegion(target.get("class"), subject, className, "").render();
    return target;
  }

  protected Template bindRegion(Template targetTpl, Template subject, String typeName, String i) {
    targetTpl.set("RegionTpl", typeName).set("i", i);
    for (String location : subject.names()) {
      String propName =  CaseHelper.convert(CaseFormat.UPPER_CAMEL, location);
      targetTpl.get("location").set("name", location).set("Name", propName).set("RegionTpl", typeName).set("i", i).render();
    }

    for (String region : subject.regionNames()) {
      String propName = CaseHelper.convert(CaseFormat.UPPER_CAMEL, region);
      String childType =  propName + "Tpl";
      targetTpl.get("region").set("region", region).set("Region", propName).set("RegionTpl", childType).set("i", i).render();
      targetTpl.append("classes", bindRegion(beanTpl.get("class"), subject.get(region), childType, i + indent));
    }
    return targetTpl;
  }
}
