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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.jproggy.snippetory.Repo;
import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.test.Page.Section;
import org.junit.Test;

public class MetaphorRepTest {
	@Test 
	public void test1() throws Exception {
		// prepare necessary data
		ResourceBundle labels = ResourceBundle.getBundle("labels", Locale.US);
		Map<String, String> errors =  new HashMap<String, String>();
		errors.put("field12", "Error messages might be red");
		String data11 = "val1";
		String data12 = "val2";
		String data13 = "";
		String data21 = "";
		List<String> values22 = Arrays.asList("choice1", "choice2", "choice3");
		List<String> data22 = Arrays.asList("choice1", "choice2");
		List<String> values23 = Arrays.asList("opt1", "opt2", "opt3");
		String data23 = "opt2";

		// render the data
		// first create the new page. The constructor takes some important data.
		Page page = new Page(labels.getString("data_enter"), "dataSink.do", errors, labels);
		
		// The first section contains only text fields
		Section section1 = page.createSection(labels.getString("sec1"));
		section1
		  .addTextAttrib("field11", data11)
		  .addTextAttrib("field12", data12)
		  .addTextAttrib("field13", data13)
		  .render();
		
		// The second section adds more complicated controls
		Section section2 = page.createSection(labels.getString("sec2"));
		section2
		  .addTextAttrib("field21", data21)
		  .addMultiSelectionAttrib("field22", values22, data22)
		  .addSelectionAttrib("field23", values23, data23)
		  .render();
		
		// for an example putting the template here makes it simple. In real live an own file
		// might be better. At least a little more advanced formatting would be nice ;-)
		Template explanation = 
				Repo.parse("{v:text} <ul><t:points><li>{v:point}</li></t:points></ul>");
		explanation.set("text", labels.getString("desc.text"));
		explanation.get("points").set("point", labels.getString("desc.point1")).render();
		explanation.get("points").set("point", labels.getString("desc.point2")).render();
		
		// render the explanation
		Section section3 = page.createSection(labels.getString("sec3"));
		section3.addDescription(explanation).render();
		
		// finally put it all out
		page.render(System.out);
		
	}
}
