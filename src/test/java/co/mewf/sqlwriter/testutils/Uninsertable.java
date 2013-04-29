package co.mewf.sqlwriter.testutils;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

public class Uninsertable {

  @Id @GeneratedValue
  private Long id;

  private String name;
  private int age;

  private transient String transientString;
  @Transient
  private String transientAnnotationString;
  private static String staticString;
  @Column(insertable = false)
  private String notInserted;
}
