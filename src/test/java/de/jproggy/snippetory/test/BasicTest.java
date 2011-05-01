package de.jproggy.snippetory.test;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Locale;

import org.junit.Test;

import de.jproggy.snippetory.Repo;
import de.jproggy.snippetory.Snippetory;
import de.jproggy.snippetory.Syntaxes;

public class BasicTest {
//	@Test
	public void encoding() throws Exception {
		Snippetory html = Repo.parse("{v:test enc='html'}");
		html.set("test", "<");
		assertEquals("&lt;", html.toString());
		html.append("test", "-");
		assertEquals("&lt;-", html.toString());
		html.set("test", ">'");
		assertEquals(">'", html.toString());
		Snippetory plain = Repo.parse("{v:test enc='plain'}");
		plain.set("test", "<");
		assertEquals("<", plain.toString());
		plain.append("test", "-");
		assertEquals("<-", plain.toString());
		plain.set("test", "<");
		assertEquals("<", plain.toString());
		Snippetory string = Repo.parse("{v:test enc='string'}");
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
		assertEquals(">>\\\'&lt;>", string.toString());
		string.append("test", plain);
		string.append("test", string);
	}
	
	@Test
	public void formatString() {
		Snippetory stretch = Repo.parse("{v:test stretch='5r'}");
		stretch.set("test", "x");
		assertEquals("    x", stretch.toString());
		stretch.set("test", "123456");
		assertEquals("123456", stretch.toString());
		Snippetory shorten = Repo.parse("{v:test shorten='5...'}");
		shorten.set("test", "x");
		assertEquals("x", shorten.toString());
		shorten.set("test", "123456");
		assertEquals("12...", shorten.toString());
		shorten.set("test", "12345");
		assertEquals("12345", shorten.toString());
	}
	
	@Test
	public void formatNumber() {
		Snippetory number = Repo.parse("{v:test number=\"0.00#\"}");
		number.set("test", "x");
		assertEquals("x", number.toString());
		number.set("test", "123456");
		assertEquals("123456", number.toString());
		number.set("test", 1.6);
		assertEquals("1.60", number.toString());
		number.set("test", 1.6333);
		number = Repo.parse("{v:test}");
		number.set("test", "x");
		assertEquals("x", number.toString());
		number.set("test", "123456");
		assertEquals("123456", number.toString());
		number.set("test", 1.6);
		assertEquals("1.6", number.toString());
		number.set("test", 1.6333);
		assertEquals("1.6333", number.toString());
		number = Repo.parse("{v:test number='0.00#'}", Locale.GERMANY);
		number.set("test", 1.55);
		assertEquals("1,55", number.toString());
		number.set("test", 1.55555);
		assertEquals("1,556", number.toString());
	}
	
	@Test
	public void formatDate() {
		Snippetory date = Repo.parse("{v:test date='JS_NEW'}");
		date.set("test", java.sql.Date.valueOf("2011-10-15"));
		assertEquals("new Date(2011, 10, 15)", date.toString());
		date = Repo.parse("{v:test date='JS_NEW'}", Locale.GERMAN);
		date.set("test", java.sql.Date.valueOf("2011-10-15"));
		assertEquals("new Date(2011, 10, 15)", date.toString());
		date = Repo.parse("{v:test date='medium'}", Locale.GERMAN);
		date.set("test", java.sql.Date.valueOf("2011-10-15"));
		assertEquals("15.10.2011", date.toString());
		date = Repo.parse("<t:test date='medium'>Date 1: {v:d1} Date 2: {v:d2} </t:test>", Locale.GERMAN).get("test");
		date.set("d1", java.sql.Date.valueOf("2011-10-15"));
		date.set("d2", java.sql.Date.valueOf("2011-10-06"));
		assertEquals("Date 1: 15.10.2011 Date 2: 06.10.2011 ", date.toString());
	}
	
	@Test
	public void delimiter() {
		Snippetory t1 = Repo.parse("in ({v:test delimiter=', '})");
		t1.append("test", 5);
		assertEquals("in (5)", t1.toString());
		t1.append("test", 8);
		assertEquals("in (5, 8)", t1.toString());
		t1.append("test", 5);
		assertEquals("in (5, 8, 5)", t1.toString());
		Snippetory t2 = Repo.parse("\"{v:test delimiter='\",\"'}\"");
		t2.append("test", 5);
		assertEquals("\"5\"", t2.toString());
		t2.append("test", "hallo");
		assertEquals("\"5\",\"hallo\"", t2.toString());
	}
	
	@Test
	public void x() throws Exception {
		Method def = Snippetory.class.getMethod("render", Snippetory.class, String.class);
		 Snippetory method = Repo.parse("{v:type} {v:name}(<t:param delimiter=', '>{v:type} param{v:i}</t:param>)");
		  method.set("type", def.getReturnType().getSimpleName())
		        .set("name", def.getName());
		  for (int i = 0; i < def.getParameterTypes().length; i++) {
		    method.get("param")
		          .set("type", def.getParameterTypes()[i].getSimpleName())
		          .set("i", i)
		          .render();
		  }
		  method.render(System.out);
	}
	
	@Test
	public void childTempates() {
		Snippetory t1 = Repo.parse("in<t:test> and out</t:test> and around");
		assertEquals("in and around", t1.toString());
		t1.append("test", t1.get("test"));
		assertEquals("in and out and around", t1.toString());
		t1.append("test", t1.get("test"));
		assertEquals("in and out and out and around", t1.toString());
		t1.clear();
		assertEquals("in and around", t1.toString());
		Snippetory t2 = Repo.parse("<t:outer>in<t:test> and {v:test}</t:test> and around</t:outer>").get("outer");
		t2.get("test").append("test", "hallo").render();
		assertEquals("in and hallo and around", t2.toString());
	}
	
	@Test
	public void hiddenBlox() {
		Snippetory t1 = Repo.read("/*t:test*/ i++; /*!t:test*/")
				.syntax(Syntaxes.HIDDEN_BLOCKS).parse();
		assertEquals(" i++; ", t1.get("test").toString());		
		Snippetory t2 = Repo.read("<!--t:test url='utf-8' */ i++; /*!t:test-->")
				.syntax(Syntaxes.HIDDEN_BLOCKS).parse();
		assertEquals(" i++; ", t2.get("test").toString());		
		Snippetory t3 = Repo.read("/*t:test--> i++; <!--!t:test*/")
				.syntax(Syntaxes.HIDDEN_BLOCKS).parse();
		assertEquals(" i++; ", t3.get("test").toString());		
		Snippetory t4 = Repo.read("<!--t:test--> i++; <!--!t:test-->")
				.syntax(Syntaxes.HIDDEN_BLOCKS).parse();
		assertEquals(" i++; ", t4.get("test").toString());		
	}
	@Test
	public void lineRemoval() {
		Snippetory t1 = Repo.parse(" \n <t:test>  \n  i++; \n\r  </t:test>  \n ");
		t1.append("test", t1.get("test"));
		assertEquals(" \n  i++; \n\r ", t1.toString());		
	}
	@Test
	public void lineRemovalHB() {
		Snippetory t1 = Repo.read("  /*t:test*/  \n i++; \n   /*!t:test*/  \n")
				.syntax(Syntaxes.HIDDEN_BLOCKS).parse();
		t1.append("test", t1.get("test"));
		assertEquals(" i++; \n", t1.toString());		
	}
}
