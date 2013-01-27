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

import org.jproggy.snippetory.TemplateContext;
import org.jproggy.snippetory.spi.CharDataSupport;
import org.jproggy.snippetory.spi.FormatFactory;
import org.jproggy.snippetory.spi.SimpleFormat;
import org.jproggy.snippetory.spi.TemplateNode;

public class CropFormatter implements FormatFactory {

	@Override
	public CropFormat create(String definition, TemplateContext ctx) {
		int width = Integer.parseInt(definition);
		return new CropFormat(width);
	}
	
	public static class CropFormat extends SimpleFormat {
		private final int length;
		private String mark = "";

		public CropFormat(int length) {
			super();
			this.length = length;
		}
		
		public void setMark(String mark) {
			this.mark = mark;
		}

		@Override
		public Object format(TemplateNode location, Object value) {
			CharSequence s = CharDataSupport.toCharSequence(value);
			if (s.length() <= length) return value;
			return new StringBuilder(s.subSequence(0, length - mark.length())).append(mark);
		}

		@Override
		public boolean supports(Object value) {
			return CharDataSupport.isCharData(value);
		}
	}
}
