package co.mewf.sqlwriter.mapping;

public class ColumnInfo {
  private final TableInfo table;
  public final String name;
  private final String alias;

  ColumnInfo(TableInfo table, String column, String alias) {
    this.table = table;
    this.name = column;
    this.alias = alias;
  }

  @Override
  public String toString() {
    return table + "." + name + (alias != null ? " AS \"" + alias + "\"" : "");
  }
}