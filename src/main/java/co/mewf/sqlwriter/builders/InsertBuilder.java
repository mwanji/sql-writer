package co.mewf.sqlwriter.builders;

import co.mewf.sqlwriter.mapping.ColumnInfo;
import co.mewf.sqlwriter.mapping.TableInfo;

import java.util.ArrayList;
import java.util.List;

public class InsertBuilder {

  private final TableInfo table;
  private final List<ColumnInfo> columns = new ArrayList<ColumnInfo>();

  public InsertBuilder(Class<?> entityClass) {
    this.table = new TableInfo(entityClass);
  }

  public InsertBuilder columns(String... columns) {
    for (String column : columns) {
      this.columns .add(table.column(column));
    }
    return this;
  }

  public String sql() {
    return toString();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("INSERT INTO ").append(table.name).append('(');

    for (ColumnInfo column : columns) {
      builder.append(column.name).append(", ");
    }
    builder.delete(builder.length() - 2, builder.length());
    builder.append(") VALUES(");
    for (ColumnInfo column : columns) {
      builder.append("?, ");
    }
    builder.delete(builder.length() - 2, builder.length());
    builder.append(')');

    return builder.toString();
  }
}
