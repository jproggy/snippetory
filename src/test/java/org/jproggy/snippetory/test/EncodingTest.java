package org.jproggy.snippetory.test;

import static org.junit.Assert.*;

import org.jproggy.snippetory.Encodings;
import org.jproggy.snippetory.Syntaxes;
import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.engine.IncompatibleEncodingException;
import org.junit.Test;

public class EncodingTest {

	@Test
	public void encodingXML() throws Exception {
		TemplateContext ctx = new TemplateContext().encoding(Encodings.xml).syntax(Syntaxes.FLUYT);
		Template t = ctx.parse("$test");
		t.set("test", "<test>\n&amp;\n</test>");
		assertEquals("&lt;test>\n&amp;amp;\n&lt;/test>", t.toString());
		t.set("test", Encodings.xml.wrap("<test>&amp;</test>"));
		assertEquals("<test>&amp;</test>", t.toString());
		t.set("test", Encodings.html.wrap("<test>&amp;</test>"));
		assertEquals("<test>&amp;</test>", t.toString());
		try {
			t.set("test", Encodings.string.wrap("<test>&amp;</test>"));
			fail();
		} catch (IncompatibleEncodingException e) {
			//expected
		}
		t.set("test", Encodings.NULL.wrap("<test>\n&amp;\n</test>"));
		assertEquals("<test>\n&amp;\n</test>", t.toString());
	}

	@Test
	public void encodingHTML() throws Exception {
		TemplateContext ctx = new TemplateContext().encoding(Encodings.html).syntax(Syntaxes.FLUYT);
		Template t = ctx.parse("$test");
		t.set("test", "<test>&amp;</test>\n\rfoo\rbar");
		assertEquals("&lt;test>&amp;amp;&lt;/test><br />foo<br />bar", t.toString());
		t.set("test", Encodings.xml.wrap("<test>&amp;</test>"));
		assertEquals("<test>&amp;</test>", t.toString());
		t.set("test", Encodings.html.wrap("<test>&amp;</test>"));
		assertEquals("<test>&amp;</test>", t.toString());
		try {
			t.set("test", Encodings.string.wrap("<test>&amp;</test>"));
			fail();
		} catch (IncompatibleEncodingException e) {
			//expected
		}
		t.set("test", Encodings.NULL.wrap("<test>\n&amp;\n</test>"));
		assertEquals("<test>\n&amp;\n</test>", t.toString());
	}

	@Test
	public void encodingString() throws Exception {
		TemplateContext ctx = new TemplateContext().encoding(Encodings.string).syntax(Syntaxes.FLUYT);
		Template t = ctx.parse("$test");
		t.set("test", "<test>&amp;</test>\n\rfoo\rbar");
		assertEquals("<test>&amp;</test>\\n\\rfoo\\rbar", t.toString());
		t.set("test", Encodings.xml.wrap("<test>&amp;</test>"));
		assertEquals("<test>&amp;</test>", t.toString());
		t.set("test", Encodings.html.wrap("<test>&amp;</test>"));
		assertEquals("<test>&amp;</test>", t.toString());
		t.set("test", Encodings.string.wrap("<test>&amp;</test>"));
		assertEquals("<test>&amp;</test>", t.toString());
		t.set("test", Encodings.html_string.wrap("<test>&amp;</test>"));
		assertEquals("<test>&amp;</test>", t.toString());
		t.set("test", Encodings.NULL.wrap("<test>\n&amp;\n</test>"));
		assertEquals("<test>\n&amp;\n</test>", t.toString());
	}

	@Test
	public void encodingPlain() throws Exception {
		TemplateContext ctx = new TemplateContext().encoding(Encodings.plain).syntax(Syntaxes.FLUYT);
		Template t = ctx.parse("$test");
		t.set("test", "<test>&amp;</test>\n\rfoo\rbar");
		assertEquals("<test>&amp;</test>\n\rfoo\rbar", t.toString());
		try {
			t.set("test", Encodings.xml.wrap("<test>&amp;</test>"));
			fail();
		} catch (IncompatibleEncodingException e) {
			//expected
		}
		try {
			t.set("test", Encodings.html.wrap("<test>&amp;</test>"));
			fail();
		} catch (IncompatibleEncodingException e) {
			//expected
		}
		try {
			t.set("test", Encodings.string.wrap("<test>&amp;</test>"));
			fail();
		} catch (IncompatibleEncodingException e) {
			//expected
		}
		try {
			t.set("test", Encodings.html_string.wrap("<test>&amp;</test>"));
			fail();
		} catch (IncompatibleEncodingException e) {
			//expected
		}
		t.set("test", Encodings.NULL.wrap("<test>\n&amp;\n</test>"));
		assertEquals("<test>\n&amp;\n</test>", t.toString());
    }

    @Test
    public void encodingURL() throws Exception {
      TemplateContext ctx = new TemplateContext().encoding(Encodings.url).syntax(Syntaxes.FLUYT);
      Template t = ctx.parse("$test");
      t.set("test", "a.b cä+");
      assertEquals("a.b+c%C3%A4%2B", t.toString());
    }

    @Test
    public void encodingHtmlString() throws Exception {
		TemplateContext ctx = new TemplateContext().encoding(Encodings.html_string).syntax(Syntaxes.FLUYT);
		Template t = ctx.parse("$test");
		t.set("test", "<test>&amp;</test>\n\rfoo\rbar");
		assertEquals("&lt;test>&amp;amp;&lt;/test><br />foo<br />bar", t.toString());
		t.set("test", Encodings.xml.wrap("<test>\n&amp;\n</test>"));
		assertEquals("<test>\\n&amp;\\n</test>", t.toString());
		t.set("test", Encodings.html.wrap("<test>\n&amp;\n</test>"));
		assertEquals("<test>\\n&amp;\\n</test>", t.toString());
		t.set("test", Encodings.NULL.wrap("<test>\n&amp;\n</test>"));
		assertEquals("<test>\n&amp;\n</test>", t.toString());
		try {
			t.set("test", Encodings.string.wrap("<test>&amp;</test>"));
			fail();
		} catch (IncompatibleEncodingException e) {
			//expected
		}
	}
	
	@Test
	public void unkown() {
		try {
			Syntaxes.FLUYT.parse("$test(enc='test')");
			fail();
		} catch (RuntimeException e) {
			assertEquals("Encoding test not found.", e.getCause().getMessage());
		}
	}
}
