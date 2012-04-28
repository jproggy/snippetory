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

import java.util.ServiceLoader;

/**
 * This is just an interface to tag classes that are loaded on startup to register additional
 * elements on startup. Additional to tagging there must be a file named 
 * org.jproggy.snippetory.spi.Configurer containing the name of your implementation class.
 * Snippetory will use the {@link ServiceLoader} mechanism to load those classes to enable
 * you to have your additions available when you need them. Be aware that Snippetory can only 
 * load services if it's {@link ClassLoader} can find them.
 *   
 * @author Sir RotN
 */
public interface Configurer {

}
