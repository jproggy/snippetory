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

import java.util.Locale;

/**
 * To be able to use a format in Snippetory one has to register FormatFactory able to create
 * it. Each {@link Format} has to be defined by a single attribute value. This means sometimes
 * there will be some parsing to do. 
 * 
 * @author B. Ebertz
 */
public interface FormatFactory {
	/**
	 * 
	 * @param definition the attribute value from the template 
	 * @param locale the locale defined for the template
	 * @return the format that will be assigned to the location
	 */
	Format create(String definition, Locale locale);	
}
