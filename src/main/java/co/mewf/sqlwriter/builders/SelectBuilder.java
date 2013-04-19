package co.mewf.sqlwriter.builders;

import co.mewf.sqlwriter.mapping.Join;
import co.mewf.sqlwriter.mapping.TableInfo;
import co.mewf.sqlwriter.utils.Strings;

import java.util.ArrayList;
import java.util.List;

public class SelectBuilder {

  private final List<TableInfo> tables = new ArrayList<TableInfo>();
  private final List<SelectWhereBuilder> wheres = new ArrayList<SelectWhereBuilder>();
  private final QualifierBuilder qualifier = new QualifierBuilder(this);
  private TableInfo currentTable;
  private TableInfo rootTable;

  /**
   * Retrieves the columns from the given entity and inner joins it to the first entity given to the builder.
   */
  public SelectBuilder from(Class<?> entityClass) {
    join(entityClass);
    tables.add(currentTable);

    return this;
  }

  /**
   * Inner joins this entity to the first entity given to the builder.
   */
  public SelectBuilder join(Class<?> entityClass) {
    currentTable = new TableInfo(entityClass);

    if (rootTable == null) {
      rootTable = currentTable;
      return this;
    }

    Join join = currentTable.join(rootTable.entityClass);

    rootTable.joins.add(join);

    return this;
  }

  public SelectBuilder join(Class<?> from, Class<?> to) {
    rootTable.join(from, to);
    return this;
  }

  public SelectBuilder columns(String... columns) {
    for (String column : columns) {
      column(column, null);
    }

    return this;
  }

  public SelectBuilder column(String column, String alias) {
    currentTable.columns.add(this.currentTable.column(column, alias));
    return this;
  }

  public SelectWhereBuilder where() {
    SelectWhereBuilder where = new SelectWhereBuilder(currentTable, this, wheres.isEmpty());
    wheres.add(where);

    return where;
  }

  public SelectBuilder asc(String column) {
    qualifier.orderBy(currentTable.column(column), QualifierBuilder.Order.ASC);
    return this;
  }

  public SelectBuilder desc(String column) {
    qualifier.orderBy(currentTable.column(column), QualifierBuilder.Order.DESC);
    return this;
  }

  public SelectBuilder limit(int limit) {
    qualifier.limit(limit);
    return this;
  }

  public SelectBuilder offset(int offset) {
    qualifier.offset(offset);
    return this;
  }

  public String sql() {
    return toString();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("SELECT ");

    for (TableInfo table : tables) {
      table.toColumnsString(builder).append(", ");
    }

    Strings.chompChomp(builder).append(" FROM ").append(rootTable.name);
    rootTable.toJoinString(builder);

    if (!wheres.isEmpty()) {
      builder.append(" WHERE");
      for (SelectWhereBuilder where : wheres) {
        where.toString(builder);
      }
    }

    return qualifier.toString(builder).toString();
  }
}
