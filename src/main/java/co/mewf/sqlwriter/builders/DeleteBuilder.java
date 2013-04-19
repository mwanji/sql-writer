package co.mewf.sqlwriter.builders;

import co.mewf.sqlwriter.mapping.TableInfo;


public class DeleteBuilder {

  private final Class<?> entityClass;
  private final TableInfo table;
  private final QualifierBuilder qualifier = new QualifierBuilder(this);
  private WhereBuilder where;

  public DeleteBuilder(Class<?> entityClass) {
    this.entityClass = entityClass;
    this.table = new TableInfo(entityClass);
  }

  public WhereBuilder where() {
    where = new WhereBuilder(table, this, true);
    return where;
  }

  public DeleteBuilder asc(String column) {
    qualifier.orderBy(table.column(column), QualifierBuilder.Order.ASC);
    return this;
  }

  public DeleteBuilder desc(String column) {
    qualifier.orderBy(table.column(column), QualifierBuilder.Order.DESC);
    return this;
  }

  public DeleteBuilder limit(int limit) {
    qualifier.limit(limit);
    return this;
  }

  public String sql() {
    return toString();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("DELETE FROM ").append(table.name);
    if (where != null) {
      builder.append(" WHERE");
      where.toString(builder);
    }
    qualifier.toString(builder);

    return builder.toString();
  }
}
