/*
 * $Id$
 */

package ki;

/**
 * �ϐ����Ǘ�����e�[�u��
 */
public final class VariableTable implements SchemaConstants{

  /**
   * �ϐ��e�[�u���𐶐�����
   */
  private VariableTable() 
  {
  }

  /**
   * �ϐ��e�[�u�����擾����
   * @return �ϐ��e�[�u��
   */
  public static VariableTable getVariableTable()
  {
    return table;
  }

  /**
   * �ꎞ��Ɨp�ϐ��e�[�u�����擾����
   * @return �ϐ��e�[�u��
   */
  public static VariableTable getPseudoVariableTable()
  {
    return pseudoTable;
  }

  /**
   * �w��̐������ϐ���o�^����
   * @param num �o�^����ϐ��̐�
   */
  public void addVars(int num)
  {
    numVars += num;
    
    // �ő吔�X�V�H
    if (numVars > maxNumVars)
      maxNumVars = numVars;
    
    // �z��T�C�Y�̊g��
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
   * �w��̐������ϐ����폜����
   * @param num �폜����ϐ��̐�
   */
  public void removeVars(int num)
  {
    numVars -= num;
  }

  /**
   * �o�^����Ă���ϐ��̐����擾����
   * @return �ϐ��̐�
   */
  public int size()
  {
    return numVars;
  }

  /**
   * �ϐ��e�[�u���̌��݂̏�Ԃ��擾����
   * @return ���݂̏��
   */
  public int getCurState()
  {
    return state;
  }

  /**
   * �ϐ��e�[�u�����w��̏�Ԃ܂Ŗ߂�
   * @param s �w��̏��
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
   * �w��Ԗڂ̑���̗������擾����
   * @param no �w��Ԗ�
   * @return �w��Ԗڂɑ�����������ϐ��ԍ�
   */
  public int getHistory(int no)
  {
    return history[no];
  }

  /**
   * �ϐ��̒l�i�X�L�[�}�j����������
   * @param var    �l���擾�������ϐ��X�L�[�}
   * @param offset �l���擾�������ϐ��X�L�[�}�̃I�t�Z�b�g
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

      // �ϐ��̒l���ݒ肳��Ă��Ȃ��ꍇ
      if (curSchema == null) {
        foundSchema = var;
        foundOffset = offset;
        return;
      }

      // �ϐ��̒l���ݒ肳��Ă���ꍇ

      // ���ꂪ�ϐ��ȊO�Ȃ�ΕԂ�
      if (curSchema.getKind() != VARIABLE) {
        foundSchema = curSchema;
        foundOffset = curOffset;
        return;
      }

      // �ϐ��̖��O���X�V
      varName = curSchema.getName() + curOffset;

      // �ϐ��̏ꍇ�C�l�����ǂ�
      var       = curSchema;
      offset    = curOffset;
      curSchema = curSchemata[varName];
      curOffset = curOffsets[varName];
    }
  }

  /**
   * �ϐ��̒l�i�X�L�[�}�j���擾����D�K�����O�� findValue() ���Ăяo��
   * �Ă������Ɓi���ׂĂ͍������̂��߁j�D
   * @return �ϐ��̒l
   */
  public Schema getSchemaValue()
  {
    return foundSchema;
  }

  /**
   * �ϐ��̒l�i�X�L�[�}�j�̃I�t�Z�b�g���擾����D�K�����O�� 
   * findValue() ���Ăяo���Ă������Ɓi���ׂĂ͍������̂��߁j�D
   * @return �ϐ��̒l�̃I�t�Z�b�g
   */
  public int getOffsetValue()
  {
    return foundOffset;
  }

  /**
   * �w��̕ϐ��ԍ��̕ϐ��i�X�L�[�}�j���擾����
   * @param varName �w��̕ϐ��ԍ�
   * @return �w��̕ϐ��ԍ��̕ϐ��i�X�L�[�}�j
   */
  public Schema getSchema(int varName)
  {
    return curSchemata[varName];
  }

  /**
   * �w��̕ϐ��ԍ��̃I�t�Z�b�g���擾����
   * @param varName �w��̕ϐ��ԍ�
   * @return �w��̕ϐ��ԍ��̃I�t�Z�b�g
   */
  public int getOffset(int varName)
  {
    return curOffsets[varName];
  }

  /**
   * �����K�p����
   * @param varSchema   �ϐ��̃X�L�[�}
   * @param varOffset   �ϐ��̃I�t�Z�b�g
   * @param bindSchema  �ϐ��ɑ������X�L�[�}
   * @param bindOffset  �ϐ��ɑ������X�L�[�}�̃I�t�Z�b�g
   */
  public void substitute(Schema varSchema, int varOffset, Schema bindSchema, int bindOffset)
  {
    int varName = varSchema.getName() + varOffset;
    
    curSchemata[varName] = bindSchema;
    curOffsets[varName]  = bindOffset;

    // �����T�C�Y�̊g��
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
   * �����K�p����
   * @param s �K�p������
   */
  public void substitute(Substitution s)
  {
    // �����T�C�Y�̊g��
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
   * �ϐ��e�[�u���̏�Ԃ�����������
   */
  public void clear()
  {
    // �o�^����Ă���ϐ��̐��i���p�̕����m�ۂ��Ă����j
    numVars = START_VAR_NO_OF_DYN_LITERAL + MAX_NUM_VARS_PER_DYN_LITERAL;
    // �����ɑ��݂����ϐ��̐��̍ő吔
    maxNumVars = numVars;
    // �ϐ��e�[�u���̏��
    int state = 0;
  }

  /**
   * ���̃I�u�W�F�N�g�̒l��\���������Ԃ�
   * @return �I�u�W�F�N�g�̕�����\��
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
   * �œK���̂��߂̏����o�͂���
   */
  public void printOptInfo()
  {
    System.out.println("VarTable    : max num        = " + 
                       maxNumVars + " (def:" + INITIAL_CAPACITY + ")");
    System.out.println("            : hist size      = " + 
                       history.length + " (def:" + INITIAL_CAPACITY + ")");
  }

  /** �ϐ��̒l�i�X�L�[�}�j�̃��X�g */
  private Schema[] curSchemata = new Schema[INITIAL_CAPACITY];
  /** �ϐ��̒l�i�X�L�[�}�j�̃I�t�Z�b�g�̃��X�g */
  private int[] curOffsets = new int[INITIAL_CAPACITY];

  /** ����̗��� */
  private int[] history = new int[INITIAL_CAPACITY];

  /** �o�^����Ă���ϐ��̐��i���p�̕����m�ۂ��Ă����j */
  private int numVars = START_VAR_NO_OF_CLAUSES;
  /** �����ɑ��݂����ϐ��̐��̍ő吔 */
  private int maxNumVars = numVars;

  /** �ϐ��e�[�u���̏�� */
  private int state = 0;

  /** �������ʁF�X�L�[�} */
  private Schema foundSchema = null;
  /** �������ʁF�I�t�Z�b�g */
  private int foundOffset = 0;

  //
  // �e��萔��`
  //

  /** �����ϐ��z��T�C�Y */
  private final static int INITIAL_CAPACITY = 10000;                                         

  /** ���I�ɐ�������郊�e�����Ɋ܂܂��ϐ��̊J�n�ԍ� */
  public final static int START_VAR_NO_OF_DYN_LITERAL = 0;

  /** 
   * ���I�ɐ�������郊�e�����Ɋ܂܂��ϐ��̍ő吔�D���I�ɐ������ꂽ
   * ���e�����Ɋ܂܂��ϐ��̐���\�����邱�Ƃ͂ł��Ȃ��D�����ŕϐ��e�[
   * �u���̓���̗̈���C���̂��߂Ɋm�ۂ��Ă����D���I�ɐ������ꂽ���e
   * �����́C�ϐ���Ԃ����L����̂ŁC����قǑ傫�ȋ�Ԃ��m�ۂ���K�v
   * �͂Ȃ��D�����e�ʂ�����Ȃ��ꍇ�́C���كR���p�C������K�v������ */
  public final static int MAX_NUM_VARS_PER_DYN_LITERAL = 200;

  /** �߃f�[�^�x�[�X�̊J�n�ϐ��ԍ� */
  public final static int START_VAR_NO_OF_CLAUSES = 
    START_VAR_NO_OF_DYN_LITERAL + MAX_NUM_VARS_PER_DYN_LITERAL;

  /** �B��̕ϐ��e�[�u���I�u�W�F�N�g */
  private final static VariableTable table = new VariableTable();

  /** �ꎞ��Ɨp�̕ϐ��e�[�u���I�u�W�F�N�g */
  private final static VariableTable pseudoTable = new VariableTable();
}
