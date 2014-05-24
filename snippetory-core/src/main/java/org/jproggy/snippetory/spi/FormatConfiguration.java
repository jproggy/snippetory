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
 * The FormatConfiguration allows Snippetory to support complex state handling approaches
 * like counting invokations on several nodes. However, most Formats are state less and such
 * don't need any state handling at all. In those cases SimpleFormat is a great alternative.
 * No matter wether implemented directly or via SimpleFormat it allows one to implement 
 * sub-attributes by providing setter-methods or by implementing DynamicAttributes.
 * The types supported by those setter methods are  numbers, boolean, enums and String.
 * This can be extended by registering {@link  java.beans.ProperyEditor PropertyEditors} on 
 * {@link java.beans.PropertyEditorManager PropertyEditorManager}
 * 
 * @author B. Ebertz
 */
public interface FormatConfiguration {
	
	/**
	 * Allows the Configuration to control the instanciation of Format.
	 * @see StateContainer
	 */
	Format getFormat(TemplateNode node);
}
