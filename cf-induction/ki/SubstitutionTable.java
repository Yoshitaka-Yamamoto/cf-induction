
package ki;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;

public final class SubstitutionTable implements SchemaConstants{

	public SubstitutionTable(){
	
		this.c_indexes = new ArrayList<Integer>();
		this.substitutions = new ArrayList<Hashtable<Schema, List<Schema>>>();
		
	}
		
	
	//This function is used at the first stage
	public void subsumptionCheck(Clause cls, Schema lit, int k)
	{
		
		//The possible substitutions that enable lit to subsume cls are 
		//to be registered to an element in substitutions 
		boolean first = true; //flag representing that lit first subsumes cls 

		for(int i = 0; i < cls.size(); i++)
		{
			Schema s_i = (Schema)cls.get(i);
			if( (s_i.getKind() != lit.getKind()) || (s_i.getName() != lit.getName()) ){
				continue; // s_i is not unifiable with lit
			}
			else{
				//s_i is unifiable with lit
				List s_i_args = s_i.getArgsList();
				List lit_args = lit.getArgsList();
				if(lit_args.size() == s_i_args.size()) // irregular processing
				{
					//vtList: list of pairs each of which is of form <Variable, Term>
					LinkedList<Pair> vtList = new LinkedList<Pair>();
					int j = 0;
					while(j < lit_args.size())
					{
						Schema lit_j = (Schema)lit_args.get(j);
						Schema s_i_j = (Schema)s_i_args.get(j);
						if( lit_j.getKind() == VARIABLE ){
							
							//If this variable has been already instantiated before, 
							//we cannot unify it unless it is the same as s_i_j
							
							Schema unifiedTerm = this.getUnifiedTerm(vtList, lit_j);
							
							if( unifiedTerm == null){ 
								// substitution should be of form: lit_j / s_i_j
								Pair pair = new Pair(lit_j, s_i_j);
								vtList.add(pair); 
							}
							else if(unifiedTerm.getName() != s_i_j.getName()){
								break;
							}
						}
						//lit_j and s_i_j are supposed to be constants 
						//(we do not care about functions in this moment!!// )
						else if(lit_j.getName() != s_i_j.getName()){
							break;
						}
						j++;
					}
					if( j == lit_args.size() ){
						// lit subsumes s_i_j
						if(first){
							/* Initialization */

							//Set k to c_indexes, that is, k-th clause in positive bottom theopy is subsumed
							Integer k_integer = new Integer(k);
							this.c_indexes.add(k_integer);
							Hashtable<Schema, List<Schema>> hashTable = new Hashtable<Schema, List<Schema>>();
							this.substitutions.add(hashTable);
							
							first = false;
						}
						//Set this vtList to the last (altenatively ``current'') element in substitutions
						Hashtable<Schema, List<Schema>> hLastTable = (Hashtable<Schema, List<Schema>>)this.substitutions.get(this.substitutions.size() - 1);
						for(int r = 0; r < vtList.size(); r++){
							Pair p_r = (Pair)vtList.get(r);
							//p_r = <Variable, Term>
							Schema p_r_var = (Schema)p_r.getElement1(); 
							Schema p_r_ter = (Schema)p_r.getElement2();
							if(!hLastTable.containsKey(p_r_var))
							{
	
								LinkedList<Schema> newTermList = new LinkedList<Schema>();
								newTermList.add(p_r_ter);
								hLastTable.put(p_r_var, newTermList);
									
							}
							
							else {
								List<Schema> termList = (List<Schema>)hLastTable.get(p_r_var);
								termList.add(p_r_ter);
							}
						}
					}
				}
			}			
		}
	}
	
	
	
