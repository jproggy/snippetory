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

package org.jproggy.snippetory.sql.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jproggy.snippetory.engine.Location;
import org.jproggy.snippetory.engine.TemplateFragment;

public class StatementFragment extends TemplateFragment implements StatementBinder {

  public StatementFragment(CharSequence data) {
    super(data);
  }

  @Override
  public int bindTo(PreparedStatement stmt, int offset) throws SQLException {
    return offset;
  }

  @Override
  public StatementFragment start(int start) {
    return new StatementFragment(this.subSequence(0,start));
  }

  @Override
  public StatementFragment end(int start) {
    return new StatementFragment(this.subSequence(start, this.length()));
  }

  @Override
  public StatementFragment cleanCopy(Location parent) {
    return this;
  }
}
