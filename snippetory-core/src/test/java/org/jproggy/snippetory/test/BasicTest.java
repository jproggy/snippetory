/*******************************************************************************
 * Copyright (c) 2011-2012 JProggy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * EXCEPT AS EXPRESSLY SET FORTH IN THIS AGREEMENT, THE PROGRAM IS PROVIDED ON AN
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS OF TITLE,
 * NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE
 *******************************************************************************/

package org.jproggy.snippetory.test;

import static org.jproggy.snippetory.Syntaxes.FLUYT_CC;
import static org.jproggy.snippetory.Syntaxes.XML_ALIKE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.TimeZone;

import junit.framework.Assert;

import org.jproggy.snippetory.Repo;
import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.engine.ParseError;
import org.junit.Test;

public class BasicTest {
  static {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
  }

	@Test
	public void delimiter() {
		Template t1 = XML_ALIKE.parse("in ({v:test delimiter=', '})");
		t1.append("test", 5);
		assertEquals("in (5)", t1.toString());
		t1.append("test", 8);
		assertEquals("in (5, 8)", t1.toString());
		t1.append("test", 5);
		assertEquals("in (5, 8, 5)", t1.toString());
	}

	@Test
	public void delimiterEscaped() throws Exception {
		Template t2 = XML_ALIKE.parse("\"{v:test delimiter='\",\"'}\"");
		assertEquals("\"{v:test delimiter='\",\"'}\"", t2.toString());
		t2.append("test", 5);
		assertEquals("\"5\"", t2.toString());
		t2.append("test", "hallo");
		assertEquals("\"5\",\"hallo\"", t2.toString());
	}

	@Test
	public void indexDemo() throws Exception {
		  Method def = Template.class.getMethod("render", Template.class, String.class);

		  // Repo provides methods to read this from class path, file, Reader and so on.
		  // The typical workflow consists of getting the template data, configure the
		  // context and finally parse to materialize the template object.
		  Template method = Repo.readResource("method.tpl").syntax(FLUYT_CC).parse();

		  // bind parent data
		  String typeName = def.getReturnType().getSimpleName();
		  method.set("type", typeName);
		  method.set("name", def.getName());

		  for (int i = 0; i < def.getParameterTypes().length; i++) {
		    String paramType = def.getParameterTypes()[i].getSimpleName();

		    // bind child data -> the builder interface helps to have concise code
		    method.get("param").set("type", paramType).set("i", i).render();
		  }
		  assertEquals("void render(Template param0, String param1);",
				  method.toString());
	}

	@Test
	public void childTempates() {
		Template t1 = XML_ALIKE.parse("in<t:test> and out</t:test> and around");
		t1.render();
		assertEquals("in and around", t1.toString());
		t1.append("test", t1.get("test"));
		assertEquals("in and out and around", t1.toString());
		t1.append("test", t1.get("test"));
		assertEquals("in and out and out and around", t1.toString());
		t1.clear();
		assertEquals("in and around", t1.toString());
	}

	@Test
	public void childTempatesNested() throws Exception {
		Template t2 = XML_ALIKE.parse("<t:outer>in<t:test> and {v:test}</t:test> and around</t:outer>").get("outer");
		t2.get("test").append("test", "hallo").render();
		assertEquals("in and hallo and around", t2.toString());
	}

	@Test
	public void syntaxSwitchFail() {
		try {
			XML_ALIKE.parse(" {v:test} \n <s:C_COMMENTS_X />  \n /*${test}*/ \r\n  /*Syntax:FLUYT*/  \n #test ");
			fail();
		} catch (RuntimeException e) {
			assertEquals("Unkown syntax: C_COMMENTS_X", e.getCause().getMessage());
		}
	}

	@Test
	public void syntaxSwitch() {
		Template t1 = XML_ALIKE.parse(" {v:test} \n <s:C_COMMENTS />  \n /*${test}*/ \r\n  /*Syntax:FLUYT*/  \n $test ");
		t1.set("test", "blub");
		assertEquals(" blub \n blub \r\n blub ", t1.toString());
		t1 = XML_ALIKE.parse(" {v:test} \n // Syntax:C_COMMENTS   \n /*${test}*/ \r\n  /* Syntax:FLUYT */  \n $test ");
		t1.set("test", "blub");
		assertEquals(" blub \n blub \r\n blub ", t1.toString());
		t1 = XML_ALIKE.parse(" {v:test} \n <!-- Syntax:C_COMMENTS -->  \n /*${test}*/ \r\n  # Syntax:FLUYT  \n $test ");
		t1.set("test", "blub");
		assertEquals(" blub \n blub \r\n blub ", t1.toString());
	}

