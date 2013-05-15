package co.mewf.sqlwriter.dialects;

import co.mewf.sqlwriter.builders.QualifierBuilder;
import co.mewf.sqlwriter.builders.SelectWhereBuilder;
import co.mewf.sqlwriter.builders.WhereBuilder;
import co.mewf.sqlwriter.mapping.ColumnInfo;
import co.mewf.sqlwriter.mapping.TableInfo;
import co.mewf.sqlwriter.utils.Strings;

import java.util.List;

public class StandardDialect implements Dialect {

  @Override
  public String select(TableInfo rootTable, List<TableInfo> tables, List<SelectWhereBuilder> wheres, QualifierBuilder qualifier) {
    StringBuilder builder = new StringBuilder("SELECT ");

    for (TableInfo table : tables) {
      table.toColumnsString(builder).append(", ");
    }

    Strings.chompChomp(builder).append(" FROM ").append(rootTable);
    rootTable.toJoinString(builder);

    if (!wheres.isEmpty()) {
      builder.append(" WHERE");
      for (SelectWhereBuilder where : wheres) {
        where.toString(builder);
      }
    }

    return builder.append(qualifier).toString();
  }

  @Override
  public String insert(TableInfo table, List<ColumnInfo> columns) {
    StringBuilder builder = new StringBuilder("INSERT INTO ").append(table).append('(');

    if (columns.isEmpty()) {
      columns.addAll(table.getInsertableColumns());
    }

    for (ColumnInfo column : columns) {
      builder.append(column(null, column.name, null)).append(", ");
    }
    builder.delete(builder.length() - 2, builder.length());
    builder.append(") VALUES(");
    for (ColumnInfo column : columns) {
      builder.append("?, ");
    }
    builder.delete(builder.length() - 2, builder.length());
    builder.append(')');

    return builder.toString();
  }

  @Override
  public String update(TableInfo table, List<ColumnInfo> columns, WhereBuilder where, QualifierBuilder qualifier) {
    StringBuilder builder = new StringBuilder("UPDATE ").append(table).append(" SET");

    if (columns.isEmpty()) {
      columns.addAll(table.getUpdatableColumns());
    }

    for (ColumnInfo column : columns) {
      builder.append(' ').append(column).append(" = ?,");
    }
    builder.deleteCharAt(builder.length() - 1);

    if (where != null) {
      builder.append(" WHERE");
      where.toString(builder);
    }

    builder.append(qualifier);

    return builder.toString();
  }

  @Override
  public String delete(TableInfo table, WhereBuilder where, QualifierBuilder qualifier) {
    StringBuilder builder = new StringBuilder("DELETE FROM ").append(table);
    if (where != null) {
      builder.append(" WHERE");
      where.toString(builder);
    }
    builder.append(qualifier);

    return builder.toString();
  }

  @Override
  public String table(TableInfo table) {
    return table.name;
  }

  @Override
  public String join(String type, TableInfo root, ColumnInfo from, ColumnInfo to) {
    return " " + type + " JOIN " + root + " ON " + from + " = " + to;
  }

  @Override
  public String column(TableInfo table, String name, String alias) {
    return (table != null ? table + "." : "") + name + (alias != null ? " AS \"" + alias + "\"" : "");
  }

  @Override
  public String function(String name, TableInfo table, String column, String alias) {
    return name + "(" + table + "." + column + ")" + (alias != null ? " AS \"" + alias + "\"" : "");
  }

  @Override
  public String qualify(String order, int limit, int offset) {
    StringBuilder builder = new StringBuilder(order);

    if (limit > -1) {
      builder.append(" LIMIT ").append(limit);
    }

    if (offset > -1) {
      builder.append(" OFFSET ").append(offset);
    }

    return builder.toString();
  }
}
