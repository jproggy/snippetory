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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jproggy.snippetory.Repo;
import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.engine.Location;
import org.jproggy.snippetory.engine.Region;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class RegionTest {
	@SuppressWarnings("unchecked")
	@Test 
	public void charAtTest() {
		Location placeHolder = new Location(null, "", Collections.EMPTY_MAP, "", Locale.US);
		List<Object> parts = Arrays.asList((Object)"", "test", "yagni");
		Region region = new Region(placeHolder, parts, Collections.EMPTY_MAP);
		assertEquals('t', region.charAt(0));
		assertEquals('e', region.charAt(1));
		assertEquals('s', region.charAt(2));
		assertEquals('t', region.charAt(3));
		assertEquals('y', region.charAt(4));
		assertEquals('a', region.charAt(5));
		assertEquals('g', region.charAt(6));
		assertEquals('n', region.charAt(7));
		assertEquals('i', region.charAt(8));
		assertEquals('y', region.charAt(4));
		assertEquals('e', region.charAt(1));
		parts = Arrays.asList((Object)"test", "", "yagni", "", "jproggy");
		region = new Region(placeHolder, parts, Collections.EMPTY_MAP);
		assertEquals('y', region.charAt(4));
		assertEquals('y', region.charAt(15));
	}
	private Template template;
	private String[] variants = { "row1", "row2", "row3", };
	private List<Object> data = new ArrayList<Object>();

	@Before
	public void init() {
		template = Repo.readResource("testTable.htm").locale(Locale.US).parse();
		for (int i = 0; i < 111; i++) {
			data.add(String.valueOf(i));
		}
	}

	@Test
	public void test100() {
		testN(100);
	}

	@Test
	public void test1000() {
		testN(1000);
	}

	@Test
	public void test100_1000() {
		for (int i = 0; i < 1000; i++) {
			template.clear();
			testN(100);
		}
	}

	@Test
	public void test10000() {
		testN(10000);
	}

	@Test
	public void test100000() {
		testN(65000);
	}

	@Test
	@Ignore
	public void test1000000() {
		testN(1000000);
	}

	public void testN(int n) {
		int i = 0; 
		try {
			int count = 0;
			for (; i < n; i++) {
				Template row = template.get(variants[i % variants.length]);
				for (int x = 0; x < 10; x++) {
					row.get("x").set("val", data.get(count)).render();
					count++;
					if (count >= data.size()) count = 0;
				}
				row.render("row1");
			}
			@SuppressWarnings("unused")
			CharSequence x = template.toString();
		} finally {
			Assert.assertEquals(n, i);
		}
	}
}
