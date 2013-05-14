package co.mewf.sqlwriter.mapping;

import co.mewf.sqlwriter.dialects.Dialect;

class SingleJoin implements JoinInfo {
  private final TableInfo root;
  private final ColumnInfo from;
  private final ColumnInfo to;
  private final String type;
  private final Dialect dialect;

  SingleJoin(TableInfo root, ColumnInfo from, ColumnInfo to, String type, Dialect dialect) {
    this.root = root;
    this.from = from;
    this.to = to;
    this.type = type;
    this.dialect = dialect;
  }

  @Override
  public StringBuilder toString(StringBuilder builder) {
    return builder.append(dialect.join(type, root, from, to));
  }
}