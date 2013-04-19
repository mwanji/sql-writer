package co.mewf.sqlwriter;

import co.mewf.sqlwriter.builders.DeleteBuilder;
import co.mewf.sqlwriter.builders.InsertBuilder;
import co.mewf.sqlwriter.builders.SelectBuilder;
import co.mewf.sqlwriter.builders.UpdateBuilder;

public class Queries {

  public static SelectBuilder select() {
    return new SelectBuilder();
  }

  public static InsertBuilder insert(Class<?> entityClass) {
    return new InsertBuilder(entityClass);
  }

  public static UpdateBuilder update(Class<?> entityClass) {
    return new UpdateBuilder(entityClass);
  }

  public static DeleteBuilder delete(Class<?> entityClass) {
    return new DeleteBuilder(entityClass);
  }

  private Queries() {}
}
