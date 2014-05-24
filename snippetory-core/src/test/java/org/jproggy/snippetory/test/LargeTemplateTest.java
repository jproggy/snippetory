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
	}

	@Test
	public void fluyt() {
		template = new TemplateContext().uriResolver(UriResolver.resource()).getTemplate("large.tpl");
		Template fluyt = template.get("FLUYT");
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
			if (!regions.contains(name)) t.set(name, "");
		}
	}
}
