/*
 * $Id$
 */


package ki;

import java.util.ArrayList;
import java.util.List;

/**
 * 変数スキーマの集合
 */
public final class VariableList {

  /**
   * 指定の変数を加える
   * @param var 追加する変数
   */
  public void add(Schema var)
  {
    if (!vars.contains(var)) 
      vars.add(var);
  }

  /**
   * 指定の変数リストを足し合わせる
   * @param list 追加する変数リスト
   */
  public void addAll(VariableList list)
  {
    // 要素の重複がないようにして追加する
    for (int i=0; i < list.size(); i++) {
      Schema var = list.get(i);
      if (!vars.contains(var)) 
        vars.add(var);
    }
  }

  public boolean equal(List<Schema> list){
	  int i = 0;
	  boolean flag = true;
	  while(i < this.vars.size()){
		  Schema s_i = (Schema)this.vars.get(i);
		  if(list.contains(s_i) != true){
			  flag = false;
			  break;
		  }
		  i++;
	  }
	  return flag;
  }
  public void remove(Schema var){
	  this.vars.remove(var);
  }
  
  /**
   * 指定の変数スキーマが含まれているかどうか調べる
   * @param var 調べる変数スキーマ
   * @return 含まれている場合は true を返す
   */
  public boolean contains(Schema var)
  {
    return vars.contains(var);
  }

  /**
   * 指定番目の変数を返す
   * @param index 指定番目
   * @return 指定番目の変数
   */
  public Schema get(int index)
  {
    return (Schema)vars.get(index);
  }

  /**
   * 変数の数を返す
   * @return 変数の数
   */
  public int size()
  {
    return vars.size();
  }

  public boolean isEmpty(){
	  return this.vars.isEmpty();
  }
  /**
   * このオブジェクトの値を表す文字列を返す
   * @return オブジェクトの文字列表示
   */
  public String toString() 
  {
    return vars.toString();
  }

  /** 変数のリスト */
  private ArrayList<Schema> vars = new ArrayList<Schema>();
}
