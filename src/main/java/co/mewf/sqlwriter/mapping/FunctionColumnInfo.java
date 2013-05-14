package co.mewf.sqlwriter.mapping;

import co.mewf.sqlwriter.dialects.Dialect;


class FunctionColumnInfo extends ColumnInfo {

  private final String function;
  private final String alias;
  private final TableInfo table;
  private final Dialect dialect;

  FunctionColumnInfo(TableInfo table, String function, String column, String alias, Dialect dialect) {
    super(table, column, alias, dialect);
    this.table= table;
    this.function = function;
    this.alias = alias;
    this.dialect = dialect;
  }

  @Override
  public String toString() {
    return dialect.function(function, table, name, alias);
  }
}
