package co.mewf.sqlwriter.utils;

public class Strings {

  /**
   * Deletes the last two characters.
   */
  public static StringBuilder chompChomp(StringBuilder builder) {
    return builder.delete(builder.length() - 2, builder.length());
  }

  private Strings() {}
}
