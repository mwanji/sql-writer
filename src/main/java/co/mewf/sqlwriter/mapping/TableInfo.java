package co.mewf.sqlwriter.mapping;

import co.mewf.sqlwriter.utils.Strings;

import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

public class TableInfo {
  private static final String SEPARATOR = "_";
  public final Class<?> entityClass;
  public final String name;
  public final List<JoinInfo> joins = new ArrayList<JoinInfo>();
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

  public FunctionColumnInfo addFunction(String function, String column, String alias) {
    FunctionColumnInfo functionColumnInfo = new FunctionColumnInfo(this, function, getColumnName(column), alias);
    columns.add(functionColumnInfo);

    return functionColumnInfo;
  }

  public TableInfo join(Class<?> from, Class<?> to) {
    return join(from, to, "INNER");
  }

  public TableInfo leftJoin(Class<?> from, Class<?> to) {
    return join(from, to, "LEFT");
  }

  public TableInfo rightJoin(Class<?> from, Class<?> to) {
    return join(from, to, "RIGHT");
  }

  public Collection<ColumnInfo> getInsertableColumns() {
    PropertyDescriptorWrapper[] wrappers = PropertyDescriptorWrapper.of(entityClass);
    ArrayList<ColumnInfo> insertableColumns = new ArrayList<ColumnInfo>();

    for (PropertyDescriptorWrapper descriptor : wrappers) {
      AccessibleObject accessibleObject = descriptor.getAccessibleObject();
      if (Entities.isTransient(accessibleObject) ||
          Entities.isStatic(descriptor.getMember()) ||
          accessibleObject.isAnnotationPresent(GeneratedValue.class) ||
          !descriptor.getMember().getDeclaringClass().equals(entityClass) ||
          (accessibleObject.isAnnotationPresent(Column.class) && !accessibleObject.getAnnotation(Column.class).insertable())) {
        continue;
      }

      insertableColumns.add(column(descriptor.getName()));
    }

    return insertableColumns;
  }

