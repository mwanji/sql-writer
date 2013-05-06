package co.mewf.sqlwriter.mapping;

public class ManyToManyJoin implements JoinInfo {


  private final JoinInfo firstJoin;
  private final JoinInfo secondJoin;

  public ManyToManyJoin(JoinInfo firstJoin, JoinInfo secondJoin) {
    this.firstJoin = firstJoin;
    this.secondJoin = secondJoin;

  }

  @Override
  public StringBuilder toString(StringBuilder builder) {
    return secondJoin.toString(firstJoin.toString(builder));
  }
}
