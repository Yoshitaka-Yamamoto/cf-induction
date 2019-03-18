/*
* $Id$
 */

package ki;


public final class SymbolTable {

  private SymbolTable()
  {
  }

  public static SymbolTable getSymbolTable()
  {
    return table;
  }

  public int put(String name)
  {
    int id = 0;
    names[maxID + 1] = name;   
    while (!names[id].equals(name))
      id++;

    if ((maxID + 1) != id)
      return id;

    maxID++;

    if (names.length == maxID + 1) {
      String[] dest = new String[ names.length * 2 ];
      System.arraycopy(names, 0, dest, 0, names.length);
      names = dest;
    }

    names[id] = name;

    return id;
  }

 
  public int get(String name)
  {
    for (int i=0; i <= maxID; i++) 
      if (names[i].equals(name))
        return i;

    return -1;
  }


  public String get(int id)
  {
    return names[id];
  }

  public String toString()
  {
    StringBuffer str = new StringBuffer(512);

    for (int i=0; i <= maxID; i++) 
      str.append( i + " : " + names[i] + "\n" );

    return str.toString();
  }

  public static void main(String[] args) 
  {
    SymbolTable table = SymbolTable.getSymbolTable();

    table.put("func");
    table.put("a");
    table.put("b");
    table.put("c");
    table.put("a");

    System.out.println(table);

    System.out.println(table.get(0));
    System.out.println(table.get(1));
    System.out.println(table.get(2));
    System.out.println(table.get(3));
  }  

  private String[] names = new String[INITIAL_CAPACITY];
  private int maxID = -1;

  public final static int UNDEFINED = -1;

  private final static int INITIAL_CAPACITY = 100;
  private final static SymbolTable table = new SymbolTable();

  public final static int LIST_FUNC_ID = table.put(".");
  public final static int EMPTY_LIST_ID = table.put("[]");
}
