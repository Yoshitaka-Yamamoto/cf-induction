/*
 * $Id$
 */

package ki;

import java.util.ArrayList;
import java.util.List;

/**
 * 出現する項を管理するテーブル
 */
public final class TermTable {

  /**
   * 項テーブルを生成する
   */
  private TermTable()
  {
  }

  /**
   * 項テーブルを取得する
   * @return 項テーブル
   */
  public static TermTable getTermTable()
  {
    return table;
  }

  /**
   * 項を登録する (変数を含む)
   * @param 項を登録する記号
   * @return 項の ID 
   */
  public int put(Schema name)
  {
    // 検索
    int id = 0;
    names[maxID + 1] = name;    // 番兵
    while (!names[id].equals(name))
      id++;

    // 登録済み？
    if ((maxID + 1) != id)
      return id;

    // 新規登録
    maxID++;

    // 配列のサイズが足りない？
    if (names.length == maxID + 1) {
      Schema[] dest = new Schema[ names.length * 2 ];
      System.arraycopy(names, 0, dest, 0, names.length);
      names = dest;
    }

    // 記号登録
    names[id] = name;

    return id;
  }

  /**
   * 指定の項の ID を取得する
   * @param name ID を取得したい項
   * @return 項の ID．項が登録されていない場合 -1 を返す
   */
  public int get(Schema name)
  {
    for (int i=0; i <= maxID; i++) 
      if (names[i].equals(name))
        return i;

    return -1;
  }

  /**
   * 項 ID から項を取得する
   * @param id 項 ID
   * @return 項
   */
  public Schema get(int id)
  {
    return names[id];
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

  /**
   * 検査用 main 関数
   * @param arg[] コマンド行引数の配列
   */
  public static void main(String[] args) 
  {
	  
  }  

  /** 記号名を保持する配列 */
  private Schema[] names = new Schema[INITIAL_CAPACITY];
  /** 最大の記号 ID */
  private int maxID = -1;

  /** 定義されていない名前である */
  public final static int UNDEFINED = -1;

  /** 初期記号配列サイズ */
  private final static int INITIAL_CAPACITY = 100;
  /** 唯一の記号テーブルオブジェクト */
  private final static TermTable table = new TermTable();

 
}
