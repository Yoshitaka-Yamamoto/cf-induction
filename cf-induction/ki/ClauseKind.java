/*
 * $Id$
 */

package ki;

/**
 * �߂̎�ނ�\���萔
 */
public interface ClauseKind
{
  /** ������ */
  public static final int CARC = 0;
  /** ���� */
  public static final int HYPOTHESIS = 1;
  /** �ϑ����� */
  public static final int OBSERVATIONS = 2;
  /** �w�i�m�� */
  public static final int BACKGROUND = 3;
  public static final int DROP = 4;	
  public static final int POSITIVE = 5;
  public static final int NEGATIVE = 6;	
	
}
