
package ki;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;

public final class Subsumption implements SchemaConstants{
	
	public Subsumption(){
	
		this.hypo = new ArrayList<Schema>();
		
	}
	
	public Subsumption(Candidate cand){
		
		this.hypo = cand.getHypo();
		
	}
	
	public Subsumption(List<Schema> hypo){
	
		this.hypo = hypo;
		
	}
	
	public Subsumption(Clause hypo){
	
		this.hypo = hypo.getLiterals();
		
	}
	
	public void add(Schema s){
		
		this.hypo.add(s);
		
	}
	
	public void remove(Schema s){
	
		this.hypo.remove(s);
		
	}
	
	public boolean subsumptionCheck(Clause gc)
	{
		
		List<Schema> hypo = this.hypo;
		
		//System.out.println("hypo: "+ hypo);
		//System.out.println("gc: "+gc);
		
		Hashtable<Schema, List<Schema>> ht = new Hashtable<Schema, List<Schema>>();
		
		//Narrow checking
		Iterator vc_itr = hypo.iterator();
		while(vc_itr.hasNext())
		{
			
			Schema vci = (Schema)vc_itr.next();
			
			int j = 0;
			boolean hasCandidate = false;
			while( j < gc.size() )
			{
				Schema gcj = (Schema)gc.get(j);
				if( (vci.getName() == gcj.getName()) && (vci.getKind() == gcj.getKind()) ){
					break;
					//This gcj can be unified with vci
					//if(this.getSubstitutions(vci, gcj, ht, htStatus)){
					//hasCandidate = true;
					//}
				}
				j++;
			}
			if( j == gc.size()  ){ 
				//This vci can no be unified with any literal in gc clause
				return false;
			}
		}
		
		//Deep checking
		for( int k = 0; k < hypo.size(); k++ )
		{
			Schema s_k = (Schema)hypo.get(k);
			//System.out.println("s_k: "+s_k);
			//System.out.println("gc: "+gc);
			ArrayList<ArrayList<Pair>> vtList = this.getVTList(s_k, gc);
			//System.out.println("vtList: "+vtList);
			
			ht = mergeCheck(vtList, ht);
			
			if( ht == null ){
				return false;
			}
			//System.out.println("ht: "+ht);
		}
		
		return true;

	}
	
	
	public ArrayList<ArrayList<Pair>> getVTList(Schema var, Clause gc)
	{
		
		//Hashtable<Schema, Integer> htStatus = new Hashtable<Schema, Integer>();
		//Integer zeroStatus = new Integer(0);
		
		ArrayList<ArrayList<Pair>> vtList = new ArrayList<ArrayList<Pair>>();			
		
		for(int i = 0; i < gc.size(); i++)
		{
			
			Schema term = (Schema)gc.get(i);
			
			//System.out.println("term: "+term);
			
			if(term.getKind() != var.getKind() || term.getName() != var.getName()){
				continue;
			}
			else{
				List<Schema> varList = var.getArgsList();
				List<Schema> termList = term.getArgsList();
				
				if(varList.size() != termList.size()){
					continue;
				}
				else{
					
					boolean bondflag = true;
					ArrayList<Pair> vtListTemp = new ArrayList<Pair>();

					for(int j = 0; j < varList.size(); j++)
					{
						Schema var_j = (Schema)varList.get(j);
						Schema term_j = (Schema)termList.get(j);
						
						if(var_j.getKind() == VARIABLE){
							//This is a variable
							
							Schema preBond = this.getBond(vtListTemp, var_j);
							if(preBond == null)
							{
								//This variable is newly appeared in the schema: var
								Pair newPair = new Pair(var_j, term_j);
								vtListTemp.add(newPair);
							}
							else if( preBond.getName() != term_j.getName() || preBond.getKind() != term_j.getKind() )
							{
								//The previous substitution does not correspond to term_j. Then, we cannot add newPair
								//to vtList, and need to skip this schema
								bondflag = false;
								break;
							}
									
						}
						else {
							//This is supposed to be a constant
							if( var_j.getName() != term_j.getName() || var_j.getKind() != term_j.getKind() ){
								//We cannot unify this constant to the term, accordingly we skip this schema
								bondflag = false;
								break;
							}
						}
					}

					if(bondflag){
						//We can add every pair in vtListTemp to vtList. 
						//for(int r = 0; r < vtListTemp.size(); r++){
						
							//Pair pair_r = (Pair)vtListTemp.get(r); 
						vtList.add(vtListTemp);
							
						//}
							
					}
				}
			}
		}
		return vtList;
	}
	
