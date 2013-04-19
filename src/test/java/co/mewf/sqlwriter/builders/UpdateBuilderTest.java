package co.mewf.sqlwriter.builders;

import static co.mewf.sqlwriter.Queries.update;
import static org.junit.Assert.assertEquals;
import co.mewf.sqlwriter.Queries;
import co.mewf.sqlwriter.testutils.Simple;

import org.junit.Test;

public class UpdateBuilderTest {

  @Test
  public void should_update_table() {
    assertEquals("UPDATE Simple SET Simple.name = ?, Simple.other = ?", Queries.update(Simple.class).set("name", "other").toString());
  }

  @Test
  public void should_filter_and_qualify() {
    String sql = update(Simple.class).set("name").limit(3).desc("other").asc("name").where().eq("other").toString();
    assertEquals("UPDATE Simple SET Simple.name = ? WHERE Simple.other = ? ORDER BY Simple.other DESC, Simple.name ASC LIMIT 3", sql);
  }

  @Test
  public void sql_should_alias_toString() {
    UpdateBuilder update = update(Simple.class).set("name");
    assertEquals(update.toString(), update.sql());
  }
}
