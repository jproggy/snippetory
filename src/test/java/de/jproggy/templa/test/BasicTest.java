package de.jproggy.templa.test;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;

import de.jproggy.templa.Templa;
import de.jproggy.templa.Templa.Syntax;
import de.jproggy.templa.Template;
import de.jproggy.templa.impl.IncompatibleEncodingException;

public class BasicTest {
	@Test
	public void encoding() throws Exception {
		Template html = Templa.fromString("{v:test enc='html'}");
		html.set("test", "<");
		assertEquals("&lt;", html.toString());
		html.append("test", "-");
		assertEquals("&lt;-", html.toString());
		html.set("test", ">'");
		assertEquals(">'", html.toString());
		Template plain = Templa.fromString("{v:test enc='plain'}");
		plain.set("test", "<");
		assertEquals("<", plain.toString());
		plain.append("test", "-");
		assertEquals("<-", plain.toString());
		plain.set("test", "<");
		assertEquals("<", plain.toString());
		Template string = Templa.fromString("{v:test enc='string'}");
		string.set("test", "<");
		assertEquals("<", string.toString());
		string.append("test", "\"");
		assertEquals("<\\\"", string.toString());
		string.set("test", ">");
		assertEquals(">", string.toString());
		try {
			plain.append("test", html);
			fail();
		} catch (IncompatibleEncodingException e) {
			assertEquals("<", plain.toString());
		}
		try {
			plain.append("test", string);
			
		} catch (IncompatibleEncodingException e) {
			assertEquals("<", plain.toString());
		}
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
		Template stretch = Templa.fromString("{v:test stretch='5r'}");
		stretch.set("test", "x");
		assertEquals("    x", stretch.toString());
		stretch.set("test", "123456");
		assertEquals("123456", stretch.toString());
		Template shorten = Templa.fromString("{v:test shorten='5...'}");
		shorten.set("test", "x");
		assertEquals("x", shorten.toString());
		shorten.set("test", "123456");
		assertEquals("12...", shorten.toString());
		shorten.set("test", "12345");
		assertEquals("12345", shorten.toString());
	}
	
	@Test
	public void formatNumber() {
		Template number = Templa.fromString("{v:test number=\"0.00#\"}");
		number.set("test", "x");
		assertEquals("x", number.toString());
		number.set("test", "123456");
		assertEquals("123456", number.toString());
		number.set("test", 1.6);
		assertEquals("1.60", number.toString());
		number.set("test", 1.6333);
		number = Templa.fromString("{v:test}");
		number.set("test", "x");
		assertEquals("x", number.toString());
		number.set("test", "123456");
		assertEquals("123456", number.toString());
		number.set("test", 1.6);
		assertEquals("1.6", number.toString());
		number.set("test", 1.6333);
		assertEquals("1.6333", number.toString());
		number = Templa.fromString("{v:test number='0.00#'}", Locale.GERMANY);
		number.set("test", 1.55);
		assertEquals("1,55", number.toString());
		number.set("test", 1.55555);
		assertEquals("1,556", number.toString());
	}
	
	@Test
	public void formatDate() {
		Template date = Templa.fromString("{v:test date='JS_NEW'}");
		date.set("test", java.sql.Date.valueOf("2011-10-15"));
		assertEquals("new Date(2011, 10, 15)", date.toString());
		date = Templa.fromString("{v:test date='JS_NEW'}", Locale.GERMAN);
		date.set("test", java.sql.Date.valueOf("2011-10-15"));
		assertEquals("new Date(2011, 10, 15)", date.toString());
		date = Templa.fromString("{v:test date='medium'}", Locale.GERMAN);
		date.set("test", java.sql.Date.valueOf("2011-10-15"));
		assertEquals("15.10.2011", date.toString());
		date = Templa.fromString("<t:test date='medium'>Date 1: {v:d1} Date 2: {v:d2} </t:test>", Locale.GERMAN).get("test");
		date.set("d1", java.sql.Date.valueOf("2011-10-15"));
		date.set("d2", java.sql.Date.valueOf("2011-10-06"));
		assertEquals("Date 1: 15.10.2011 Date 2: 06.10.2011 ", date.toString());
	}
	
	@Test
	public void delimiter() {
		Template t1 = Templa.fromString("in ({v:test delimiter=', '})");
		t1.append("test", 5);
		assertEquals("in (5)", t1.toString());
		t1.append("test", 8);
		assertEquals("in (5, 8)", t1.toString());
		t1.append("test", 5);
		assertEquals("in (5, 8, 5)", t1.toString());
		Template t2 = Templa.fromString("\"{v:test delimiter='\",\"'}\"");
		t2.append("test", 5);
		assertEquals("\"5\"", t2.toString());
		t2.append("test", "hallo");
		assertEquals("\"5\",\"hallo\"", t2.toString());
	}
	
	@Test
	public void childTempates() {
		Template t1 = Templa.fromString("in<t:test> and out</t:test> and around");
		assertEquals("in and around", t1.toString());
		t1.append("test", t1.get("test"));
		assertEquals("in and out and around", t1.toString());
		t1.append("test", t1.get("test"));
		assertEquals("in and out and out and around", t1.toString());
		t1.clear();
		assertEquals("in and around", t1.toString());
		Template t2 = Templa.fromString("<t:outer>in<t:test> and {v:test}</t:test> and around</t:outer>").get("outer");
		t2.get("test").append("test", "hallo").render();
		assertEquals("in and hallo and around", t2.toString());
	}
	
	@Test
	public void hiddenBlox() {
		Template t1 = Syntax.HIDDEN_BLOCKS.fromString("/*t:test*/ i++; /*!t:test*/");
		assertEquals(" i++; ", t1.get("test").toString());		
		Template t2 = Syntax.HIDDEN_BLOCKS.fromString("<!--t:test url='utf-8' */ i++; /*!t:test-->");
		assertEquals(" i++; ", t2.get("test").toString());		
		Template t3 = Syntax.HIDDEN_BLOCKS.fromString("/*t:test--> i++; <!--!t:test*/");
		assertEquals(" i++; ", t3.get("test").toString());		
		Template t4 = Syntax.HIDDEN_BLOCKS.fromString("<!--t:test--> i++; <!--!t:test-->");
		assertEquals(" i++; ", t4.get("test").toString());		
	}
	@Test
	public void lineRemoval() {
		Template t1 = Syntax.HIDDEN_BLOCKS.fromString("  /*t:test*/  \n i++; \n   /*!t:test*/  \n");
		t1.append("test", t1.get("test"));
		assertEquals(" i++; ", t1.toString());		
	}
}
