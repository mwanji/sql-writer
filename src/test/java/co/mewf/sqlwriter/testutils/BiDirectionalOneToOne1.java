package co.mewf.sqlwriter.testutils;

import javax.persistence.Id;
import javax.persistence.OneToOne;

public class BiDirectionalOneToOne1 {

  @Id
  private Long id;

  @OneToOne(mappedBy = "biDirectionalOneToOne1")
  private BiDirectionalOneToOne2 biDirectionalOneToOne2;
}
