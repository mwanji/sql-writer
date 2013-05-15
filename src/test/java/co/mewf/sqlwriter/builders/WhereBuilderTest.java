package co.mewf.sqlwriter.builders;

import static co.mewf.sqlwriter.Queries.select;
import static co.mewf.sqlwriter.Queries.update;
import static co.mewf.sqlwriter.builders.WhereBuilder.Bool.OR;
import static org.junit.Assert.assertEquals;
import co.mewf.sqlwriter.testutils.ParentToMany;
import co.mewf.sqlwriter.testutils.Simple;
import co.mewf.sqlwriter.testutils.SimpleJpa;
import co.mewf.sqlwriter.testutils.SimpleOneToMany;
import co.mewf.sqlwriter.testutils.SimpleOneToOne;

import org.junit.Test;

public class WhereBuilderTest {

  private final SelectBuilder select = select().from(Simple.class);

  @Test
  public void should_use_and_by_default() {
    assertEquals(select + " WHERE Simple.name = ? AND Simple.other = ?", select.where().eq("name").eq("other").toString());
  }

  @Test
  public void should_use_or() {
    assertEquals(select + " WHERE Simple.name = ? OR Simple.other = ?", select.where().eq("name").eq(OR, "other").toString());
  }

  @Test
  public void should_use_comparison() {
    assertEquals(select + " WHERE Simple.name > ? AND Simple.other < ?", select.where().gt("name").lt("other").toString());
  }

  @Test
  public void should_use_or_for_less_than() {
    assertEquals(select + " WHERE Simple.name > ? OR Simple.other < ?", select.where().gt("name").lt(OR, "other").toString());
  }

  @Test
  public void should_use_or_for_greater_than() {
    assertEquals(select + " WHERE Simple.name < ? OR Simple.other > ?", select.where().lt("name").gt(OR, "other").toString());
  }

  @Test
  public void should_use_between() {
    assertEquals(select + " WHERE Simple.other BETWEEN ? AND ?", select.where().between("other").toString());
  }

  @Test
  public void should_filter_different_tables() {
    String sql = select().from(Simple.class).where().eq("name").from(SimpleOneToOne.class).where().eq("other").toString();
    assertEquals(select.from(SimpleOneToOne.class) + " WHERE Simple.name = ? AND SimpleOneToOne.other = ?", sql);
  }

  @Test
  public void should_filter_different_tables_using_or() {
    String sql = select().from(Simple.class).where().eq("name").from(SimpleOneToOne.class).where().eq(OR, "other").toString();
    assertEquals(select.from(SimpleOneToOne.class) + " WHERE Simple.name = ? OR SimpleOneToOne.other = ?", sql);
  }

  @Test
  public void should_filter_joined_tables() {
    String sql = select().from(Simple.class).where().eq("name").join(SimpleOneToOne.class).where().eq(OR, "other").toString();
    assertEquals(select.join(SimpleOneToOne.class) + " WHERE Simple.name = ? OR SimpleOneToOne.other = ?", sql);
  }

  @Test
  public void should_use_name_from_annotation() {
    assertEquals(select().from(SimpleJpa.class) + " WHERE simple_with_jpa.name_with_jpa = ?", select().from(SimpleJpa.class).where().eq("name").toString());
  }

  @Test
  public void should_use_source_of_last_join() {
    String sql = select().from(Simple.class).join(SimpleOneToMany.class).join(ParentToMany.class, SimpleOneToMany.class).where().eq("other").toString();
    assertEquals("SELECT Simple.* FROM Simple INNER JOIN SimpleOneToMany ON SimpleOneToMany.id = Simple.simpleOneToMany_id INNER JOIN ParentToMany ON ParentToMany.id = SimpleOneToMany.parentToMany_id WHERE ParentToMany.other = ?", sql);
  }

  @Test
  public void sql_should_alias_toString() {
    String sql = update(Simple.class).set("name").where().eq("id").sql();
    assertEquals(update(Simple.class).set("name").where().eq("id").toString(), sql);
  }

  @Test
  public void should_use_like() {
    String sql = select().from(Simple.class).where().like("name").sql();
    assertEquals("SELECT Simple.* FROM Simple WHERE Simple.name LIKE ?", sql);
  }

  @Test
  public void should_use_in() {
    assertEquals("SELECT Simple.* FROM Simple WHERE Simple.name IN (?, ?, ?)", select().from(Simple.class).where().in("name", 3).sql());
  }
}
