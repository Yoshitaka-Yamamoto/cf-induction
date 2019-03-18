/*
 * $Id$
 */

package ki;

import java.util.ArrayList;
import java.util.List;

/**
 * �o�����鍀���Ǘ�����e�[�u��
 */
public final class TermTable {

  /**
   * ���e�[�u���𐶐�����
   */
  private TermTable()
  {
  }

  /**
   * ���e�[�u�����擾����
   * @return ���e�[�u��
   */
  public static TermTable getTermTable()
  {
    return table;
  }

  /**
   * ����o�^���� (�ϐ����܂�)
   * @param ����o�^����L��
   * @return ���� ID 
   */
  public int put(Schema name)
  {
    // ����
    int id = 0;
    names[maxID + 1] = name;    // �ԕ�
    while (!names[id].equals(name))
      id++;

    // �o�^�ς݁H
    if ((maxID + 1) != id)
      return id;

    // �V�K�o�^
    maxID++;

    // �z��̃T�C�Y������Ȃ��H
    if (names.length == maxID + 1) {
      Schema[] dest = new Schema[ names.length * 2 ];
      System.arraycopy(names, 0, dest, 0, names.length);
      names = dest;
    }

    // �L���o�^
    names[id] = name;

    return id;
  }

  /**
   * �w��̍��� ID ���擾����
   * @param name ID ���擾��������
   * @return ���� ID�D�����o�^����Ă��Ȃ��ꍇ -1 ��Ԃ�
   */
  public int get(Schema name)
  {
    for (int i=0; i <= maxID; i++) 
      if (names[i].equals(name))
        return i;

    return -1;
  }

  /**
   * �� ID ���獀���擾����
   * @param id �� ID
   * @return ��
   */
  public Schema get(int id)
  {
    return names[id];
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

  /**
   * �����p main �֐�
   * @param arg[] �R�}���h�s�����̔z��
   */
  public static void main(String[] args) 
  {
	  
  }  

  /** �L������ێ�����z�� */
  private Schema[] names = new Schema[INITIAL_CAPACITY];
  /** �ő�̋L�� ID */
  private int maxID = -1;

  /** ��`����Ă��Ȃ����O�ł��� */
  public final static int UNDEFINED = -1;

  /** �����L���z��T�C�Y */
  private final static int INITIAL_CAPACITY = 100;
  /** �B��̋L���e�[�u���I�u�W�F�N�g */
  private final static TermTable table = new TermTable();

 
}
