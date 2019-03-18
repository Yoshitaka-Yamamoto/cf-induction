
package ki;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Hashtable;
import java.util.Set;
import java.util.Iterator;


public final class Comparison{

	
	public Comparison(Schema literal, Mode mode, Type type, Candidate curCand){
	
		VarTerTable orgVtTable = null;
		
		if(curCand != null){
		
			orgVtTable = curCand.getVTtable();
				
		}
		
		this.literal = literal;
		this.litArgs = literal.getArgsList();
		this.mode = mode;
		this.modeArgs = mode.getModeArgs();
		this.type = type;
				
		ArrayList<Pair> vtTable = new ArrayList<Pair>();
		
		if(orgVtTable != null){
			//Setting the initial variable index 
			this.varIndex = orgVtTable.getNumVars();
			//Extracting the previously created variable-term substitutions
			Hashtable h_elements = orgVtTable.getVTStable();
			Set elements = (Set)h_elements.keySet();
			Iterator e_itr = elements.iterator();
			while(e_itr.hasNext()){
				Schema s_e_itr = (Schema)e_itr.next();
				Pair p_e_itr = (Pair)h_elements.get(s_e_itr);
				Schema t_e_itr = (Schema)p_e_itr.getElement1();
				Pair newPair = new Pair(s_e_itr, t_e_itr);
				this.vtTablePre.add(newPair);
				//System.out.println(newPair);
			}
		}
		this.createVarTerTable(0, vtTable);		
		//System.out.println("varTer: "+this.varTerTables);
	}
	
	public void createVarTerTable(int depth, ArrayList<Pair> vtTable)
	{

		//System.out.println("depth = "+depth+". vtTable: "+vtTable);

		if(depth == this.modeArgs.size()){
			//System.out.println("reached vtTable: "+vtTable);
			
			ArrayList<Pair> vtTableCopy = new ArrayList<Pair>();
			for(int i = 0; i < vtTable.size(); i++){
				vtTableCopy.add(vtTable.get(i));
			}
			this.varTerTables.add(vtTableCopy);
			//this.varIndex = 0;
			return;
		}
		
		else {
			Schema schema = (Schema)this.litArgs.get(depth);
			MSchema mschema = (MSchema)this.modeArgs.get(depth);
			if(mschema.getKind() != 2){
				//a new varible-term pair can be generated
				Schema newVar = Schema.createVariable(this.varIndex);
				this.varIndex++;
				Pair newPair = new Pair(newVar, schema);
				vtTable.add(newPair);
				depth++;
				this.createVarTerTable(depth, vtTable);
				//reset
				depth--;
				this.varIndex--;
				vtTable.remove(vtTable.size() - 1);
				
				for(int i = 0; i < depth + this.vtTablePre.size(); i++){
					Pair iPair;
					Schema term;
					//some previously created (in this function) variable-term pair may be re-used
					if( i < depth ){
						iPair = (Pair)vtTable.get(i);
						term = (Schema)iPair.getElement2();
					}
					//some previously created (in the anticipant candidate) variable-term pair may be re-used
					else{
						iPair = (Pair)this.vtTablePre.get(i - depth);
						term = (Schema)iPair.getElement2();
					}
					if( term.equals(schema) ){
						vtTable.add(iPair);
						depth++;
						this.createVarTerTable(depth, vtTable);
						depth--;
						vtTable.remove(vtTable.size() - 1);
					}
				}
			}
			else {
				Pair newPair = new Pair(schema, schema);
				vtTable.add(newPair);
				depth++;
				this.createVarTerTable(depth, vtTable);
				depth--;
				vtTable.remove(newPair);
			}
			
			return;

		}
	}
	
	public int getSize(){
	
		return this.varTerTables.size();
		
	}
	
	public ArrayList<Pair> get(int i){
	
		ArrayList<Pair> vtTable = this.varTerTables.get(i);
		return vtTable;
		
	}
	
	public String toString(){
	
		return this.varTerTables.toString();
		
	}
	
	
	// Field
	Schema literal = null;
	List litArgs = null;
	Mode mode = null;
	List modeArgs = null;

	Type type = null;
	
	//Representing all the possble VarTerTables
	LinkedList<ArrayList<Pair>> varTerTables = new LinkedList<ArrayList<Pair>>();
	//Representing the previous substitutions
	ArrayList<Pair> vtTablePre = new ArrayList<Pair>();
	//Variable index
	int varIndex = 0;
	
}


