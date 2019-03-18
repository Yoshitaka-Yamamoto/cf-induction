/*
 * $Id$
 */

package ki;

import java.io.PrintStream;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;


public final class Schema implements SchemaConstants,Cloneable {

  private Schema(int kind, int name, List args)
  {
    assert (CONSTANT <= kind && kind <= NEG_PREDICATE);

    this.kind = kind;    
    this.name = name;    
	  
    if (args != null) {
      this.args    = new Schema[ args.size() + 1 ];         
      this.numArgs = args.size();
      for (int i=args.size() - 1; i >= 0; i--) {
        this.args[i] = (Schema)args.get(i);                 
        this.args[i].parent = this;                         
        this.args[i].right  = this.args[i+1];               
        if (this.args[i].kind == VARIABLE)                  
          this.varList.add(this.args[i]);
        else if (this.args[i].numArgs > 0)
          this.varList.addAll(this.args[i].varList);
        if (maxTermDepth < this.args[i].maxTermDepth + 1)   
          maxTermDepth = this.args[i].maxTermDepth + 1;
      }
    }
    else {
      this.args = new Schema[1];                          
    }
    this.id = numCreatedObjects++;
  }
  
	
  private Schema(int kind, int name, Schema[] args)
  {
    assert ( (CONSTANT <= kind && kind <= POS_NEG_PREDICATE) || (kind == SKOLEM));

    this.kind = kind;    
    this.name = name;    
    this.args = args;    

	  //if(this.kind == SKOLEM){
		//  System.out.println("skolem");
	  //}
	   
    this.numArgs = args.length - 1;
    for (int i=numArgs - 1; i >= 0; i--) {
      this.args[i].parent = this;                        
      this.args[i].right  = this.args[i+1];              
      if (this.args[i].kind == VARIABLE)                 
        this.varList.add(this.args[i]);
      else if (this.args[i].numArgs > 0)
        this.varList.addAll(this.args[i].varList);
      if (maxTermDepth < this.args[i].maxTermDepth + 1)  
        maxTermDepth = this.args[i].maxTermDepth + 1;
    }

    this.id = numCreatedObjects++;
  }
  
  public static Schema createConstant(int name)
  {
    return new Schema(CONSTANT, name, new Schema[1]);
  }

  public static Schema createVariable(int name)
  {
    return new Schema(VARIABLE, name, new Schema[1]);
  }

  public static Schema createSkolem(int name)
  {
	  return new Schema(SKOLEM, name, new Schema[1]);
  }
	
  public static Schema createSkolem(int name, int i)
  {
	  SkolemTable skTable = SkolemTable.getSkolemTable();
	  Schema skolem = new Schema(SKOLEM, name, new Schema[1]);
	  skTable.put(skolem);
	  return skolem;
  }
  
  public static Schema createFunction(int name, List args)
  {
    return new Schema(FUNCTION, name, args);
  }

  public static Schema createPosPredicate(int name, List args)
  {
    return new Schema(POS_PREDICATE, name, args);
  }

  public static Schema createNegPredicate(int name, List args)
  {
    return new Schema(NEG_PREDICATE, name, args);
  }

  
  
  public static Schema createPosNegPredicate(int name, List args)
 {
	  return new Schema(POS_NEG_PREDICATE, name, args);
 }
	
  public static Schema create(int kind, int name, Schema[] args)
  {
    return new Schema(kind, name, args);
  }
	
  public int getKind()
  {
    return kind;
  }

  public void setKind(int i){
	  this.kind = i;
  }
	
	
  public int getName()
  {
    return name;
  }
	
  public Schema[] getArgs()
  {
    return args;
  }

  public List<Schema> getArgsList()
  {
	 ArrayList<Schema> argList = new ArrayList<Schema>();
	 for(int i = 0; i < this.args.length-1; i++){
		 argList.add(this.args[i]);
	 }
	 //System.out.println("argList:"+argList);
	 return argList;
  }
  
 
  public int getArity()
  {
    return numArgs;
  }


  public int getNumVars()
  {
    return varList.size();
  }


  public VariableList getVars()
  {
    return varList;
  }
    


  public Schema getMostLeftChild()
  {
    return args[0];
  }


  public Schema getParentSchema()
    
  {
    return parent;
  }


  public Schema getRightSchema()
  {
    return right;
  }

 
  public Schema getNextSchema()
  {
    if (numArgs != 0)
      return args[0];

    if (right != null)
      return right;

    if (parent == null)
      return null;

    Schema p = parent;
    while (p.right == null) 
      if ((p = p.parent) == null)
        return null;

    return p.right;
  }

