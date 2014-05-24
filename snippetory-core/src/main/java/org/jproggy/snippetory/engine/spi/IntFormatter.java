package org.jproggy.snippetory.engine.spi;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.spi.SimpleFormat;

public class IntFormatter extends NumFormatter {
	

	public IntFormatter() {
		super(Long.class, Integer.class, Short.class, Byte.class, BigInteger.class, AtomicInteger.class, AtomicLong.class);
	}

	@Override
	public SimpleFormat create(String definition, TemplateContext ctx) {
		if (definition.isEmpty()) return super.create("tostring", ctx);
		return super.create(definition, ctx);
	}
}
