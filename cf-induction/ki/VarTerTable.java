
package ki;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;

public final class VarTerTable{
	
	public VarTerTable(){
		
	}
	
	
	//Methods
	
	//Deep clone
	public VarTerTable clone(){
		
		VarTerTable clone = new VarTerTable();
		
		//Copying numVars
		clone.numIncompVars = this.numIncompVars;
		clone.numIndVars = this.numIndVars;
		
		//Copying vtsTable;
		Set vts = (Set)this.vtsTable.keySet();
		Iterator vts_itr = vts.iterator();
		
		while(vts_itr.hasNext()){
			Schema s = (Schema)vts_itr.next();
			Pair p = (Pair)this.vtsTable.get(s);
			Integer status = (Integer)p.getElement2();
			Schema term = (Schema)p.getElement1();
			Pair p_clone = new Pair(term, status);
			clone.vtsTable.put(s, p_clone);
		}
		
		//Copuing compTable;
		Set comp = (Set)this.compTable.keySet();
		Iterator comp_itr = comp.iterator();
		
		while(comp_itr.hasNext()){
			Pair p = (Pair)comp_itr.next();
			Integer i = (Integer)this.compTable.get(p);
			Integer newInt = new Integer(i.intValue());
			clone.compTable.put(p, newInt);
		}
		return clone;
		
	}
	
	public boolean contains(Schema variable){
		
		return this.vtsTable.containsKey(variable);
		
	}
	

	
	public void setDepVar(Schema var){
		

		//Set the status of this variable as a dependent variable
		Pair pair = this.vtsTable.get(var);
		//System.out.println("setting this var: "+var+":"+pair);
		if(pair != null){
			Integer pre_status = (Integer)pair.getElement2();
			if(pre_status.intValue() == 1){
				Integer status = new Integer(0);
				pair.setE2(status);
				this.numIndVars--;
			}
		}
		//System.out.println("after:"+pair);
	}
	
	public void statusSetOne()
	{
		Collection list = this.vtsTable.values();
		Iterator itr = list.iterator();
		Integer one = new Integer(1);
		while(itr.hasNext()){
			Pair p = (Pair)itr.next();
			Integer p_s = (Integer)p.getElement2();
			if(p_s.intValue() == 2){
				p.setE2(one);
			}
		}
	}
	
	
	public void put(Schema var, Schema term){
		
		//Set the status of this variable as an independent varible
		Integer status = new Integer(2);
		Pair pair = new Pair(term, status);
		this.vtsTable.put(var, pair);
		this.numIndVars++;
	}
	
	
	public void unlocked(){
		
		Set compSet = this.compTable.keySet();
		Iterator compSetItr = compSet.iterator();
		while(compSetItr.hasNext()){
			
			Pair pItr = (Pair)compSetItr.next();
			Integer status = this.compTable.get(pItr);
			if(status.intValue() == 2){
				this.compTable.put(pItr, one);
			}
		}
	}
	/** ``get method'' in Hashtable does not work well here */
	/** adhot checkeing */

	public Integer getComp(Pair pair){
	
		Integer ret = null;
		
		Set compSet = this.compTable.keySet();
		Iterator compSetItr = compSet.iterator();
		while(compSetItr.hasNext()){
			
			Pair pItr = (Pair)compSetItr.next();
			if( pItr.equals(pair) ){
				return (Integer)this.compTable.get(pItr);
			}		
		}
		return ret;
	}
	
	public Pair getCompKey(Pair pair){
		
		Pair ret = null;
		
		Set compSet = this.compTable.keySet();
		Iterator compSetItr = compSet.iterator();
		while(compSetItr.hasNext()){
			
			Pair pItr = (Pair)compSetItr.next();
			if( pItr.equals(pair) ){
				return pItr;
			}		
		}
		return ret;
	}
	
