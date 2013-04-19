package co.mewf.sqlwriter.testutils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "simple_with_jpa")
public class SimpleJpa {

  @Id
  private Long id;

  @Column(name = "name_with_jpa")
  private String name;
}
