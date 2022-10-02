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

import java.util.Map;
import java.util.WeakHashMap;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.engine.spi.ToggleFormatter;

/**
 * Simplifies handling based on TemplateNode. The problem with this is, that the state is not
 * bound directly to one node, but rather to a parent of the node or a node even higher in this hierarchy.
 * In some cases it is necessary to collect data over more than one node. Like counters for instance.
 * It handles resolving the right key and creating new objects.
 *
 * @param <V> is the type of the values kept in this container. Typically, this is a {@link Format}.
 * @see ToggleFormatter
 */
public abstract class StateContainer<V> {
  private final Map<TemplateNode, V> data = new WeakHashMap<>();
  private final KeyResolver resolver;

  /**
   *
   */
  public StateContainer(KeyResolver resolver) {
    super();
    this.resolver = resolver;
  }

  /**
   * Create a new instance to handle your state. Typically, this will create a
   * {@link Format}. This method is only called if there is no value crated yet for the same node.
   */
  protected abstract V createValue(TemplateNode key);

  /**
   * Get the state handler for the given node. The node will be resolved, according to the KeyResolver.
   */
  public V get(TemplateNode key) {
    key = resolver.resolve(key);
    return data.computeIfAbsent(key, this::createValue);
  }

  public void clear(TemplateNode key) {
    data.remove(resolver.resolve(key));
  }

  public void put(TemplateNode key, V value) {
    key = resolver.resolve(key);
    data.put(key, value);
  }

  /**
   * Calculates the node to bind the state to based on node the node provided.
   */
  public interface KeyResolver {
    KeyResolver PARENT = TemplateNode::getParent;

    /**
     * Binds the state to an instance of a template even but not on a copy
     * created by calling {@link Template#get} without parameter.
     * Be aware such a copy uses same instance of FormatConfiguration
     * but should be completely independent.
     */
    KeyResolver ROOT = org -> {
      while (org.getParent() != null)
        org = org.getParent();
      return org;
    };

    static KeyResolver up(int levels) {
      return org -> {
        for (int i = 0; (i < levels) && (org.getParent() != null); i++) {
          org = org.getParent();
        }
        return org;
      };
    }

    TemplateNode resolve(TemplateNode org);
  }
}
