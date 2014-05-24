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