  public Schema getNextSchemaWithoutChild()
  {
    if (right != null)
      return right;

    Schema p = parent;
    while (p.right == null) 
      if ((p = p.parent) == null)
        return null;

    return p.right;
  }

  public int getMaxTermDepth()
  {
    return maxTermDepth;
  }

  public void negate()
  {
	  Schema s = this;
	  while(s != null){
		  if(s.kind == POS_PREDICATE){
			  //System.out.println("1");
		  	  s.kind = NEG_PREDICATE;
		  }
		  else if(s.kind == NEG_PREDICATE){
			  //System.out.println("2");
			  s.kind = POS_PREDICATE;
		  }
		  else if(s.kind == VARIABLE){
			  //System.out.println("3");
			  s.kind = SKOLEM;
		  }
	  	  s = s.getNextSchema();
		  //System.out.println(s);
	  }
  }

  public Schema negatedSchema(){
	//System.out.println("this schema:"+this);
  	int kind = this.getKind();
  	int name = this.getName();
  	
  	if(kind == VARIABLE){
  		return Schema.createSkolem(name, 1);
  	}
  	else if(kind == CONSTANT){
  		return Schema.createConstant(name);
  	}	
  	
  	else{
  		if(kind == POS_PREDICATE){
  			//System.out.println("1");
  			kind = NEG_PREDICATE;
  		}
  		else if(kind == NEG_PREDICATE){
  			//System.out.println("2");
  			kind = POS_PREDICATE;
  		}
  		
  		Schema[] args = this.getArgs();
		ArrayList<Schema> argList = new ArrayList<Schema>();
  		if(args != null){
  			for(int i = 0; i < args.length-1; i++){
  				Schema s_i_copy = args[i].negatedSchema();
  				argList.add(s_i_copy);
  			}
  		}
  		//System.out.println("kind:"+kind+"name"+name+"arg:"+argList);
  		return new Schema(kind, name, argList);
  	}
  	
  }

	public void negate_pred()
	{	
		if(this.kind == POS_PREDICATE){
			this.kind = NEG_PREDICATE;
		}
		
		else if(this.kind == NEG_PREDICATE){
			this.kind = POS_PREDICATE;
		}
			
}	
	
 
  public boolean isListFunc()
  {
    return kind == FUNCTION && name == SymbolTable.LIST_FUNC_ID;
  }

 
  public boolean isEmptyList()
  {
    return kind == CONSTANT && name == SymbolTable.EMPTY_LIST_ID;
  }


  public int getID()
  {
    return id;
  }

  public boolean equals(Schema s)
  {
	  
	  if(kind == POS_NEG_PREDICATE || s.kind == POS_NEG_PREDICATE){
		  
		  if ( name != s.name || numArgs != s.numArgs )
			  return false;
		  for (int i=0; i < numArgs; i++)
			  if ( ! args[i].equals( s.args[i] ) )
				  return false;

		  return true;
	  }
	  else{
		  if ( kind != s.kind || name != s.name || numArgs != s.numArgs )
			  return false;

		  for (int i=0; i < numArgs; i++)
			  if ( ! args[i].equals( s.args[i] ) )
				  return false;
		  
		  return true;  
	  
	  }	  
	  
  }

	
	public boolean isTautology(Schema schema){
		Schema s = (Schema)schema.clone();
		return this.equals(s);
	}

  public boolean complementaryEquals(Schema s)
  {
	assert ( kind == POS_PREDICATE || kind == NEG_PREDICATE );  

	if ( kind != ( (POS_PREDICATE + NEG_PREDICATE) - s.kind) )
      return false;
    if ( name != s.name || numArgs != s.numArgs )            
        return false;
    for (int i=0; i < numArgs; i++)
      if ( ! args[i].equals( s.args[i] ) )
        return false;
    
    return true;
  }

  

  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    else if (obj == null || getClass() != obj.getClass())
      return false;

