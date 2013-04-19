package co.mewf.sqlwriter.testutils;

import javax.persistence.Column;
import javax.persistence.Id;

public class PropertyAccess {

  @Id
  public Long getId() {
    return null;
  }

  @Column(name = "name_property")
  public String getName() {
    return null;
  }
}
