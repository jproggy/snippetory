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

package org.jproggy.snippetory.sql.spi;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.sql.Statement;

/**
 *
 * <pre>
 * SELECT name, ext_id FROM myTable
 * WHERE 1=1
 * -- ${
 * AND ext_id like :ext_id || '%'
 * -- $mysql{
 * AND ext_id like concat(:ext_id, '%')
 * -- }$
 * -- }$
 * </pre>
 */
public class VariantResolver extends StatementWrapper {
  private final String variant;

  public VariantResolver(Statement template, String variant) {
    super(template);
    this.variant = variant;
  }

  @Override
  protected Statement wrap(Template toBeWrapped) {
    if (toBeWrapped == null) return null;
    return new VariantResolver(((Statement)toBeWrapped), variant);
  }

  @Override
  public Statement get(String... names) {
    if (names.length == 1) {
      Statement statement = super.get(names);
      if (statement.regionNames().contains(variant)) {
        statement = statement.get(variant);
      }
      return statement;
    }
    return super.get(names);
  }

  public static Wrapper wrap(String variant) {
    return new Wrapper(variant);
  }

  public static class Wrapper implements PostProcessor {
    private final String variant;

    public Wrapper(String variant) {
      this.variant = variant;
    }

    @Override
    public Statement processRepository(Statement repo) {
      return new VariantResolver(repo, variant);
    }

  }

}