    return this.equals((Schema)obj);
  }


  public int hashCode()
  {
    int code;
    if ( args == null )
      code = (kind + 1) * name;
    else {
      code = (kind + 1) * name;
      for (int i=0; i < numArgs; i++)
        code *= args[i].hashCode();
    }

    return code;
  }


  public String toString() 
  {
    if (isListFunc()) 
      return "[" + args[0] + toStringAsListArgs(args[1]) + "]";

    StringBuffer s = new StringBuffer(6000);
    
    switch (kind) {
    case VARIABLE:
    	VariableTable varTable = VariableTable.getVariableTable();
    	Schema current = varTable.getSchema(name);
    	if(current != null){
    		//System.out.println("current_name:"+current.getName());
    		s.append("X_" + current.getName());
    	}
		//name = current.getName();
    	else{
    		s.append("X_" + name);
    	}
		break;
	case SKOLEM:
		s.append("sk" + name);
		break;
		
    case NEG_PREDICATE:
		s.append("-");
		s.append(symbolTable.get(name));
		break;
    case CONSTANT:
    case FUNCTION:
	case GROUND:		
    case POS_PREDICATE:
      s.append(symbolTable.get(name));
      break;
	case POS_NEG_PREDICATE:
		s.append("+-");
		s.append(symbolTable.get(name));
		break;
    }

    if ( numArgs != 0 ) {
      int i;
      s.append("(");
      for (i=0; i < numArgs - 1; i++)
        s.append(args[i] + ", ");
      s.append(args[i] + ")");
    }        

    return s.toString();
  }
  
  public Clause createTaut(){
	  Clause ret = new Clause();
	  List<Schema> list = this.getArgsList();
	  int name = this.getName();
	  Schema posTaut = Schema.createPosPredicate(name, list);
	  Schema negTaut = Schema.createNegPredicate(name, list);
	  ret.addTaut(posTaut);
	  ret.addTaut(negTaut);
	  //System.out.println("pos_taut_var"+posTaut.getVars()+"neg_taut_var"+negTaut.getVars()+"taut_var:"+ret.getNumVars());
	  return ret;
  }

	

	public static String toSimpleString(Schema schema, int offset) 
{
		StringBuffer sb = new StringBuffer(20);
		
		switch (schema.getKind()) {
			case VARIABLE:
				sb.append("_" + (schema.getName() + offset));
				break;
			case SKOLEM:
				sb.append("sk" + (schema.getName() + offset));
				break;
			case NEG_PREDICATE:
				sb.append("-");
			case CONSTANT:
			case FUNCTION:
			case POS_PREDICATE:
				sb.append(symbolTable.get(schema.getName()));
				break;
		}
		
		int arity = schema.getArity();
		if ( arity != 0 ) {
			sb.append("(");
			Schema[] args = schema.getArgs();
			int         i = 0;
			for (; i < arity - 1; i++)
				sb.append(Schema.toSimpleString(args[i], offset) + ",");
			sb.append(Schema.toSimpleString(args[i], offset) + ")");
		}        
		
		return sb.toString();
}	

 
  private static String toStringAsListArgs(Schema schema) 
  {
    if (schema.isListFunc())
      return ", " + schema.args[0] + toStringAsListArgs(schema.args[1]);
    if (schema.isEmptyList())
      return "";
    
    return " | " + schema;
  }

 
  public void list(PrintStream out) 
  {
    out.println(this);

    if (args == null)
      return;

    Schema s = args[0];
    while (s != null) {
      
      StringBuffer str   = new StringBuffer();
      Schema       ss    = s;
      while (ss.parent != null) {
        ss = ss.parent;
        if (ss.right != null)
          str.insert(0, "|  ");
        else
          str.insert(0, "   ");
      }

      if (s.right != null)
        str.append("+- " + s);
      else
        str.append("+- " + s);

      out.println(str.toString());

      s = s.getNextSchema();
    }
  }


    public Object clone()
    {
            return new Schema(kind,name,args);
    }

    public Schema copy(int varNum, int num){
    	int kind = this.getKind();
    	int name = this.getName();
    	
    	if(kind == VARIABLE){
    		name = name + varNum*num;
    		return Schema.createVariable(name);
    	}
    	else if(kind == CONSTANT){
    		return Schema.createConstant(name);
    	}
    	
    	else{
    		Schema[] args = this.getArgs();
			ArrayList<Schema> argList = new ArrayList<Schema>();
    		if(args != null){
    			for(int i = 0; i < args.length-1; i++){
    				Schema s_i_copy = args[i].copy(varNum, num);
    				argList.add(s_i_copy);
    			}
    		}
    		return new Schema(kind, name, argList);
    	}
    	
    }
    
    public Substitution getUnitSub(Schema sIdObj) {
    	//VariableTable varTable = VariableTable.getVariableTable();
    	// TODO Auto-generated method stub
    	if( (this.getName() == sIdObj.getName()) && (this.getKind() == sIdObj.getKind()) ){
    		Substitution ret = Substitution.create();
    		List thisChild = this.getArgsList();
    		List sIdObjChild = sIdObj.getArgsList();
    		for(int i = 0; i < thisChild.size(); i++){
    			Schema thisChildI = (Schema)thisChild.get(i);
    			Schema sIdObjChildI = (Schema)sIdObjChild.get(i);
    			if(thisChildI.getKind() == VARIABLE){
    				//int thisoffset = varTable.getOffset(thisChildI.getID());
    				//int sIdoffset = varTable.getOffset(sIdObjChildI.getID());
    				ret.add(thisChildI,0,sIdObjChildI, 0);
    			}
    			
    		}
    		
    		return ret;
    	}
    	else{
    		return null;
    	}
    }
    public boolean containsVar(Schema x){
    	//VariableTable vt = VariableTable.getVariableTable();
    	//Schema thisCurrent = vt.getSchema
    	if(this.getVars().contains(x))
    		return true;
    	else
    		return false;
    }
     

  public static int getNumCreatedObjects()
  {
    return numCreatedObjects;
  }

	public static void main(String[] args) 
   {
	  
	    symbolTable.put("pred");
	    symbolTable.put("foo");
	    symbolTable.put("bar");
	    symbolTable.put("a");
	    symbolTable.put("b");
	    symbolTable.put("c");
	    symbolTable.put("d");
	  
	Schema x = Schema.createVariable(0);
	Schema y = Schema.createVariable(1);
	List<Schema> schemas = new ArrayList<Schema>();
	schemas.add(y);
	int foo_id = symbolTable.get("foo");
	Schema s_y = Schema.createFunction(foo_id, schemas);
	//System.out.println("s_y"+s_y);
	
	VariableTable varTable = VariableTable.getVariableTable();
	int x_offset = varTable.getOffset(0);
	int s_y_offset = varTable.getOffset(1);
	
	System.out.println("x_offset:"+x_offset);
	System.out.println("s_y_offset:"+s_y_offset);
	
	
	Substitution sub = Substitution.create();
	//sub.add(x, x_offset, s_y, s_y_offset);
	sub.add(y, 0, x, 0);
	
	System.out.println("Before Substitution");
	//System.out.print("varTable:"+varTable);
	System.out.println("x:"+x+"***** s_y:"+s_y);
	varTable.substitute(sub);
	varTable.findValue(y,0);
	y = varTable.getSchemaValue();
	System.out.println("After Substitution");
	//System.out.print("varTable:"+varTable);
	System.out.println(":"+x+"**** s_y:"+s_y);
	
	

    
    Schema c1 = Schema.createConstant(3);
    Schema c2 = Schema.createConstant(4);
    List<Schema>   as = new ArrayList<Schema>();
    as.add(c1);
    as.add(c2);
    Schema c3 = Schema.createConstant(5);
    Schema c4 = Schema.createConstant(6);
    List<Schema>   as2 = new ArrayList<Schema>();
    as2.add(c3);
    as2.add(c4);
    Schema foo  = Schema.createFunction(1, as);
    Schema bar  = Schema.createFunction(2, as2);
    List<Schema>   as3 = new ArrayList<Schema>();
    as3.add(foo);
    as3.add(bar);
    Schema pred = Schema.createNegPredicate(3, as3);
    System.out.println("foo="+foo);
        System.out.println("list -----------------------------");
    pred.list(System.out);
    System.out.println("toString -------------------------");
    System.out.println(pred);

    System.out.println("Schema.numCreatedObjects = " + Schema.getNumCreatedObjects());
    
  }

  private int kind = CONSTANT;

private int name = 0;
  private Schema args[] = null;
  private int numArgs = 0;
  private VariableList varList = new VariableList();
  private Schema parent = null;
  private Schema right = null;
  private int maxTermDepth = 0;
  private int id = 0;
 

  private static int numCreatedObjects = 0;
  
  public final static SymbolTable symbolTable = SymbolTable.getSymbolTable();
  
  
public boolean hasInDepVal(ArrayList<Schema> dVarList) {
	int i = 0;
	while(i< this.varList.size()){
		if(dVarList.contains(this.varList.get(i)) != true){
			return false;
		}
		i++;
	}
	
	// TODO Auto-generated method stub
	return true;
}


}