  public Collection<ColumnInfo> getUpdatableColumns() {
    PropertyDescriptorWrapper[] wrappers = PropertyDescriptorWrapper.of(entityClass);
    ArrayList<ColumnInfo> updatableColumns = new ArrayList<ColumnInfo>();

    for (PropertyDescriptorWrapper descriptor : wrappers) {
      AccessibleObject accessibleObject = descriptor.getAccessibleObject();
      if (Entities.isTransient(accessibleObject) ||
          Entities.isStatic(descriptor.getMember()) ||
          accessibleObject.isAnnotationPresent(GeneratedValue.class) ||
          !descriptor.getMember().getDeclaringClass().equals(entityClass) ||
          (accessibleObject.isAnnotationPresent(Column.class) && !accessibleObject.getAnnotation(Column.class).updatable())) {
        continue;
      }

      updatableColumns.add(column(descriptor.getName()));
    }

    return updatableColumns;
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
    for (JoinInfo join : joins) {
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

  private TableInfo join(Class<?> from, Class<?> to, String type) {
    TableInfo fromTable = new TableInfo(from);
    JoinInfo join = fromTable.join(to, type);
    joins.add(join);

    return fromTable;
  }

  private JoinInfo join(Class<?> targetClass, String type) {
    ColumnInfo idColumn = getIdColumn();
    TableInfo targetTable = new TableInfo(targetClass);
    ColumnInfo targetIdColumn = targetTable.getIdColumn();

    for (PropertyDescriptorWrapper property : PropertyDescriptorWrapper.of(targetClass)) {
      AccessibleObject accessibleObject = property.getAccessibleObject();
      String columnName = Entities.getAnnotatedColumnName(accessibleObject);
      JoinInfo join = null;

      if (accessibleObject.isAnnotationPresent(OneToOne.class) && accessibleObject.getAnnotation(OneToOne.class).mappedBy().isEmpty() && entityClass.equals(property.getPropertyType())) {
        if (columnName.isEmpty()) {
          columnName = columnize(name + SEPARATOR + idColumn.name);
        }
        join = new SingleJoin(this, idColumn, targetTable.column(columnName), type);
      }

      if (accessibleObject.isAnnotationPresent(ManyToOne.class) && entityClass.equals(property.getPropertyType())) {
        if (columnName.isEmpty()) {
          columnName = columnize(name + SEPARATOR + idColumn.name);
        }
        join = new SingleJoin(this, idColumn, targetTable.column(columnName), type);
      }

      if (accessibleObject.isAnnotationPresent(OneToMany.class) && entityClass.equals(property.getGenericPropertyType().getActualTypeArguments()[0])) {
        if (columnName.isEmpty()) {
          columnName = columnize(targetTable.name + SEPARATOR + targetIdColumn.name);
        }
        join = new SingleJoin(this, targetIdColumn, column(columnName), type);
      }

      if (accessibleObject.isAnnotationPresent(ManyToMany.class) && accessibleObject.getAnnotation(ManyToMany.class).mappedBy().isEmpty() && entityClass.equals(property.getGenericPropertyType().getActualTypeArguments()[0])) {
        String joinTableName = accessibleObject.isAnnotationPresent(JoinTable.class) ? accessibleObject.getAnnotation(JoinTable.class).name() : targetTable.name + SEPARATOR + name;
        String firstJoinToColumnName = accessibleObject.isAnnotationPresent(JoinTable.class) && accessibleObject.getAnnotation(JoinTable.class).joinColumns().length > 0 ? accessibleObject.getAnnotation(JoinTable.class).joinColumns()[0].name() : columnize(targetTable.name + SEPARATOR + targetIdColumn.name);
        String secondJoinToCoumnName = accessibleObject.isAnnotationPresent(JoinTable.class) && accessibleObject.getAnnotation(JoinTable.class).inverseJoinColumns().length > 0 ? accessibleObject.getAnnotation(JoinTable.class).inverseJoinColumns()[0].name() : columnize(name + SEPARATOR + idColumn.name);

        TableInfo joinTable = new TableInfo(joinTableName);
        SingleJoin firstJoin = new SingleJoin(joinTable, targetIdColumn, joinTable.column(firstJoinToColumnName), type);
        SingleJoin secondJoin = new SingleJoin(this, idColumn, joinTable.column(secondJoinToCoumnName), type);

        join = new ManyToManyJoin(firstJoin, secondJoin);
      }

      if (join != null) {
        joins.add(join);
        return join;
      }
    }

    for (PropertyDescriptorWrapper property : PropertyDescriptorWrapper.of(entityClass)) {
      AccessibleObject field = property.getAccessibleObject();
      String columnName = Entities.getAnnotatedColumnName(field);
      JoinInfo join = null;

      if (field.isAnnotationPresent(OneToOne.class) && targetClass.equals(property.getPropertyType()) && field.getAnnotation(OneToOne.class).mappedBy().isEmpty()) {
        if (columnName.isEmpty()) {
          columnName = columnize(targetTable.name + SEPARATOR + targetIdColumn.name);
        }
        join = new SingleJoin(this, targetIdColumn, column(columnName), type);
      }

      if (field.isAnnotationPresent(ManyToOne.class) && targetClass.equals(property.getPropertyType())) {
        if (columnName.isEmpty()) {
          columnName = columnize(property.getName() + SEPARATOR + targetIdColumn.name);
        }
        join = new SingleJoin(this, targetIdColumn, column(columnName), type);
      }

      if (field.isAnnotationPresent(OneToMany.class) && targetClass.equals(property.getGenericPropertyType().getActualTypeArguments()[0])) {
        if (columnName.isEmpty()) {
          columnName = columnize(name + SEPARATOR + idColumn.name);
        }

        join = new SingleJoin(this, idColumn, targetTable.column(columnName), type);
      }

      if (field.isAnnotationPresent(ManyToMany.class) && field.getAnnotation(ManyToMany.class).mappedBy().isEmpty() && targetClass.equals(property.getGenericPropertyType().getActualTypeArguments()[0])) {
        String joinTableName = field.isAnnotationPresent(JoinTable.class) ? field.getAnnotation(JoinTable.class).name() : name + SEPARATOR + targetTable.name;
        String firstJoinToColumnName = field.isAnnotationPresent(JoinTable.class) && field.getAnnotation(JoinTable.class).inverseJoinColumns().length > 0 ? field.getAnnotation(JoinTable.class).inverseJoinColumns()[0].name() : columnize(targetTable.name + SEPARATOR + targetIdColumn.name);
        String secondJoinToCoumnName = field.isAnnotationPresent(JoinTable.class) && field.getAnnotation(JoinTable.class).joinColumns().length > 0 ? field.getAnnotation(JoinTable.class).joinColumns()[0].name() : columnize(name + SEPARATOR + idColumn.name);

        TableInfo joinTable = new TableInfo(joinTableName);
        SingleJoin firstJoin = new SingleJoin(joinTable, targetIdColumn, joinTable.column(firstJoinToColumnName), type);
        SingleJoin secondJoin = new SingleJoin(this, idColumn, joinTable.column(secondJoinToCoumnName), type);

        join = new ManyToManyJoin(firstJoin, secondJoin);
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

  private TableInfo(String name) {
    this.name = name;
    this.entityClass = null;
  }
}