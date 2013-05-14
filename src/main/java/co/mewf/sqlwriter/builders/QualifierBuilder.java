package co.mewf.sqlwriter.builders;

import co.mewf.sqlwriter.mapping.ColumnInfo;



public class QualifierBuilder {

  private final Object builder;
  private final StringBuilder order = new StringBuilder();
  private int limit = -1;
  private int offset = -1;

  static enum Order {
    ASC, DESC;
  }

  QualifierBuilder(Object builder) {
    this.builder = builder;
  }

  QualifierBuilder limit(int limit) {
    this.limit = limit;
    return this;
  }

  public StringBuilder toString(StringBuilder stringBuilder) {
    stringBuilder.append(this.order);
    if (limit > -1) {
      stringBuilder.append(" LIMIT ").append(limit);
    }
    if (offset > -1) {
      stringBuilder.append(" OFFSET ").append(offset);
    }

    return stringBuilder;
  }

  void offset(int offset) {
    this.offset = offset;
  }

  void orderBy(ColumnInfo column, QualifierBuilder.Order sortOrder) {
    if (order.length() == 0) {
      order.append(" ORDER BY ");
    } else {
      order.append(", ");
    }
    order.append(column).append(" ").append(sortOrder);
  }

  @Override
  public String toString() {
    return builder.toString();
  }
}
