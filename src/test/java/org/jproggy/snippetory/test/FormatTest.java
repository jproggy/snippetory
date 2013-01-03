package org.jproggy.snippetory.test;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Locale;

import org.jproggy.snippetory.Repo;
import org.jproggy.snippetory.Template;
import org.junit.Test;

public class FormatTest {
	private static final java.sql.Date D2 = java.sql.Date.valueOf("2011-10-06");
	private static final java.sql.Date D1 = java.sql.Date.valueOf("2011-10-15");
	private static final Date D1_TIME = new Date(D1.getTime() + 3915000l);

	@Test
	public void formatStretch() {
		Template stretch = Repo.parse("{v:test stretch='5r'}");
		stretch.set("test", "x");
		assertEquals("    x", stretch.toString());
		stretch.set("test", "123456");
		assertEquals("123456", stretch.toString());
	}
	
	@Test
	public void formatPad() {
		Template pad = Repo.parse("{v:test pad='5' pad.align='right'}");
		pad.set("test", "x");
		assertEquals("    x", pad.toString());
		pad.set("test", "123456");
		assertEquals("123456", pad.toString());
	}
	
	@Test
	public void formatShorten() {
		Template shorten = Repo.parse("{v:test shorten='5...'}");
		shorten.set("test", "x");
		assertEquals("x", shorten.toString());
		shorten.set("test", "123456");
		assertEquals("12...", shorten.toString());
		shorten.set("test", "12345");
		assertEquals("12345", shorten.toString());
	}
	
	@Test
	public void formatCrop() {
		Template crop = Repo.parse("{v:test crop='5' crop.mark='...'}");
		crop.set("test", "x");
		assertEquals("x", crop.toString());
		crop.set("test", "123456");
		assertEquals("12...", crop.toString());
		crop.set("test", "12345");
		assertEquals("12345", crop.toString());
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
	public void formatDateJS() {
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
	public void formatDateGerman() {
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
	public void formatDateInternational() {
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
	public void formatDateDefaultGerman() {
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
	public void dateInheritance() {
		Template date = Repo.read("<t:test>Date: {v:d1}<t:> Other date: {v:d2}</t:>\n</t:test>").attrib("date", "medium_long").parse();
		date.get("test").set("d1", D1).set("d2", D1_TIME).render();
		date.get("test").set("d1", D2).render();
		assertEquals("Date: Oct 15, 2011 12:00:00 AM CEST Other date: Oct 15, 2011 1:05:15 AM CEST\nDate: Oct 6, 2011 12:00:00 AM CEST\n", date.toString());
		date = date.get();
		date.get("test").set("d1", D1).set("d2", D1_TIME).render();
		date.get("test").set("d1", D2).render();
		assertEquals("Date: Oct 15, 2011 12:00:00 AM CEST Other date: Oct 15, 2011 1:05:15 AM CEST\nDate: Oct 6, 2011 12:00:00 AM CEST\n", date.toString());
		
		date = Repo.parse("<t:test date='short'>Date 1: {v:d1} Date 2: {v:d2} </t:test>", Locale.GERMAN).get("test");
		date.set("d1", D1);
		date.set("d2", D2);
		assertEquals("Date 1: 15.10.11 Date 2: 06.10.11 ", date.toString());
		
		date = Repo.parse("<t:test date=\"short_full\">Date 1: {v:d1 date='sql'} Date 2: {v:d2} </t:test>", Locale.GERMAN).get("test");
		date.set("d1", D1);
		date.set("d2", D2);
		assertEquals("Date 1: 2011-10-15 Date 2: 06.10.11 00:00 Uhr MESZ ", date.toString());
		
		date = Repo.read("<t:test>Date 1: {v:d1 date='_medium'} Date 2: {v:d2 date='long_short'} </t:test>")
				.locale(Locale.GERMAN).attrib("date", "long_short").parse().get("test");
		date.set("d1", D1_TIME );
		date.set("d2", D2);
		assertEquals("Date 1: 01:05:15 Date 2: 6. Oktober 2011 00:00 ", date.toString());
	}
	
	@Test
	public void toggle() {
		Template t = Repo.parse("<t:test>{v:toggle='1;2;3'}. {v: toggle='unpair;pair'}\n</t:test>");
		bindPLain(t);
		assertEquals("1. unpair\n2. pair\n3. unpair\n", t.toString());
		t.clear();
		bindPLain(t);
		assertEquals("1. unpair\n2. pair\n3. unpair\n", t.toString());
		
		t = Repo.parse("<t:test>{v:x toggle='1;2;3'}. {v:x toggle='unpair;pair'}\n</t:test>");
		bindX(t);
		assertEquals("1. unpair\n2. pair\n3. unpair\n", t.toString());
		t.clear();
		bindX(t);
		assertEquals("1. unpair\n2. pair\n3. unpair\n", t.toString());
		t.clear();
		t.get("test").set("x", 1).render();
		t.get("test").set("x", 0).render();
		t.get("test").set("x", -1).render();
		assertEquals("1. unpair\n2. pair\n3. unpair\n", t.toString());
		
		t = Repo.parse("<t:test><t: toggle='1. ;2. ;3. ;4. '>von {v:von} nach {v:nach}\n</t:></t:test>");
		bindTrack(t);
		assertEquals("1. von Hergersweiler nach Winden\n2. von 6 nach {v:nach}\n", t.toString());
		t.clear();
		bindTrack(t);
		assertEquals("1. von Hergersweiler nach Winden\n2. von 6 nach {v:nach}\n", t.toString());
		t = t.get();
		bindTrack(t);
		assertEquals("1. von Hergersweiler nach Winden\n2. von 6 nach {v:nach}\n", t.toString());
	}

	private void bindX(Template t) {
		t.get("test").set("x", 1).render();
		t.get("test").set("x", 2).render();
		t.get("test").set("x", 3).render();
	}

	private void bindPLain(Template t) {
		t.get("test").render();
		t.get("test").render();
		t.get("test").render();
	}

	private void bindTrack(Template t) {
		t.get("test").render();
		t.get("test").set("von", "Hergersweiler").set("nach", "Winden").render();
		t.get("test").render();
		t.get("test").set("von", 6).render();
	}
}
