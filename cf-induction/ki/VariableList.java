/*
 * $Id$
 */


package ki;

import java.util.ArrayList;
import java.util.List;

/**
 * �ϐ��X�L�[�}�̏W��
 */
public final class VariableList {

  /**
   * �w��̕ϐ���������
   * @param var �ǉ�����ϐ�
   */
  public void add(Schema var)
  {
    if (!vars.contains(var)) 
      vars.add(var);
  }

  /**
   * �w��̕ϐ����X�g�𑫂����킹��
   * @param list �ǉ�����ϐ����X�g
   */
  public void addAll(VariableList list)
  {
    // �v�f�̏d�����Ȃ��悤�ɂ��Ēǉ�����
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
   * �w��̕ϐ��X�L�[�}���܂܂�Ă��邩�ǂ������ׂ�
   * @param var ���ׂ�ϐ��X�L�[�}
   * @return �܂܂�Ă���ꍇ�� true ��Ԃ�
   */
  public boolean contains(Schema var)
  {
    return vars.contains(var);
  }

  /**
   * �w��Ԗڂ̕ϐ���Ԃ�
   * @param index �w��Ԗ�
   * @return �w��Ԗڂ̕ϐ�
   */
  public Schema get(int index)
  {
    return (Schema)vars.get(index);
  }

  /**
   * �ϐ��̐���Ԃ�
   * @return �ϐ��̐�
   */
  public int size()
  {
    return vars.size();
  }

  public boolean isEmpty(){
	  return this.vars.isEmpty();
  }
  /**
   * ���̃I�u�W�F�N�g�̒l��\���������Ԃ�
   * @return �I�u�W�F�N�g�̕�����\��
   */
  public String toString() 
  {
    return vars.toString();
  }

  /** �ϐ��̃��X�g */
  private ArrayList<Schema> vars = new ArrayList<Schema>();
}
