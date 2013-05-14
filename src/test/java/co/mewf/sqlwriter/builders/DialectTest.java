package co.mewf.sqlwriter.builders;

import static org.junit.Assert.assertEquals;
import co.mewf.sqlwriter.Queries;
import co.mewf.sqlwriter.Queries.Query;
import co.mewf.sqlwriter.dialects.Dialect;
import co.mewf.sqlwriter.dialects.StandardDialect;
import co.mewf.sqlwriter.mapping.TableInfo;
import co.mewf.sqlwriter.testutils.Simple;

import org.junit.Test;

public class DialectTest {

  private final Dialect backTickDialect = new StandardDialect() {
    @Override
    public String column(TableInfo table, String name, String alias) {
      return super.column(table, "`" + name + "`", alias);
    };

    @Override
    public String table(TableInfo table) {
      return "`" + table.name + "`";
    };
  };

  private final Query query = Queries.query(backTickDialect);

  @Test
  public void select_should_use_dialect() {
    String sql = query.select().from(Simple.class).columns("other").where().eq("id").sql();
    assertEquals("SELECT `Simple`.`other` FROM `Simple` WHERE `Simple`.`id` = ?", sql);
  }

  @Test
  public void update_should_use_dialect() {
    String sql = query.update(Simple.class).set("name").where().eq("id").sql();
    assertEquals("UPDATE `Simple` SET `Simple`.`name` = ? WHERE `Simple`.`id` = ?", sql);
  }

  @Test
  public void insert_should_use_dialect() {
    String sql = query.insert(Simple.class).sql();
    assertEquals("INSERT INTO `Simple`(`id`, `name`) VALUES(?, ?)", sql);
  }

  @Test
  public void delete_should_use_dialect() {
    String sql = query.delete(Simple.class).where().eq("id").sql();
    assertEquals("DELETE FROM `Simple` WHERE `Simple`.`id` = ?", sql);
  }
}
