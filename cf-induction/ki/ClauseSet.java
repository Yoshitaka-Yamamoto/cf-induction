/*
 * $Id$
 */

package ki;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Pattern;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.Runtime;



public final class ClauseSet implements ClauseKind, SchemaConstants
{

	public ClauseSet()
	{
		this.clauses = new ArrayList<Clause>();
	}
	
	public ClauseSet(List<Clause> clauses){
		this.clauses = clauses;
	}

	public ClauseSet(ClauseSet clauses)
	{
		
	}
	
	public void add(Clause c)
	{
		clauses.add(c);
	}
	
	
	public void addBySubsumed(Clause c){
		List<Clause> clauses = new ArrayList<Clause>();
		//System.out.println("Target Clause:"+c);
		Iterator i = this.iterator();
		boolean flag = true;
		while(i.hasNext()){
			Clause c_i = (Clause)i.next();
			//System.out.println("Check Clause"+c_i);
			if(c_i.isContained(c)){
				//c_i subsumes c
				//System.out.println("Omitting");
				flag = false;
				//clauses.add(c_i);
				break;
			}
			else if(c.isContained(c_i) == false){
				//System.out.println("Replacing");
				clauses.add(c_i);
				//c_i.replacedBy(c);
				//flag = false;
				//break;
			}
		}
		if(flag){
			//System.out.println("Adding!");
			clauses.add(c);
			this.clauses = clauses;
		}
		
	}
	
	public void addByEqual(Clause c){
		Iterator i = this.iterator();
		boolean flag = true;
		while(i.hasNext()){
			Clause c_i = (Clause)i.next();
			if(c_i.equals(c)){
				flag = false;
				break;
			}
		}
		if(flag){
			this.add(c);
		}
	}
	
 	

	public int size(){
		return this.clauses.size();
	}

	public int getLitNum(){
		int ret = 0;
		Iterator itr = this.clauses.iterator();
		while(itr.hasNext()){
			Clause c_i = (Clause)itr.next();
			ret += c_i.size();
		}
		return ret;
	}
	
	public static void changeKindtoCarc(List<Clause> carc){
		
		Iterator<Clause> iter = carc.iterator();
		while(iter.hasNext()){
			Clause c = (Clause)iter.next();
			c.setKindToCarc();
		}
	}
	
	public List<Clause> get(){
		return this.clauses;
	}
	
	public Clause get(int i){
		return this.clauses.get(i);
	}
	
	public ClauseSet dualization()
	{
		StringBuffer str = new StringBuffer(5000);
		Hashtable<Schema, Integer> ht = new Hashtable<Schema, Integer>();
		
		ArrayList<Schema> schemaList = new ArrayList<Schema>();
		
		ClauseSet cs = this;
		int id_ij = 1;
		
		Iterator cs_i = cs.iterator();
		while(cs_i.hasNext()){
			Clause c_i = (Clause)cs_i.next();
			for(int j = 0; j < c_i.size(); j++){
				Schema c_ij = (Schema)c_i.get(j);
				if(ht.containsKey(c_ij) != true){
					schemaList.add(c_ij);
					str.append(id_ij+" ");
					ht.put(c_ij, new Integer(id_ij));
					id_ij++;
				}
				else{
					Integer id = (Integer)ht.get(c_ij);
					str.append(id+" ");
				}
			}
			str.append("\n");
		}
		String line = str.toString();
		
		//System.out.println(line);
		
		ClauseSet neg_obs = new ClauseSet();
		
		try{
			FileWriter dualinputstream = new FileWriter("ki/data/dualInput.txt");
			dualinputstream.write(line);
			dualinputstream.close();
			
			Process dualProcess = null;
			BufferedReader inStreamDual = null;
			dualProcess = Runtime.getRuntime().exec("java -classpath dualization Main ki/data/dualInput.txt -uno1");
			inStreamDual = new BufferedReader(new InputStreamReader(dualProcess.getInputStream() ));
			
			String lline;
			String[] arrayline;
			boolean flag = false;
			while( (lline = inStreamDual.readLine()) != null){
				
				//System.out.println(lline);
				
				if(lline.startsWith("[Output]")){
					flag = true;
				}
				else if( flag  && lline.startsWith(" [") ){
					Clause c = new Clause();
					Pattern ptn = Pattern.compile("\\[|\\s|\\]");
					arrayline = ptn.split(lline);
					for(int line_i = 2; line_i < arrayline.length; line_i++){
						Integer int_i = Integer.valueOf(arrayline[line_i]);
						Schema s_i = schemaList.get(int_i-1);
						if(s_i.getKind() == CONSTANT){
							//System.out.println("s_i:"+s_i);
							c.setKind(DROP);
						}
						Schema s_i_neg = s_i.negatedSchema();
						//System.out.println("s_i:"+s_i+"s_i_neg:"+s_i_neg);
						c.add(s_i_neg);
					}
					//System.out.println("c:"+c);
					if(!c.isTautology()){
						if(c.getKind() == DROP)
							//neg_obs.addBySubsumed(c);
							neg_obs.add(c);
						else {
							//neg_obs.addBySubsumed(c);
							neg_obs.add(c);
						}
					}

				}
			}
			inStreamDual.close();
			dualProcess.destroy();
		}catch(IOException e){}
		
		//return neg_obs;
		neg_obs.temp = new ArrayList<Clause>();
		
		//System.out.println(neg_obs);
		//return neg_obs;
		List<Clause> min_neg_obs = neg_obs.minimize();
		if(min_neg_obs != null){
			return new ClauseSet(min_neg_obs);
		}
		else {
			return new ClauseSet();
		}

	}
	
