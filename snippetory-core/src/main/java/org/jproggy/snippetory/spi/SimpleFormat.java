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
package org.jproggy.snippetory.spi;

/**
 * Simplifies creation of new Formats by unifying the concepts of {@link Format} 
 * and {@link FormatConfiguration}. This only works as long as no sophisticated
 * state management is needed. So it's fine for all state less Formats. 
 * 
 * @author B. Ebertz
 */
public abstract class SimpleFormat implements Format, FormatConfiguration {

	@Override
	public Format getFormat(TemplateNode node) {
		return this;
	}

	@Override
	public void clear(TemplateNode location) {
	}

}
