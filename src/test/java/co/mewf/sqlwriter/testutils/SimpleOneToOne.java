package co.mewf.sqlwriter.testutils;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

public class SimpleOneToOne {

  @Id
  private Long id;

  @OneToOne
  private Simple simple;

  @OneToOne
  @JoinColumn(name = "sotm_fk")
  private SimpleOneToMany simpleOneToMany;
}