	public Hashtable<Schema, List<Schema>> mergeCheck(ArrayList<ArrayList<Pair>> vtList, Hashtable<Schema, List<Schema>> ht)
	{
		
		if(vtList.size() == 0){
			//There is no subsutitution to be added
			return null;
		}
		
		else {
			ArrayList<Pair> sub_0 = (ArrayList<Pair>)vtList.get(0);

			ArrayList<Schema> regVars = new ArrayList<Schema>();
			ArrayList<Schema> notRegVars = new ArrayList<Schema>();
			
			for(int i = 0; i < sub_0.size(); i++)
			{
				Pair pair_i = (Pair)sub_0.get(i);
				Schema var_i = (Schema)pair_i.getElement1();
				if(ht.containsKey(var_i)){
					regVars.add(var_i);
				}
				else {
					notRegVars.add(var_i);
				}
			}
			
			if( regVars.size() == 0 )
			{				
				//We just add the substitution to the Hashtable ht
				
				int htElementLength = this.getElementLength(ht);
				//System.out.println("htElementLength: "+htElementLength);
				//We copy ht vtList.size - 1 times
				this.copy(ht, vtList.size() - 1);
				
				//System.out.println("copyafter: "+ht);
				
				for(int i = 0; i < vtList.size(); i++)
				{	
					
					ArrayList<Pair> subList = (ArrayList<Pair>)vtList.get(i);
					
					for(int j = 0; j < subList.size(); j++){
					
						Pair pair_j = (Pair)subList.get(j);
						Schema var_j = (Schema)pair_j.getElement1();
						Schema term_j = (Schema)pair_j.getElement2();
						
						List<Schema> list_j = ht.get(var_j);
						if(list_j == null){
							
							LinkedList<Schema> newList = this.addListCopy(term_j, htElementLength);
							ht.put(var_j, newList);
							
						}
						else {
							list_j = this.addListCopy(list_j, term_j, htElementLength);
						}
					}
				}
				return ht;
				//System.out.println("ht_before: "+ht);
			}

			else{
				
				//We create the new Hashtable

				Hashtable<Schema, List<Schema>> htNew = new Hashtable<Schema, List<Schema>>();
				
				for(int i = 0; i < vtList.size(); i++)
				{
					ArrayList<Pair> subList = (ArrayList<Pair>)vtList.get(i);
					//If there is a substitution in ht containing this target Pair,
					//then we add it to hsNew
					ArrayList<ArrayList<Pair>> subListComp = this.getSubstitutions(subList, ht, regVars);
					//System.out.println(i + " th sublist: "+subList);
					//System.out.println(subListComp);
					this.set(subListComp, htNew);
					//System.out.println("htNew: " + htNew);
				}
				if( htNew.size() == 0){
					return null;
				}
				ht = null;
				//System.out.println("ht_merge: "+ht);
				return htNew;
			}
		}		
	}
	
	public LinkedList<Schema> addListCopy(Schema term, int times){
	
		LinkedList<Schema> ret = new LinkedList<Schema>();
		if(times == 0){
			ret.add(term);
		}
		else{
			for(int i = 0; i < times; i++){
				ret.add(term);
			}
		}
		return ret;
		
	}
	
	public List<Schema> addListCopy(List<Schema> list, Schema term, int times){
		
		if(times == 0){
			list.add(term);
		}
		else{
			for(int i = 0; i < times; i++){
				list.add(term);
			}
		}
		return list;
		
	}
	
	public int getElementLength(Hashtable<Schema, List<Schema>> ht){
	
		int ret = 0;
		Collection htCol = (Collection)ht.values();
		Iterator itr = htCol.iterator();
		while(itr.hasNext()){
			List list = (List)itr.next();
			ret = list.size();
			break;
		}
		return ret;
		
	}
	
	
	
