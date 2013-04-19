package co.mewf.sqlwriter.testutils;

import java.util.List;

import javax.persistence.Id;
import javax.persistence.OneToMany;

public class ParentToMany {

  @Id
  private Long id;

  @OneToMany
  private List<SimpleOneToMany> many;
}
