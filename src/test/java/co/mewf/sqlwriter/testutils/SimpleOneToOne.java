package co.mewf.sqlwriter.testutils;

import javax.persistence.Id;
import javax.persistence.OneToOne;

public class SimpleOneToOne {

  @Id
  private Long id;

  @OneToOne
  private Simple simple;
}
