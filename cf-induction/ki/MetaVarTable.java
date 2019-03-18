/*
 * $Id$
 */

package ki;

/**
 * �ϐ����𐳋K�����邽�߂̕ϐ��e�[�u��
 */
public final class MetaVarTable {
  
  /**
   * �ϐ����𐳋K�����邽�߂̕ϐ��e�[�u���𐶐�����
   * @param size �e�[�u���̏����T�C�Y
   */
  public MetaVarTable(int size)
  {
    // �ϐ������i�[����z����m��
    names = new int[size];
  }
  
  /**
   * �ϐ���o�^����
   * @param name �o�^����ϐ��ԍ�
   * @return ���K�����ꂽ�ϐ��ԍ�
   */
  public int put(int name)
  {
    // ����
    int id = 0;
    names[maxID + 1] = name;    // �ԕ�
    while (names[id] != name)
      id++;
    
    // �o�^�ς݁H
    if ((maxID + 1) != id)
      return id;
    
    // �V�K�o�^
    maxID++;
    
    // �z��̃T�C�Y������Ȃ��H
    if (names.length == maxID + 1) {
      int[] dest = new int[ names.length * 2 ];
      System.arraycopy(names, 0, dest, 0, names.length);
      names = dest;
    }
    
    // �ϐ��o�^
    names[maxID] = name;
    
    return maxID;
  }
  
  /**
   * �o�^����Ă���ϐ��̐���Ԃ�
   * @return �o�^����Ă���ϐ��̐�
   */
  public int size()
  {
    return maxID + 1;
  }
  
  /**
   * �ϐ��e�[�u�����N���A����
   */
  public void clear()
  {
    maxID = -1;
  }
  
  /**
   * ���̃I�u�W�F�N�g��\����������擾����
   * @return ���̃I�u�W�F�N�g��\��������
   */
  public String toString()
  {
    StringBuffer str = new StringBuffer(512);
    
    for (int i=0; i <= maxID; i++) 
      str.append( i + " : " + names[i] + "\n" );
    
    return str.toString();
  }
  
  /** �ϐ�����ێ�����z�� */
  private int[] names = null;
  /** �ő�̕ϐ� ID */
  private int maxID = -1;
}