	@Test
	public void attributeEscaping() {
		Template t = XML_ALIKE.parse("{v:x delimiter='\\''\tprefix=\"\\\"\" suffix='\\\\'}");
		assertEquals("", t.toString());
		t.append("x", "1").append("x", 2).append("x", "3");
		assertEquals("\"1'2'3\\", t.toString());
		t = XML_ALIKE.parse("{v:x delimiter='\\n'\tprefix=\"\\t\" suffix='\\r\'}");
		assertEquals("", t.toString());
		t.append("x", "1").append("x", 2).append("x", "3");
		assertEquals("\t1\n2\n3\r", t.toString());
		t = XML_ALIKE.parse("{v:x default='\\b'\tprefix=\"\\f\" suffix='xx'}");
		assertEquals("\b", t.toString());
		t.append("x", "1").append("x", 2).append("x", "3");
		assertEquals("\f123xx", t.toString());
	}

	@Test
	public void backward() {
		Template t = XML_ALIKE.parse("<a href='test.html'>Here</a> " +
				"<t:test_bw backward=\"href='(.*)'\" enc=\"url\">" +
				"{v:path delimiter='/'}/{v:file}.html" +
				"</t:test_bw>");
		t.get("test_bw").append("path", "x s").append("path", "xy+z").set("file", "tesst").render();
		assertEquals("<a href='x+s/xy%2Bz/tesst.html'>Here</a> ", t.toString());

		try {
			XML_ALIKE.parse("lsdfkjsdfl {v:x backward='test'}" );
			Assert.fail();
		} catch (ParseError e) {
			assertTrue(e.getMessage().contains("target not found"));
			assertTrue(e.getMessage().contains("test"));
			assertTrue(e.getMessage().contains("{v:x backward='test'}"));
		}

		Template t2 = XML_ALIKE.parse("Hello world{v:x backward='Hello' default='Liahallo'}{v:x backward='world' default='Welt'}");
		assertEquals("Liahallo Welt", t2.toString());

		try {
			XML_ALIKE.parse("Hello world{v:x backward='world' default='Welt'}{v:x backward='Hello' default='Liahallo'}");
			fail();
		} catch(ParseError e) {
			assertTrue(e.getMessage().contains("target not found"));
			assertTrue(e.getMessage().contains("Hello"));
			assertTrue(e.getMessage().contains("{v:x backward='Hello' default='Liahallo'}"));
		}

		try {
			XML_ALIKE.parse("Hello world{v:x backward='(Hello)(v)' default='Liahallo'}{v:x backward='world' default='Welt'}");
			fail();
		} catch(ParseError e) {
			assertTrue(e.getMessage(), e.getMessage().contains("target not found: (Hello)(v)"));
			assertTrue(e.getMessage(), e.getMessage().contains("{v:x backward='(Hello)(v)' default='Liahallo'}"));
		}

		try {
			XML_ALIKE.parse("Hello Hello world{v:x backward='(Hello)' default='Liahallo'}{v:x backward='world' default='Welt'}");
			fail();
		} catch(ParseError e) {
			assertTrue(e.getMessage(), e.getMessage().contains("backward target ambigous: (Hello)"));
			assertTrue(e.getMessage(), e.getMessage().contains("{v:x backward='(Hello)' default='Liahallo'}"));
		}

		try {
			XML_ALIKE.parse("Hello world{v:x backward='(Hel)(lo)' default='Liahallo'}{v:x backward='world' default='Welt'}");
			fail();
		} catch(ParseError e) {
			assertTrue(e.getMessage(), e.getMessage().contains("only one match group allowed: (Hel)(lo)"));
			assertTrue(e.getMessage(), e.getMessage().contains("{v:x backward='(Hel)(lo)' default='Liahallo'}"));
		}
	}

