package co.mewf.sqlwriter.builders;

import static org.junit.Assert.assertEquals;
import co.mewf.sqlwriter.Queries;
import co.mewf.sqlwriter.testutils.Simple;

import org.junit.Test;

public class InsertBuilderTest {

  @Test
  public void should_include_columns() {
    assertEquals("INSERT INTO Simple(name, other) VALUES(?, ?)", Queries.insert(Simple.class).columns("name", "other").toString());
  }

  @Test
  public void sql_should_alias_toString() {
    InsertBuilder builder = Queries.insert(Simple.class).columns("name", "other");
    assertEquals(builder.toString(), builder.sql());
  }
}
