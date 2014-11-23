/// Copyright JProggy
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.

package org.jproggy.snippetory.spi;

/**
 * SyntaxID is typically implemented by an enum used to identify a named {@link Syntax}.
 * However, to be able to use a Syntax it has to be registered via the
 * {@link Syntax.Registry#register(SyntaxID, Syntax) Syntax.REGISTRY.register} method.
 *
 * @author B. Ebertz
 */
public interface SyntaxID {
	/**
	 * @return a name identifying the syntax uniquely.
	 */
	String getName();
}
