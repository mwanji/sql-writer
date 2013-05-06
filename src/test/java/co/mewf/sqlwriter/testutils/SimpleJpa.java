package co.mewf.sqlwriter.testutils;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "simple_with_jpa")
public class SimpleJpa {

  @Id
  private Long id;

  @Column(name = "name_with_jpa")
  private String name;

  @ManyToMany(mappedBy = "bidirectionalManyToManyOwner")
  private Set<ParentToMany> bidirectionalManyToMany;
}
