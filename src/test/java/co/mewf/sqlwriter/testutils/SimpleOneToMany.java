package co.mewf.sqlwriter.testutils;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

public class SimpleOneToMany {

  @Id
  private Long id;

  @OneToMany
  private List<Simple> simples;

  @ManyToOne
  private PkId pkId;
}
