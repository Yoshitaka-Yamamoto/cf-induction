
package ki;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Hashtable;

public final class Generalization{
	
	
	public Generalization(List<Clause> posBotTheory, List<Clause> negBotTheory, List<Mode> modes, Type type, double param, int debag)
	{
		this.debag = debag;
		this.param = param;
		this.modes = modes;
		this.curPosBotTheory = posBotTheory;
		this.curNegBotTheory = negBotTheory;
		this.hypoNum = hypoNum;
		//For each mode, we add it and its copies 
		//(we allow to copy each mode its ``number'' time) 
		for(int i = 0; i < modes.size(); i++){
			Mode mode_i = (Mode)modes.get(i);
			int num_i = mode_i.getNumber();
			for(int j = 0; j < num_i; j++){
				this.modeTable.add(mode_i);
			}
		}
		this.type = type;
		this.numModes = this.modeTable.size();
	}
	
	public void setParam(int minSize, int maxSize, int minLength, int maxLength, double cBias, double iBias, int trials, boolean constTerm, boolean onInd, double vBias)
	{
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.cBias = cBias;
		this.iBias = iBias;
		this.trials = trials;
		this.constTerm = constTerm;
		this.onInd = onInd;
		this.vBias = vBias;
		
		
	}
	
	//Method
	public void setBotClause(int k)
	{
		//Set the current bottom clause
		Clause curBotClause = (Clause)this.curPosBotTheory.get(k);
		this.curBotClause = curBotClause;
		
		//Set the status table 4'
		Subsumption sub = new Subsumption();
		Integer consistent = new Integer(1);
		Integer inconsistent = new Integer(0);
		this.curBotStatus = new Hashtable<Schema, Integer>();
		for(int i = 0; i < this.curBotClause.size(); i++)
		{
			Schema s_i = (Schema)this.curBotClause.get(i);
			sub.add(s_i);
			int j = 0;
			while( j < this.curNegBotTheory.size() ){
				Clause neg_c_j = (Clause)this.curNegBotTheory.get(j);
				if( !sub.subsumptionCheck(neg_c_j) ){
					break;
				}
				j++;
			}
			if( j == this.curNegBotTheory.size() ){
				//The negative bottom theory is subsumed only by s_i
				this.curBotStatus.put(s_i, inconsistent); 
			}
			else{
				this.curBotStatus.put(s_i, consistent);
			}
			sub.remove(s_i);
		}
		//System.out.println(this.curBotStatus);
		
		//Set the correspondence table 3 and status table 3'
		this.modeLitsTable = new ArrayList<LinkedList<Schema>>();
		ArrayList<Integer> modeStatus = new ArrayList<Integer>();
		
		for(int i = 0; i < this.modeTable.size(); i++)
		{
			Mode mode_i = (Mode)this.modeTable.get(i);
			//System.out.println("mode_i: "+mode_i);
			LinkedList<Schema> litsList_i = new LinkedList<Schema>();
			//For each literal in the current bottom clause, 
			//if it belongs to mode_i, we add it to the correspondence table 3
			boolean m_status = false;
			for(int j = 0; j < curBotClause.size(); j++){
				Schema lit_j = (Schema)curBotClause.get(j);
				//System.out.println("lit_j: "+lit_j);
				if( mode_i.containsLit(lit_j, this.type) ){
					//System.out.println("contain");
					litsList_i.add(lit_j);
					Integer status_j = (Integer)this.curBotStatus.get(lit_j);
					//lit_j is consistent
					if( (m_status == false) && (status_j.intValue() == 1) ){
						m_status = true;
					}
				}
			}
			this.modeLitsTable.add(litsList_i);
			if( m_status ){
				modeStatus.add(consistent);
			}
			else {
				modeStatus.add(inconsistent);
			}
		}
		
		this.modeStatus = new ArrayList<Integer>();
		
		boolean stateFlag = false;
		for(int i = 0; i < modeStatus.size(); i++){
			
			if(stateFlag){
				
				this.modeStatus.add(inconsistent);
			}
			else{
				
				int j = i;
				while(j < modeStatus.size()){
					
					Integer int_j = (Integer)modeStatus.get(j);
					if(int_j.intValue() == 1){
						break;
					}
					j++;
					
				}
				if(j == modeStatus.size()){
					stateFlag = true;
					this.modeStatus.add(inconsistent);
				}
				else{
					this.modeStatus.add(consistent);
				}
			}
		}
		
		//System.out.println("modeLitsTable: "+this.modeLitsTable);
		if(this.debag >= 5){
			System.out.println("modeStatus: "+this.modeStatus);
		}
	}
	
