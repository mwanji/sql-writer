package co.mewf.sqlwriter.builders;

import static co.mewf.sqlwriter.Queries.select;
import static org.junit.Assert.assertEquals;
import co.mewf.sqlwriter.Queries;
import co.mewf.sqlwriter.testutils.Simple;

import org.junit.Test;

public class QualifierBuilderTest {

  private final SelectBuilder select = Queries.select().from(Simple.class);

  @Test
  public void should_limit() {
    assertEquals(select + " LIMIT 10", select().from(Simple.class).limit(10).sql());
  }

  @Test
  public void should_order_ascending() {
    assertEquals(select + " ORDER BY Simple.name ASC LIMIT 10", select.limit(10).asc("name").sql());
  }
}
