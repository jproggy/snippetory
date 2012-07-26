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

import static org.jproggy.snippetory.Syntaxes.HIDDEN_BLOCKS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Locale;

import junit.framework.Assert;

import org.jproggy.snippetory.Encodings;
import org.jproggy.snippetory.Repo;
import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.engine.ParseError;
import org.junit.Test;

public class BasicTest {
	@Test
	public void encoding() throws Exception {
		Template html = Repo.parse("{v:test enc='html'}");
		html.set("test", "<");
		assertEquals("&lt;", html.toString());
		html.append("test", "-");
		assertEquals("&lt;-", html.toString());
		html.set("test", ">'");
		assertEquals(">'", html.toString());
		Template plain = Encodings.plain.parse("{v:test}");
		plain.set("test", "<");
		assertEquals("<", plain.toString());
		plain.append("test", "-");
		assertEquals("<-", plain.toString());
		plain.set("test", "<");
		assertEquals("<", plain.toString());
		Template string = Repo.parse("{v:test enc='string'}");
		string.set("test", "<");
		assertEquals("<", string.toString());
		string.append("test", "\"");
		assertEquals("<\\\"", string.toString());
		string.set("test", ">");
		assertEquals(">", string.toString());
		html.append("test", plain);
		assertEquals(">'&lt;", html.toString());
		html.append("test", string);
		string.append("test", html);
		assertEquals(">>'&lt;>", string.toString());
		string.append("test", plain);
		string.append("test", string);
	}
	
	@Test
	public void formatString() {
		Template stretch = Repo.parse("{v:test stretch='5r'}");
		stretch.set("test", "x");
		assertEquals("    x", stretch.toString());
		stretch.set("test", "123456");
		assertEquals("123456", stretch.toString());
		Template shorten = Repo.parse("{v:test shorten='5...'}");
		shorten.set("test", "x");
		assertEquals("x", shorten.toString());
		shorten.set("test", "123456");
		assertEquals("12...", shorten.toString());
		shorten.set("test", "12345");
		assertEquals("12345", shorten.toString());
	}
	
	@Test
	public void formatNumber() {
		Template number = Repo.parse("{v:test number=\"0.00#\"}", Locale.GERMAN);
		number.set("test", "x");
		assertEquals("x", number.toString());
		number.set("test", "123456");
		assertEquals("123456", number.toString());
		number.set("test", 1.6);
		assertEquals("1,60", number.toString());
		number.set("test", 1.6333);
		number = Repo.parse("{v:test}", Locale.US);
		number.set("test", "x");
		assertEquals("x", number.toString());
		number.set("test", "123456");
		assertEquals("123456", number.toString());
		number.set("test", 1.6);
		assertEquals("1.6", number.toString());
		number.set("test", 1.6333);
		assertEquals("1.633", number.toString());
		number = Repo.parse("{v:test number='0.00#'}", Locale.GERMANY);
		number.set("test", 1.55);
		assertEquals("1,55", number.toString());
		number.set("test", 1.55555);
		assertEquals("1,556", number.toString());
	}
	
	@Test
	public void formatDate() {
		Template date = Repo.parse("{v:test date='JS_NEW'}");
		date.set("test", java.sql.Date.valueOf("2011-10-15"));
		assertEquals("new Date(2011, 10, 15)", date.toString());
		date = Repo.parse("{v:test date='JS_NEW'}", Locale.GERMAN);
		date.set("test", java.sql.Date.valueOf("2011-10-15"));
		assertEquals("new Date(2011, 10, 15)", date.toString());
		date = Repo.parse("{v:test date='medium'}", Locale.GERMAN);
		date.set("test", java.sql.Date.valueOf("2011-10-15"));
		assertEquals("15.10.2011", date.toString());
		date = Repo.parse("{v:test date=''}", Locale.GERMAN);
		date.set("test", java.sql.Date.valueOf("2011-10-15"));
		assertEquals("15.10.2011", date.toString());
		date = Repo.parse("{v:test date=\"\"}", Locale.GERMAN);
		date.set("test", java.sql.Date.valueOf("2011-10-15"));
		assertEquals("15.10.2011", date.toString());
		date = Repo.parse("<t:test date=''></t:test>", Locale.GERMAN);
		date.set("test", java.sql.Date.valueOf("2011-10-15"));
		assertEquals("15.10.2011", date.toString());
		date = Repo.parse("<t:test date=\"\"></t:test>", Locale.GERMAN);
		date.set("test", java.sql.Date.valueOf("2011-10-15"));
		assertEquals("15.10.2011", date.toString());
		date = Repo.parse("<t:test date='medium'>Date 1: {v:d1} Date 2: {v:d2} </t:test>", Locale.GERMAN).get("test");
		date.set("d1", java.sql.Date.valueOf("2011-10-15"));
		date.set("d2", java.sql.Date.valueOf("2011-10-06"));
		assertEquals("Date 1: 15.10.2011 Date 2: 06.10.2011 ", date.toString());
		date = Repo.parse("<t:test date=\"short_full\">Date 1: {v:d1 date='sql'} Date 2: {v:d2} </t:test>", Locale.GERMAN).get("test");
		date.set("d1", java.sql.Date.valueOf("2011-10-15"));
		date.set("d2", java.sql.Date.valueOf("2011-10-06"));
		assertEquals("Date 1: 2011-10-15 Date 2: 06.10.11 00:00 Uhr MESZ ", date.toString());
		date = Repo.read("<t:test>Date 1: {v:d1 date='_medium'} Date 2: {v:d2} </t:test>")
				.locale(Locale.GERMAN).attrib("date", "long_short").parse().get("test");
		date.set("d1", new Date(java.sql.Date.valueOf("2011-10-15").getTime() + 3915000l) );
		date.set("d2", java.sql.Date.valueOf("2011-10-06"));
		assertEquals("Date 1: 01:05:15 Date 2: 6. Oktober 2011 00:00 ", date.toString());
	}
	