	@Test
	public void errors() {
		try {
			XML_ALIKE.parse("before<t:>startend</t:>after");
			fail();
		} catch (Exception e) {
			assertEquals("Conditional region needs to contain at least one named location, or will never be rendered  Error while parsing </t:> at line 1 character 18", e.getMessage());
		}
		try {
			XML_ALIKE.parse("before\n<t:>\rstartend\r\n</t:end>\r\nafter");
			fail();
		} catch (Exception e) {
			assertEquals("1 unclosed conditional regions detected  Error while parsing </t:end>\r\n at line 4 character 1", e.getMessage());
		}
		try {
			XML_ALIKE.parse("before<t:start>startend</t:end>after");
			fail();
		} catch (Exception e) {
			assertEquals("end found but start expected  Error while parsing </t:end> at line 1 character 23", e.getMessage());
		}
		try {
			XML_ALIKE.parse("before<t:x>startend</tx>after");
			fail();
		} catch (Exception e) {
			assertEquals("No end element for x  Error while parsing startend</tx>after at line 1 character 11", e.getMessage());
		}
		try {
			XML_ALIKE.parse("before<tx>startend</t:x>after");
			fail();
		} catch (Exception e) {
			assertEquals("x found but file end expected  Error while parsing </t:x> at line 1 character 18", e.getMessage());
		}
		try {
			XML_ALIKE.parse("before<tx>startend</t:x>after");
			fail();
		} catch (Exception e) {
			assertEquals("x found but file end expected  Error while parsing </t:x> at line 1 character 18", e.getMessage());
		}
	}

	@Test
	public void conditionalRegionsSimple() {
		Template t = XML_ALIKE.parse("before<t:>->{v:test null='null' delimiter=' '}<-</t:>after");
		assertEquals("beforeafter", t.toString());
		t.set("test", null);
		assertEquals("beforeafter", t.toString());
		t.set("blub", null);
		assertEquals("beforeafter", t.toString());
		t.append("test", null);
		assertEquals("beforeafter", t.toString());
		t.append("test", "test");
		assertEquals("before->null null test<-after", t.toString());
		t.set("test", "blub");
		assertEquals("before->blub<-after", t.toString());
	}

	@Test
	public void conditionalRegions() {
		Template t = XML_ALIKE.read("before-><t:test><t: pad='30' pad.align='right'><t: pad='12' pad.fill='.'>start{v:test}</t:><middle>{v:test}end</t:></t:test><-after").parse();
		assertEquals("before-><-after", t.toString());
		Template test = t.get("test");
		assertEquals("", test.toString());
		test.set("test", "xxx");
		assertEquals("    startxxx....<middle>xxxend", test.toString());
		test.render();
		assertEquals("before->    startxxx....<middle>xxxend<-after", t.toString());
		test.clear();
		assertEquals("", test.toString());
		assertEquals("before->    startxxx....<middle>xxxend<-after", t.toString());
		test.set("test", "xxx");
		assertEquals("    startxxx....<middle>xxxend", test.toString());
		Template test2 = t.get("test");
		assertEquals("", test2.toString());
		test2.set("test", "222");
		assertEquals("    start222....<middle>222end", test2.toString());
		assertEquals("    startxxx....<middle>xxxend", test.toString());
		test2.append("test", "s");
		assertEquals("   start222s...<middle>222send", test2.toString());
		assertEquals("    startxxx....<middle>xxxend", test.toString());
		test2.append("tust", "s");
		assertEquals("   start222s...<middle>222send", test2.toString());
		test2.set("tust", "s");
		assertEquals("   start222s...<middle>222send", test2.toString());
		test2.render();
		assertEquals("before->    startxxx....<middle>xxxend   start222s...<middle>222send<-after", t.toString());
		t = t.get();
		assertEquals("before-><-after", t.toString());
		test = t.get("test");
		assertEquals("", test.toString());
		test.set("test", "xxx");
		assertEquals("    startxxx....<middle>xxxend", test.toString());
		test.render();
		assertEquals("before->    startxxx....<middle>xxxend<-after", t.toString());
		test.clear();
		assertEquals("", test.toString());
		assertEquals("before->    startxxx....<middle>xxxend<-after", t.toString());
		test.set("test", "xxx");
		assertEquals("    startxxx....<middle>xxxend", test.toString());
		test2 = t.get("test");
		assertEquals("", test2.toString());
		test2.set("test", "222");
		assertEquals("    start222....<middle>222end", test2.toString());
		assertEquals("    startxxx....<middle>xxxend", test.toString());
		test2.append("test", "s");
		assertEquals("   start222s...<middle>222send", test2.toString());
		assertEquals("    startxxx....<middle>xxxend", test.toString());
		test2.append("tust", "s");
		assertEquals("   start222s...<middle>222send", test2.toString());
		test2.set("tust", "s");
		assertEquals("   start222s...<middle>222send", test2.toString());
		test2.render();
		assertEquals("before->    startxxx....<middle>xxxend   start222s...<middle>222send<-after", t.toString());
	}

}
