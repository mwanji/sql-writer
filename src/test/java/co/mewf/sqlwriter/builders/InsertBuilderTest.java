package co.mewf.sqlwriter.builders;

import static co.mewf.sqlwriter.Queries.insert;
import static org.junit.Assert.assertEquals;
import co.mewf.sqlwriter.Queries;
import co.mewf.sqlwriter.testutils.Simple;
import co.mewf.sqlwriter.testutils.Uninsertable;
import co.mewf.sqlwriter.testutils.UninsertableProperties;

import org.junit.Test;

public class InsertBuilderTest {

  @Test
  public void should_include_columns() {
    assertEquals("INSERT INTO Simple(name, other) VALUES(?, ?)", Queries.insert(Simple.class).columns("name", "other").sql());
  }

  @Test
  public void sql_should_alias_toString() {
    InsertBuilder builder = insert(Simple.class).columns("name", "other");
    assertEquals(builder.toString(), builder.sql());
  }

  @Test
  public void should_add_all_columns() {
    String sql = insert(Simple.class).sql();
    assertEquals("INSERT INTO Simple(id, name) VALUES(?, ?)", sql);
  }

  @Test
  public void should_not_add_generated_or_uninsertable_columns() {
    String sql = insert(Uninsertable.class).sql();
    assertEquals("INSERT INTO Uninsertable(name, age) VALUES(?, ?)", sql);
  }

  @Test
  public void should_not_add_generated_or_uninsertable_columns_from_properties() {
    String sql = insert(UninsertableProperties.class).sql();
    assertEquals("INSERT INTO UninsertableProperties(age, name) VALUES(?, ?)", sql);
  }
}
