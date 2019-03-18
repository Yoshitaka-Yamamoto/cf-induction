/*
 * $Id$
 */

package ki;

public interface SchemaConstants {

  /*
   * スキーマの種類の定義 
   */

  /** 定数である */
  public final static int CONSTANT = 0;
  /** 変数である */
  public final static int VARIABLE = 1;
  /** 関数である */
  public final static int FUNCTION = 2;
  /** 正の述語である */
  public final static int POS_PREDICATE = 3;
  /** 負の述語である */
  public final static int NEG_PREDICATE = 4;
  /** 正負の述語である */
  public final static int POS_NEG_PREDICATE = 5;
  /** スコーレム変数である */
  public final static int SKOLEM = 6;
  /** 定義されていない */
  public final static int UNDEFINED = -1;
  
  public final static int GROUND = 7;	
  
}
