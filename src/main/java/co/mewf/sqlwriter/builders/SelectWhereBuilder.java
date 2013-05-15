package co.mewf.sqlwriter.builders;

import co.mewf.sqlwriter.builders.WhereBuilder.Bool;
import co.mewf.sqlwriter.mapping.TableInfo;

public class SelectWhereBuilder {

  private final SelectBuilder select;
  private final WhereBuilder where;

  public SelectWhereBuilder(TableInfo table, SelectBuilder filterable, boolean first) {
    select = filterable;
    this.where = new WhereBuilder(table, filterable, first);
  }

  public SelectBuilder from(Class<?> entityClass) {
    return select.from(entityClass);
  }

  public SelectBuilder join(Class<?> entityClass) {
    return select.join(entityClass);
  }

  public SelectWhereBuilder eq(String column) {
    return eq(Bool.AND, column);
  }

  public SelectWhereBuilder eq(Bool bool, String column) {
    where.eq(bool, column);
    return this;
  }

  public SelectWhereBuilder gt(String column) {
    return gt(Bool.AND, column);
  }

  public SelectWhereBuilder gt(Bool bool, String column) {
    where.gt(bool, column);
    return this;
  }

  public SelectWhereBuilder lt(String column) {
    return lt(Bool.AND, column);
  }

  public SelectWhereBuilder lt(Bool bool, String column) {
    where.lt(bool, column);
    return this;
  }

  public SelectWhereBuilder between(String column) {
    where.between(column);
    return this;
  }

  public SelectWhereBuilder like(String column) {
    where.like(column);
    return this;
  }

  /**
   * @param parameterCount The number of parameters that will be passed to the IN function.
   */
  public SelectWhereBuilder in(String column, int parameterCount) {
    where.in(column, parameterCount);
    return this;
  }

  /**
   * @return the StringBuilder passed in, after having appended to it
   */
  public StringBuilder toString(StringBuilder builder) {
    return where.toString(builder);
  }

  public String sql() {
    return toString();
  }

  @Override
  public String toString() {
    return select.toString();
  }
}
