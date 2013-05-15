package co.mewf.sqlwriter.builders;

import co.mewf.sqlwriter.dialects.Dialect;
import co.mewf.sqlwriter.mapping.TableInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Methods such as from and join set the context for methods such as columns, where, asc, etc.
 *
 * E.g. columns("name") will add the name column from the entity last passed to a from or join method.
 */
public class SelectBuilder {

  private final List<TableInfo> tables = new ArrayList<TableInfo>();
  private final List<SelectWhereBuilder> wheres = new ArrayList<SelectWhereBuilder>();
  private final QualifierBuilder qualifier;
  private TableInfo currentTable;
  private TableInfo rootTable;
  private final Dialect dialect;

  public SelectBuilder(Dialect dialect) {
    this.dialect = dialect;
    this.qualifier = new QualifierBuilder(this, dialect);
  }

  /**
   * Retrieves the columns from the given entity and inner joins it to the previous entity given to the builder.
   *
   * entityClass becomes the current entity.
   *
   */
  public SelectBuilder from(Class<?> entityClass) {
    if (rootTable == null) {
      rootTable = new TableInfo(entityClass, dialect);
      currentTable = rootTable;
    } else {
      join(entityClass);
    }
    tables.add(currentTable);

    return this;
  }

  /**
   * Inner joins this entity to the previous entity given to the builder.
   *
   * entityClass becomes the current entity.
   */
  public SelectBuilder join(Class<?> entityClass) {
    return join(entityClass, currentTable.entityClass);
  }

  /**
   * Joins from and to. from becomes the current entity.
   */
  public SelectBuilder join(Class<?> from, Class<?> to) {
    currentTable = rootTable.join(from, to);

    return this;
  }


  /**
   * Left joins this entity to the previous entity given to the builder.
   *
   * entityClass becomes the current entity.
   */
  public SelectBuilder leftJoin(Class<?> entityClass) {
    currentTable = rootTable.leftJoin(entityClass, currentTable.entityClass);
    return this;
  }


  /**
   * Right joins this entity to the previous entity given to the builder.
   *
   * entityClass becomes the current entity.
   */
  public SelectBuilder rightJoin(Class<?> entityClass) {
    currentTable = rootTable.rightJoin(entityClass, currentTable.entityClass);
    return this;
  }

  /**
   * Adds the columns of the current entity to the select clause
   */
  public SelectBuilder columns(String... columns) {
    for (String column : columns) {
      column(column, null);
    }

    return this;
  }

  public SelectBuilder column(String column, String alias) {
    currentTable.columns.add(this.currentTable.column(column, alias));
    return this;
  }

  public SelectBuilder function(String function, String column) {
    return function(function, column, null);
  }

  public SelectBuilder function(String function, String column, String alias) {
    currentTable.addFunction(function, column, alias);
    return this;
  }

  public SelectWhereBuilder where() {
    SelectWhereBuilder where = new SelectWhereBuilder(currentTable, this, wheres.isEmpty());
    wheres.add(where);

    return where;
  }

  public SelectBuilder asc(String column) {
    qualifier.orderBy(currentTable.column(column), QualifierBuilder.Order.ASC);
    return this;
  }

  public SelectBuilder desc(String column) {
    qualifier.orderBy(currentTable.column(column), QualifierBuilder.Order.DESC);
    return this;
  }

  public SelectBuilder limit(int limit) {
    qualifier.limit(limit);
    return this;
  }

  public SelectBuilder offset(int offset) {
    qualifier.offset(offset);
    return this;
  }

  public String sql() {
    return toString();
  }

  @Override
  public String toString() {
    return dialect.select(rootTable, tables, wheres, qualifier);
  }
}
