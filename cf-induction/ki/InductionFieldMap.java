package ki;

import java.util.ArrayList;

public final class InductionFieldMap {

	/**
	 *  ���݂̉��������������̈�ɑ����邩�ǂ����������}�b�v
	 */
	
	/** �R���X�g���N�^ */
	public InductionFieldMap(){
		
	}
	
	/** ���\�b�h*/
	
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
	/** �C���X�^���X */
	public ArrayList<String> ifmap = new ArrayList<String>(); 
	private boolean sizeflag = true;
	
}
