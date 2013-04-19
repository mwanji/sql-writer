package co.mewf.sqlwriter.builders;

import static org.junit.Assert.assertEquals;
import co.mewf.sqlwriter.mapping.ColumnInfo;
import co.mewf.sqlwriter.mapping.TableInfo;
import co.mewf.sqlwriter.testutils.EmptyNames;

import org.junit.Test;

public class TableInfoTest {

  @Test
  public void should_ignore_empty_table_annotation() {
    TableInfo table = new TableInfo(EmptyNames.class);

    assertEquals("EmptyNames", table.name);
  }

  @Test
  public void should_ignore_empty_column_name_annotation() {
    TableInfo table = new TableInfo(EmptyNames.class);
    ColumnInfo column = table.column("emptyColumnName");

    assertEquals(table.name + ".emptyColumnName", column.toString());
  }
}
