/*
 * $Id$
 */

package ki;

/**
 * ����Ƃ́C�ϐ��X�L�[�}�ƃX�L�[�}�Ƃ̊Ԃ̊֌W�t���̏W���ł���
 * <ul>
 *  <li> �e�ϐ��͍��X�P�̃X�L�[�}�Ɗ֌W�t�����Ă���D
 *  <li> ����X�L�[�}�Ɋ֘A�t�����Ă���ϐ��́C���̂ǂ̃X�L�[�}�̒�
 *       �ɂ��o�����Ȃ��D
 * </ul> */
public final class Substitution {
    
  /**
   * ��̑���𐶐�����
   */
  private Substitution()
  {
    // ������
    varSchemata  = new Schema[INITIAL_CAPACITY];
    varOffsets   = new int[INITIAL_CAPACITY];
    bindSchemata = new Schema[INITIAL_CAPACITY];
    bindOffsets  = new int[INITIAL_CAPACITY];
  }

  /**
   * �w��̑���Ɠ�������𐶐�����
   * @param g  �w��̑��
   */
  private Substitution(Substitution g)
  {
    // ������
    varSchemata  = new Schema[g.varSchemata.length];
    varOffsets   = new int[g.varSchemata.length];
    bindSchemata = new Schema[g.varSchemata.length];
    bindOffsets  = new int[g.varSchemata.length];
    size         = g.size;
    // �R�s�[
    for (int i=0; i < size; i++) {
      varSchemata[i]  = g.varSchemata[i];
      varOffsets[i]   = g.varOffsets[i];
      bindSchemata[i] = g.bindSchemata[i];
      bindOffsets[i]  = g.bindOffsets[i];
    }      
  }

  /**
   * ��̑���𐶐�����
   */
  public static Substitution create()
  {
    return new Substitution();
  }

  /**
   * �w��̑���Ɠ�������𐶐�����
   * @param g  �w��̑��
   */
  public static Substitution create(Substitution g)
  {
    return new Substitution(g);
  }

  /**
   * �w��Ԗڂ̕ϐ��X�L�[�}��Ԃ�
   * @param n ���Ԗ�
   * @return �ϐ��X�L�[�}
   */
  public Schema getVarSchema(int n)
  {
    return varSchemata[n];
  }

  /**
   * �w��Ԗڂ̕ϐ��X�L�[�}�̃I�t�Z�b�g��Ԃ�
   * @param n ���Ԗ�
   * @return �ϐ��X�L�[�}�̃I�t�Z�b�g
   */
  public int getVarOffset(int n)
  {
    return varOffsets[n];
  }

  /**
   * �w��Ԗڂ̑����X�L�[�}��Ԃ�
   * @param n ���Ԗ�
   * @return �����X�L�[�}
   */
  public Schema getBindSchema(int n)
  {
    return bindSchemata[n];
  }

  /**
   * �w��Ԗڂ̑����X�L�[�}�̃I�t�Z�b�g��Ԃ�
   * @param n ���Ԗ�
   * @return �����X�L�[�}�̃I�t�Z�b�g
   */
  public int getBindOffset(int n)
  {
    return bindOffsets[n];
  }

  /**
   * ����̗v�f����Ԃ�
   * @return ����̗v�f��
   */
  public int size()
  {
    return size;
  }

  /**
   * ����ɗv�f��ǉ�����
   * @param varSchema   �ϐ��̃X�L�[�}
   * @param varOffset   �ϐ��̃I�t�Z�b�g
   * @param bindSchema  �ϐ��ɑ������X�L�[�}
   * @param bindOffset  �ϐ��ɑ������X�L�[�}�̃I�t�Z�b�g
   */
  public void add(Schema varSchema, int varOffset, Schema bindSchema, int bindOffset)
  {
    // �z��T�C�Y�̊g��
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

    // �o�^
    varSchemata[size]  = varSchema;
    varOffsets[size]   = varOffset;
    bindSchemata[size] = bindSchema;
    bindOffsets[size]  = bindOffset;

    size++;
  }

  /**
   * ����̕ϐ��I�t�Z�b�g������������
   * @param from ���������Ώۂ̕ϐ��I�t�Z�b�g�ԍ�
   * @param to   ����������̕ϐ��I�t�Z�b�g�ԍ�
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
   * ���̃I�u�W�F�N�g�̒l��\���������Ԃ�
   * @return �I�u�W�F�N�g�̕�����\��
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
   * �����p main �֐�
   * @param arg[] �R�}���h�s�����̔z��
   */
  public static void main(String[] args) 
  {
  }

  /** �ϐ��X�L�[�}�̃��X�g */
  private Schema[] varSchemata = null;
  /** �ϐ��X�L�[�}�̃I�t�Z�b�g�̃��X�g */
  private int[] varOffsets = null;

  /** �ϐ��ɑ������X�L�[�}�̃��X�g */
  private Schema[] bindSchemata = null;
  /** �ϐ��ɑ������X�L�[�}�̃I�t�Z�b�g�̃��X�g */
  private int[] bindOffsets = null;

  /** ����̃T�C�Y */
  private int size = 0;
  
  /** ����̏����T�C�Y */
  private final static int INITIAL_CAPACITY = 10;
}