	public void reset(Candidate bestCand)
	{
	
		//Removing this cand from hypothesis
		//(this cand must be located in the last index)
		this.hypothesis.remove(this.hypothesis.size() - 1);
		
		//Puting the bottom theory back
		this.curPosBotTheory = this.prePosBotTheory;
		this.curNegBotTheory = this.preNegBotTheory;
	}
	
	public void update(Candidate bestCand)
	{
		
		//Adding the best candidate to hypothesis
		
		Clause hypo = new Clause(bestCand.getHypo());
		this.hypothesis.add(hypo);
		
		//Updating the bottom theory
		List<Integer> c_indexes = bestCand.getCindexes();
		ArrayList<Clause> newPosBot = new ArrayList<Clause>();
		ArrayList<Clause> newNegBot = new ArrayList<Clause>();
		
		this.prePosBotTheory = this.curPosBotTheory;
		this.preNegBotTheory = this.curNegBotTheory;
		
		if(c_indexes.size() == this.curPosBotTheory.size()){

			this.curPosBotTheory = newPosBot;
			//We have finished
		}
		else{
			for(int i = 0; i < this.curPosBotTheory.size(); i++){
				
				Integer i_int = new Integer(i);
				if(!c_indexes.contains(i_int)){
					
					Clause c = (Clause)this.curPosBotTheory.get(i);
					newPosBot.add(c);
					
				}
			}
			
			//Updating the negative bottom theory
			Subsumption sub = new Subsumption(bestCand);
			for(int i = 0; i < this.curNegBotTheory.size(); i++){
				
				Clause c = (Clause)this.curNegBotTheory.get(i);
				if(!sub.subsumptionCheck(c)){
					//System.out.println("does not subsume"+c);
					newNegBot.add(c);
					
				}
			}
			//System.out.println("curNegBotTheory size: "+this.curNegBotTheory.size());
			this.curPosBotTheory = newPosBot;
			this.curNegBotTheory = newNegBot;
		}
	}
	
	public void enumerate(int sign, List<Clause> curPosBotTheory, List<Clause> curNegBotTheory)
	{
		
		if(sign != -1 && this.hypoNum == 0){
			return;
		}
		
		else{
			this.curPosBotTheory = curPosBotTheory;
			this.curNegBotTheory = curNegBotTheory;
			
			boolean enuflag = false;
			//boolean numflag = false; 
			
			if(sign != 1){
				//enumeration mode
				enuflag = true;
			}
		
			if(curPosBotTheory.size() == 0)
			{
				if( (this.minSize == -1) || (this.minSize != -1 && this.hypothesis.size() >= this.minSize) )
				{
					if( sign > 0 ){
						this.hypoNum--;
						if(this.testProblem != null){
							this.testProblem.write(this.hypothesis, this.hypoNum);
							//System.out.println(this);
						}
						else{
							System.out.println("Hypo index: "+this.hypoNum);
							System.out.println(this);
						}
					}
					else{
						int hypoIndex = this.hypoNum + 2;
						this.hypoNum++;
						if(this.testProblem != null){
							this.testProblem.write(this.hypothesis, hypoIndex);
							//System.out.println(this);
						}
						else{
							System.out.println("Hypo index: "+hypoIndex);
							System.out.println(this);
						}
					}
				}
				return;
			}
			
			//while(true){
			//this.times++;
			//System.out.println("Trial number: "+this.times);
			
			Generalization gene = new Generalization(this.curPosBotTheory, this.curNegBotTheory, this.modes, this.type, this.param, this.debag);
			gene.setParam(this.minSize, this.maxSize, this.minLength, this.maxLength, this.cBias, this.iBias, this.trials, this.constTerm, this.onInd, this.vBias);
			if(this.testProblem != null){			
				gene.setTestProblem(this.testProblem);
			}
			gene.setBotClause(0);
			//System.out.println(gene.toString(1));	
			gene.refine(-1, enuflag, 0);
			while(gene.hasNext())
			{
				//hypothesis size checking 
				Candidate cand = gene.next();
				if(this.maxSize != -1 && this.hypothesis.size() >= this.maxSize){
					continue;
				}
				this.update(cand);
				//System.out.println("cur1: "+this.toString(2) );
				this.enumerate(sign, this.curPosBotTheory, this.curNegBotTheory);
				
				this.hypothesis.remove(this.hypothesis.size() - 1);
				this.curPosBotTheory = curPosBotTheory;
				this.curNegBotTheory = curNegBotTheory;

				//System.out.println("cur2: "+this.toString(2) );
				//System.out.println(this.toString(1));
			}
		}
	}
	
