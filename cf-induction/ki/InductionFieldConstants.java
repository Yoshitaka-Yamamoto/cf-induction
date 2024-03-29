/*
 * $Id$
 */

package ki;

/**
 * 仮説発見領域における定数
 */
public interface InductionFieldConstants {

  /** 仮説発見領域に属している */
  public static final int BELONGING = 0;
  /** 仮説長の制限のために属していない */
  public static final int NO_BELONGING_CLAUSE_LENGTH = 1;
  /** 仮説の節数制限のため属していない */
  public static final int NO_BELONGING_CLAUSE_SIZE = 2;
  /** 項の深さ制限のため属していない */
  public static final int NO_BELONGING_TERM_DEPTH = 3;
  /** そもそも属していない */
  public static final int NO_BELONGING = 4;

}
