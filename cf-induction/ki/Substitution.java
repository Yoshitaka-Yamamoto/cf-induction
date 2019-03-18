/*
 * $Id$
 */

package ki;

/**
 * 代入とは，変数スキーマとスキーマとの間の関係付けの集合である
 * <ul>
 *  <li> 各変数は高々１つのスキーマと関係付けられている．
 *  <li> あるスキーマに関連付けられている変数は，他のどのスキーマの中
 *       にも出現しない．
 * </ul> */
public final class Substitution {
    
  /**
   * 空の代入を生成する
   */
  private Substitution()
  {
    // 初期化
    varSchemata  = new Schema[INITIAL_CAPACITY];
    varOffsets   = new int[INITIAL_CAPACITY];
    bindSchemata = new Schema[INITIAL_CAPACITY];
    bindOffsets  = new int[INITIAL_CAPACITY];
  }

  /**
   * 指定の代入と同じ代入を生成する
   * @param g  指定の代入
   */
  private Substitution(Substitution g)
  {
    // 初期化
    varSchemata  = new Schema[g.varSchemata.length];
    varOffsets   = new int[g.varSchemata.length];
    bindSchemata = new Schema[g.varSchemata.length];
    bindOffsets  = new int[g.varSchemata.length];
    size         = g.size;
    // コピー
    for (int i=0; i < size; i++) {
      varSchemata[i]  = g.varSchemata[i];
      varOffsets[i]   = g.varOffsets[i];
      bindSchemata[i] = g.bindSchemata[i];
      bindOffsets[i]  = g.bindOffsets[i];
    }      
  }

  /**
   * 空の代入を生成する
   */
  public static Substitution create()
  {
    return new Substitution();
  }

  /**
   * 指定の代入と同じ代入を生成する
   * @param g  指定の代入
   */
  public static Substitution create(Substitution g)
  {
    return new Substitution(g);
  }

  /**
   * 指定番目の変数スキーマを返す
   * @param n 何番目
   * @return 変数スキーマ
   */
  public Schema getVarSchema(int n)
  {
    return varSchemata[n];
  }

  /**
   * 指定番目の変数スキーマのオフセットを返す
   * @param n 何番目
   * @return 変数スキーマのオフセット
   */
  public int getVarOffset(int n)
  {
    return varOffsets[n];
  }

  /**
   * 指定番目の束縛スキーマを返す
   * @param n 何番目
   * @return 束縛スキーマ
   */
  public Schema getBindSchema(int n)
  {
    return bindSchemata[n];
  }

  /**
   * 指定番目の束縛スキーマのオフセットを返す
   * @param n 何番目
   * @return 束縛スキーマのオフセット
   */
  public int getBindOffset(int n)
  {
    return bindOffsets[n];
  }

  /**
   * 代入の要素数を返す
   * @return 代入の要素数
   */
  public int size()
  {
    return size;
  }

  /**
   * 代入に要素を追加する
   * @param varSchema   変数のスキーマ
   * @param varOffset   変数のオフセット
   * @param bindSchema  変数に代入するスキーマ
   * @param bindOffset  変数に代入するスキーマのオフセット
   */
  public void add(Schema varSchema, int varOffset, Schema bindSchema, int bindOffset)
  {
    // 配列サイズの拡張
    if (varSchemata.length == size) {
      Schema[] newVarSchemata  = new Schema[size * 2];
      int[]    newVarOffsets   = new int[size * 2];
      Schema[] newBindSchemata = new Schema[size * 2];
      int[]    newBindOffsets  = new int[size * 2];
      System.arraycopy(varSchemata,  0, newVarSchemata,  0, size);
      System.arraycopy(varOffsets,   0, newVarOffsets,   0, size);
      System.arraycopy(bindSchemata, 0, newBindSchemata, 0, size);
      System.arraycopy(bindOffsets,  0, newBindOffsets,  0, size);
      varSchemata  = newVarSchemata;
      varOffsets   = newVarOffsets;
      bindSchemata = newBindSchemata;
      bindOffsets  = newBindOffsets;
    }

    // 登録
    varSchemata[size]  = varSchema;
    varOffsets[size]   = varOffset;
    bindSchemata[size] = bindSchema;
    bindOffsets[size]  = bindOffset;

    size++;
  }

  /**
   * 代入の変数オフセットを書き換える
   * @param from 書き換え対象の変数オフセット番号
   * @param to   書き換え後の変数オフセット番号
   */
  public void shift(int from, int to)
  {
    for (int i=0; i < size; i++) {
      if (varOffsets[i] == from) 
        varOffsets[i] = to;
      if (bindOffsets[i] == from)
        bindOffsets[i] = to;
    }
  }

  /**
   * このオブジェクトの値を表す文字列を返す
   * @return オブジェクトの文字列表示
   */
  public String toString() 
  {
    StringBuffer str = new StringBuffer("{");
    for (int i=0; i < size; i++) {
      int varName = varSchemata[i].getName() + varOffsets[i];
      str.append("_" + varName + "/" + Schema.toSimpleString(bindSchemata[i], bindOffsets[i]));
      if ( i + 1 < size )
        str.append(", ");
    }
    str.append("}");

    return str.toString();
  }

  /**
   * 検査用 main 関数
   * @param arg[] コマンド行引数の配列
   */
  public static void main(String[] args) 
  {
  }

  /** 変数スキーマのリスト */
  private Schema[] varSchemata = null;
  /** 変数スキーマのオフセットのリスト */
  private int[] varOffsets = null;

  /** 変数に代入するスキーマのリスト */
  private Schema[] bindSchemata = null;
  /** 変数に代入するスキーマのオフセットのリスト */
  private int[] bindOffsets = null;

  /** 代入のサイズ */
  private int size = 0;
  
  /** 代入の初期サイズ */
  private final static int INITIAL_CAPACITY = 10;
}
