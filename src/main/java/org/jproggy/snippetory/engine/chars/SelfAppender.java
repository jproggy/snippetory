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

package org.jproggy.snippetory.engine.chars;


/**
 * Whenever one char sequence is appended to another, it analyzes
 * the target type to figure out an efficient way. If the type of the
 * target is unknown single characters are read, which is pretty slow.
 * This method works as a workaround  for that design flaw.
 * 
 * @author B. Ebertz
 */
public interface SelfAppender {
  /**
   * 
   */
	<T extends Appendable> T appendTo (T to);
}
