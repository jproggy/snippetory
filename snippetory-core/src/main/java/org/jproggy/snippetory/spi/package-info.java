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

/**
 * The Service provider interface contains classes enabling the user to extend and configure the behavior
 * of the Snippetory engine. Those extensions can be packaged into jar files and loaded by
 * the {@link java.util.ServiceLoader} mechanism. See {@link org.jproggy.snippetory.spi.Configurer} for further
 * information.
 * <br />
 * The classes {@link org.jproggy.snippetory.spi.Format}, {@link org.jproggy.snippetory.spi.Syntax}, and
 * {@link org.jproggy.snippetory.spi.Encoding} contain a REGISTRY attribute to allow extensions as well
 * as overriding default behavior.
 */
package org.jproggy.snippetory.spi;