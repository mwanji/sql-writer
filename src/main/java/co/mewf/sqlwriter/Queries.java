package co.mewf.sqlwriter;

import co.mewf.sqlwriter.builders.DeleteBuilder;
import co.mewf.sqlwriter.builders.InsertBuilder;
import co.mewf.sqlwriter.builders.SelectBuilder;
import co.mewf.sqlwriter.builders.UpdateBuilder;
import co.mewf.sqlwriter.dialects.Dialect;
import co.mewf.sqlwriter.dialects.StandardDialect;

public class Queries {

  private static final Query STANDARD_QUERY = new Query(new StandardDialect());

  public static SelectBuilder select() {
    return STANDARD_QUERY.select();
  }

  public static InsertBuilder insert(Class<?> entityClass) {
    return STANDARD_QUERY.insert(entityClass);
  }

  public static UpdateBuilder update(Class<?> entityClass) {
    return STANDARD_QUERY.update(entityClass);
  }

  public static DeleteBuilder delete(Class<?> entityClass) {
    return STANDARD_QUERY.delete(entityClass);
  }

  public static Query query(Dialect dialect) {
    return new Query(dialect);
  }

  public static class Query {

    private final Dialect dialect;

    public SelectBuilder select() {
      return new SelectBuilder(dialect);
    }

    public InsertBuilder insert(Class<?> entityClass) {
      return new InsertBuilder(entityClass, dialect);
    }

    public UpdateBuilder update(Class<?> entityClass) {
      return new UpdateBuilder(entityClass, dialect);
    }

    public DeleteBuilder delete(Class<?> entityClass) {
      return new DeleteBuilder(entityClass, dialect);
    }

    private Query(Dialect dialect) {
      this.dialect = dialect;
    }
  }

  private Queries() {}
}
