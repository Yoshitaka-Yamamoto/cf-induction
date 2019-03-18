/*
 * $Id$
 */

package ki;

/**
 * 変数名を正規化するための変数テーブル
 */
public final class MetaVarTable {
  
  /**
   * 変数名を正規化するための変数テーブルを生成する
   * @param size テーブルの初期サイズ
   */
  public MetaVarTable(int size)
  {
    // 変数名を格納する配列を確保
    names = new int[size];
  }
  
  /**
   * 変数を登録する
   * @param name 登録する変数番号
   * @return 正規化された変数番号
   */
  public int put(int name)
  {
    // 検索
    int id = 0;
    names[maxID + 1] = name;    // 番兵
    while (names[id] != name)
      id++;
    
    // 登録済み？
    if ((maxID + 1) != id)
      return id;
    
    // 新規登録
    maxID++;
    
    // 配列のサイズが足りない？
    if (names.length == maxID + 1) {
      int[] dest = new int[ names.length * 2 ];
      System.arraycopy(names, 0, dest, 0, names.length);
      names = dest;
    }
    
    // 変数登録
    names[maxID] = name;
    
    return maxID;
  }
  
  /**
   * 登録されている変数の数を返す
   * @return 登録されている変数の数
   */
  public int size()
  {
    return maxID + 1;
  }
  
  /**
   * 変数テーブルをクリアする
   */
  public void clear()
  {
    maxID = -1;
  }
  
  /**
   * このオブジェクトを表す文字列を取得する
   * @return このオブジェクトを表す文字列
   */
  public String toString()
  {
    StringBuffer str = new StringBuffer(512);
    
    for (int i=0; i <= maxID; i++) 
      str.append( i + " : " + names[i] + "\n" );
    
    return str.toString();
  }
  
  /** 変数名を保持する配列 */
  private int[] names = null;
  /** 最大の変数 ID */
  private int maxID = -1;
}