	public void enumerate(int i)
	{
		
		enumerate(i, this.curPosBotTheory, this.curNegBotTheory);
	
	}
	
	public void setTestProblem(TestProblem testProblem)
	{
		
		this.testProblem = testProblem;
		
	}
	
	
	public void start(int hypoNum)
	{
	
		this.hypoNum = hypoNum;
				
		if(hypoNum == -1){
			if(this.testProblem == null){
				System.out.println("**Enumeration mode**");
			}
			//Enumeration
			enumerate(-1);
		}
		else if(hypoNum == 1){
			enumerate(1);
		}
		else {
			enumerate(hypoNum);
		}
	}
	
	
	public void refine(int candIndex, boolean enuflag, int curTrials)
	{
		
	    int nextModeIndex;
		
		//Getting the next mode index;
		if(candIndex == -1){
			//Initialization
			nextModeIndex = 0;
			this.candTable = new ArrayList<Candidate>();
			this.heapCandTree = new HeapCandTree();
		}
		else {
			Candidate curCand = (Candidate)this.candTable.get(candIndex);
			nextModeIndex = curCand.getModeIndex() + 1;
		}
		
		for(int i = nextModeIndex; i < this.modeTable.size(); i++){
			refine(candIndex, i);
			//break;
		}
		
		//We repeat to refine the current candidate until the heapCandTree becomes empty 
		
		while(true)
		{
			
			//System.out.println("Size: "+this.heapCandTree.getSize()+". Value: "+this.maxValue);
			//Getting the best candidate
			Pair nextPair = this.heapCandTree.delete_max();
			if( nextPair == null ){
				//System.out.println("Refinement is finished [there is no longer any candidate]");
				//return;
				break;
				//return null;
			}
			Integer bestCandIndex = (Integer)nextPair.getElement1();
			//Integer bestValue = (Integer)nextPair.getElement2();
			//System.out.println("Current best one: "+bestValue);
			Candidate bestCand = this.candTable.get(bestCandIndex.intValue());
			//System.out.println("maxLength: "+this.maxLength+". length: "+bestCand.getLength());
			curTrials++;
			
			if( this.trials != -1 && this.trials < curTrials){
				if(this.debag >= 3){
					System.out.println("pruning-6");
				}
				break;
			}
			
			if(this.debag >= 5){
				System.out.println("limitTrailas: "+this.trials+". curTrials: "+curTrials);
			}
			else if(this.debag >= 2){
				System.out.println("current best (detail): "+bestCand.toString(1));
			}
			else if(this.debag >= 1){
				System.out.println("current best: "+bestCand.toString());
			}
			
			//Updating the number of trials
			
			//Clause length checking
			if( (this.maxLength == -1) || ( this.maxLength != -1 && bestCand.getLength() <= this.maxLength) )
			{
				boolean termination = false;
				if(this.constTerm){
					termination = bestCand.isConsistent();
				}
				else{
					termination= bestCand.isTerminated();
				}
				if( termination ){
					if(this.debag >= 4){
						System.out.println("terminate: "+bestCand.toString());
					}	
					if( this.maxValue < bestCand.getValue() ){
						this.maxValue = bestCand.getValue();
					}

					if( (this.minLength == -1) || ( this.minLength != -1 && bestCand.getLength() >= this.minLength) ){
						if(this.debag >= 0 ){
							System.out.println("max-legth: "+this.maxLength+"length: "+bestCand.getLength()+"best: "+bestCand.toString());
						}
						this.bestStroing(bestCandIndex, bestCand.getValue());
					}
					//System.out.println("papar: "+this.param );
					
					if(bestCand.getValue() > (this.param)*(this.maxValue)){
						//System.out.println("continuing: "+bestCandIndex.intValue());
						//Continuing to refine this candidate
						refine(bestCandIndex.intValue(), enuflag, curTrials);
					}
					else if(this.debag >= 3){
						System.out.println("pruning-1");
					}
					//return;
			
				}
				else {
					//Pruning
					if(bestCand.getEstimate() >= (this.param)*(this.maxValue)){
						if(this.debag >= 3){
							System.out.println("continuing [trials: "+ curTrials +", estimatation: "+bestCand.getEstimate()+", maxValue: "+(this.param)*(this.maxValue)+"]");
						}
						//System.out.println("continuing [maxValue:"+ this.maxValue+", candIndex: "+bestCandIndex.intValue()+"]");
						//System.out.println("continuing: "+bestCandIndex.intValue());
						//Continuing to refine this candidate
						refine(bestCandIndex.intValue(), enuflag, curTrials);
					}
					else if(this.debag >= 3){
						System.out.println("pruning-2");
					}

				}
				if(!enuflag){
					break;
				}
			}
			else if(this.debag >= 3){
				System.out.println("pruning-3");
			}
		}
		
		return;
	}
		
	
	public void refine(int candIndex, int nextModeIndex)
	{
		
		Candidate curCand = null;
		
		//Checking if at least one consisntent hypothesis can be found in future
		if(candIndex != -1){
			curCand = (Candidate)this.candTable.get(candIndex);
			if(!curCand.getStatus()){
				Integer nextModeStatus = (Integer)this.modeStatus.get(nextModeIndex);
				if(nextModeStatus.intValue() == 0){
					if(this.debag >= 3){
						System.out.println("pruring-4");
					}
					return;
				}
			}
			
			if( this.maxLength != -1 && (curCand.getLength() >= this.maxLength) ){
				if(this.debag >= 3){
					System.out.println("pruning-5");
				}
				return;
			}

		}

		LinkedList curModeLits = (LinkedList)this.modeLitsTable.get(nextModeIndex);
		if(this.debag >= 5){
			System.out.println("candIndex: "+candIndex+" nextModeIndex: "+nextModeIndex);
			System.out.println("curModeLits: "+curModeLits);
		}
		
		Iterator lit_iter = curModeLits.iterator();
		while( lit_iter.hasNext() )
		{
			Schema lit_i = (Schema)lit_iter.next();	
			makeCandidate(curCand, lit_i, nextModeIndex);
			//break; //6/10
		}
	}
	
	
	public void makeCandidate(Candidate curCand, Schema literal, int modeIndex)
	{
		if(this.debag >= 5){
			System.out.println("cur-literal: "+literal);
		}
		Mode mode = (Mode)this.modeTable.get(modeIndex);
		
		//Creating the comparion class that makes all the possible variable-term tables
		
		//VarTerTable vtTable = null;
		//List<Schema> curHypo = null;
		
		Integer modeStatusInt = (Integer)this.modeStatus.get(modeIndex);
		Integer litStatusInt = (Integer)this.curBotStatus.get(literal);
		boolean status = false;
		if(modeStatusInt.intValue() == 1 && litStatusInt.intValue() == 1){
			status = true;
		}
		
		if(curCand != null){
			
			if(curCand.getStatus()){
				//System.out.println("hello");
			}
			
		}
		
		Comparison comp = new Comparison(literal, mode, this.type, curCand);
		
		/*** 
		 * End here 6/8. 
		 * vtTable is now the variable-term list which 
		 * appears only in the newly added literal
		 ***/
		//System.out.println("comp: "+comp);

		for(int i = 0; i < comp.getSize(); i++)
		{
			
			ArrayList<Pair> vtTable_i = comp.get(i); 
			//System.out.println("vtTable_i: "+vtTable_i);

			Candidate newCand = new Candidate(vtTable_i, mode, modeIndex, curCand, this.cBias, this.iBias, this.onInd, this.vBias);
			if(status){
				newCand.set(true);
			}
			
			//Schema newLit = newCand.getLastSchema();
			
			//Updating the subsumption relation with the positive and negative bottom theory
			Subsumption sub = new Subsumption(newCand);

			if(curCand == null){
				//at first stage
				
				//Positive bottom theory
				for(int j = 0; j < this.curPosBotTheory.size(); j++){
					
					Clause p_c_j = (Clause)this.curPosBotTheory.get(j);
					if(sub.subsumptionCheck(p_c_j)){
						newCand.set(j);
					}
					
				}
				//Negative bottom theory
				int j = 0;
				while(j < this.curNegBotTheory.size()){
					
					Clause n_c_j = (Clause)this.curNegBotTheory.get(j);
					if(!sub.subsumptionCheck(n_c_j)){
						//System.out.println("does not subsume "+n_c_j);
						break;
					}
					j++;
				}
				if( j == this.curNegBotTheory.size() ){
					//newcand is inconsistent with the background theory
					newCand.setInconsistency(1);
				}
				else {
					//newcand is consistent with the background theory
					newCand.setInconsistency(0);
				}

			}
			else {
				//at some further stage
				
				List curCindexes = curCand.getCindexes();
				//Positive bottom theory
				for(int j = 0; j < curCindexes.size(); j++){
					
					Integer c_j = (Integer)curCindexes.get(j);
					Clause p_c_j = (Clause)this.curPosBotTheory.get(c_j.intValue());
					if(!sub.subsumptionCheck(p_c_j)){
						//newCand does not subsume p_c_j. 
						//Then, we remove its index from c_indexes in newCand
						newCand.remove(c_j);
					}
				}
				
				//Negative bottom theory
				if( !curCand.isConsistent() )
				{
					int j = 0;
					while(j < this.curNegBotTheory.size()){
						
						Clause n_c_j = (Clause)this.curNegBotTheory.get(j);
						if(!sub.subsumptionCheck(n_c_j)){
							break;
						}
						j++;
					}
					if( j != this.curNegBotTheory.size() ){
						//newcand becomes consistent with the background theory
						newCand.setInconsistency(0);
					}
				}
			}			
			//updating the evauation function of newCand
			newCand.update();
			if(this.debag >= 6){
				System.out.println(newCand.toString());
			}
			//Storing newCand
			this.storing(newCand);
			//System.out.println(this.candTable.size()+" th");
			//break; //6/10
		}
	}
	