	//This function is used at some further stage
	public void subsumptionCheck(Clause cls, Schema lit, SubstitutionTable preSTable, int k)
	{
		
		//The possible substitutions that enable lit to subsume cls are 
		//to be registered to an element in substitutions 
		boolean first = true; //flag representing that lit first subsumes cls 

		for(int i = 0; i < cls.size(); i++)
		{
			Schema s_i = (Schema)cls.get(i);
			if( (s_i.getKind() != lit.getKind()) || (s_i.getName() != lit.getName()) ){
				continue; // s_i is not unifiable with lit
			}
			else{
				//s_i is unifiable with lit
				List s_i_args = s_i.getArgsList();
				List lit_args = lit.getArgsList();
				if(lit_args.size() == s_i_args.size()) // irregular processing
				{
					//vtList: list of pairs each of which is of form <Variable, Term>
					LinkedList<Pair> vtList = new LinkedList<Pair>();
					int j = 0;
					while(j < lit_args.size())
					{
						//lit_j: variable
						Schema lit_j = (Schema)lit_args.get(j);
						//s_i_j: term
						Schema s_i_j = (Schema)s_i_args.get(j);
						
						if( lit_j.getKind() == VARIABLE ){
							
							//If this variable has been already instantiated before, 
							//we cannot unify it unless it is the same as s_i_j
							Schema term = this.getUnifiedTerm(vtList, lit_j);
							if( term == null){
								//This var is not bonded by any term in vtList 	
								List unifiedVars = preSTable.get(lit_j, s_i_j, vtList, k);
								//This var is newly appeared 
								if(unifiedVars == null){
									//substitution should be of form: lit_j / s_i_j
									Pair pair = new Pair(lit_j, s_i_j);
									vtList.add(pair); 
								}
							}
							else if(term.getName() != s_i_j.getName()){
								break;
							}												
						}
						//lit_j and s_i_j are supposed to be constants 
						//(we do not care about functions in this moment!!// )
						else if(lit_j.getName() != s_i_j.getName()){
							break;
						}
						j++;
					}
					if( j == lit_args.size() ){
						// lit subsumes s_i_j
						if(first){
							/* Initialization */
							
							//Set k to c_indexes, that is, k-th clause in positive bottom theopy is subsumed
							Integer k_integer = new Integer(k);
							this.c_indexes.add(k_integer);
							Hashtable<Schema, List<Schema>> hashTable = new Hashtable<Schema, List<Schema>>();
							this.substitutions.add(hashTable);
							
							first = false;
						}
						//Set this vtList to the last (altenatively ``current'') element in substitutions
						Hashtable<Schema, List<Schema>> hLastTable = (Hashtable<Schema, List<Schema>>)this.substitutions.get(this.substitutions.size() - 1);
						
						for(int r = 0; r < vtList.size(); r++){
							Pair p_r = (Pair)vtList.get(r);
							//p_r = <Variable, Term>
							Schema p_r_var = (Schema)p_r.getElement1(); 
							Schema p_r_ter = (Schema)p_r.getElement2();
							if(!hLastTable.containsKey(p_r_var))
							{
								
								LinkedList<Schema> newTermList = new LinkedList<Schema>();
								newTermList.add(p_r_ter);
								hLastTable.put(p_r_var, newTermList);
									
							}
								
							else {
								List<Schema> termList = (List<Schema>)hLastTable.get(p_r_var);
								termList.add(p_r_ter);
							}
						}
					}
				}
			}
		}
		
	}
	
	public void concatinate(SubstitutionTable preSTable, int k, int r){
		
		Hashtable<Schema, List<Schema>> preTable = (Hashtable<Schema, List<Schema>>)preSTable.getHashTable(r);
		Hashtable<Schema, List<Schema>> hTable = this.getHashTable(k);
		
		//System.out.println("concatinating:");
		//System.out.println("hTable: "+hTable);
		//System.out.println("preTable: "+preTable);
		
		//We concatinate preTable into hTable and then return it
		Set preSet = (Set)preTable.keySet();
		Iterator preItr = preSet.iterator();
		
		while(preItr.hasNext()){
			Schema preVar = (Schema)preItr.next();
			//System.out.println("preVar: "+preVar);
			List hPreVarList = (List)hTable.get(preVar);
			if(hPreVarList == null){
				//this preVar is not resistered yet in htable
				List preVarList = (List)preTable.get(preVar);
				int preSize = preVarList.size();
				int hSize = this.getElementNum(hTable); 
				// the number of elements (substitutions) that each termList has in hTable
				
				if(preSize > 1){
				//We copy each substitution (presize - 1) times
					Set hSet = (Set)hTable.keySet();
					Iterator hItr = hSet.iterator();
					while(hItr.hasNext()){
						Schema hVar = (Schema)hItr.next();
						List<Schema> hVarList= (List<Schema>)hTable.get(hVar);
						this.copyHList(hVarList, preSize - 1); // <= copying themselves
						//System.out.println("hVarList: "+hVarList);
					} 
				}
				//We make new substitions for var and register them to htable
				//Irregular processing
				if(hSize != 0){
					LinkedList<Schema> newTerms = this.createPreList(preVarList, hSize);
					//System.out.println("newTerms: "+newTerms);
					hTable.put(preVar, newTerms);	
				}
				else {
					System.out.println("Error happen [concatinating process]");
				}

				
			}
		}
		//System.out.println("after: "+hTable);	
	}
	
