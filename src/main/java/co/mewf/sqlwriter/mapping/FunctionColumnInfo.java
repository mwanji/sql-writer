package co.mewf.sqlwriter.mapping;


class FunctionColumnInfo extends ColumnInfo {

  private final String function;
  private final String alias;
  private final TableInfo table;

  FunctionColumnInfo(TableInfo table, String function, String column, String alias) {
    super(table, column, alias);
    this.table= table;
    this.function = function;
    this.alias = alias;
  }

  @Override
  public String toString() {
    return function + "(" + table + "." + name + ")" + (alias != null ? " AS \"" + alias + "\"" : "");
  }
}
