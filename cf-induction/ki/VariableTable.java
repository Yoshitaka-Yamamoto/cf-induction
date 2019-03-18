/*
 * $Id$
 */

package ki;

/**
 * 変数を管理するテーブル
 */
public final class VariableTable implements SchemaConstants{

  /**
   * 変数テーブルを生成する
   */
  private VariableTable() 
  {
  }

  /**
   * 変数テーブルを取得する
   * @return 変数テーブル
   */
  public static VariableTable getVariableTable()
  {
    return table;
  }

  /**
   * 一時作業用変数テーブルを取得する
   * @return 変数テーブル
   */
  public static VariableTable getPseudoVariableTable()
  {
    return pseudoTable;
  }

  /**
   * 指定の数だけ変数を登録する
   * @param num 登録する変数の数
   */
  public void addVars(int num)
  {
    numVars += num;
    
    // 最大数更新？
    if (numVars > maxNumVars)
      maxNumVars = numVars;
    
    // 配列サイズの拡張
    if (curSchemata.length < numVars) {
      Schema[] newCurSchemata = new Schema[numVars * 2];
      int[]    newCurOffsets  = new int[numVars * 2];
      System.arraycopy(curSchemata, 0, newCurSchemata, 0, curSchemata.length);
      System.arraycopy(curOffsets,  0, newCurOffsets,  0, curSchemata.length);
      curSchemata = newCurSchemata;
      curOffsets  = newCurOffsets;
    }
  }

  /**
   * 指定の数だけ変数を削除する
   * @param num 削除する変数の数
   */
  public void removeVars(int num)
  {
    numVars -= num;
  }

  /**
   * 登録されている変数の数を取得する
   * @return 変数の数
   */
  public int size()
  {
    return numVars;
  }

  /**
   * 変数テーブルの現在の状態を取得する
   * @return 現在の状態
   */
  public int getCurState()
  {
    return state;
  }

  /**
   * 変数テーブルを指定の状態まで戻す
   * @param s 指定の状態
   */
  public void backtrackTo(int s)
  {
    for (int i=state-1; i >= s; i--)
      curSchemata[history[i]] = null;
    state = s;

//      for (int i=0; i < state; i++)
//        if (curSchemata[history[i]] == null)
//          System.out.println("@ null [" + i + "] time = " + TimeStep.get());
  }

  /**
   * 指定番目の代入の履歴を取得する
   * @param no 指定番目
   * @return 指定番目に代入があった変数番号
   */
  public int getHistory(int no)
  {
    return history[no];
  }

  /**
   * 変数の値（スキーマ）を検索する
   * @param var    値を取得したい変数スキーマ
   * @param offset 値を取得したい変数スキーマのオフセット
   */
  public void findValue(Schema var, int offset)
  {
    int    varName   = var.getName() + offset;
    Schema curSchema = curSchemata[varName];
    int    curOffset = curOffsets[varName];

//      if (Debugger.isOnNow(DBG_VAR_TBL_DETAIL))
//        System.out.println(, "findValue(" + varName + ")");

    while (true) {

//        if (Debugger.isOnNow(DBG_VAR_TBL_DETAIL)) {
//          System.out.print(" var  = _" + varName);
//          if (curSchema != null)
//            System.out.println(" : curVal = " + BSchema.toSimpleString(curSchema, curOffset));
//          else
//            System.out.println(" : curVal = null");
//        }

      // 変数の値が設定されていない場合
      if (curSchema == null) {
        foundSchema = var;
        foundOffset = offset;
        return;
      }

      // 変数の値が設定されている場合

      // それが変数以外ならば返す
      if (curSchema.getKind() != VARIABLE) {
        foundSchema = curSchema;
        foundOffset = curOffset;
        return;
      }

      // 変数の名前を更新
      varName = curSchema.getName() + curOffset;

      // 変数の場合，値をたどる
      var       = curSchema;
      offset    = curOffset;
      curSchema = curSchemata[varName];
      curOffset = curOffsets[varName];
    }
  }

  /**
   * 変数の値（スキーマ）を取得する．必ず直前に findValue() を呼び出し
   * ておくこと（すべては高速化のため）．
   * @return 変数の値
   */
  public Schema getSchemaValue()
  {
    return foundSchema;
  }

  /**
   * 変数の値（スキーマ）のオフセットを取得する．必ず直前に 
   * findValue() を呼び出しておくこと（すべては高速化のため）．
   * @return 変数の値のオフセット
   */
  public int getOffsetValue()
  {
    return foundOffset;
  }

  /**
   * 指定の変数番号の変数（スキーマ）を取得する
   * @param varName 指定の変数番号
   * @return 指定の変数番号の変数（スキーマ）
   */
  public Schema getSchema(int varName)
  {
    return curSchemata[varName];
  }

  /**
   * 指定の変数番号のオフセットを取得する
   * @param varName 指定の変数番号
   * @return 指定の変数番号のオフセット
   */
  public int getOffset(int varName)
  {
    return curOffsets[varName];
  }

