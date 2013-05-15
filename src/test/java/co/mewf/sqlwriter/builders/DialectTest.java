package co.mewf.sqlwriter.builders;

import static org.junit.Assert.assertEquals;
import co.mewf.sqlwriter.Queries;
import co.mewf.sqlwriter.Queries.Query;
import co.mewf.sqlwriter.dialects.Dialect;
import co.mewf.sqlwriter.dialects.StandardDialect;
import co.mewf.sqlwriter.mapping.TableInfo;
import co.mewf.sqlwriter.testutils.Simple;
import co.mewf.sqlwriter.utils.Strings;

import java.util.List;

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

  @Test
  public void should_translate_limit_to_top() {
    StandardDialect topDialect = new StandardDialect() {
      @Override
      public String select(TableInfo rootTable, List<TableInfo> tables, List<SelectWhereBuilder> wheres, QualifierBuilder qualifier) {
        StringBuilder builder = new StringBuilder("SELECT ");

        if (qualifier.hasLimit()) {
          builder.append("TOP ").append(qualifier.getLimit()).append(" ");
        }

        for (TableInfo table : tables) {
          table.toColumnsString(builder).append(", ");
        }

        Strings.chompChomp(builder).append(" FROM ").append(rootTable);
        rootTable.toJoinString(builder);

        if (!wheres.isEmpty()) {
          builder.append(" WHERE");
          for (SelectWhereBuilder where : wheres) {
            where.toString(builder);
          }
        }

        return builder.append(qualifier).toString();
      }

      @Override
      public String qualify(String order, int limit, int offset) {
        StringBuilder builder = new StringBuilder(order);

        if (offset > -1) {
          builder.append(" OFFSET ").append(offset);
        }

        return builder.toString();
      }
    };


    String sql = Queries.query(topDialect).select().from(Simple.class).limit(5).sql();
    assertEquals("SELECT TOP 5 Simple.* FROM Simple", sql);
  }
}
