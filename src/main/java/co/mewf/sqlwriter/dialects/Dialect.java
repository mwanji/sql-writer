package co.mewf.sqlwriter.dialects;

import co.mewf.sqlwriter.builders.QualifierBuilder;
import co.mewf.sqlwriter.builders.SelectWhereBuilder;
import co.mewf.sqlwriter.builders.WhereBuilder;
import co.mewf.sqlwriter.mapping.ColumnInfo;
import co.mewf.sqlwriter.mapping.TableInfo;

import java.util.List;

public interface Dialect {
  String select(TableInfo rootTable, List<TableInfo> tables, List<SelectWhereBuilder> wheres, QualifierBuilder qualifier);
  String join(String type, TableInfo root, ColumnInfo from, ColumnInfo to);
  String column(TableInfo table, String name, String alias);
  String function(String name, TableInfo table, String column, String alias);
  String table(TableInfo table);
  String insert(TableInfo table, List<ColumnInfo> columns);
  String update(TableInfo table, List<ColumnInfo> columns, WhereBuilder where, QualifierBuilder qualifiers);
  String delete(TableInfo table, WhereBuilder where, QualifierBuilder qualifier);
  String qualify(String order, int limit, int offset);
}
