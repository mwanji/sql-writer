package co.mewf.sqlwriter.testutils;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OneToMany;

public class PropertyAccess {

  @Id
  public Long getId() {
    return null;
  }

  @Column(name = "name_property")
  public String getName() {
    return null;
  }

  @OneToMany
  public Set<Simple> getSimples() {
    return null;
  }
}
