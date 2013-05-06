package co.mewf.sqlwriter.testutils;

import java.util.List;
import java.util.Set;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

public class ParentToMany {

  @Id
  private Long id;

  @OneToMany
  private List<SimpleOneToMany> many;

  @ManyToMany
  private Set<Simple> manyToMany;

  @ManyToMany
  private Set<SimpleJpa> bidirectionalManyToManyOwner;

  @ManyToMany
  @JoinTable(name = "ptm_pk", joinColumns = @JoinColumn(name = "p_2_many_fk"), inverseJoinColumns = @JoinColumn(name = "pk_id_fk"))
  private Set<PkId> pkIds;
}
