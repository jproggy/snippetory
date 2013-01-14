package org.jproggy.snippetory.test;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Locale;

import org.jproggy.snippetory.Repo;
import org.jproggy.snippetory.Template;
import org.junit.Test;

public class DateFormatTest {
	private static final java.sql.Date D2 = java.sql.Date.valueOf("2011-10-06");
	private static final java.sql.Date D1 = java.sql.Date.valueOf("2011-10-15");
	private static final Date D1_TIME = new Date(D1.getTime() + 3915000l);

	@Test
	public void js() {
		Template date = Repo.parse("{v:test date='JS'}");
		date.set("test", D1);
		assertEquals("new Date(2011, 10, 15)", date.toString());
		date = Repo.parse("{v:test date='JS_JS'}", Locale.GERMAN);
		date.set("test", D1_TIME);
		assertEquals("new Date(2011, 10, 15, 01, 05, 15)", date.toString());
		date = Repo.parse("{v:test date='_JS'}");
		date.set("test", D1_TIME);
		assertEquals("new Date(0, 0, 0, 01, 05, 15)", date.toString());
	}
	
	@Test
	public void sql() {
		Template date = Repo.parse("{v:test date='sql'}");
		date.set("test", D1);
		assertEquals("2011-10-15", date.toString());
		date = Repo.parse("{v:test date='sql_sql'}", Locale.GERMAN);
		date.set("test", D1_TIME);
		assertEquals("2011-10-15 01:05:15", date.toString());
		date = Repo.parse("{v:test date='_sql'}");
		date.set("test", D1_TIME);
		assertEquals("01:05:15", date.toString());
	}
	
	@Test
	public void german() {
		Template date = Repo.parse("{v:test date='long'}", Locale.GERMAN);
		date.set("test", D1);
		assertEquals("15. Oktober 2011", date.toString());
		date = Repo.parse("{v:test date='full_short'}", Locale.GERMAN);
		date.set("test", D1);
		assertEquals("Samstag, 15. Oktober 2011 00:00", date.toString());
		date = Repo.parse("{v:test date=\"full_full\"}", Locale.GERMAN);
		date.set("test", D1);
		assertEquals("Samstag, 15. Oktober 2011 00:00 Uhr MESZ", date.toString());
		date = Repo.parse("<t:test date='short_long'></t:test>", Locale.GERMAN);
		date.set("test", D1);
		assertEquals("15.10.11 00:00:00 MESZ", date.toString());
		date = Repo.parse("<t:test date=\"w/MM yyyy\"></t:test>", Locale.GERMAN);
		date.set("test", D1);
		assertEquals("41/10 2011", date.toString());
	}
	
	@Test
	public void international() {
		Template date = Repo.parse("{v:test date='long'}", Locale.SIMPLIFIED_CHINESE);
		date.set("test", D1);
		assertEquals("2011年10月15日", date.toString());
		date = Repo.parse("{v:test date='long_long'}", Locale.JAPANESE);
		date.set("test", D1);
		assertEquals("2011/10/15 0:00:00 CEST", date.toString());
		date = Repo.read("{v:test}").locale(Locale.FRENCH).attrib("date", "full_long").parse();
		date.set("test", D1);
		assertEquals("samedi 15 octobre 2011 00:00:00 CEST", date.toString());
		date = Repo.parse("{v:test date=\"\"}", Locale.KOREAN);
		date.set("test", D1);
		assertEquals("2011. 10. 15", date.toString());
	}
	
	@Test
	public void defaultGerman() {
		Template date = Repo.parse("{v:test}", Locale.GERMAN);
		date.set("test", D1);
		assertEquals("15.10.2011", date.toString());
		date = Repo.parse("{v:test date=\"\"}", Locale.GERMAN);
		date.set("test", D1);
		assertEquals("15.10.2011", date.toString());
		date = Repo.parse("<t:test date=''></t:test>", Locale.GERMAN);
		date.set("test", D1);
		assertEquals("15.10.2011", date.toString());
		date = Repo.parse("<t:test date=\"\"></t:test>", Locale.GERMAN);
		date.set("test", D1);
		assertEquals("15.10.2011", date.toString());
	}
	
	@Test
	public void dateInheritanceRoot() {
		Template date = Repo.read("<t:test>Date: {v:d1}<t:> Other date: {v:d2}</t:>\n</t:test>").attrib("date", "medium_long").parse();
		date.get("test").set("d1", D1).set("d2", D1_TIME).render();
		date.get("test").set("d1", D2).render();
		assertEquals("Date: Oct 15, 2011 12:00:00 AM CEST Other date: Oct 15, 2011 1:05:15 AM CEST\nDate: Oct 6, 2011 12:00:00 AM CEST\n", date.toString());
		date.set("test", D2);
		assertEquals("Oct 6, 2011 12:00:00 AM CEST", date.toString());
		date = date.get();
		date.get("test").set("d1", D1).set("d2", D1_TIME).render();
		date.get("test").set("d1", D2).render();
		assertEquals("Date: Oct 15, 2011 12:00:00 AM CEST Other date: Oct 15, 2011 1:05:15 AM CEST\nDate: Oct 6, 2011 12:00:00 AM CEST\n", date.toString());
		
		date = Repo.read("<t:>Date: {v:d1}<t:test> Other date: {v:d2}</t:test></t:>").attrib("date", "short_medium").parse();
		date.get("test").set("d2", D1_TIME).render();
		date.set("d1", D1);
		assertEquals("Date: 10/15/11 12:00:00 AM Other date: 10/15/11 1:05:15 AM", date.toString());
		date.set("test", D2);
		assertEquals("Date: 10/15/11 12:00:00 AM10/6/11 12:00:00 AM", date.toString());
	}
	
	@Test
	public void dateInheritanceRegion() {
		Template date = Repo.parse("<t:test date='short'>Date 1: {v:d1}<t:test date='short'> Date 2: {v:d1}</t:test></t:test>").get("test");
		date.set("d1", D1);
		date.get("test").set("d1", D2).render();
		assertEquals("Date 1: 10/15/11 Date 2: 10/6/11", date.toString());
		
		date = Repo.parse("<t:test date=\"short_full\">Date 1: {v:d1 date='sql'}<t:> Date 2: {v:d2} </t:></t:test>", Locale.GERMAN).get("test");
		date.set("d1", D1);
		date.set("d2", D2);
		assertEquals("Date 1: 2011-10-15 Date 2: 06.10.11 00:00 Uhr MESZ ", date.toString());
		
		date = Repo.read("<t:test date='_medium'><t:>Date 1: {v:d1}</t:><t:test><t:> Date 2: {v:d2} </t:></t:test></t:test>")
				.locale(Locale.GERMAN).attrib("date", "long_short").parse().get("test");
		date.set("d1", D1_TIME );
		date.get("test").set("d2", D2).render();
		assertEquals("Date 1: 01:05:15 Date 2: 00:00:00 ", date.toString());
	}
	
	@Test
	public void dateInheritanceConditional() {
		Template date = Repo.parse("before<t: date='sql'>-><t:>{v:test}</t:><-</t:>after");
		assertEquals("beforeafter", date.toString());
		date.set("test", D1);
		assertEquals("before->2011-10-15<-after", date.toString());
	}
}
