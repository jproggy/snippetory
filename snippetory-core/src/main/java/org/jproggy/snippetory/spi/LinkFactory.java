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

import org.jproggy.snippetory.TemplateContext;

/**
 * A LinkFactory is registered to create a new linking attribute.
 * <p>
 * Link definition in templates is done  by attributes. Every format has exactly one main
 * attribute and an arbitrary number of sub-attributes. The name of sub-attributes starts
 * with the name of the main attribute followed by a dot and the name of the sub-attribute.
 * That way every link has it's own name space.
 * </p>
 * <p>
 * To be able to use a link in Snippetory one has to register LinkFactory able to create
 * it. The creation process of a {@link Link} consists of three steps. First an instance of
 * {@code FormatFactory} is registered at {@link Format#REGISTRY}. The FormatFactory
 * creates a {@link FormatConfiguration}. The  FormatConfiguration in turn will get the
 * sub-attributes provided via setter-methods. The FormatConcigutation is stored, and
 * every time a new Location is copied the configuration will be asked for a format instance.
 * </p>
 *
 * @author B. Ebertz
 *
 */
public interface LinkFactory {
  /**
   * Instantiate the {@link FormatConfiguration} to be kept in the mate data of a template
   * and will be used for creating real template nodes. For each real template node
   *  {@link FormatConfiguration#getFormat(TemplateNode)} is called exactly once.
   *  However, there is always one node that never gets used to render a template, but only
   *  for cloning all the others.
   *
   * @param definition the attribute value from the template
   * @param ctx the TemplateContext provides additional information like the locale
   * @return a FormatConfiguration that will be kept as meta data of this node
   */
  Link create(String definition, TemplateContext ctx);
}
