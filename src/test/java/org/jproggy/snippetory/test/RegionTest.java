package org.jproggy.snippetory.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jproggy.snippetory.engine.Location;
import org.jproggy.snippetory.engine.Region;
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
}