	@Test
	public void delimiter() {
		Template t1 = Repo.parse("in ({v:test delimiter=', '})");
		t1.append("test", 5);
		assertEquals("in (5)", t1.toString());
		t1.append("test", 8);
		assertEquals("in (5, 8)", t1.toString());
		t1.append("test", 5);
		assertEquals("in (5, 8, 5)", t1.toString());
		Template t2 = Repo.parse("\"{v:test delimiter='\",\"'}\"");
		t2.append("test", 5);
		assertEquals("\"5\"", t2.toString());
		t2.append("test", "hallo");
		assertEquals("\"5\",\"hallo\"", t2.toString());
	}
	
	@Test
	public void indexDemo() throws Exception {
		  Method def = Template.class.getMethod("render", Template.class, String.class);
		  
		  // In a real world application we wouldn't have the template definition inside
		  // the code. Repo provides methods to read this from class path, file, Reader 
		  // and so on.
		  // switch over to another syntax. This could be done in template two. 
		  // Even multiple times.
		  Template method = Repo.read(
				  
		  	  // The mock data 'Template' and 'render' ensures to compile while the mark
		  	  // up code is hidden in comments
		  	  //         |------|                      |-----|
		      "/*t:type*/Template/*!t:type*/ /*t:name*/render/*!t:name*/" + 

		      // Inside this block we use the xml variant of syntax. Then the entire block
		      // appears as a comment to a compiler. 
		  	  //             meta data        repeated area
		  	  //          |------------|   |-----------------|
		      "(/*t:param delimiter=', '-->{v:type} param{v:i}<!--!t:param*/);"      
		  ).syntax(HIDDEN_BLOCKS)
		  // --> The US locale is typically a good choice for machine readable output.
		  .locale(Locale.US)
		  // After configuration we finally parse the template
		  .parse();
		  
		  String typeName = def.getReturnType().getSimpleName();
		  
		  // bind parent data
		  method.set("type", typeName).set("name", def.getName());
		  
		  for (int i = 0; i < def.getParameterTypes().length; i++) {
		    String paramType = def.getParameterTypes()[i].getSimpleName();
		    
		    // bind child data
		    method.get("param").set("type", paramType).set("i", i).render();

		  }
		  assertEquals("void render(Template param0, String param1);", 
				  method.toString());
	}
	
	@Test
	public void childTempates() {
		Template t1 = Repo.parse("in<t:test> and out</t:test> and around");
		assertEquals("in and around", t1.toString());
		t1.append("test", t1.get("test"));
		assertEquals("in and out and around", t1.toString());
		t1.append("test", t1.get("test"));
		assertEquals("in and out and out and around", t1.toString());
		t1.clear();
		assertEquals("in and around", t1.toString());
		Template t2 = Repo.parse("<t:outer>in<t:test> and {v:test}</t:test> and around</t:outer>").get("outer");
		t2.get("test").append("test", "hallo").render();
		assertEquals("in and hallo and around", t2.toString());
	}

