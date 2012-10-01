package org.jproggy.snippetory.test;

import static org.junit.Assert.assertNotNull;

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
	}

}
