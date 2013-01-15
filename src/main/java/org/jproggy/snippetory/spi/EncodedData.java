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

import org.jproggy.snippetory.engine.EncodingRegistry;



/**
 * Combines character data as pay load with information about it's encoding 
 * as additional meta data.
 * 
 * @author B. Ebertz
 */
public interface EncodedData {
	/**
	 * The encoding is represented by it's name. The name can be resolved 
	 * by  {@link EncodingRegistry#get(String) Encoding.REGISTRY.get(String)}.
	 */
	String getEncoding();
	
	/**
	 * Convert to a char sequence.
	 * This methods is added, because a toString method might be more expensive
	 * in many cases. This method allows to return a StringBuilder instead of a String.
	 * This might avoid copying the data from the StringBuilder into a String. 
	 */
	CharSequence toCharSequence();
}