	@Test
	public void hiddenBlox() {
		Template t1 = HIDDEN_BLOCKS.parse("/*t:test*/ i++; /*!t:test*/");
		assertEquals(" i++; ", t1.get("test").toString());
		Template t2 = HIDDEN_BLOCKS.parse("<!--t:test enc='url'*/ i++; /*!t:test-->");
		assertEquals(" i++; ", t2.get("test").toString());
		Template t3 = HIDDEN_BLOCKS.parse("/*t:test--> i++; <!--!t:test*/");
		assertEquals(" i++; ", t3.get("test").toString());
		Template t4 = HIDDEN_BLOCKS.parse("<!--t:test--> i++; <!--!t:test-->");
		assertEquals(" i++; ", t4.get("test").toString());
		Template t5 = HIDDEN_BLOCKS.parse("/*t:test stretch=\"10\"*/ i++; /*!t:test*/");
		t5.get("test").render();
		assertEquals(" i++;     ", t5.toString());
		Template t6 = HIDDEN_BLOCKS.parse("<!--t:test date=''*/ i++; /*!t:test-->");
		t6.get("test").render();
		assertEquals(" i++; ", t6.toString());
	}

	@Test
	public void lineRemoval() {
		Template t1 = Repo.parse(" \n <t:test>  \n  i++; \r\n  </t:test>  \n ");
		assertEquals("  i++; \r\n", t1.get("test").toString());
		t1.get("test").render();
		assertEquals(" \n  i++; \r\n ", t1.toString());
		
		t1 = Repo.parse(" <t:test>  \n  i++; \r\n  </t:test> ");
		assertEquals("  i++; \r\n", t1.get("test").toString());
		t1.get("test").render();
		assertEquals("  i++; \r\n", t1.toString());
		
		t1 = Repo.parse(" <t:test>  x\n  i++; \r\n  </t:test> ");
		assertEquals("  x\n  i++; \r\n", t1.get("test").toString());
		t1.get("test").render();
		assertEquals("   x\n  i++; \r\n", t1.toString());
		
		t1 = Repo.parse(" \r <t:test>  \r  i++; \r  </t:test>  \r ");
		assertEquals("  i++; \r", t1.get("test").toString());
		t1.get("test").render();
		assertEquals(" \r  i++; \r ", t1.toString());
		
		t1 = Repo.parse(" \r<t:test>\r  i++; \r</t:test> \r ");
		assertEquals("  i++; \r", t1.get("test").toString());
		t1.get("test").render();
		assertEquals(" \r  i++; \r ", t1.toString());
		
		t1 = Repo.parse(" \r<t:test>  \r  i++; \r  </t:test> \r ");
		assertEquals("  i++; \r", t1.get("test").toString());
		t1.get("test").render();
		assertEquals(" \r  i++; \r ", t1.toString());
		
		t1 = Repo.parse(" \r <t:test>\r  i++; \r</t:test>  \r ");
		assertEquals("  i++; \r", t1.get("test").toString());
		t1.get("test").render();
		assertEquals(" \r  i++; \r ", t1.toString());
		
		t1 = Repo.parse(" \r\n <t:test>  \r\n  i++; \r\n  </t:test>  \r\n ");
		assertEquals("  i++; \r\n", t1.get("test").toString());
		t1.get("test").render();
		assertEquals(" \r\n  i++; \r\n ", t1.toString());
		
		t1 = Repo.parse(" \r\n  <t:test>\r\n  i++; \r\n  </t:test>\r\n ");
		assertEquals("  i++; \r\n", t1.get("test").toString());
		t1.get("test").render();
		assertEquals(" \r\n  i++; \r\n ", t1.toString());
		
		t1 = Repo.parse(" \r\n\t<t:test>\t\r\n  i++; \r\n\t</t:test>\r\n ");
		assertEquals("  i++; \r\n", t1.get("test").toString());
		t1.get("test").render();
		assertEquals(" \r\n  i++; \r\n ", t1.toString());

		t1 = Repo.parse(" \r\n<t:test>  \r\n  i++; \r\n</t:test>  \r\n ");
		assertEquals("  i++; \r\n", t1.get("test").toString());
		t1.get("test").render();
		assertEquals(" \r\n  i++; \r\n ", t1.toString());

		t1 = Repo.parse(" \r\n<t:test>\r\n  i++; \r\n</t:test>\r\n ");
		assertEquals("  i++; \r\n", t1.get("test").toString());
		t1.get("test").render();
		assertEquals(" \r\n  i++; \r\n ", t1.toString());
	}