	public void copy(Hashtable<Schema, List<Schema>> ht, int times){
	
		if(times == 0){
			return;
		}	
		Set htSet = ht.keySet();
		Iterator itr = htSet.iterator();
		while(itr.hasNext()){
		
			Schema s = (Schema)itr.next();
			List<Schema> list = (List<Schema>)ht.get(s);
			if(list == null){
				break;
			}
			else {
				int size = list.size();
				for(int i = 0; i < times; i++){
				
					//copying list
					for(int j = 0; j < size; j++){
						list.add((Schema)list.get(j));
					}
				}
			}
		}
	}
	
	
	public ArrayList<ArrayList<Pair>> getSubstitutions(ArrayList<Pair> subList, Hashtable<Schema, List<Schema>> ht, ArrayList<Schema> regVars)
	{
		//irregular processing
		if(subList.size() == 0){
			return null;
		}
		
		ArrayList<ArrayList<Pair>> totalSub = new ArrayList<ArrayList<Pair>>();
		
		int u = 0;
		Pair p_u = null;
		Schema var_u = null;
		Schema term_u = null; 
		while(u < subList.size()){
			p_u = (Pair)subList.get(u);
			var_u = (Schema)p_u.getElement1();
			if(regVars.contains(var_u)){
				break;
			}
			u++;
		}
		term_u = (Schema)p_u.getElement2();
		List<Schema> list_u = (List<Schema>)ht.get(var_u);
	
		for(int i = 0; i < list_u.size(); i++)
		{
			ArrayList<Pair> sub = new ArrayList<Pair>();
			Schema s_i = (Schema)list_u.get(i);
			if( s_i.getName() == term_u.getName() && s_i.getKind() == term_u.getKind() )
			{
				int j;
				//Check other substitutions
				for( j = 0; j < subList.size(); j++)
				{
					if( j == u){
						continue; // We do not need to check the case taht j = u
					}
					
					Pair p_j = (Pair)subList.get(j);
					//System.out.println("p_j = "+p_j);
					Schema var_j = (Schema)p_j.getElement1();
					Schema term_j = (Schema)p_j.getElement2();
					
					List<Schema> list_j = (List<Schema>)ht.get(var_j);
					
					if( list_j == null ){
						//System.out.println("test2");
						//var_j has not been registered in ht 
						sub.add(p_j);
					}
					else{
						Schema s_j_i = (Schema)list_j.get(i);
						//System.out.println("s_j_i:"+s_j_i);
						//System.out.println("term_j:"+var_j);
						if( s_j_i.getName() != term_j.getName() || s_j_i.getKind() != term_j.getKind() ){
							break;
						}
					}
				}
				
				if( j == subList.size() ){
					//System.out.println("test");
					//We find a corresponding substitution
					sub = this.addSubstitution(i, ht, sub);
					totalSub.add(sub);
				}
			}
			
		}
		return totalSub;
	}
	
	public ArrayList<Pair> addSubstitution(int i, Hashtable<Schema, List<Schema>> ht, ArrayList<Pair> sub)
	{
		
		//Getting i-th term in each element of ht
		Set htSet = ht.keySet();
		Iterator itr = htSet.iterator();
		while(itr.hasNext()){
		
			Schema s = (Schema)itr.next();
			List<Schema> list = (List<Schema>)ht.get(s);
			Schema t = (Schema)list.get(i);
			Pair pair = new Pair(s, t);
			sub.add(pair);
		}
		return sub;
	}
	
	public void set(ArrayList<ArrayList<Pair>> lists, Hashtable<Schema, List<Schema>> table)
	{
	
		for(int i = 0; i < lists.size(); i++){
		
			ArrayList<Pair> list_i = (ArrayList<Pair>)lists.get(i);

			for( int j = 0; j < list_i.size(); j++){
			
				Pair p_i_j = (Pair)list_i.get(j);
				Schema var_i_j = (Schema)p_i_j.getElement1();
				Schema term_i_j = (Schema)p_i_j.getElement2();
				
				List<Schema> curList = (List<Schema>)table.get(var_i_j);
				if( curList == null ){
				
					LinkedList<Schema> newList = new LinkedList<Schema>();
					newList.add(term_i_j);
					table.put(var_i_j, newList);
					
				}
				else {
					curList.add(term_i_j);
				}
			}
		}
	}
	
	
	public Schema getBond(List<Pair> vtList, Schema var){
		
		Iterator itr = vtList.iterator();
		while(itr.hasNext()){
			Pair pair = (Pair)itr.next();
			Schema varT = (Schema)pair.getElement1();
			if(varT.getName() == var.getName() && varT.getKind() == var.getKind()){
				
				return (Schema)pair.getElement2();
				
			}
			
		}
		return null;
	}
	