	public List<Clause> minimize(){
		
		if(this.size() == 0){
			return null;
		}
		//System.out.println(this.size_hypo);
		if(this.size() == 1){
			Clause clause_last = (Clause)this.get(0);
			//this.size_hypo += this.getSize(clause_last);
			//System.out.println("c_last: "+clause_last);
			this.temp.add(clause_last);	
			//System.out.println("Size: "+ this.size_hypo+"Adding "+clause_last);
			return this.temp;
		}
		else{
			Clause clause = (Clause)this.get(0);
			//System.out.println("clause: "+clause);
			int flag = 0; 
			int i = 1; 
			while(i < this.size()){
				Clause clause_i = (Clause)this.get(i);
				//System.out.println("clause_i: "+clause_i);
				if(clause_i.isContained(clause)){
					//System.out.println("j is removed");
					this.remove(clause_i);
					flag = 1;
					break;
				}
				else if(clause.isContained(clause_i)){
					//System.out.println("cls is removed");
					this.remove(clause);
					flag = 2;
					break;
				}
				else {
					i++;
				}
			}
			if(flag == 0){
				
				if(this.temp == null){
					System.out.println("null");
				}
				this.temp.add(clause);
				//System.out.println("Size: "+ this.size_hypo+"Adding "+clause);
				this.remove(clause);
				return this.minimize();
			}
			else {
				return this.minimize();
			}
		}
	}
	
	public void remove(Clause c){
		this.clauses.remove(c);
	}
	
	public boolean filtering(InductionField indField)
	{
		ClauseSet cls_new = new ClauseSet();
		ArrayList predicates = indField.getPredicates();
		Iterator i = this.iterator();
		while( i.hasNext()){
			Clause c_i = (Clause)i.next();
			int[] removedList = new int[c_i.size()];
			int removedNum = 0;
			for(int j = 0; j < c_i.size(); j++){
				Schema c_i_j = (Schema)c_i.get(j);
				if(predicates.contains(c_i_j) == false){
					removedList[j] = 1;
					removedNum++;
				}
				else{
					removedList[j] = 0;
				}
			}
			if(removedNum == c_i.size()){
				return false;
			}
			else{
				Clause c_i_new = new Clause();
				for(int j = 0; j < c_i.size(); j++){
					if(removedList[j] == 0){
						c_i_new.add((Schema)c_i.get(j));
					}
				}
				//System.out.println("c_i_new:"+c_i_new);
				cls_new.addBySubsumed(c_i_new);
				//cls_new.addByEqual(c_i_new);
				//for(j = 0; j < this.clauses.size(); j++)
			}
		}
		this.clauses = cls_new.getClauses();
		return true;
	}		
	
	public void removeTautologies(){
		List<Clause> list = new ArrayList<Clause>(); 
		Iterator i = this.iterator();
		while(i.hasNext()){
			Clause c_i = (Clause)i.next();
			if(c_i.isTautology() == false){
				list.add(c_i);
			}
		}
		this.clauses = list;
	}
	
	
	
	public List<Clause> getClauses(){
		return this.clauses;
	}
	
	
	public Iterator iterator()
	{
		return clauses.iterator();
	}
	
    public Clause getClause(int i)
    {
		return (Clause)clauses.get(i);
    }
	
	public String toString() 
	{
		StringBuffer str = new StringBuffer(1024);
		
		Iterator i = clauses.iterator();
		while(i.hasNext()){
			str.append(" "+ i.next() + "\n");
		}
		return str.toString();
	}
	
	public boolean equals(ClauseSet cs){
		
		if(this.size() != cs.size()){
			return false;
		}
		int i = 0;
		while(i < this.size()){
			Clause cs_i = (Clause)cs.get(i);
			if(this.contains(cs_i) == false){
				return false;
			}
			//System.out.println(this_i+"equals to"+cs_i);
			i++;
		}
		
		return true;
		
	}
	
	public boolean contains(Clause c){
		int i = 0;
		while(i < this.size()){
			Clause c_i = (Clause)this.get(i);
			if(c_i.equals(c)){
				return true;
			}
			i++;
		}
		return false;
	}
	
	
	public void removeAll()
	{
		List<Clause> newlist = new ArrayList<Clause>();
		clauses = newlist ;
	}
	
	
	private List<Clause> clauses = null;
	private List<Clause> temp = null;
}

