
/*
 * $Id$
 */

package ki;

import java.util.List;
import java.util.ArrayList;

public final class MSchema {


	public MSchema(int name, int kind)
	{
		this.name = name;
		this.kind = kind;
	}

	public static MSchema createTerm(int name, int kind)
	{
		return new MSchema(name, kind);
	}

	public int getName()
	{
		return this.name;
	}
	
	public int getKind()
	{
		return this.kind;
	}
	
	public boolean containsSchema(Schema schema, Type type)
	{
		
		int schemaKind = type.getValue( schema.toString() );
		//System.out.println("schemaKind:"+schemaKind);
		
		if(this.getName() == schemaKind){
			return true;
		}
		else {
			return false;
		}

	}
	
	
	public String toString()
	{
		StringBuffer str = new StringBuffer(512);
		if(this.kind == 0)
			str.append("+");
		else if(this.kind == 1)
			str.append("-");
		else
			str.append("#");
		SymbolTable symbolTable = SymbolTable.getSymbolTable();
		str.append(symbolTable.get(this.name));
		return str.toString();
		
	}
	
	// the name of this mode
	private int name = 0;
	// kind in {0, 1, 2} 
	// if      kind = 0 then this term is an input one
	// else if kind = 1 then this term is an output one 
	// else    kind = 2 then this term is a (ground) constant one
	private int kind = 0;
}