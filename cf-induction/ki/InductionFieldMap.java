package ki;

import java.util.ArrayList;

public final class InductionFieldMap {

	/**
	 *  現在の仮説が仮説発見領域に属するかどうかを示すマップ
	 */
	
	/** コンストラクタ */
	public InductionFieldMap(){
		
	}
	
	/** メソッド*/
	
	public void set(boolean flag){
		if(flag == true){
			this.ifmap.add("t");
		}
		else{
			this.ifmap.add("f");
		}
	}
	
	public void setSize(boolean flag){
		if(flag == true){
			this.sizeflag = true;
		}
		else{
			this.sizeflag = false;
		}
	}
	
	public String toString(){
		return this.ifmap.toString();
	}
	/** インスタンス */
	public ArrayList<String> ifmap = new ArrayList<String>(); 
	private boolean sizeflag = true;
	
}
