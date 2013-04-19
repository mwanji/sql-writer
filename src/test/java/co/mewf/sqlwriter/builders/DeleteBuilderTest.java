package co.mewf.sqlwriter.builders;

import static co.mewf.sqlwriter.Queries.delete;
import static org.junit.Assert.assertEquals;
import co.mewf.sqlwriter.Queries;
import co.mewf.sqlwriter.testutils.Simple;

import org.junit.Test;

public class DeleteBuilderTest {

  @Test
  public void should_filter() {
    assertEquals("DELETE FROM Simple WHERE Simple.id = ?", Queries.delete(Simple.class).where().eq("id").toString());
  }

  @Test
  public void should_qualify() {
    assertEquals("DELETE FROM Simple ORDER BY Simple.name DESC, Simple.other ASC LIMIT 4", delete(Simple.class).desc("name").asc("other").limit(4).toString());
  }

  @Test
  public void sql_should_alias_toString() {
    DeleteBuilder delete = delete(Simple.class);
    delete.where().eq("id");
    assertEquals(delete.toString(), delete.sql());
  }
}