	public int getElementNum(Hashtable<Schema, List<Schema>> tables){
	
		Collection col = (Collection)tables.values();
		Iterator itr = col.iterator();
		List list = (List)itr.next();
		return list.size();

	}
	
	public void copyHList(List<Schema> list, int times){
		int num = list.size();
		for(int i = 0; i < times; i++){
			//i times copy
			for(int j = 0; j < num; j++){
				Schema s = (Schema)list.get(j);
				list.add(s);
			}
		}
	}
	
	public LinkedList<Schema> createPreList(List list, int times){
	
		LinkedList<Schema> ret = new LinkedList<Schema>();	
		
		for(int i = 0; i < list.size(); i++){
			
			for(int j = 0; j < times; j++){
			
				ret.add((Schema)list.get(i));
				
			}
			
		}
		return ret;
		
	}
	
	
	public Schema getUnifiedTerm(LinkedList<Pair> vtList, Schema var){
	
		Iterator itr = vtList.iterator();
		while(itr.hasNext()){
			Pair pair = (Pair)itr.next();
			Schema var_itr = (Schema)pair.getElement1();
			if( var_itr.getKind() == VARIABLE && var_itr.getName() == var.getName() ){
				return (Schema)pair.getElement2();
			}
		}
		return null;
		
		
	}
	public int getSubsumedClauseSize(){
		
		return this.c_indexes.size();
	
	}
	
	public Integer getCindex(int i){
		return (Integer)this.c_indexes.get(i);
	}
	
	public Hashtable<Schema, List<Schema>> getHashTable(int i){
		
		return (Hashtable<Schema, List<Schema>>)this.substitutions.get(i);
		
	}
	
	public int getSubsumedIndex(int k){
	
		int ret = -1;
		for(int i = 0; i < this.c_indexes.size(); i++){
			Integer i_int = (Integer)this.c_indexes.get(i);
			if(i_int.intValue() == k){
				ret = i;
			}
		}
		return ret;
		
	}
	
	
	public List get(Schema var, Schema term, List<Pair> vtList, int k){
		
		int kk = this.getSubsumedIndex(k);
		//System.out.println("k: "+k);
		//System.out.println("substitutions: "+this.substitutions);
		Hashtable hs = (Hashtable)this.substitutions.get(kk);		
		//hs: substition lists in the previous candidate 
		//    for the kth clause in the bottom theory
		
		List varList = (List)hs.get(var);
		//if hs does not include var, then, we do not need to care 
		//about the previous substitution list
		if(varList == null){
			return null;
		}
		else {
			int i = 0;
			while(i < varList.size()){
				//possible terms to be used to substitute var
				Schema u_term = (Schema)varList.get(i);
				if(u_term.getKind() == term.getKind() && u_term.getName() == term.getName()){
					//We can find a corresponding substitution at i-th element 
					break;
				}
				
				i++;
			}
			if(i == varList.size()){
				//there is no corresponding substitution
				return null;
			}
			else {
				//We extract the other related variables' substitutions
				Set varLists = (Set)hs.keySet();
				Iterator itr = varLists.iterator();
				while(itr.hasNext()){
				
					Schema s_itr = (Schema)itr.next();
					List list_itr = (List)hs.get(s_itr);
					if(i < list_itr.size()){
						Schema t_itr = (Schema)list_itr.get(i);
						Pair p_itr = new Pair(s_itr, t_itr);
						vtList.add(p_itr);
					}
				}
				return vtList;
			}
		}
	}
	
	public String toString(){
	
		StringBuffer str = new StringBuffer(1024);
		//str.append("  c_indexes: "+this.c_indexes+"\n");
		str.append("  substitutions: \n");
		for(int i = 0; i < this.substitutions.size(); i++){
			str.append("  "+this.c_indexes.get(i)+" th clause: ");
			str.append(this.substitutions.get(i)+"\n");

		}
		return str.toString();
	}
	
	
	//Field
	
	/* 1. List of clause indexes in the bottom theory, each of which is subsumed by this candidate */
	List<Integer> c_indexes = null;	
	/* 2. List of substitution tables, each of which represents every substitution by which this
	   candidate subsumes a certain clause in the above list */
	List<Hashtable<Schema, List<Schema>>> substitutions = null;
	
}


