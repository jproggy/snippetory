/// Copyright JProggy
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.

package org.jproggy.snippetory.test;

import static org.jproggy.snippetory.Syntaxes.C_COMMENTS;
import static org.junit.Assert.assertEquals;

import org.jproggy.snippetory.Template;
import org.junit.Test;

@Deprecated
public class CCommentTest {
	@Test
	public void cComments() {
		Template t1 = C_COMMENTS.parse("/*${test*/ i++; /*test}*/");
		assertEquals(" i++; ", t1.get("test").toString());
		Template t2 = C_COMMENTS.parse("/*${test enc='url'*/ i++; /*test}*/");
		assertEquals(" i++; ", t2.get("test").toString());
		Template t3 = C_COMMENTS.parse("// ${test \n i++; \n// test}");
		assertEquals(" i++; \n", t3.get("test").toString());
		Template t5 = C_COMMENTS.parse("/*${test stretch=\"10\"*/ i++; /*test}*/");
		t5.get("test").render();
		assertEquals(" i++;     ", t5.toString());
		Template t6 = C_COMMENTS.parse("/*${test date=''*/ i++; /*test}*/");
		t6.get("test").render();
		assertEquals(" i++; ", t6.toString());
	}


	@Test
	public void lineRemoval() {
		Template t1 = C_COMMENTS.parse("  // ${ test  \n i++; \n   // test }  \n");
		t1.append("test", t1.get("test"));
		assertEquals(" i++; \n", t1.toString());
		t1 = C_COMMENTS.parse("//${test\n i++; \n//test}\n");
		t1.append("test", t1.get("test"));
		assertEquals(" i++; \n", t1.toString());
		t1 = C_COMMENTS.parse("//${test  \n i++; \n   //test}\n");
		t1.append("test", t1.get("test"));
		assertEquals(" i++; \n", t1.toString());
		t1 = C_COMMENTS.parse("  //${test\n i++; \n//test}  ");
		t1.append("test", t1.get("test"));
		assertEquals(" i++; \n", t1.toString());
		Template t7 = C_COMMENTS.parse("  //${test crop='4' crop.mark='-'  \n i++; \n   // test}  \n");
		t7.get("test").render();
		t7.get("test").render();
		assertEquals(" i+- i+-", t7.toString());
		Template t8 = C_COMMENTS.parse("/*${test*//*${test stretch='8r'}*/\n //test}\n");
		t8.get("test").append("test", "12345").append("test", "123").render();
		t8.get("test").set("test", "test").render();
		assertEquals("   12345     123\n    test\n", t8.toString());
	}

	@Test
	public void delimiter() {
		Template t1 = C_COMMENTS.parse("in (/*${test delimiter=', '}*/)");
		t1.append("test", 5);
		assertEquals("in (5)", t1.toString());
		t1.append("test", 8);
		assertEquals("in (5, 8)", t1.toString());
		t1.append("test", 5);
		assertEquals("in (5, 8, 5)", t1.toString());
		Template t2 = C_COMMENTS.parse("\"/*${test delimiter='\",\"'}*/\"");
		t2.append("test", 5);
		assertEquals("\"5\"", t2.toString());
		t2.append("test", "hallo");
		assertEquals("\"5\",\"hallo\"", t2.toString());
	}

	@Test
	public void childTempates() {
		Template t1 = C_COMMENTS.parse("in/*${test*/ and out/*test}*/ and around");
		assertEquals("in and around", t1.toString());
		t1.append("test", t1.get("test"));
		assertEquals("in and out and around", t1.toString());
		t1.append("test", t1.get("test"));
		assertEquals("in and out and out and around", t1.toString());
		t1.clear();
		assertEquals("in and around", t1.toString());
		Template t2 = C_COMMENTS.parse("/*${outer*/in/*${test*/ and /*${test}*//*test}*/ and around/*outer}*/").get("outer");
		t2.get("test").append("test", "hallo").render();
		assertEquals("in and hallo and around", t2.toString());
	}
}
