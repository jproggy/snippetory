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

package org.jproggy.snippetory.groovy


import org.jproggy.snippetory.Encodings
import org.jproggy.snippetory.Repo
import org.jproggy.snippetory.Syntaxes
import org.jproggy.snippetory.Template
import org.jproggy.snippetory.spi.EncodedData
import org.jproggy.snippetory.spi.Encoding
import org.jproggy.snippetory.spi.SyntaxID

class TemplateBuilder implements EncodedData {
  private Map names = [:]
  private Template current
  private Stack<Template> stack = new Stack<>()

  TemplateBuilder(String data, SyntaxID s = Syntaxes.FLUYT, Encoding e = Encodings.NULL) {
    this(Repo.read(data).syntax(s).encoding(e).parse())
  }

  TemplateBuilder(Template parent) {
    current = parent
  }

  void setProperty(String name, Object value) {
    current.set(name, value)
  }

  @Override
  String getEncoding() {
    current.getEncoding()
  }

  @Override
  CharSequence toCharSequence() {
    return current.toCharSequence()
  }

  private void call(Closure c) {
    c.delegate = this
    c()
  }

  private void call(Map<String, ?> params) {
    for (e in params) {
      doInvokeMethod(e.key, e.value)
    }
  }

  private void call(Map params, Closure c) {
    call(params)
    call(c)
  }

  protected Template createNode(String name) {
    return current.get(name)
  }

  protected Template createNode(String name, value) {
    if (value instanceof Template) {
      names[value] = name
      return value
    } else {
      current.append(name, value)
      return null
    }
  }

  protected Template createNode(String name, List value) {
    for (v in value) current.append(name, v)
    return null
  }

  protected Template createNode(String name, Map attributes) {
    Template node = current.get(name)
    for (e in attributes) {
        node.append(e.key.toString(), e.value)
    }
    return node
  }

  Object invokeMethod(String methodName, Object args) {
    return doInvokeMethod(methodName, args)
  }

  protected Object doInvokeMethod(String name, Object[] args) {
    Template node = null
    Closure closure = Args.getClosure(args)
    List list = Args.toList(args, closure)
    List data

    switch (list.size()) {
      case 0:
        node = createNode(name)
        break
      case 1:
        Object object = list.get(0)
        if (object instanceof Map) {
          node = createNode(name, (Map) object)
        } else if (object instanceof Iterable<?> || object.class.isArray()) {
          if (closure) {
            data = object.asList()
          } else {
            node = createNode(name, object.asList())
          }
        } else {
          node = createNode(name, object)
        }
        break
      default:
        if (closure) {
          data = list
        } else {
          node = createNode(name, list)
        }
    }

    if (closure) {
      if (node) {
        push(node)
        call(closure)
        pop()
      } else if (data) {
        closure.delegate = this
        for (val in data) {
          push(createNode(name))
          closure.call(val)
          current.render()
          pop()
        }
      }
    }
    if (node) {
      names[node] ? node.render(current, (String) names.remove(node)) : node.render()
    }
    return this
  }

  @Override
  String toString() {
    return current.toString()
  }

  protected Template pop() {
    current = stack.pop()
  }

  protected Template push(Template node) {
    stack.push(current)
    current = node
  }
}