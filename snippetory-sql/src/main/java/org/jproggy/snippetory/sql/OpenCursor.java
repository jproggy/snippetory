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

import java.util.Iterator;

import org.jproggy.snippetory.sql.spi.RowProcessor;

public interface OpenCursor<T> extends Cursor<T> {
  @Override
  public OpenIterator<T> iterator();

  public void processRow(RowProcessor proc);
  public void updateRow();

  public interface OpenIterator<T> extends Iterator<T> {
    public void processRow(RowProcessor proc);
    public void updateRow();
  }
}
