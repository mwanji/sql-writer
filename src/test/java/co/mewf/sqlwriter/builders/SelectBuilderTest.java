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
import co.mewf.sqlwriter.testutils.SimpleJoinColumn;
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
    String sql = select().from(SimpleJpa.class).columns("name").sql();
    assertEquals("SELECT simple_with_jpa.name_with_jpa FROM simple_with_jpa", sql);
  }

  @Test
  public void should_filter() {
    String sql = select().from(Simple.class).where().eq("id").toString();
    assertEquals("SELECT Simple.* FROM Simple WHERE Simple.id = ?", sql);
  }

  @Test
  public void should_qualify() {
    String sql = select().from(Simple.class).asc("name").desc("other").limit(10).offset(7).sql();
    assertEquals("SELECT Simple.* FROM Simple ORDER BY Simple.name ASC, Simple.other DESC LIMIT 10 OFFSET 7", sql);
  }

  @Test
  public void should_select_all_columns_from_multiple_tables() {
    String sql = select().from(Simple.class).from(SimpleOneToOne.class).sql();
    assertEquals("SELECT Simple.*, SimpleOneToOne.* FROM Simple INNER JOIN SimpleOneToOne ON Simple.id = SimpleOneToOne.simple_id", sql);
  }

  @Test
  public void should_associate_columns_to_tables() {
    String sql = select().from(Simple.class).columns("name", "another").from(SimpleOneToOne.class).column("name", "aliased_name").columns("another_jpa").sql();
    assertEquals("SELECT Simple.name, Simple.another, SimpleOneToOne.name AS \"aliased_name\", SimpleOneToOne.another_jpa FROM Simple INNER JOIN SimpleOneToOne ON Simple.id = SimpleOneToOne.simple_id", sql);
  }

  @Test
  public void should_join_many_to_one_automatically() {
    String sql = select().from(Simple.class).from(SimpleRelation.class).sql();
    assertEquals("SELECT Simple.*, SimpleRelation.* FROM Simple INNER JOIN SimpleRelation ON Simple.id = SimpleRelation.simple_id", sql);

    sql = select().from(SimpleRelation.class).from(Simple.class).sql();
    assertEquals("SELECT SimpleRelation.*, Simple.* FROM SimpleRelation INNER JOIN Simple ON Simple.id = SimpleRelation.simple_id", sql);
  }

  @Test
  public void should_join_one_to_one_automatically() {
    String sql = select().from(Simple.class).columns("name").from(SimpleOneToOne.class).sql();
    assertEquals("SELECT Simple.name, SimpleOneToOne.* FROM Simple INNER JOIN SimpleOneToOne ON Simple.id = SimpleOneToOne.simple_id", sql);

    sql = select().from(SimpleOneToOne.class).from(Simple.class).columns("name").sql();
    assertEquals("SELECT SimpleOneToOne.*, Simple.name FROM SimpleOneToOne INNER JOIN Simple ON Simple.id = SimpleOneToOne.simple_id", sql);
  }

  @Test
  public void should_join_one_to_one_on_annotated_column() {
    String sql = select().from(SimpleOneToOne.class).from(SimpleOneToMany.class).sql();
    assertEquals("SELECT SimpleOneToOne.*, SimpleOneToMany.* FROM SimpleOneToOne INNER JOIN SimpleOneToMany ON SimpleOneToMany.id = SimpleOneToOne.sotm_fk", sql);

    sql = select().from(SimpleOneToMany.class).from(SimpleOneToOne.class).sql();
    assertEquals("SELECT SimpleOneToMany.*, SimpleOneToOne.* FROM SimpleOneToMany INNER JOIN SimpleOneToOne ON SimpleOneToMany.id = SimpleOneToOne.sotm_fk", sql);
}

  @Test
  public void should_join_bidirectional_one_to_one_automatically() {
    String sql = select().from(BiDirectionalOneToOne1.class).from(BiDirectionalOneToOne2.class).sql();
    assertEquals("SELECT BiDirectionalOneToOne1.*, BiDirectionalOneToOne2.* FROM BiDirectionalOneToOne1 INNER JOIN BiDirectionalOneToOne2 ON BiDirectionalOneToOne1.id = BiDirectionalOneToOne2.biDirectionalOneToOne1_id", sql);

    sql = select().from(BiDirectionalOneToOne2.class).from(BiDirectionalOneToOne1.class).sql();
    assertEquals("SELECT BiDirectionalOneToOne2.*, BiDirectionalOneToOne1.* FROM BiDirectionalOneToOne2 INNER JOIN BiDirectionalOneToOne1 ON BiDirectionalOneToOne1.id = BiDirectionalOneToOne2.biDirectionalOneToOne1_id", sql);
}

  @Test
  public void should_join_one_to_many_automatically() {
    String sql = select().from(Simple.class).column("name", "name_alias").from(SimpleOneToMany.class).sql();
    assertEquals("SELECT Simple.name AS \"name_alias\", SimpleOneToMany.* FROM Simple INNER JOIN SimpleOneToMany ON SimpleOneToMany.id = Simple.simpleOneToMany_id", sql);

    sql = select().from(SimpleOneToMany.class).from(Simple.class).column("name", "name_alias").sql();
    assertEquals("SELECT SimpleOneToMany.*, Simple.name AS \"name_alias\" FROM SimpleOneToMany INNER JOIN Simple ON SimpleOneToMany.id = Simple.simpleOneToMany_id", sql);
  }

  @Test
  public void should_use_field_as_join_column_name() {
    String sql = select().from(PkId.class).from(SimpleOneToMany.class).sql();
    assertEquals("SELECT PkId.*, SimpleOneToMany.* FROM PkId INNER JOIN SimpleOneToMany ON PkId.pk = SimpleOneToMany.pkId_pk", sql);

    sql = select().from(SimpleOneToMany.class).from(PkId.class).sql();
    assertEquals("SELECT SimpleOneToMany.*, PkId.* FROM SimpleOneToMany INNER JOIN PkId ON PkId.pk = SimpleOneToMany.pkId_pk", sql);
  }

  @Test
  public void should_get_column_name_from_annotation_on_many_to_one() {
    String sql = select().from(PkId.class).from(SimpleJoinColumn.class).sql();
    assertEquals("SELECT PkId.*, SimpleJoinColumn.* FROM PkId INNER JOIN SimpleJoinColumn ON PkId.pk = SimpleJoinColumn.pk_fk", sql);

    sql = select().from(SimpleJoinColumn.class).from(PkId.class).sql();
    assertEquals("SELECT SimpleJoinColumn.*, PkId.* FROM SimpleJoinColumn INNER JOIN PkId ON PkId.pk = SimpleJoinColumn.pk_fk", sql);
  }

  @Test
  public void should_get_column_name_from_annotation_on_one_to_many() {
    String sql = select().from(SimpleJpa.class).join(SimpleOneToMany.class).sql();
    assertEquals("SELECT simple_with_jpa.* FROM simple_with_jpa INNER JOIN SimpleOneToMany ON SimpleOneToMany.id = simple_with_jpa.renamed_one_to_many", sql);

    sql = select().from(SimpleOneToMany.class).from(SimpleJpa.class).sql();
    assertEquals("SELECT SimpleOneToMany.*, simple_with_jpa.* FROM SimpleOneToMany INNER JOIN simple_with_jpa ON SimpleOneToMany.id = simple_with_jpa.renamed_one_to_many", sql);
  }

  @Test
  public void should_inner_join() {
    String sql = select().from(Simple.class).join(SimpleOneToMany.class).sql();
    assertEquals("SELECT Simple.* FROM Simple INNER JOIN SimpleOneToMany ON SimpleOneToMany.id = Simple.simpleOneToMany_id", sql);
  }

  @Test
  public void should_inner_join_multiple_tables() {
    String sql = select().from(Simple.class).join(SimpleOneToMany.class).join(ParentToMany.class, SimpleOneToMany.class).sql();
    assertEquals("SELECT Simple.* FROM Simple INNER JOIN SimpleOneToMany ON SimpleOneToMany.id = Simple.simpleOneToMany_id INNER JOIN ParentToMany ON ParentToMany.id = SimpleOneToMany.parentToMany_id", sql);
  }

  @Test
  public void should_order_on_last_selected_table() {
    String sql = select().from(Simple.class).asc("name").from(SimpleOneToOne.class).desc("other").sql();
    assertEquals(select().from(Simple.class).from(SimpleOneToOne.class) + " ORDER BY Simple.name ASC, SimpleOneToOne.other DESC", sql);
  }

  @Test
  public void should_order_on_last_joined_table() {
    String sql = select().from(Simple.class).asc("name").join(SimpleOneToOne.class).desc("other").sql();
    assertEquals(select().from(Simple.class).join(SimpleOneToOne.class) + " ORDER BY Simple.name ASC, SimpleOneToOne.other DESC", sql);
  }

  @Test
  public void should_order_on_source_of_last_join() {
    String sql = select().from(Simple.class).join(SimpleOneToMany.class).join(ParentToMany.class, SimpleOneToMany.class).asc("other").sql();
    assertEquals("SELECT Simple.* FROM Simple INNER JOIN SimpleOneToMany ON SimpleOneToMany.id = Simple.simpleOneToMany_id INNER JOIN ParentToMany ON ParentToMany.id = SimpleOneToMany.parentToMany_id ORDER BY ParentToMany.other ASC", sql);
  }

  @Test
  public void should_use_property() {
    assertEquals("SELECT PropertyAccess.name_property FROM PropertyAccess", select().from(PropertyAccess.class).columns("name").sql());
  }

  @Test
  public void sql_should_alias_toString() {
    assertEquals(select().from(Simple.class).toString(), select().from(Simple.class).sql());
    assertEquals(select().from(Simple.class).where().eq("id").toString(), select().from(Simple.class).where().eq("id").sql());
  }

  @Test
  public void should_accept_custom_sql_functions() {
    String sql = select().from(Simple.class).function("AVG", "age", "average_age").sql();
    assertEquals("SELECT AVG(Simple.age) AS \"average_age\" FROM Simple", sql);
  }

  @Test
  public void should_left_join() {
    String sql = select().from(Simple.class).leftJoin(SimpleRelation.class).sql();
    assertEquals("SELECT Simple.* FROM Simple LEFT JOIN SimpleRelation ON Simple.id = SimpleRelation.simple_id", sql);
  }

  @Test
  public void should_right_join() {
    String sql = select().from(Simple.class).rightJoin(SimpleRelation.class).sql();
    assertEquals("SELECT Simple.* FROM Simple RIGHT JOIN SimpleRelation ON Simple.id = SimpleRelation.simple_id", sql);
  }

  @Test
  public void should_join_unidirectional_many_to_many() {
    String sql = select().from(Simple.class).from(ParentToMany.class).sql();
    assertEquals("SELECT Simple.*, ParentToMany.* FROM Simple INNER JOIN ParentToMany_Simple ON Simple.id = ParentToMany_Simple.simple_id INNER JOIN ParentToMany ON ParentToMany.id = ParentToMany_Simple.parentToMany_id", sql);

    sql = select().from(ParentToMany.class).from(Simple.class).sql();
    assertEquals("SELECT ParentToMany.*, Simple.* FROM ParentToMany INNER JOIN ParentToMany_Simple ON ParentToMany.id = ParentToMany_Simple.parentToMany_id INNER JOIN Simple ON Simple.id = ParentToMany_Simple.simple_id", sql);
  }

  @Test
  public void should_join_bidirectional_many_to_many() {
    String sql = select().from(ParentToMany.class).from(SimpleJpa.class).sql();
    assertEquals("SELECT ParentToMany.*, simple_with_jpa.* FROM ParentToMany INNER JOIN ParentToMany_simple_with_jpa ON ParentToMany.id = ParentToMany_simple_with_jpa.parentToMany_id INNER JOIN simple_with_jpa ON simple_with_jpa.id = ParentToMany_simple_with_jpa.simple_with_jpa_id", sql);

    sql = select().from(SimpleJpa.class).from(ParentToMany.class).sql();
    assertEquals("SELECT simple_with_jpa.*, ParentToMany.* FROM simple_with_jpa INNER JOIN ParentToMany_simple_with_jpa ON simple_with_jpa.id = ParentToMany_simple_with_jpa.simple_with_jpa_id INNER JOIN ParentToMany ON ParentToMany.id = ParentToMany_simple_with_jpa.parentToMany_id", sql);
  }

  @Test
  public void should_get_join_table_and_columns_from_annotation_on_unidirectional_many_to_many() {
    String sql = select().from(ParentToMany.class).from(PkId.class).sql();
    assertEquals("SELECT ParentToMany.*, PkId.* FROM ParentToMany INNER JOIN ptm_pk ON ParentToMany.id = ptm_pk.p_2_many_fk INNER JOIN PkId ON PkId.pk = ptm_pk.pk_id_fk", sql);

    sql = select().from(PkId.class).from(ParentToMany.class).sql();
    assertEquals("SELECT PkId.*, ParentToMany.* FROM PkId INNER JOIN ptm_pk ON PkId.pk = ptm_pk.pk_id_fk INNER JOIN ParentToMany ON ParentToMany.id = ptm_pk.p_2_many_fk", sql);
  }
}
