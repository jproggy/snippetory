package org.jproggy.snippetory.test;

import static org.jproggy.snippetory.Syntaxes.FLUYT;
import static org.junit.Assert.assertEquals;

import org.jproggy.snippetory.Template;
import org.junit.Test;

public class FluytTest {
	@Test
	public void hiddenBlox() {
		Template t1 = FLUYT.parse("$test{ i++; }test$");
		assertEquals(" i++; ", t1.get("test").toString());
		Template t2 = FLUYT.parse("$test(enc='url'){ i++; }test$");
		assertEquals(" i++; ", t2.get("test").toString());
		Template t3 = FLUYT.parse("$test{ i++; }$");
		assertEquals(" i++; ", t3.get("test").toString());
		Template t4 = FLUYT.parse("${ $test i++; }$");
		assertEquals("", t4.toString());
		t4.set("test", "");
		assertEquals("  i++; ", t4.toString());
		t4.set("test", "blub");
		assertEquals(" blub i++; ", t4.toString());
 		Template t5 = FLUYT.parse("${${$test(stretch=\"10\"){ i++; }test$}$}$");
 		assertEquals("", t5.toString());
		t5.get("test").render();
		assertEquals(" i++;     ", t5.toString());
//		Template t6 = FLUYT.parse("<!--t:test date=''*/ i++; /*!t:test-->");
//		t6.get("test").render();
//		assertEquals(" i++; ", t6.toString());
	}


	@Test
	public void lineRemovalHB() {
		Template t1 = FLUYT.parse("  $test{  \n i++; \n   }test$  \n");
		t1.append("test", t1.get("test"));
		assertEquals(" i++; \n", t1.toString());
		t1 = FLUYT.parse("$test{\n i++; \n}test$\n");
		t1.append("test", t1.get("test"));
		assertEquals(" i++; \n", t1.toString());
		t1 = FLUYT.parse("$test{  \n i++; \n   }test$\n");
		t1.append("test", t1.get("test"));
		assertEquals(" i++; \n", t1.toString());
		t1 = FLUYT.parse("  $test{\n i++; \n}test$  ");
		t1.append("test", t1.get("test"));
		assertEquals(" i++; \n", t1.toString());
		Template t7 = FLUYT.parse("  $test(shorten='4-'){  \n i++; \n   }test$  \n");
		t7.get("test").render();
		t7.get("test").render();
		assertEquals(" i+- i+-", t7.toString());
		Template t8 = FLUYT.parse("$test{$test(stretch='8r')\n }test$\n");
		t8.get("test").append("test", "12345").append("test", "123").render();
		t8.get("test").set("test", "test").render();
		assertEquals("   12345     123\n    test\n", t8.toString());
	}
	
	@Test
	public void delimiter() {
		Template t1 = FLUYT.parse("in ($test(delimiter=', '))");
		t1.append("test", 5);
		assertEquals("in (5)", t1.toString());
		t1.append("test", 8);
		assertEquals("in (5, 8)", t1.toString());
		t1.append("test", 5);
		assertEquals("in (5, 8, 5)", t1.toString());
		Template t2 = FLUYT.parse("\"$test(delimiter='\",\"')\"");
		t2.append("test", 5);
		assertEquals("\"5\"", t2.toString());
		t2.append("test", "hallo");
		assertEquals("\"5\",\"hallo\"", t2.toString());
	}
	
	@Test
	public void childTempates() {
		Template t1 = FLUYT.parse("in$test{ and out}test$ and around");
		assertEquals("in and around", t1.toString());
		t1.append("test", t1.get("test"));
		assertEquals("in and out and around", t1.toString());
		t1.append("test", t1.get("test"));
		assertEquals("in and out and out and around", t1.toString());
		t1.clear();
		assertEquals("in and around", t1.toString());
		Template t2 = FLUYT.parse("$outer{in$test{ and $test}test$ and around}outer$").get("outer");
		t2.get("test").append("test", "hallo").render();
		assertEquals("in and hallo and around", t2.toString());
	}
}
