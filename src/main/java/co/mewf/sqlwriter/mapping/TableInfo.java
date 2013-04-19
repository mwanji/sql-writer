package co.mewf.sqlwriter.mapping;

import co.mewf.sqlwriter.utils.Strings;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

public class TableInfo {
  private static final String SEPARATOR = "_";
  public final Class<?> entityClass;
  public final String name;
  public final List<Join> joins = new ArrayList<Join>();
  public final List<ColumnInfo> columns = new ArrayList<ColumnInfo>();

  public TableInfo(Class<?> entityClass) {
    this.entityClass = entityClass;
    this.name = getTableName(entityClass);
  }

  public ColumnInfo column(String column) {
    return column(column, null);
  }

  public ColumnInfo column(String column, String alias) {
    return new ColumnInfo(this, getColumnName(column), alias);
  }

  public TableInfo join(Class<?> from, Class<?> to) {
    TableInfo fromTable = new TableInfo(from);
    Join join = fromTable.join(to);
    joins.add(join);

    return fromTable;
  }

  public StringBuilder toColumnsString(StringBuilder builder) {
    if (columns.isEmpty()) {
      return builder.append(name).append(".*");
    }

    for (ColumnInfo column : columns) {
      builder.append(column).append(", ");
    }

    return Strings.chompChomp(builder);
  }

  public StringBuilder toJoinString(StringBuilder builder) {
    for (Join join : joins) {
      join.toString(builder);
    }

    return builder;
  }

  @Override
  public String toString() {
    return name;
  }

  private String getTableName(Class<?> entityClass) {
    String tableName = entityClass.getSimpleName();
    if (entityClass.isAnnotationPresent(Table.class)) {
      String name = entityClass.getAnnotation(Table.class).name();
      if (name != null && !name.isEmpty()) {
        tableName = name;
      }
    }
    return tableName;
  }

  private String getColumnName(String defaultName) {
    try {
      return PropertyDescriptorWrapper.of(entityClass, defaultName).getColumnName("");
    } catch (Exception e) {
      // field doesn't exist, fall back to given name
      return defaultName;
    }
  }

  private Join join(Class<?> targetClass) {
    for (Field field : targetClass.getDeclaredFields()) {
      field.setAccessible(true);
      Join join = null;

      if (field.isAnnotationPresent(OneToOne.class) && targetClass.equals(field.getType()) && field.getAnnotation(OneToOne.class).mappedBy().isEmpty()) {
        TableInfo targetTable = new TableInfo(targetClass);
        ColumnInfo idColumn = targetTable.getIdColumn();
        join = new Join(this, idColumn, targetTable.column(columnize(targetTable.name + SEPARATOR + idColumn.name)));
      }

      if (Entities.isToOneRelation(field) && entityClass.equals(field.getType())) {
        TableInfo targetTable = new TableInfo(targetClass);
        ColumnInfo joinColumn = getIdColumn();
        join = new Join(this, joinColumn, targetTable.column(columnize(name + SEPARATOR + joinColumn.name)));
      }

      if (field.isAnnotationPresent(OneToMany.class) && entityClass.equals(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0])) {
        TableInfo targetTable = new TableInfo(targetClass);
        ColumnInfo targetColumn = targetTable.getIdColumn();
        String joinColumnName = columnize(targetTable.name + SEPARATOR + targetColumn.name);
        join = new Join(this, targetColumn, column(joinColumnName));
      }

      if (join != null) {
        joins.add(join);
        return join;
      }
    }

    for (Field field : entityClass.getDeclaredFields()) {
      field.setAccessible(true);
      Join join = null;

      if (field.isAnnotationPresent(OneToOne.class) && targetClass.equals(field.getType()) && field.getAnnotation(OneToOne.class).mappedBy().isEmpty()) {
        TableInfo targetTable = new TableInfo(targetClass);
        ColumnInfo idColumn = getIdColumn();
        join = new Join(this, idColumn, targetTable.column(columnize(name + SEPARATOR + idColumn.name)));
      }

      if (field.isAnnotationPresent(ManyToOne.class) && targetClass.equals(field.getType())) {
        ColumnInfo idColumn = new TableInfo(targetClass).getIdColumn();
        join = new Join(this, idColumn, column(field.getName() + SEPARATOR + idColumn.name));
      }

      if (field.isAnnotationPresent(OneToMany.class) && targetClass.equals(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0])) {
        ColumnInfo idColumn = getIdColumn();
        join = new Join(this, idColumn, new TableInfo(targetClass).column(columnize(name + SEPARATOR + idColumn.name)));
      }

      if (join != null) {
        joins.add(join);
        return join;
      }
    }

    return null;
  }

  private ColumnInfo getIdColumn() {
    PropertyDescriptorWrapper idAccessorWrapper = PropertyDescriptorWrapper.forId(entityClass);
    return column(idAccessorWrapper.getColumnName(""));
  }

  private String columnize(String s) {
    return Character.toLowerCase(s.charAt(0)) + s.substring(1);
  }
}