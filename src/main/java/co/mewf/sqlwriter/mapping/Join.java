package co.mewf.sqlwriter.mapping;

public class Join {
  private final TableInfo root;
  private final ColumnInfo from;
  private final ColumnInfo to;

  public Join(TableInfo root, ColumnInfo from, ColumnInfo to) {
    this.root = root;
    this.from = from;
    this.to = to;
  }

  public StringBuilder toString(StringBuilder builder) {
    return builder.append(" INNER JOIN ").append(root.name).append(" ON ").append(from).append(" = ").append(to);
  }
}