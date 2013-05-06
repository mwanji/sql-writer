package co.mewf.sqlwriter.mapping;

class SingleJoin implements JoinInfo {
  private final TableInfo root;
  private final ColumnInfo from;
  private final ColumnInfo to;
  private final String type;

  SingleJoin(TableInfo root, ColumnInfo from, ColumnInfo to, String type) {
    this.root = root;
    this.from = from;
    this.to = to;
    this.type = type;
  }

  public StringBuilder toString(StringBuilder builder) {
    return builder.append(' ').append(type).append(" JOIN ").append(root.name).append(" ON ").append(from).append(" = ").append(to);
  }
}