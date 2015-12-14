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

package org.jproggy.snippetory.sql;

import org.jproggy.snippetory.Template;

/**
 * A {@code Repository} is essentially a bunch of statements stored in a file.
 */
public class Repository {
  private final Statement repo;

  public Repository(Statement repo) {
    super();
    this.repo = repo;
  }

  public Statement get(String... name) {
    return (Statement)repo.get(name);
  }

  public Template toTemplate() {
    return repo;
  }
}
