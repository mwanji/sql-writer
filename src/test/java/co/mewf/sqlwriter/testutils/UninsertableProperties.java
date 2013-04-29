package co.mewf.sqlwriter.testutils;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

public class UninsertableProperties {

  @Id @GeneratedValue
  public Long getId() {
    return null;
  }

  public String getName() {
    return null;
  }

  public int getAge() {
    return 0;
  }

  @Transient
  public String transientAnnotation() {
    return null;
  }

  public static String staticString() {
    return null;
  }

  @Column(insertable = false)
  public static String notInserted() {
    return null;
  }
}
