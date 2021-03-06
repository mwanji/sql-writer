package co.mewf.sqlwriter.builders;

import co.mewf.sqlwriter.builders.QualifierBuilder.Order;
import co.mewf.sqlwriter.dialects.Dialect;
import co.mewf.sqlwriter.mapping.ColumnInfo;
import co.mewf.sqlwriter.mapping.TableInfo;

import java.util.ArrayList;
import java.util.List;

public class UpdateBuilder {

  private List<ColumnInfo> columns = new ArrayList<ColumnInfo>();
  private TableInfo table;
  private WhereBuilder where;
  private QualifierBuilder qualifiers;
  private final Dialect dialect;

  public UpdateBuilder(Class<?> entityClass, Dialect dialect) {
    this.table = new TableInfo(entityClass, dialect);
    this.dialect = dialect;
    this.qualifiers = new QualifierBuilder(this, dialect);
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
    return dialect.update(table, columns, where, qualifiers);
  }
}
