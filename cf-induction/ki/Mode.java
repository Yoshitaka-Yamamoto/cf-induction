
/*
 * $Id$
 */

package ki;

import java.util.List;
import java.util.ArrayList;

public final class Mode implements SchemaConstants {


	public Mode(int name, int number, int kind, List<MSchema> modeArgs)
	{
		this.name = name;
		this.number = number;
		this.kind = kind;
		this.modeArgs = modeArgs;
	}

	public static Mode createMode(int name, int number, int kind, List<MSchema> modeArgs)
	{
		
		//System.out.println("kind = " + kind);
		return new Mode(name, number, kind, modeArgs);
	}
	
	public int getKind(){
		return this.kind;
	}
	
	public int getNumber(){
		return this.number;
	}
	
	public int getName(){
		return this.name;
	}
	
	public List getModeArgs(){
		return this.modeArgs;
	}
	
	public boolean containsLit(Schema literal)
	{
		//symbol checking
		if( (literal.getKind() == POS_PREDICATE) && (this.kind == 0) ){
			return false;
		}
		else if( (literal.getKind() == NEG_PREDICATE) && (this.kind == 1) ){
			return false;
		}
		//predicate-name checking
		else if( literal.getName() != this.name ){
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean containsLit(Schema literal, Type type)
	{
	
		//symbol checking
		if( (literal.getKind() == POS_PREDICATE) && (this.kind == 1) ){
			return false;
		}
		else if( (literal.getKind() == NEG_PREDICATE) && (this.kind == 0) ){
			return false;
		}
		//predicate-name checking
		else if( literal.getName() != this.name ){
			return false;
		}
		
		//argument checking
		else {
			int i = 0;
			List args = literal.getArgsList();
			if(args.size() != this.modeArgs.size()){
				//System.out.println("containsLits error at Mode.class:::"+args+"->"+args.size()+":"+this.modeArgs+"->"+this.modeArgs.size());
				return false; // Irregular processing
			}
			else{
				while( i < this.modeArgs.size() ){
				
					MSchema ms_i = (MSchema)this.modeArgs.get(i);
					if(!ms_i.containsSchema((Schema)args.get(i), type)){
						return false;
					}
					i++;
				}
				return true;
			}
			
		}
	}
	
	public boolean hasSameMschema(Mode mode)
	{
		
		boolean ret = false;
		
		// return true if both has the same name and 
		// has the same list of MSchemas
		if( this.name == mode.getName() ){
			
			ret = true;
			
			if( this.modeArgs.size() != mode.getModeArgs().size() )
				return false;
			
			int i = 0;
			while(i < this.modeArgs.size()){
					
				MSchema m_i_this = (MSchema)this.modeArgs.get(i);
				MSchema m_i_mode = (MSchema)mode.getModeArgs().get(i);
			
				//We do not check if two Mschemas have the same kind
				//(but just checking their names)
				if( m_i_this.getName() != m_i_mode.getName() ){
					ret = false;
					break;
				}
				i++;			
			}
		}
		return ret;
			
	}
	
	public int getLabel()
	{
		return this.codeLabel;
	}

	public void setLabel()
	{
		this.codeLabel = 1;
	}
	
	public String toString()
	{
		
		StringBuffer str = new StringBuffer(1024);
		
		if(this.kind == 0)
			str.append("modeh(");
		else
			str.append("modeb(");

		SymbolTable symbolTable = SymbolTable.getSymbolTable();
		str.append( this.number + ", " +symbolTable.get(this.name) + "(" );
		for(int i = 0; i < modeArgs.size() - 1; i++){
			str.append((MSchema)modeArgs.get(i));
			str.append(", ");
		}
		str.append((MSchema)modeArgs.get(modeArgs.size() - 1));
		str.append("))");
		
		return str.toString();
					   
	}
	
	// label to be used only for coding
	private int codeLabel = 0;
	
	// the name of this mode
	private int name = 0;
	// the number of literals to be appeared in each hypothesis clause 
	private int number = 1;	
	// kind in {0, 1} 
	// if   kind = 0 then this mode is for head literals
	// else kind = 1 then this mode is for body literals
	private int kind = 0;
	// the list of schemas that this mode contains 
	private List<MSchema> modeArgs = null;
}