	//stub	
	public static void main(String[] args)
	{
		
		
		//System.out.println(args.length);
		if(args.length != 1){
			System.out.println("usage: java Subsumption [problem-path]" );
		}
		else{
			//System.out.println(args[0]);
			CFProblem cfp = CFProblem.create(args[0]);
			List clauses = cfp.getInputClauses();
			//System.out.println(clauses);
			
			if(clauses.size() < 2){
				System.out.println("we need multiple clauses (more than or equal to 2 cluases)");
			}
			
			else {
				Clause c_0 = (Clause)clauses.get(0);
				System.out.println("c_0: "+c_0);
				Subsumption sub = new Subsumption(c_0);
				for(int i = 1; i < clauses.size(); i++){
					Clause c_i = (Clause)clauses.get(i);
					System.out.println("c_"+i);
					if( sub.subsumptionCheck(c_i) ){
						System.out.println(" is subsumed ");
					}
					else {
						System.out.println(" is not subsumed: "+c_i);
					}

				}
			}
		}
		
		
		/* For an initial checking
		SymbolTable symbolTable = SymbolTable.getSymbolTable();
		
		symbolTable.put("p");
		symbolTable.put("q");
		symbolTable.put("0");
		symbolTable.put("1");
		symbolTable.put("2");
		symbolTable.put("3");
		symbolTable.put("4");

		Schema x = Schema.createVariable(0);
		Schema y = Schema.createVariable(1);
		Schema z = Schema.createVariable(2);
		Schema u = Schema.createVariable(3);
		
		Schema zero = Schema.createConstant(symbolTable.get("0"));
		Schema one = Schema.createConstant(symbolTable.get("1"));
		Schema two = Schema.createConstant(symbolTable.get("2"));
		Schema three = Schema.createConstant(symbolTable.get("3"));
		Schema four = Schema.createConstant(symbolTable.get("4"));
		
		List<Schema> x_y = new ArrayList<Schema>();
		x_y.add(x); 
		x_y.add(y);
		
		List<Schema> y_z = new ArrayList<Schema>();
		y_z.add(y);
		y_z.add(z);
		
		List<Schema> x_x = new ArrayList<Schema>();
		x_x.add(x);
		x_x.add(x);
		
		List<Schema> z_u = new ArrayList<Schema>();
		z_u.add(z);
		z_u.add(u);
		
		List<Schema> zero_one = new ArrayList<Schema>();
		zero_one.add(zero);
		zero_one.add(one);
		
		List<Schema> one_two = new ArrayList<Schema>();
		one_two.add(one);
		one_two.add(two);
		
		List<Schema> three_one = new ArrayList<Schema>();
		three_one.add(three);
		three_one.add(one);
		
		List<Schema> two_four = new ArrayList<Schema>();
		two_four.add(two);
		two_four.add(four);
		
		List<Schema> four_four = new ArrayList<Schema>();
		four_four.add(four);
		four_four.add(four);
		
		List<Schema> four_one = new ArrayList<Schema>();
		four_one.add(four);
		four_one.add(one);
		
		Schema p_x_y = Schema.createPosPredicate(symbolTable.get("p"), x_y);
		Schema p_y_z = Schema.createPosPredicate(symbolTable.get("p"), y_z);
		Schema p_x_x = Schema.createPosPredicate(symbolTable.get("p"), x_x);
		Schema q_z_u = Schema.createPosPredicate(symbolTable.get("q"), z_u);

		Schema p_zero_one = Schema.createPosPredicate(symbolTable.get("p"), zero_one);
		Schema p_one_two = Schema.createPosPredicate(symbolTable.get("p"), one_two);
		Schema q_three_one = Schema.createPosPredicate(symbolTable.get("q"), three_one);
		Schema p_two_four = Schema.createPosPredicate(symbolTable.get("p"), two_four);
		Schema p_four_four = Schema.createPosPredicate(symbolTable.get("p"), four_four);
		Schema q_four_one = Schema.createPosPredicate(symbolTable.get("q"), four_one);
		
		List<Schema> c = new ArrayList<Schema>();
		List<Schema> d = new ArrayList<Schema>();
		
		c.add(p_x_y);
		c.add(p_y_z);
		c.add(p_x_x);
		c.add(q_z_u);
		
		d.add(p_zero_one);
		d.add(p_one_two);
		d.add(q_three_one);
		d.add(p_two_four);
		//d.add(p_four_four);
		d.add(q_four_one);
		
		System.out.println("c: "+c);
		System.out.println("d: "+d);	
		
		Clause d_c = new Clause(d);
		
		Subsumption sub = new Subsumption(c);
		if(sub.subsumptionCheck(d_c)){
			System.out.println(" c subsumes d");
		}
		else{
			System.out.println(" c does not subsume d");
		}
		*/
	}
	
	//Field
	List<Schema> hypo = null;
	
}


