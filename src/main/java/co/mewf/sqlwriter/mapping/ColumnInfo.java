package co.mewf.sqlwriter.mapping;

import co.mewf.sqlwriter.dialects.Dialect;

public class ColumnInfo {
  private final TableInfo table;
  public final String name;
  private final String alias;
  private final Dialect dialect;

  ColumnInfo(TableInfo table, String column, String alias, Dialect dialect) {
    this.table = table;
    this.name = column;
    this.alias = alias;
    this.dialect = dialect;
  }

  @Override
  public String toString() {
    return dialect.column(table, name, alias);
  }
}