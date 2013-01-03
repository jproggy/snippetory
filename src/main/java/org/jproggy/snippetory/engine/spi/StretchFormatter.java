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
import org.jproggy.snippetory.engine.spi.PadFormatter.Alignment;
import org.jproggy.snippetory.engine.spi.PadFormatter.PadFormat;
import org.jproggy.snippetory.spi.FormatFactory;

public class StretchFormatter implements FormatFactory {
	@Override
	public PadFormat create(String definition, TemplateContext ctx) {
		PadFormat f = null;
		int length = 0;
		Alignment left = null;
		for (char c : definition.toCharArray()) {
			if (f == null) {
				if (c >= '0' && c <= '9') {
					length = (10 * length) + (c - '0');
					continue;
				}
				f = new PadFormat(length);
			}
			if (c == 'l') {
				if (left != null) throw new IllegalArgumentException("Alingment already defined");
				left = Alignment.left;
			} else if (c == 'r') {
				if (left != null) throw new IllegalArgumentException("Alingment already defined");
				left = Alignment.right;
			}
		}
		if (length == 0) {
			throw new IllegalArgumentException("no length defined");
		} else if (f == null) {
			f = new PadFormat(length);
		}
		if (left != null) f.setAlign(left);
		return f;
	}
}
