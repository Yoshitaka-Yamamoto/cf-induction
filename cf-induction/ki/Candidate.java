
package ki;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

public final class Candidate implements SchemaConstants {
	
	
	public Candidate(ArrayList<Pair> varTable, Mode mode, int modeIndex, Candidate preCand, double posCoverPara, double inConsisPara, boolean onInd, double varPara)
	{
		
		this.posCoverPara = posCoverPara;
		this.inConsisPara = inConsisPara;
		this.onInd = onInd;
		this.varPara = varPara;
		
		VarTerTable preVTtable = null;
		List<Schema> preHypo = null;
		List<Integer> preCindexes = null;
		
		//Inherit the status of the previous candidate
		if(preCand != null){
			preVTtable = preCand.getVTtable();
		    preHypo = preCand.getHypo();
			preCindexes = preCand.getCindexes();
			//Deep copy
			
			for(int i = 0; i < preHypo.size(); i++){
				Schema phi = (Schema)preHypo.get(i);
				this.hypo.add(phi);
			}
			
			for(int i = 0; i < preCindexes.size(); i++){
				Integer ci = (Integer)preCindexes.get(i);
				this.c_indexes.add(ci);
			}
			
			
			//Consistency with the background
			if(preCand.isConsistent()){
				this.inconsistency = 0;
			}
			
			this.status = preCand.getStatus();
			
		}
		
		this.modeIndex = modeIndex;
		this.mode = mode;
		
		if(preVTtable != null){
			//vtTable can be inherited
			this.vtTable = preVTtable.clone();
			//System.out.println("inherited vtTable: \n"+ this.vtTable);
		}
		else {
			this.vtTable  = new VarTerTable();
		}
		
		ArrayList<Schema> args = new ArrayList<Schema>();
		List modeArgs = mode.getModeArgs();
		
		//Irregular processing from here
		boolean updateComp = true;
		if(modeArgs.size() != varTable.size()){
			updateComp = false;
		}
		//end here
		
		for(int i = 0; i < varTable.size(); i++){
			
			Pair p_i = (Pair)varTable.get(i);
			Schema var = (Schema)p_i.getElement1();
			Schema ter = (Schema)p_i.getElement2();
			
			if(var.getKind() == VARIABLE){
				//register this pair in vtsTable
				if(!this.vtTable.contains(var)) {
					this.vtTable.put(var, ter);
				}
				else {
					//System.out.println("hello");
					//this variable has been already registered
					this.vtTable.setDepVar(var);
				}   
				
				if(updateComp){
					MSchema ms = (MSchema)modeArgs.get(i);
					//update compTable 
					if( mode.getKind() == 0 && ms.getKind() == 0){
						//this var should be registered
						this.vtTable.update(var, 0); 
						// 0 means that var is an input variable in head mode
					}
					else if( mode.getKind() == 1 && ms.getKind() == 0 ){
						//this var should be registered too
						this.vtTable.update(var, 1);
						// 1 means that var is an input variable in body mode
						
					}
					else if( mode.getKind() == 1 && ms.getKind() == 1 ){
						//this var should be registered too
						this.vtTable.update(var, 2);
						// 2 means that var is an output variable in body mode
					}
					// Note that we do not need to redister any output variable in head mode
					
				}
			}
			args.add(var);
		}
		this.vtTable.unlocked();
		this.vtTable.statusSetOne();
		
		//create a new schema 
		Schema cand; 
		if(mode.getKind() == 0){
			cand = Schema.createPosPredicate(mode.getName(), args);
		}
		else {
			cand = Schema.createNegPredicate(mode.getName(), args);
		}
		this.hypo.add(cand);
	}
	
	
	
		
			
	public Schema getLastSchema(){
		
		return (Schema)this.hypo.get(this.hypo.size() - 1);
		
	}
	
	public int getValue(){
		
		return this.value;
		
	}
	
	public int getEstimate(){
	
		int coverPos = this.c_indexes.size();
		int length = this.hypo.size();
		double posCoverPara = this.posCoverPara;

		int estimate = (int)posCoverPara*coverPos - (length + 1);
		return estimate;
	}
	
	public int getModeIndex(){
		
		return this.modeIndex;
		
	}
	
	public VarTerTable getVTtable(){
		
		return this.vtTable;
		
	}
	
	
	public List<Integer> getCindexes(){
		
		return this.c_indexes;
		
	}
		
	public List<Schema> getHypo(){
		
		return this.hypo;
		
	}
	
	public int getLength(){
		return this.hypo.size();
	}
	
