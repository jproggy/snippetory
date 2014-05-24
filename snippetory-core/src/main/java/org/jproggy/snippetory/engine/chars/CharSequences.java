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

import java.io.IOException;

import org.jproggy.snippetory.engine.SnippetoryException;
import org.jproggy.snippetory.spi.CharDataSupport;

/**
 * CharSequences is a helper class to implement 'CompoundCharSequences' i.e.
 * CharSequences consisting of several parts.
 * <br />
 *
 * @author B. Ebertz
 */
public abstract class CharSequences implements CharSequence, SelfAppender {
	private CharSequence recentCS = null;
	private int csIndex = -1;
	private int recentStart = 0;

	/**
	 * the number of distinct parts connected by this instance
	 */
	protected abstract int partCount();

	/**
	 * provide one of the parts
	 */
	protected abstract CharSequence part(int index);

	/**
	 * there are some optimizations for subsequent calls of charAt with increasing index.
	 */
	@Override
	public char charAt(int index) {
		if (index < recentStart) {
			recentStart = 0;
			recentCS = null;
			csIndex = -1;
		}
		while ((recentCS == null || (index - recentStart) >= recentCS.length()) && csIndex + 1 < partCount()) {
			if (recentCS != null) {
				recentStart += recentCS.length();
			}
			csIndex++;
			recentCS = part(csIndex);
		}
		return recentCS.charAt(index - recentStart);
	}

	@Override
	public int length() {
		int l = 0;
		for (int i = 0; i < partCount(); i++) {
			l += part(i).length();
		}
		return l;
	}

    @Override
	public String toString() {
		return appendTo(new StringBuilder()).toString();
	}

	@Override
	public String subSequence(int start, int end) {
		return this.toString().substring(start, end);
  }

  public static void append(Appendable target, Object value) {
    try {
      append(target, CharDataSupport.toCharSequence(value));
    } catch (IOException e) {
      throw new SnippetoryException(e);
    }
  }

  public static void append(Appendable target, CharSequence value) throws IOException {
  	if (value instanceof SelfAppender) {
  		((SelfAppender) value).appendTo(target);
  	} else {
  		target.append(value);
  	}
  }
}
