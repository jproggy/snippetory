package org.jproggy.snippetory.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.UriResolver;
import org.junit.BeforeClass;
import org.junit.Test;

public class LargeTemplateTest {
	private static Template template;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
    template = new TemplateContext().uriResolver(UriResolver.resource()).getTemplate("large.tpl");
	}

  @Test
  public void fluyt() {
    Template fluyt = template.get("FLUYT");
    assertNotNull(fluyt);
    renderAll(fluyt);
    assertEquals(-1, fluyt.toString().indexOf('$'));
  }

  @Test
  public void xmlAlike() {
    Template section = template.get("XML_ALIKE");
    assertNotNull(section);
    renderAll(section);
    assertEquals(-1, section.toString().indexOf(":t"));
    assertEquals(-1, section.toString().indexOf("{:v"));
    assertEquals(17, section.toString().indexOf(","));
  }

  @Test
  public void cComments() {
    Template fluyt = template.get("C_COMMENTS");
    assertNotNull(fluyt);
    renderAll(fluyt);
    assertEquals(-1, fluyt.toString().indexOf('$'));
  }

	private void renderAll(Template t) {
		Set<String> regions = t.regionNames();
		for (String name : regions) {
			Template child = t.get(name);
			renderAll(child);
			child.render();
		}
		for (String name : t.names()) {
			if (!regions.contains(name)) t.set(name, name + ' ');
		}
	}
}