  /**
   * 代入を適用する
   * @param varSchema   変数のスキーマ
   * @param varOffset   変数のオフセット
   * @param bindSchema  変数に代入するスキーマ
   * @param bindOffset  変数に代入するスキーマのオフセット
   */
  public void substitute(Schema varSchema, int varOffset, Schema bindSchema, int bindOffset)
  {
    int varName = varSchema.getName() + varOffset;
    
    curSchemata[varName] = bindSchema;
    curOffsets[varName]  = bindOffset;

    // 履歴サイズの拡張
    if (history.length == state) {
      int[] newHistory = new int[history.length * 2];
      System.arraycopy(history, 0, newHistory, 0, history.length);
      history = newHistory;
    }

    history[state++] = varName;

//      for (int i=0; i < state; i++)
//        for (int j=i+1; j < state; j++)
//          if (history[i] == history[j]) {
//            System.out.println("@ dup time = " + TimeStep.get());
//            for (int k=0; k < state; k++)
//              System.out.print("[" + history[k] + "]");
//            System.out.println();
//            break;
//          }
  }

  /**
   * 代入を適用する
   * @param s 適用する代入
   */
  public void substitute(Substitution s)
  {
    // 履歴サイズの拡張
    if (history.length < state + s.size()) {
      int[] newHistory = new int[(state + s.size()) * 2];
      System.arraycopy(history, 0, newHistory, 0, history.length);
      history = newHistory;
    }

    for (int i=0; i < s.size(); i++) {
      Schema varSchema  = s.getVarSchema(i);
      int    varOffset  = s.getVarOffset(i);
      Schema bindSchema = s.getBindSchema(i);
      int    bindOffset = s.getBindOffset(i);
      int    varName    = varSchema.getName() + varOffset;
      curSchemata[varName] = bindSchema;
      curOffsets[varName]  = bindOffset;
      history[state++]     = varName;
    }

//      for (int i=0; i < state; i++)
//        for (int j=i+1; j < state; j++)
//          if (history[i] == history[j]) {
//            System.out.println("@ dup time = " + TimeStep.get());
//            for (int k=0; k < state; k++)
//              System.out.print("[" + history[k] + "]");
//            System.out.println();
//            break;
//          }
  }

  /**
   * 変数テーブルの状態を初期化する
   */
  public void clear()
  {
    // 登録されている変数の数（補題用の分を確保しておく）
    numVars = START_VAR_NO_OF_DYN_LITERAL + MAX_NUM_VARS_PER_DYN_LITERAL;
    // 同時に存在した変数の数の最大数
    maxNumVars = numVars;
    // 変数テーブルの状態
    int state = 0;
  }

  /**
   * このオブジェクトの値を表す文字列を返す
   * @return オブジェクトの文字列表示
   */
  public String toString() 
  {
    StringBuffer str = new StringBuffer(4048);

    for (int i=0; i < numVars; i++) {
      str.append("_" + i + " : ");
      if (curSchemata[i] != null) 
        str.append(Schema.toSimpleString(curSchemata[i], curOffsets[i]));
      else
        str.append("-");
      str.append("\n");
    }

    return str.toString();
  }

  /**
   * 最適化のための情報を出力する
   */
  public void printOptInfo()
  {
    System.out.println("VarTable    : max num        = " + 
                       maxNumVars + " (def:" + INITIAL_CAPACITY + ")");
    System.out.println("            : hist size      = " + 
                       history.length + " (def:" + INITIAL_CAPACITY + ")");
  }

  /** 変数の値（スキーマ）のリスト */
  private Schema[] curSchemata = new Schema[INITIAL_CAPACITY];
  /** 変数の値（スキーマ）のオフセットのリスト */
  private int[] curOffsets = new int[INITIAL_CAPACITY];

  /** 代入の履歴 */
  private int[] history = new int[INITIAL_CAPACITY];

  /** 登録されている変数の数（補題用の分を確保しておく） */
  private int numVars = START_VAR_NO_OF_CLAUSES;
  /** 同時に存在した変数の数の最大数 */
  private int maxNumVars = numVars;

  /** 変数テーブルの状態 */
  private int state = 0;

  /** 検索結果：スキーマ */
  private Schema foundSchema = null;
  /** 検索結果：オフセット */
  private int foundOffset = 0;

  //
  // 各種定数定義
  //

  /** 初期変数配列サイズ */
  private final static int INITIAL_CAPACITY = 10000;                                         

  /** 動的に生成されるリテラルに含まれる変数の開始番号 */
  public final static int START_VAR_NO_OF_DYN_LITERAL = 0;

  /** 
   * 動的に生成されるリテラルに含まれる変数の最大数．動的に生成された
   * リテラルに含まれる変数の数を予測することはできない．そこで変数テー
   * ブルの特定の領域を，そのために確保しておく．動的に生成されたリテ
   * ラルは，変数空間を共有するので，それほど大きな空間を確保する必要
   * はない．もし容量が足りない場合は，差異コンパイルする必要がある */
  public final static int MAX_NUM_VARS_PER_DYN_LITERAL = 200;

  /** 節データベースの開始変数番号 */
  public final static int START_VAR_NO_OF_CLAUSES = 
    START_VAR_NO_OF_DYN_LITERAL + MAX_NUM_VARS_PER_DYN_LITERAL;

  /** 唯一の変数テーブルオブジェクト */
  private final static VariableTable table = new VariableTable();

  /** 一時作業用の変数テーブルオブジェクト */
  private final static VariableTable pseudoTable = new VariableTable();
}