	@Test
	public void lineRemovalHB() {
		Template t1 = HIDDEN_BLOCKS.parse("  /*t:test*/  \n i++; \n   /*!t:test*/  \n");
		t1.append("test", t1.get("test"));
		assertEquals(" i++; \n", t1.toString());
		t1 = HIDDEN_BLOCKS.parse("/*t:test*/\n i++; \n/*!t:test*/\n");
		t1.append("test", t1.get("test"));
		assertEquals(" i++; \n", t1.toString());
		t1 = HIDDEN_BLOCKS.parse("/*t:test*/  \n i++; \n   /*!t:test*/\n");
		t1.append("test", t1.get("test"));
		assertEquals(" i++; \n", t1.toString());
		t1 = HIDDEN_BLOCKS.parse("  /*t:test*/\n i++; \n/*!t:test*/  ");
		t1.append("test", t1.get("test"));
		assertEquals(" i++; \n", t1.toString());
		Template t7 = HIDDEN_BLOCKS.parse("  /*t:test shorten='4-'-->  \n i++; \n   <!--!t:test*/  \n");
		t7.get("test").render();
		t7.get("test").render();
		assertEquals(" i+- i+-", t7.toString());
		Template t8 = HIDDEN_BLOCKS.parse("<!--t:test-->{v:test stretch='8r'}\n <!--!t:test-->\n");
		t8.get("test").append("test", "12345").append("test", "123").render();
		t8.get("test").set("test", "test").render();
		assertEquals("   12345     123\n    test\n", t8.toString());
	}
	
	@Test
	public void attributeEscaping() {
		Template t = Repo.parse("{v:x delimiter='\\''\tprefix=\"\\\"\" suffix='\\\\'}");
		t.append("x", "1").append("x", 2).append("x", "3");
		assertEquals("\"1'2'3\\", t.toString());
		t = Repo.parse("{v:x delimiter='\\n'\tprefix=\"\\t\" suffix='\\r\'}");
		t.append("x", "1").append("x", 2).append("x", "3");
		assertEquals("\t1\n2\n3\r", t.toString());
	}
	
	@Test
	public void backward() {
		Template t = Repo.parse("<a href='test.html'>Here</a> " +
				"<t:test_bw backward=\"href='(.*)'\" enc=\"url\">" +
				"{v:path delimiter='/'}/{v:file}.html" +
				"</t:test_bw>");
		t.get("test_bw").append("path", "x s").append("path", "xy+z").set("file", "tesst").render();
		assertEquals("<a href='x+s/xy%2Bz/tesst.html'>Here</a> ", t.toString());
		
		try {
			Repo.parse("lsdfkjsdfl {v:x backward='test'}" );
			Assert.fail();
		} catch (ParseError e) {
			assertTrue(e.getMessage().contains("target not found"));
			assertTrue(e.getMessage().contains("test"));
			assertTrue(e.getMessage().contains("{v:x backward='test'}"));
		}
		
		Template t2 = Repo.parse("Hello world{v:x backward='Hello' default='Liahallo'}{v:x backward='world' default='Welt'}");
		assertEquals("Liahallo Welt", t2.toString());
		
		try {
			Repo.parse("Hello world{v:x backward='world' default='Welt'}{v:x backward='Hello' default='Liahallo'}");
			fail();
		} catch(ParseError e) {
			assertTrue(e.getMessage().contains("target not found"));
			assertTrue(e.getMessage().contains("Hello"));
			assertTrue(e.getMessage().contains("{v:x backward='Hello' default='Liahallo'}"));
		}
	}
	
	@Test
	public void toggle() {
		Template t = Repo.parse("<t:test>{v:toggle='1;2;3'}. {v: toggle='unpair;pair'}\n</t:test>");
		t.get("test").render();
		t.get("test").render();
		t.get("test").render();
		assertEquals("1. unpair\n2. pair\n3. unpair\n", t.toString());
		t.clear();
		t.get("test").render();
		t.get("test").render();
		t.get("test").render();
		assertEquals("1. pair\n2. unpair\n3. pair\n", t.toString());
		t = Repo.parse("<t:test>{v:x toggle='1;2;3'}. {v:x toggle='unpair;pair'}\n</t:test>");
		t.get("test").set("x", 1).render();
		t.get("test").set("x", 2).render();
		t.get("test").set("x", 3).render();
		assertEquals("1. unpair\n2. pair\n3. unpair\n", t.toString());
		t.clear();
		t.get("test").set("x", 1).render();
		t.get("test").set("x", 2).render();
		t.get("test").set("x", 3).render();
		assertEquals("1. unpair\n2. pair\n3. unpair\n", t.toString());
		t.clear();
		t.get("test").set("x", 1).render();
		t.get("test").set("x", 0).render();
		t.get("test").set("x", -1).render();
		assertEquals("1. unpair\n2. pair\n3. unpair\n", t.toString());
	}
}