	public void update(Schema var, int i)
	{
	
		// i = 0: var is an input variable in head mode
		// i = 1: var is an input variable in body mode
		// i = 2: var is an output variable in body mode
	
		Integer index = new Integer(i);
		Pair pair = new Pair(var, index);
		
		//System.out.println("cur compTable: "+this.compTable);
		//System.out.println("new pair: "+pair);
		
		Integer status = this.getComp(pair);
		
		if(status == null){
			
			//System.out.println("no registered yet");
			
			//this has been not registered yet
			// case 1. i = 0  or 2 => status 0
			if( (i == 0) || (i == 2) ){
				this.compTable.put(pair, zero);
				//Check if there is the complementary pair (i = 1)
				Pair compPair = new Pair(var, one);
				Pair compKey = (Pair)this.getCompKey(compPair);
				if( compKey != null ){
					Integer compStatus = this.compTable.get(compKey);
					if(compStatus.intValue() == 1){
						this.compTable.put(compKey, zero);
						this.numIncompVars--;
					}
				}
			}
			// case 2. i = 1 => 
			else if( i == 1 ){
				Pair comp1Pair = new Pair(var, zero);
				Pair comp2Pair = new Pair(var, two);
				Integer comp1Status = (Integer)this.getComp(comp1Pair);
				Integer comp2Status = (Integer)this.getComp(comp2Pair);
				if( comp1Status == null && comp2Status == null ){
				 // status 1
					this.compTable.put(pair, two);
					this.numIncompVars++;
				}
				else {
				// status 0
					this.compTable.put(pair, zero);
				}				
			}

		}
		
	}
	

	
	public int getNumVars(){
		return this.vtsTable.size();
	}
	
	public int getNumIndVars(){
		return this.numIndVars;
	}
	
	public int getNumIncompVars(){
		return this.numIncompVars;
	}
		
	public Hashtable getVTStable(){
		return this.vtsTable;
	}
	
	public Hashtable getCOMPtable(){
		return this.compTable;
	}
	
	public String toString(){
	
		StringBuffer str = new StringBuffer(1024);
		str.append("   num of independent variables: "+this.numIndVars+ "\n");
		str.append("   num of incomplete variables: "+this.numIncompVars+ "\n");
		str.append("   vtsTable:"+this.vtsTable+"\n");
		str.append("   compTable:"+this.compTable+"\n");
		return str.toString();
	
	}
	
	public static void main(String[] args)
	{
		Schema v0 = Schema.createVariable(0);
		Schema v1 = Schema.createVariable(1);
		Schema v2 = Schema.createVariable(2);
		Schema v3 = Schema.createVariable(3);
		
		
		VarTerTable vtTable = new VarTerTable();
		vtTable.update(v0, 0);
		System.out.println(vtTable);
		System.out.println(vtTable.getNumIncompVars());	

		vtTable.update(v1, 0);
		System.out.println(vtTable);
		System.out.println(vtTable.getNumIncompVars());	

		vtTable.update(v1, 1);
		System.out.println(vtTable);
		System.out.println(vtTable.getNumIncompVars());	

		vtTable.update(v0, 1);
		System.out.println(vtTable);
		System.out.println(vtTable.getNumIncompVars());	

		vtTable.update(v2, 2);
		System.out.println(vtTable);
		System.out.println(vtTable.getNumIncompVars());	

		vtTable.update(v0, 1);
		System.out.println(vtTable);
		System.out.println(vtTable.getNumIncompVars());	

		vtTable.update(v3, 1);
		System.out.println(vtTable);
		System.out.println(vtTable.getNumIncompVars());	

		vtTable.update(v3, 2);
		vtTable.unlocked();

		System.out.println(vtTable);
		System.out.println(vtTable.getNumIncompVars());	


		
	}
	
	//Field
	
	private Integer zero = new Integer(0);
	private Integer one = new Integer(1);
	private Integer two = new Integer(2);	
	
	/* 1. Variable-term-status table */
	//Key: variable, Value: pair <term, variable status>
	private Hashtable<Schema, Pair> vtsTable = new Hashtable<Schema, Pair>();
	private int numIndVars = 0;
	/* 2. List of incomplete variables */	
	//Key: Pair <variable, an index representing that this variable is an input(+) / output(-) 
	//in a head / body literal >, Value: an index representing the status of this variable 
	private Hashtable<Pair, Integer> compTable = new Hashtable<Pair, Integer>();
	
	private int numIncompVars = 0;
		
	
}