	public void storing(Candidate cand){
	
		int index = this.candTable.size();
		this.candTable.add(cand);
		this.heapCandTree.insert(index, cand.getValue());
		
	}
	
	public void bestStroing(Integer index, int value){
	
		//System.out.println("maxValue: "+this.maxValue+" value: "+value);
		if(this.param*this.maxValue <= value){
		
			this.maxIndex.add(index);
			
		}
		
	}
		
	public boolean hasNext(){
	
		if( this.maxIndex.size() > 0){
			return true;
		}
		else{
			return false;
		}
	}
	
	public Candidate next(){
	
		Integer index = (Integer)this.maxIndex.get(0);
		Candidate cand = (Candidate)this.candTable.get(index);
		this.maxIndex.remove(0);
		return cand;
		
	}

    
    public void write()
	{
		
        StringBuffer str = new StringBuffer(10000);
        
        //Writing the positive bottom theory
        
        if(this.curPosBotTheory != null){
            
            for(int i = 0; i < this.curPosBotTheory.size(); i++){
                
                Clause curPos_i = (Clause)this.curPosBotTheory.get(i);
                str.append("cnf(an, pos, ");
                str.append(curPos_i);
                str.append(").\n");
                
            }
        
        }
        if(this.curNegBotTheory != null){
            for(int i = 0; i < this.curNegBotTheory.size(); i++){
                
                Clause curPos_i = (Clause)this.curNegBotTheory.get(i);
                str.append("cnf(an, neg, ");
                str.append(curPos_i);
                str.append(").\n");
                
            }
        }
        
        //Writing the mode declaration
        
        if(this.modes != null){
            
            for(int i = 0; i < this.modes.size(); i++){
                Mode m_i = (Mode)this.modes.get(i);
                str.append(m_i);
                str.append(".\n");
            }
            
        }
        
        
        //Writing the type declaration
        str.append(this.type.toString(2));
        
        //Writing the induction field
		str.append("induction_field(");
		str.append("size[");
		if(this.minSize != -1){
			str.append(this.minSize);
		}
		else {
			str.append("0");
		}

		str.append(":");
		if(this.maxSize != -1){
			str.append(this.maxSize);
		}
		str.append("],\n");
		str.append("length[");
		
		if(this.minLength != -1){
			str.append(this.minLength);
		}
		else{
			str.append("0");
		}
		str.append(":");
		if(this.maxLength != -1){
			str.append(this.maxLength);
		}
		str.append("]\n");
		
		str.append(").");
		
        try{
            FileWriter fw = new FileWriter("ki/data/geneProblem.txt");
            fw.write(str.toString());
            fw.close();
        }catch(IOException e){ System.out.println(e); }

    }
    
	
	public String toString(){
	
		return this.toString(0);
	
	}
	
