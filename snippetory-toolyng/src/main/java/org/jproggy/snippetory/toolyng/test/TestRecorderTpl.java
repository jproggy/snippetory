package org.jproggy.snippetory.toolyng.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestRecorderTpl {
	// ${ region
	public Map<String, Object> getData() {/*${name backward="Data" case="camelizeUpper"}*/
		List<Object> values;
		Map<String, Object> data = new HashMap<String, Object>();
		// ${ set
		data.put("${name enc='string'}", /*${data*/10/*}*/); 
		// set }
		// ${ append
		values = new ArrayList<Object>();
		// ${ value
		values.add(/*${data*/10/*}*/);
		// value }
		data.put("${name enc='string'}", /*${data*/values/*}*/); 
		// append }
		return data;
	}
	// region }
}
