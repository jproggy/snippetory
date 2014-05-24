package org.jproggy.snippetory.engine.spi;

import java.math.BigDecimal;

public class DecimalFormatter extends NumFormatter {

	public DecimalFormatter() {
		super(Float.class, Double.class, BigDecimal.class);
	}

}
