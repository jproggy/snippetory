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

package org.jproggy.snippetory.engine.spi;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Locale;

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.spi.FormatFactory;
import org.jproggy.snippetory.spi.SimpleFormat;
import org.jproggy.snippetory.spi.TemplateNode;


public class NumFormater implements FormatFactory {
	@Override
	public SimpleFormat create(String definition, TemplateContext ctx) {
		if (("".equals(definition) && isTechLocale(ctx)) 
				|| "tostring".equalsIgnoreCase(definition)) {
			return new ToStringFormat();
		}
		return new DecimalFormatWrapper(definition, ctx.getLocale());
	}

	private boolean isTechLocale(TemplateContext ctx) {
		return ctx.getLocale().equals(TemplateContext.TECH);
	}
	
	private static NumberFormat toFormat(String definition, Locale l) {
		if ("".equals(definition)) return DecimalFormat.getNumberInstance(l);
		if ("currency".equals(definition)) return DecimalFormat.getCurrencyInstance(l);
		if ("int".equals(definition)) return DecimalFormat.getIntegerInstance(l);
		if ("percent".endsWith(definition)) return DecimalFormat.getPercentInstance(l);
		if ("JS".equals(definition)) return DecimalFormat.getNumberInstance(Locale.US);
		return new DecimalFormat(definition, DecimalFormatSymbols.getInstance(l));
	}

	public static class ToStringFormat extends SimpleFormat {

		@Override
		public Object format(TemplateNode location, Object value) {
			return value.toString();
		}

		@Override
		public boolean supports(Object value) {
			return (value instanceof Number);
		}
	}	

	public static class DecimalFormatWrapper extends SimpleFormat {
		private final NumberFormat impl;

		public DecimalFormatWrapper(String definition, Locale l) {
			impl = toFormat(definition, l);
		}

		@Override
		public Object format(TemplateNode location, Object value) {
			return impl.format(value, new StringBuffer(), new FieldPosition(0));
		}

		@Override
		public boolean supports(Object value) {
			return (value instanceof Number);
		}
	}	
}
