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

package org.jproggy.snippetory.cypher;

import org.jproggy.snippetory.Template;
import org.neo4j.driver.Query;

import java.sql.PreparedStatement;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionConfig;

/**
 * Represents the template of a statement and methods to bind data as well as to tailor this statement
 * to the current needs. This means all the possibilities of a template can be used here. However,
 * there are a view specialties to mention. In general data is intended to be bound to the parameters of
 * a prepared statement. The type that's used to provide this data is used to figure out the binding
 * method. So if a {@code String} is bound {@link PreparedStatement#setString}, {@link PreparedStatement#setInt} for
 * an {@code Integer} and so on. To extend the statement itself instead of binding parameters the {@link Cypher#markAsCypher}
 * method can be used, if those parts are represented by plain {@code String}s. Parts, that are taken from other
 * statements will extend the statement as well.
 *
 * @author B.Ebertz
 */
public interface Statement extends Template {
  @Override
  Statement set(String name, Object value);
  @Override
  Statement append(String name, Object value);
  @Override
  Statement clear();

  Query toQuery();

  default Result run(Session s) {
    return s.run(toQuery(), TransactionConfig.empty());
  }
}
