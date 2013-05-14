package co.mewf.sqlwriter.builders;

import co.mewf.sqlwriter.dialects.Dialect;
import co.mewf.sqlwriter.mapping.TableInfo;


public class DeleteBuilder {

  private final Class<?> entityClass;
  private final TableInfo table;
  private final QualifierBuilder qualifier = new QualifierBuilder(this);
  private WhereBuilder where;
  private final Dialect dialect;

  public DeleteBuilder(Class<?> entityClass, Dialect dialect) {
    this.entityClass = entityClass;
    this.dialect = dialect;
    this.table = new TableInfo(entityClass, dialect);
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
    return dialect.delete(table, where, qualifier);
  }
}
