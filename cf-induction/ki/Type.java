
/*
 * $Id$
 */

package ki;

import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public final class Type implements SchemaConstants{

	public Type()
	{
	}

	public void add(Pair pair)
	{
		
		//element1: term
		Schema element1 = (Schema)pair.getElement1();
		
		//element1 should be a constant or function
		
		//System.out.println("term: "+element1+". termKind: "+element1.getKind());
		
		if(this.skolemFlag == false && element1.getKind() == SKOLEM)
		{
			//System.out.println("it is skolem");
			VariableTable vTable = VariableTable.getVariableTable();
			//System.out.println("vTable: "+vTable);
			this.skolemFlag = true;
		}
		
		//element2: kind
		Integer element2 = (Integer)pair.getElement2();
		
		if( this.indexTable.containsKey(element2) ){
			Integer valueIndex = this.indexTable.get(element2);
			int index = valueIndex.intValue();
			List<Schema> termList = this.termLists.get(index);
			termList.add(element1);
		}
		
		else {
			List<Schema> newTermList = new ArrayList<Schema>();
			newTermList.add(element1);
			this.termLists.add(newTermList);
			Integer newIndex = new Integer(this.termLists.size() - 1);
			this.indexTable.put(element2, newIndex); 
		}

		int e2Int = element2.intValue();		
		SymbolTable symbolTable = SymbolTable.getSymbolTable();
		this.sTable.put(element1.toString(), symbolTable.get(e2Int));
		
	}
	
	public void setSkolem()
	{
		if(this.skolemFlag)
		{
			//There can be some Skolem constants
			SkolemTable skTable = SkolemTable.getSkolemTable();
			//System.out.println("maxID"+skTable.getSize()+"skTable:"+skTable);
			List<Schema> skolems = new ArrayList<Schema>();
			for(int i = 0; i <= skTable.getSize(); i++){
				Schema s_i = skTable.get(i);
				skolems.add(s_i);
			}
			
			List<List<Schema>> termListsNew = new ArrayList<List<Schema>>();
			
			for(int i = 0; i < termLists.size(); i++)
			{
				List<Schema> termList = (List<Schema>)this.termLists.get(i);
				termList = replace(termList, skolems);
				//System.out.println("termList2: "+termList);
				termListsNew.add(termList);
			}
			this.termLists = termListsNew;
		}
	}
	
	public List<Schema> replace(List<Schema> termList, List<Schema> skolems){
	
		//Iterator itr = termList.iterator();
		//while(itr.hasNext()){
		//System.out.println("termList before: "+termList);
		//System.out.println("skolems: "+skolems);
		//This list can contain at most one Skolem constatn.
		int j = 0;
		while(j < termList.size())
		{
			Schema s = (Schema)termList.get(j);
			//System.out.println("s: "+s);
			if(s.getKind() == SKOLEM){
				//System.out.println("hello");
				break;
			}
			j++;
		}
		if(j != termList.size() )
		{
			termList.remove(j);
			for(int i = 0; i < skolems.size(); i++){
				Schema skolem = (Schema)skolems.get(i);
				termList.add(skolem);
			}
		}
		return termList;
	}
	
	
	public boolean hasSkolem(){
		return this.skolemFlag;
	}
	
	public int getValue(String key){
		//value: the name of type (kind) that a target schema belongs to 
		String value = this.sTable.get(key);
		SymbolTable symbolTable = SymbolTable.getSymbolTable();
		return symbolTable.get(value);
	}
	
	public Hashtable getIndexTable()
	{
		return this.indexTable;
	}
	
	public List getTermLists()
	{
		return this.termLists;
	}
	
	public String toString(int version)
	{
		StringBuffer str = new StringBuffer(1024);
         
        if(version == 1){
            SymbolTable symbolTable = SymbolTable.getSymbolTable();
            Set iTableSet = (Set)this.indexTable.keySet();
            Iterator itr = iTableSet.iterator();        

            while(itr.hasNext()){
                str.append("[");
                Integer index = (Integer)itr.next();
                Integer key = (Integer)this.indexTable.get(index.intValue());
                str.append(symbolTable.get(index.intValue()));
                str.append(":");
                str.append(this.termLists.get(key.intValue()));
                str.append("]\n");
            }
        }
        
        else{
            Set sTableSet = (Set)this.sTable.keySet();
            Iterator itrs = sTableSet.iterator();
            while(itrs.hasNext()){
                str.append("type(");
                String term = (String)itrs.next();
                String type = (String)this.sTable.get(term);
                str.append(term+", "+type+").\n");
            }
            
        }
		return str.toString();
	}
    
    public String toString()
	{
        return this.toString(1);
    }
    
	public List<Schema> getTermList(Integer k_i){
	
		// k_i: the name index of kind
		Integer t_i = this.indexTable.get(k_i);
		return this.termLists.get(t_i.intValue());
		
	}

	//stable: a pair of a term and a type (for displaying)
	//Key: term, Value: MSchema (kind)
	private Hashtable<String, String> sTable = new Hashtable<String, String>();
	//indexTable: a pair of a kind and its index for the term list where 
	//the terms belonging to it will be stored
	//Key: MSchema (kind), Value termIndex
	private Hashtable<Integer, Integer> indexTable = new Hashtable<Integer, Integer>();
	//List of lists of terms
	private List<List<Schema>> termLists = new ArrayList<List<Schema>>();
	//Flag for representing the exisitence of some Skolem constants
	private boolean skolemFlag = false;
	
	
}