	public String toString(int debagging){
	
		StringBuffer str = new StringBuffer(1024);
		
		if(debagging == 1){
			str.append("-------- Debugging print (Generalization.class) --------\n");
		
			str.append("-- Current bottom clause\n");
			str.append(this.curBotClause+"\n");
		
			if(this.modeTable != null && this.modeLitsTable != null){
				str.append("-- Mode table\n");
				for(int i = 0; i < this.modeTable.size(); i++){
					str.append(modeTable.get(i));
					str.append(":  "+this.modeLitsTable.get(i) + "\n");
				}
				str.append("-- Number of modes: "+this.numModes+"\n");
			}
		}
		
		str.append("-- Current hypothesis\n");
		for(int i = 0; i < this.hypothesis.size(); i++){
			str.append(this.hypothesis.get(i)+"\n");
		}
		
		if(debagging == 1 || debagging == 2){
			str.append("-- Current positive bottom theory\n");
			for(int i = 0; i < this.curPosBotTheory.size(); i++){
				str.append(this.curPosBotTheory.get(i)+"\n");
			}
		}

		if(debagging == 1 || debagging == 2){
			str.append("-- Current negative bottom theory\n");
			for(int i = 0; i < this.curNegBotTheory.size(); i++){
				str.append(this.curNegBotTheory.get(i)+"\n");
			}
		}
		
		
		return str.toString();
		
	}
	
