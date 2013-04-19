package co.mewf.sqlwriter.testutils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table
public class EmptyNames {

  @Id
  private Long id;

  @Column
  private String emptyColumnName;
}
