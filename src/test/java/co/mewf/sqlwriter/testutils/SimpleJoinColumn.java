package co.mewf.sqlwriter.testutils;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

public class SimpleJoinColumn {

  @Id
  private Long id;

  @OneToMany
  private List<Simple> simples;

  @ManyToOne
  @JoinColumn(name = "pk_fk")
  private PkId pkId;
}
