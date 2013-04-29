package co.mewf.sqlwriter.builders;

import co.mewf.sqlwriter.builders.QualifierBuilder.Order;
import co.mewf.sqlwriter.mapping.ColumnInfo;
import co.mewf.sqlwriter.mapping.TableInfo;

import java.util.ArrayList;
import java.util.List;

public class UpdateBuilder {

  private List<ColumnInfo> columns = new ArrayList<ColumnInfo>();
  private TableInfo table;
  private WhereBuilder where;
  private QualifierBuilder qualifiers = new QualifierBuilder(this);

  public UpdateBuilder(Class<?> entityClass) {
    this.table = new TableInfo(entityClass);
  }

  public UpdateBuilder set(String... columns) {
    for (String column : columns) {
      this.columns.add(table.column(column));
    }
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

    if (columns.isEmpty()) {
      columns.addAll(table.getUpdatableColumns());
    }

    for (ColumnInfo column : columns) {
      builder.append(' ').append(column).append(" = ?,");
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
