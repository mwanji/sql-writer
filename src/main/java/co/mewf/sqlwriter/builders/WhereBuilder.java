package co.mewf.sqlwriter.builders;

import co.mewf.sqlwriter.mapping.TableInfo;
import co.mewf.sqlwriter.utils.Strings;

public class WhereBuilder {

  private final TableInfo table;
  private final Object filterable;
  private final StringBuilder clauseBuilder = new StringBuilder();
  private boolean first;

  public static enum Bool {
    AND, OR;
  }

  WhereBuilder(TableInfo table, Object filterable, boolean first) {
    this.table = table;
    this.filterable = filterable;
    this.first = first;
  }

  public WhereBuilder eq(String column) {
    eq(Bool.AND, column);
    return this;
  }

  public WhereBuilder eq(Bool bool, String column) {
    write(bool, column, "=");
    return this;
  }

  public WhereBuilder gt(String column) {
    gt(Bool.AND, column);
    return this;
  }

  public WhereBuilder gt(Bool bool, String column) {
    write(bool, column, ">");
    return this;
  }

  public WhereBuilder lt(String column) {
    lt(Bool.AND, column);
    return this;
  }

  public WhereBuilder lt(Bool bool, String column) {
    write(bool, column, "<");
    return this;
  }

  public WhereBuilder between(String column) {
    write(Bool.AND, column, "BETWEEN ? AND");
    return this;
  }

  public WhereBuilder like(String column) {
    write(Bool.AND, column, "LIKE");
    return this;
  }

  /**
   * @return the StringBuilder passed in, after having appended to it
   */
  public StringBuilder toString(StringBuilder builder) {
    return builder.append(this.clauseBuilder);
  }

  public String sql() {
    return toString();
  }

  @Override
  public String toString() {
    return filterable.toString();
  }

  private void write(Object prefix, String column, String sign) {
    clauseBuilder.append(' ');
    if (!first || clauseBuilder.length() > 1) {
      clauseBuilder.append(prefix).append(' ');
    }
    clauseBuilder.append(table.column(column)).append(' ').append(sign).append(" ?");
  }

  /**
   * @param parameterCount The number of parameters that will be passed to the IN function.
   */
  public WhereBuilder in(String column, int parameterCount) {
    clauseBuilder.append(' ');
    if (!first || clauseBuilder.length() > 1) {
      clauseBuilder.append(Bool.AND).append(' ');
    }
    clauseBuilder.append(table.column(column)).append(" IN (");
    for (int i = 0; i < parameterCount; i++) {
      clauseBuilder.append("?, ");
    }
    Strings.chompChomp(clauseBuilder);
    clauseBuilder.append(")");

    return this;
  }
}
