package org.jproggy.snippetory.test;

import static org.jproggy.snippetory.Syntaxes.XML_ALIKE;
import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Locale;

import org.jproggy.snippetory.Template;
import org.junit.Test;

public class DateFormatTest {
	private static final java.sql.Date D1 = java.sql.Date.valueOf("2011-10-15");
	private static final Date D1_TIME = new Date(D1.getTime() + 3915000l);

	@Test
	public void jsDate() {
		Template date = XML_ALIKE.parse("{v:test date='JS'}");
		date.set("test", D1);
		assertEquals("new Date(2011, 10, 15)", date.toString());
	}
	
	@Test
	public void jsTime() {
		Template date = XML_ALIKE.parse("{v:test date='_JS'}");
		date.set("test", D1_TIME);
		assertEquals("new Date(0, 0, 0, 01, 05, 15)", date.toString());
	}
	
	@Test
	public void js() {
		Template date = XML_ALIKE.parse("{v:test date='JS_JS'}", Locale.GERMAN);
		date.set("test", D1_TIME);
		assertEquals("new Date(2011, 10, 15, 01, 05, 15)", date.toString());
	}
	
	@Test
	public void sqlDate() {
		Template date = XML_ALIKE.parse("{v:test date='sql'}");
		date.set("test", D1);
		assertEquals("2011-10-15", date.toString());
	}
	
	@Test
	public void sql() {
		Template date = XML_ALIKE.parse("{v:test date='sql_sql'}", Locale.GERMAN);
		date.set("test", D1_TIME);
		assertEquals("2011-10-15 01:05:15", date.toString());
	}
	
	@Test
	public void sqlTime() {
		Template date = XML_ALIKE.parse("{v:test date='_sql'}");
		date.set("test", D1_TIME);
		assertEquals("01:05:15", date.toString());
	}
	
	@Test
	public void germanLong() {
		Template date = XML_ALIKE.parse("{v:test date='long'}", Locale.GERMAN);
		date.set("test", D1);
		assertEquals("15. Oktober 2011", date.toString());
	}
	
	@Test
	public void germanFullShort() {
		Template date = XML_ALIKE.parse("{v:test date='full_short'}", Locale.GERMAN);
		date.set("test", D1);
		assertEquals("Samstag, 15. Oktober 2011 00:00", date.toString());
	}
	
	@Test
	public void germanFullFull() {
		Template date = XML_ALIKE.parse("{v:test date=\"full_full\"}", Locale.GERMAN);
		date.set("test", D1);
		assertEquals("Samstag, 15. Oktober 2011 00:00 Uhr MESZ", date.toString());
	}
	
	@Test
	public void germanShortLong() {
		Template date = XML_ALIKE.parse("<t:test date='short_long'></t:test>", Locale.GERMAN);
		date.set("test", D1);
		assertEquals("15.10.11 00:00:00 MESZ", date.toString());
	}
	
	@Test
	public void germanMask() {
		Template date = XML_ALIKE.parse("<t:test date=\"w/MM yyyy\"></t:test>", Locale.GERMAN);
		date.set("test", D1);
		assertEquals("41/10 2011", date.toString());
	}
	
	@Test
	public void chinaLong() {
		Template date = XML_ALIKE.parse("{v:test date='long'}", Locale.SIMPLIFIED_CHINESE);
		date.set("test", D1);
		assertEquals("2011年10月15日", date.toString());
	}
	
	@Test
	public void jpLongLong() {
		Template date = XML_ALIKE.parse("{v:test date='long_long'}", Locale.JAPANESE);
		date.set("test", D1);
		assertEquals("2011/10/15 0:00:00 CEST", date.toString());
	}
	
	@Test
	public void frFullLong() {
		Template date = XML_ALIKE.read("{v:test}").locale(Locale.FRENCH).attrib("date", "full_long").parse();
		date.set("test", D1);
		assertEquals("samedi 15 octobre 2011 00:00:00 CEST", date.toString());
	}
	
	@Test
	public void korean() {
		Template date = XML_ALIKE.parse("{v:test date=\"\"}", Locale.KOREAN);
		date.set("test", D1);
		assertEquals("2011. 10. 15", date.toString());
	}
	
	@Test
	public void defaultGerman1() {
		Template date = XML_ALIKE.parse("{v:test}", Locale.GERMAN);
		date.set("test", D1);
		assertEquals("15.10.2011", date.toString());
	}
	
	@Test
	public void defaultGerman2() {
		Template date = XML_ALIKE.parse("{v:test date=\"\"}", Locale.GERMAN);
		date.set("test", D1);
		assertEquals("15.10.2011", date.toString());
	}
	
	@Test
	public void defaultGerman3() {
		Template date = XML_ALIKE.parse("<t:test date=''></t:test>", Locale.GERMAN);
		date.set("test", D1);
		assertEquals("15.10.2011", date.toString());
	}
	
	@Test
	public void defaultGerman4() {
		Template date = XML_ALIKE.parse("<t:test date=\"\"></t:test>", Locale.GERMAN);
		date.set("test", D1);
		assertEquals("15.10.2011", date.toString());
	}
}