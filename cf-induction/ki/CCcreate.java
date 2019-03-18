/*
 * $Id$
 */

package ki;

import java.io.*;
import java.util.*;
import java.lang.Integer;
import java.lang.Runtime;

public final class CCcreate implements ClauseKind, SchemaConstants {
	
	public CCcreate(ClauseSet neg_obs, ClauseSet background, List<Mode> modes, Type type, InductionField indField)
	{
		this.newcarc = neg_obs.getClauses();
		this.carc = background.getClauses();
	 	this.indField = indField;
		this.modes = modes;
		this.type = type;
		
		for(int i = 0; i < this.newcarc.size(); i++)
		{
			Clause c_i = (Clause)newcarc.get(i);
			//System.out.println("c_i_newc: "+c_i);
			if(c_i.getNumVars() == 0){
				c_i.setKindToObservation();
				//System.out.println("added. kind = "+c_i.getKind());
				//this.copyGrounds.add(c_i);
				this.bridgeTheory.add(c_i);
			}
			else{
				c_i.setKindToObservation();
				this.copyVariables.add(c_i);
			}
		}
		for(int j = 0; j < this.carc.size(); j++){
			Clause c_j = (Clause)carc.get(j);
			//System.out.println("c_i_newc: "+c_j);
			if(c_j.getNumVars() == 0){
				c_j.setKindToBackground();
				this.copyGrounds.add(c_j);
				//this.bridgeTheory.add(c_j);
			}
			else{
				c_j.setKindToBackground();
				this.copyVariables.add(c_j);
			}
		}
		for(int i = 0; i < copyVariables.size(); i++){
			Clause c_i = (Clause)copyVariables.get(i);
			//System.out.println("c_i: "+c_i);
			//for each variable, extracting the terms to be substituted
						
			Hashtable h_i = c_i.getVarType(modes);
			
			if(h_i != null)
			{
				//There are instances from this clause: c_i
				this.set(h_i, c_i);
				while(this.hasNext())
				{
					Clause cnext = this.next(c_i, h_i);
					//System.out.println("cnext: "+cnext);
					if( cnext != null ){
						//System.out.println("b: "+cnext);
						this.copyGrounds.add(cnext);
					}
				}
			}
		}
		//System.out.println("size: "+this.bridgeTheory.size());
	}
	
	public ClauseSet getBridgeTheory(double rate)
	{
		Random random = new Random();
		int rateInt = (int)rate*100;
		for(int i = 0; i < this.copyGrounds.size(); i++){
			int randInt = random.nextInt(101);
			Clause c_i = (Clause)this.copyGrounds.get(i);
			if( randInt <= rateInt ){
				this.bridgeTheory.add(c_i);
			}
		}
		return this.bridgeTheory;
	}
	
	private List<Schema> getList(Schema s)
	{
		//Getting all the instances of the schema s 
		//that belong to the induction field
		List<Schema> schemas = new ArrayList<Schema>();
		ArrayList predicates = this.indField.getPredicates();
		for(int i = 0; i < predicates.size(); i++){
			Schema p = (Schema)predicates.get(i);
			if( p.getName() == s.getName() ){
				if( p.getKind() != s.getKind() ){					
					schemas.add(p.negatedSchema());
				}
			}
		}
		System.out.println("s: "+s);
		System.out.println("schemas: "+schemas);
		return schemas;
	}
	
	private void set(List<List<Schema>> h, Clause c)
	{
		this.curComb = new int[h.size()];
		this.maxComb = new int[h.size()];
		
		Iterator itr = h.iterator();
		for(int i = 0; i < h.size(); i++)
		{
			List<Schema> list_i = (List<Schema>)h.get(i);
			this.curComb[i] = 0;
			this.maxComb[i] = list_i.size() - 1;			
		}
	}
	
	private void set(Hashtable h, Clause c)
	{
		this.curComb = new int[h.size()];
		this.maxComb = new int[h.size()];
		
		Set keySet = h.keySet();
		Iterator itr = keySet.iterator();
		int i = 0;
		while(itr.hasNext())
		{
			Schema schema = (Schema)itr.next();
			Integer integer = (Integer)h.get(schema);
			List termList = this.type.getTermList(integer);
			
			this.curComb[i] = 0;
			this.maxComb[i] = termList.size() - 1;			
			i++;
		}
	}
	
	private boolean hasNext()
	{
		//for(int j = 0; j < this.curComb.length; j++){
		//	System.out.print(this.curComb[j]+" ");
		//}
		//System.out.println(".");
		
		int i = 0;
		while( i < this.curComb.length )
		{
			if( this.maxComb[i] != -1 ){
				if( this.curComb[i] != this.maxComb[i] ){
					this.curComb[i]++;
					break;
				}
				else {
					this.curComb[i] = 0;
				}
			}
			i++;
		}
		if( i == this.curComb.length ){
			return false;
		}
		else {
			return true;
		}
	}
	
	private Clause next(List<List<Schema>> h)
	{
		List<Schema> literals = new ArrayList<Schema>();
		for(int i = 0; i < h.size(); i++){
			List<Schema> list_i = (List<Schema>)h.get(i); 
			if(this.maxComb[i] != -1)
			{
				Schema schema = (Schema)list_i.get(this.curComb[i]);
				literals.add(schema);
			}
		}
		Clause c = new Clause(literals);
		//System.out.println("c: "+c);
		return c;	
	}
	
	private Clause next(Clause c, Hashtable h)
	{
		SimpleSubstitution sub = new SimpleSubstitution();
		Set keySet = h.keySet();
		
		//boolean flag = true;
		
		Iterator itr = keySet.iterator();
		int i = 0;
		while(itr.hasNext())
		{
			Schema variable = (Schema)itr.next();
			Integer integer = (Integer)h.get(variable);
			List termList = this.type.getTermList(integer);
			Schema term = (Schema)termList.get(this.curComb[i]);
			//System.out.println("term: "+term);
			//if( !this.indField.isRestricted() || this.checkInductionField(term) ){
			sub.add(variable, term);
				i++;
		}
			//else {
			//	flag = false;
			//	break;
			//}
		//}
		//if(flag){
		return sub.sustitute(c,this.indField);
		//}
		//else{
		//	return null;
		//}
	}
	
	
	private List<Clause> newcarc = null;
	private List<Clause> carc = null;

	private InductionField indField = null;
	private List<Mode> modes = null;
	private Type type = null;
	
	private List<Clause> copyGrounds = new ArrayList<Clause>();
	private List<Clause> copyVariables = new ArrayList<Clause>();
		
	private int[] curComb = null;
	private int[] maxComb = null;

	private ClauseSet bridgeTheory = new ClauseSet();
	//private ClauseSet posTheory = new ClauseSet();
	//private ClauseSet negTheory = new ClauseSet();

}


