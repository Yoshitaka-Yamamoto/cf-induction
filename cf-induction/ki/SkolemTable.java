/*
 * $Id$
 */

package ki;


public final class SkolemTable {
	
	private SkolemTable()
	{
	}
	
	public static SkolemTable getSkolemTable()
	{
		return table;
	}
	
	public int put(Schema name)
	{
		int id = 0;
		names[maxID + 1] = name;   
		while (!names[id].equals(name))
			id++;
		
		if ((maxID + 1) != id)
			return id;
		
		maxID++;
		
		if (names.length == maxID + 1) {
			Schema[] dest = new Schema[ names.length * 2 ];
			System.arraycopy(names, 0, dest, 0, names.length);
			names = dest;
		}
		
		names[id] = name;
		
		return id;
	}
	
	
	public int get(Schema name)
	{
		for (int i=0; i <= maxID; i++) 
			if (names[i].equals(name))
				return i;
		
		return -1;
	}
	
	
	public Schema get(int id)
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
	
	
	public int getSize(){
		return this.maxID;
	}
	
	
	private Schema[] names = new Schema[INITIAL_CAPACITY];
	private int maxID = -1;
	
	public final static int UNDEFINED = -1;
	
	private final static int INITIAL_CAPACITY = 100;
	private final static SkolemTable table = new SkolemTable();
 	
}