	public boolean getStatus(){
		return this.status;
	}
	
	public boolean isTerminated(){
		
		return this.termination;
		
	}
	
	public boolean isConsistent(){
		
		if(this.inconsistency == 1){
			return false;
		}
		else {
			return true;
		}
		
	}
	
	
	public void update()
	{
		
		int coverPos = this.c_indexes.size();
		int length = this.hypo.size();
		int indVar = this.vtTable.getNumIndVars();
		int incompVar = this.vtTable.getNumIncompVars();
		double posCoverPara = this.posCoverPara;
		double incConsisPara = this.inConsisPara;
		
		//Evaluation function f = coverPos - (length + indVar + inconsistency + incompVar)
		double coverPosDouble = posCoverPara*coverPos;
		this.value = (int)coverPosDouble - (length + (int)varPara*indVar + (int)inConsisPara*inconsistency + incompVar);
		//System.out.println("value: "+this.value);
		
		//Condition for terminating the refinement 
		// if onInd is false then it should hold that indVar = 0 && inconsistency = 0 && incompVar = 0;
		// else, it should hold that inconsistency = 0 && incompVar = 0
		
		
		if( (onInd && indVar == 0 && inconsistency == 0 && incompVar == 0) || (!onInd && inconsistency == 0 && incompVar == 0))
		{
			
			this.termination = true;
			
		}
		//if(inconsistency == 0){
		//	this.termination = true;
		//}
		
	}
	
	public void setInconsistency(int i){
		if(i == 0 || i == 1){
			this.inconsistency = i;
		}
	}
	
	public void set(int i){
	
		Integer i_int = new Integer(i);
		this.c_indexes.add(i_int);
		
	}
	
	public void set(boolean status){
		this.status = status;
	}
	
	
	
	public void remove(Integer i){
	
		this.c_indexes.remove(i);
		
	}
	
	
	public String toString(){
		return this.toString(0);
	}
	
	public String toString(int debagging){
		
		int coverPos = this.c_indexes.size();
		int length = this.hypo.size();
		int indVar = this.vtTable.getNumIndVars();
		int incompVar = this.vtTable.getNumIncompVars();
		
		StringBuffer str = new StringBuffer(1024);
		str.append("\n+++ Current Candidate +++\n");
		str.append("++hypo: "+this.hypo+"\n");
		str.append("++eval func: "+ this.value +"("+ (this.posCoverPara)+"*"+coverPos +"-"+ length + "-"+ (this.varPara) + "*"+indVar + "-" + (this.inConsisPara) + "*"+this.inconsistency + "-" + incompVar +")\n");
		if(debagging == 1){	
			str.append("++vtTable: \n"+this.vtTable+"");
			str.append("++c_indexes: "+this.c_indexes);
			//str.append("+++++++++++++++++++++++++");
			//str.append("++negSubTable ==> "+ this.negSubTable.getSubsumedClauseSize() + " clauses \n"+this.negSubTable);
			str.append("\n +++++++++++++++++++++++++");
		}
		return str.toString();
		
	}
	
	
	//Field
	
	//1. Variable-term-status table [VarTerTable]
	//9. List of incomplete variables [VarTerTable]
	private VarTerTable vtTable = null;
	
	//2. Current mode
	private Mode mode = null;
	
	//12. Current mode index
	private int modeIndex = 0;
	
	//3. List of schemas included in this candidate (a hypothesis clause)
	private ArrayList<Schema> hypo = new ArrayList<Schema>();
	
	//version 4.1, we do not reserve all the possible substitutions for each bottom clause
	//in one SubstitutionTable class for the sake of huge memory consumption, which limit 
	//the hypothesis search. Indeed, we can see it with CF-induction version 4.0
	//please use it with the following command
	// % java ki.CF problem/oddevenV1.txt 
	//It will happen that the initial heap size is lacked even with this simple toy example
	
	
	//Indexes for the positive bottom theory
	private List<Integer> c_indexes = new ArrayList<Integer>();
	
	
	//8. Flag representing the inconsistency with the background theory
	private int inconsistency = 1;
	// inconsistent => 1, consistent => 0
	
	//10. Value of evaluation function
	private int value = 0;
	
	//11. Flag representing the termination to refine this candidate
	private boolean termination = false;
	
	private double posCoverPara = 10;
	private double inConsisPara = 100;
	private double varPara = 1;
	//private double lengthPara = 1;
	
	private boolean status = false;
	
	//12. The condition of no independent variable is included for termination
	private boolean onInd = false;
	
}


