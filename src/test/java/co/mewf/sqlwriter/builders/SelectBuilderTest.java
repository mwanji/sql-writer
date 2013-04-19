package co.mewf.sqlwriter.builders;

import static co.mewf.sqlwriter.Queries.select;
import static org.junit.Assert.assertEquals;
import co.mewf.sqlwriter.Queries;
import co.mewf.sqlwriter.testutils.BiDirectionalOneToOne1;
import co.mewf.sqlwriter.testutils.BiDirectionalOneToOne2;
import co.mewf.sqlwriter.testutils.ParentToMany;
import co.mewf.sqlwriter.testutils.PkId;
import co.mewf.sqlwriter.testutils.PropertyAccess;
import co.mewf.sqlwriter.testutils.Simple;
import co.mewf.sqlwriter.testutils.SimpleJpa;
import co.mewf.sqlwriter.testutils.SimpleOneToMany;
import co.mewf.sqlwriter.testutils.SimpleOneToOne;
import co.mewf.sqlwriter.testutils.SimpleRelation;

import org.junit.Test;

public class SelectBuilderTest {

  @Test
  public void should_select_all_columns() {
    String sql = Queries.select().from(Simple.class).toString();

    assertEquals("SELECT Simple.* FROM Simple", sql);
  }

  @Test
  public void should_select_specified_columns() {
    assertEquals("SELECT Simple.name, Simple.age FROM Simple", select().from(Simple.class).columns("name", "age").toString());
  }

  @Test
  public void should_alias_columns() throws Exception {
    assertEquals("SELECT Simple.name AS \"n\" FROM Simple", select().from(Simple.class).column("name", "n").toString());
  }

  @Test
  public void should_use_names_in_annotations() {
    assertEquals("SELECT simple_with_jpa.name_with_jpa FROM simple_with_jpa", select().from(SimpleJpa.class).columns("name").toString());
  }

  @Test
  public void should_select_all_columns_from_multiple_tables() {
    String sql = select().from(Simple.class).from(SimpleOneToOne.class).toString();
    assertEquals("SELECT Simple.*, SimpleOneToOne.* FROM Simple INNER JOIN SimpleOneToOne ON SimpleOneToOne.id = Simple.simpleOneToOne_id", sql);
  }

  @Test
  public void should_associate_columns_to_tables() {
    String sql = select().from(Simple.class).columns("name", "another").from(SimpleOneToOne.class).column("name", "aliased_name").columns("another_jpa").toString();
    assertEquals("SELECT Simple.name, Simple.another, SimpleOneToOne.name AS \"aliased_name\", SimpleOneToOne.another_jpa FROM Simple INNER JOIN SimpleOneToOne ON SimpleOneToOne.id = Simple.simpleOneToOne_id", sql);
  }

  @Test
  public void should_join_many_to_one_automatically() {
    String sql = select().from(Simple.class).from(SimpleRelation.class).toString();
    assertEquals("SELECT Simple.*, SimpleRelation.* FROM Simple INNER JOIN SimpleRelation ON Simple.id = SimpleRelation.simple_id", sql);
  }

  @Test
  public void should_join_one_to_one_automatically() {
    String sql = select().from(Simple.class).columns("name").from(SimpleOneToOne.class).toString();
    assertEquals("SELECT Simple.name, SimpleOneToOne.* FROM Simple INNER JOIN SimpleOneToOne ON SimpleOneToOne.id = Simple.simpleOneToOne_id", sql);
  }

  @Test
  public void should_join_bidirectional_one_to_one_automatically() {
    String sql = select().from(BiDirectionalOneToOne1.class).from(BiDirectionalOneToOne2.class).toString();
    assertEquals("SELECT BiDirectionalOneToOne1.*, BiDirectionalOneToOne2.* FROM BiDirectionalOneToOne1 INNER JOIN BiDirectionalOneToOne2 ON BiDirectionalOneToOne2.id = BiDirectionalOneToOne1.biDirectionalOneToOne2_id", sql);
  }

