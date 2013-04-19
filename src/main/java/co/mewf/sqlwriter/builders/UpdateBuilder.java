package co.mewf.sqlwriter.builders;

import co.mewf.sqlwriter.builders.QualifierBuilder.Order;
import co.mewf.sqlwriter.mapping.TableInfo;

public class UpdateBuilder {

  private String[] columns;
  private TableInfo table;
  private WhereBuilder where;
  private QualifierBuilder qualifiers = new QualifierBuilder(this);

  public UpdateBuilder(Class<?> entityClass) {
    this.table = new TableInfo(entityClass);
  }

  public UpdateBuilder set(String... columns) {
    this.columns = columns;
    return this;
  }

  public WhereBuilder where() {
    where = new WhereBuilder(this.table, this, true);
    return where;
  }

  public UpdateBuilder limit(int limit) {
    qualifiers.limit(limit);
    return this;
  }

  public UpdateBuilder desc(String column) {
    qualifiers.orderBy(table.column(column), Order.DESC);
    return this;
  }

  public UpdateBuilder asc(String column) {
    qualifiers.orderBy(table.column(column), Order.ASC);
    return this;
  }

  public String sql() {
    return toString();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("UPDATE ").append(table).append(" SET");

    for (String column : columns) {
      builder.append(' ').append(table.column(column)).append(" = ?,");
    }
    builder.deleteCharAt(builder.length() - 1);

    if (where != null) {
      builder.append(" WHERE");
      where.toString(builder);
    }

    qualifiers.toString(builder);

    return builder.toString();
  }
}