	//stub
	public static void main(String[] args)
	{

		int i = 0;
		double param = 1;
		int hypoNum = 1;
		double cBias = 10;
		double iBias = 1;
		double vBias = 1;
		boolean onInd = false;
		
		int trials = -1;
		int debag = -1;
		boolean constTerm = false;
		
		while( i < args.length ) 
		{
			
			if ( args[i].equals("-n") || args[i].equals("-num") ) {
				if(i < args.length-1){
					//System.out.println("args++:"+args[i+1]);
					hypoNum = Integer.parseInt(args[i+1]);
					i++;
				}
			}
			
			if ( args[i].equals("-e") || args[i].equals("-enum") ) {
				//System.out.println("args++:"+args[i+1]);
				hypoNum = -1;
			}
			
			if ( args[i].equals("-r") || args[i].equals("-rate") ) {
				//System.out.println("args++:"+args[i+1]);
				if(i < args.length-1){
					//System.out.println("args++:"+args[i+1]);
					param = Double.parseDouble(args[i+1]);
					i++;
				}
			}
			
			if ( args[i].equals("-l") || args[i].equals("-debag") ) {
				//System.out.println("args++:"+args[i+1]);
				if(i < args.length-1){
					//System.out.println("args++:"+args[i+1]);
					debag = Integer.parseInt(args[i+1]);
					i++;
				}
			}
			
			if ( args[i].equals("-t") || args[i].equals("-trial") ) {
				//System.out.println("args++:"+args[i+1]);
				if(i < args.length-1){
					//System.out.println("args++:"+args[i+1]);
					trials = Integer.parseInt(args[i+1]);
					constTerm = true;
					i++;
				}
			}
			
			if ( args[i].equals("-onInd") ) {
				if(i < args.length-1){
					onInd = true;
				}
			}
			
			if ( args[i].equals("-cp") || args[i].equals("-coverParam") ) {
				//System.out.println("args++:"+args[i+1]);
				if(i < args.length-1){
					//System.out.println("args++:"+args[i+1]);
					cBias = Double.parseDouble(args[i+1]);
					i++;
				}
			}

			if ( args[i].equals("-ip") || args[i].equals("-inconsistParam") ) {
				//System.out.println("args++:"+args[i+1]);
				if(i < args.length-1){
					//System.out.println("args++:"+args[i+1]);
					iBias = Double.parseDouble(args[i+1]);
					i++;
				}
			}	
			
			if ( args[i].equals("-vp") || args[i].equals("-variableParam") ) {
				//System.out.println("args++:"+args[i+1]);
				if(i < args.length-1){
					//System.out.println("args++:"+args[i+1]);
					vBias = Double.parseDouble(args[i+1]);
					i++;
				}
			}			
			
			i++;
		}
			
		CFProblem cfp = CFProblem.create("ki/data/geneProblem.txt");
		ClauseSet bottom_p = cfp.getPosBottom();
		ClauseSet bottom_n = cfp.getNegBottom();
			
		List<Mode> modes = cfp.getInputModes();
		Type type = cfp.getInputTypes();
		InductionField indField = cfp.getInductionField();
			
		Generalization gene = new Generalization(bottom_p.get(), bottom_n.get(), modes, type, param, debag);
		gene.setParam(indField.getMinSize(), indField.getMaxSize(), indField.getMinLength(), indField.getMaxLength(), cBias, iBias, trials, constTerm, onInd, vBias);
		//System.out.println("bottom_p: "+bottom_p);
		//System.out.println("bottom_n: "+bottom_n);
		//System.out.println("modes: "+modes);
		//System.out.println("type: "+type);
		//System.out.println("induction field: "+indField);
		gene.start(hypoNum);

	}
	
	
	//Field
	private List<Mode> modes = null;
	