  @Test
  public void should_join_one_to_many_automatically() {
    String sql = select().from(Simple.class).column("name", "name_alias").from(SimpleOneToMany.class).toString();
    assertEquals("SELECT Simple.name AS \"name_alias\", SimpleOneToMany.* FROM Simple INNER JOIN SimpleOneToMany ON SimpleOneToMany.id = Simple.simpleOneToMany_id", sql);
  }

  @Test
  public void should_use_field_as_join_column_name() {
    String sql = select().from(PkId.class).from(SimpleOneToMany.class).toString();
    assertEquals("SELECT PkId.*, SimpleOneToMany.* FROM PkId INNER JOIN SimpleOneToMany ON PkId.pk = SimpleOneToMany.pkId_pk", sql);
  }

  @Test
  public void should_inner_join() {
    String sql = select().from(Simple.class).join(SimpleOneToMany.class).toString();
    assertEquals("SELECT Simple.* FROM Simple INNER JOIN SimpleOneToMany ON SimpleOneToMany.id = Simple.simpleOneToMany_id", sql);
  }

  @Test
  public void should_inner_join_multiple_tables() {
    String sql = select().from(Simple.class).join(SimpleOneToMany.class).join(ParentToMany.class, SimpleOneToMany.class).toString();
    assertEquals("SELECT Simple.* FROM Simple INNER JOIN SimpleOneToMany ON SimpleOneToMany.id = Simple.simpleOneToMany_id INNER JOIN ParentToMany ON ParentToMany.id = SimpleOneToMany.parentToMany_id", sql);
  }

  @Test
  public void should_filter() {
    String sql = select().from(Simple.class).where().eq("id").toString();
    assertEquals("SELECT Simple.* FROM Simple WHERE Simple.id = ?", sql);
  }

  @Test
  public void should_qualify() {
    assertEquals("SELECT Simple.* FROM Simple ORDER BY Simple.name ASC, Simple.other DESC LIMIT 10 OFFSET 7", select().from(Simple.class).asc("name").desc("other").limit(10).offset(7).toString());
  }

  @Test
  public void should_order_on_last_selected_table() {
    String sql = select().from(Simple.class).asc("name").from(SimpleOneToOne.class).desc("other").toString();
    assertEquals(select().from(Simple.class).from(SimpleOneToOne.class) + " ORDER BY Simple.name ASC, SimpleOneToOne.other DESC", sql);
  }

  @Test
  public void should_order_on_last_joined_table() {
    String sql = select().from(Simple.class).asc("name").join(SimpleOneToOne.class).desc("other").toString();
    assertEquals(select().from(Simple.class).join(SimpleOneToOne.class) + " ORDER BY Simple.name ASC, SimpleOneToOne.other DESC", sql);
  }

  @Test
  public void should_order_on_source_of_last_join() {
    String sql = select().from(Simple.class).join(SimpleOneToMany.class).join(ParentToMany.class, SimpleOneToMany.class).asc("other").toString();
    assertEquals("SELECT Simple.* FROM Simple INNER JOIN SimpleOneToMany ON SimpleOneToMany.id = Simple.simpleOneToMany_id INNER JOIN ParentToMany ON ParentToMany.id = SimpleOneToMany.parentToMany_id ORDER BY ParentToMany.other ASC", sql);
  }

  @Test
  public void should_use_property() {
    assertEquals("SELECT PropertyAccess.name_property FROM PropertyAccess", select().from(PropertyAccess.class).columns("name").toString());
  }

  @Test
  public void sql_should_alias_toString() {
    assertEquals(select().from(Simple.class).toString(), select().from(Simple.class).sql());
    assertEquals(select().from(Simple.class).where().eq("id").toString(), select().from(Simple.class).where().eq("id").sql());
  }
}