	//1. Correspondence table between each index (called candidate index) and 
	//its candidate
	private ArrayList<Candidate> candTable = null; 	
	
	//2. Current (positive) bottom clause
	private Clause curBotClause = null;
	
	//3. Correspondence table between each index (called mode index) and its mode 
	private ArrayList<Mode> modeTable = new ArrayList<Mode>();

	//3'. Table representing the state of each mode
	private ArrayList<Integer> modeStatus = null;
	
	//4. Correspondence table between each mode index and literals in the current 
	//bottom clause that belongs to its mode
	private ArrayList<LinkedList<Schema>> modeLitsTable = null;
	
	//4'. Hashtable representing the state of each literal in the bottom clause
	private Hashtable<Schema, Integer> curBotStatus = null;
	
	//5. Num. of modes
	private int numModes = -1;
	
	//6. Heap tree of pairs each of which consists of a candidate index 
	//and the score of its candidate
	private HeapCandTree heapCandTree = null;
	
	//7. Current hypothesis theory
	private LinkedList<Clause> hypothesis = new LinkedList<Clause>();
	
	//8. Current positive bottom theory
	private List<Clause> curPosBotTheory = null;	
	private List<Clause> prePosBotTheory = null;
	
	//9. Negative bottom theory
	private List<Clause> curNegBotTheory = null;
	
	private List<Clause> preNegBotTheory = null;
	
	//10. Type
	private Type type = null;

	//11. Trials
	private int trials = -1;
	
	//12. Hypothesis number
	private int hypoNum = 1; 
	
	//13. Current maximum value (initializing it as MIN_VALUE)
	private int maxValue = Integer.MIN_VALUE;

	//14. The index list of terminated candidates that have the max evaluation function value
	private ArrayList<Integer> maxIndex = new ArrayList<Integer>();
	
	//15. Pruning parameter  
	private double param = 1;
	
	//16. Max length of clauses in hypotheses 
	private int maxLength = -1;

	//17. Min length of clauses in hypotheses
	private int minLength = -1;

	//18 Max size of clauses in hypotheses
	private int maxSize = -1;

	//19 Min size of clauses in hypotheses
	private int minSize = -1;
	
	//20 Coverage bias in Evaluation function
	private double cBias = 10;
	
	//21 Inconsistency bias in Evaluation function
	private double iBias = 1;
	
	//22 debagging mode
	private int debag = -1;
	
	//23 consistency termination
	private static boolean constTerm = false;
	
	//24 Test example
	private TestProblem testProblem = null;
	
	//25 Parametor on the termination condition determining if the independent var is used 
	private boolean onInd = false; 
	
	//26 Parametor for peneralizing independent variables
	private double vBias = 1;
	
}


