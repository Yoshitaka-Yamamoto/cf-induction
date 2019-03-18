
/*
 * $Id$
 */

package ki;

import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;

public final class InductionField implements InductionFieldConstants, SchemaConstants {
	
	public InductionField()
	{
	}
	
	/*
	public void update(ArrayList predicates)
	{
		if(this.allowedPredicates == NO_PREDICATES )
			this.allowedPredicates = SPECIFIC_PREDICATES;
		
		for( int i = 0; i < predicates.size(); i++ ){
			Schema term = (Schema)predicates.get(i);
			term.negate();
			this.predicates.add(term);
		}
		
		System.out.println(this.predicates);
	}
	*/
	 
	public void update(List modes, Type type, InductionField inductionField)
	{
		this.setMinLength(inductionField.getMinLength());
		this.setMaxLength(inductionField.getMaxLength());		
		this.setMinSize(inductionField.getMinSize());		
		this.setMaxSize(inductionField.getMaxSize());
		this.update(modes, type);
	}
	
	public void update(List modes, Type type)
	{
				
		//For each head mode, we seek for such a body mode that 
		//has the same MSchema of it. 
		/** So far, we create only the pos-neg predicates to add their tautologies later  */
		for( int i = 0; i < modes.size(); i++ )
		{
			Mode mode_i = (Mode)modes.get(i);
			
			//System.out.println(mode_i);
			
			int kind = 1; 
			
			//kind = 0 (resp. 1, 2) => create positive (resp. negative, pos_negative) ground instances  
			if( mode_i.getKind() == 0 ){
				//System.out.println("kind = 0");
				
				//this is a head mode
				kind = 0; //positive ground instances
				
				//check if there is its complementary body mode 
				for( int j = i + 1; j < modes.size(); j++){
					
					Mode mode_j = (Mode)modes.get(j);
					
					if( mode_j.getKind() == 1){
						//this is a body mode
						
						if( mode_i.hasSameMschema( mode_j ) ){
							//mode_i and mode_j become a complementary pair
							kind = 2; //positive and negative ground instances
							
							mode_j.setLabel(); // the body mode mode_j will be instantiatated.
							//this.instantiation(mode_i, type, kind);	
							break;
						}
					}
				}
				this.instantiation(mode_i, type, kind);
			}
			else if(mode_i.getLabel() == 0){
				//System.out.println("kind = 1");
				
				//this body mode has not been instantiated yet
				this.instantiation(mode_i, type, kind);
			}
		}
	}
	
	public void instantiation(Mode mode, Type type, int kind)
	{
		
		int name = mode.getName();
		List modeArgs = mode.getModeArgs();
		int argsLength = modeArgs.size();
		Hashtable indexTable = type.getIndexTable();
		List termLists = type.getTermLists();
		
		int[] counter = new int[ argsLength ];
		int[] goal = new int[ argsLength ];
		
		boolean flag = true;
		
		//initiating
		for(int i = 0; i < argsLength; i++){
			
			counter[i] = 0;
			
			MSchema mschema_i = (MSchema)modeArgs.get(i);
			int kind_i = mschema_i.getName();
			if( !indexTable.containsKey(kind_i) ){
				flag = false;
				break;
			}
			int index_i = ((Integer)indexTable.get(kind_i)).intValue();
			List termList =  (List)termLists.get(index_i);
			
			goal[i] = termList.size();
		}
		
		//make all the possible instances
		
		
		while( flag ){
			
			List<Schema> slist = new ArrayList<Schema>();
			
			for(int i = 0; i < argsLength; i++){
				
				//For each i th Mschema, we create a constant schema that
				//correspongs to the count[i] th term of the termList
				MSchema mschema_i = (MSchema)modeArgs.get(i);
				int kind_i = mschema_i.getName();
				int index_i = ((Integer)indexTable.get(kind_i)).intValue();
				List termList =  (List)termLists.get(index_i);
				Schema target_term = (Schema)termList.get(counter[i]);
				
				slist.add(target_term);
				
			}
			
			Schema s = null;
			if( kind == 0 ){
				//create positive predicate (head literal)
				s = Schema.createPosPredicate(name, slist);
				//System.out.println("head: " +s);
			}
			else if( kind == 1 ){
				//create negative predicate (body literal)
				s = Schema.createNegPredicate(name, slist);
				//System.out.println("body: " +s);
				
			}
			else {
				//create pos-neg predicate
				s = Schema.createPosNegPredicate(name, slist);
			}
			
			//adding s to this predicates
			this.predicates.add(s);
			if(this.allowedPredicates == NO_PREDICATES )
				this.allowedPredicates = SPECIFIC_PREDICATES;
			
			//updating counter[i]
			
			flag = false;
			int j = 0;
			while( j < argsLength ){
				
				if(counter[j] < goal[j] - 1){
					//we have next target predicate
					counter[j]++;
					flag = true;
					break;
				}
				else{
					//We set 0 to count[j] and add 1 to count[j+1]
					counter[j] = 0;
					j++;
				}
			}
		}
		//System.out.println("first predicates: " + this.predicates);
		
	}
	
	public String translateProd()
	{
		StringBuffer str = new StringBuffer(1024);
		str.append("production_field(");
		if(this.allowedPredicates != NO_PREDICATES){
			str.append("[");
			if(this.allowedPredicates == SPECIFIC_PREDICATES){
				ArrayList predicates = this.predicates;
				//System.out.println("predicates: "+predicates);
				for(int i = 0; i < predicates.size(); i++){
					Schema s_i = (Schema)predicates.get(i);
					//flip the kind of s_i 
					s_i.negate_pred();
					String s_i_str = s_i.toString();
					//turn the kind of s_i back
					s_i.negate_pred();
					str.append(s_i_str);
					if(i != predicates.size()-1)
						str.append(",");
				}
				str.append("]");
			}
			else if(this.allowedPredicates == ALL_PREDICATES){
				str.append("ALL]");
			}
			else if(this.allowedPredicates == ALL_POS_PREDICATES){
				str.append("POS_ALL]");
			}
			else if(this.allowedPredicates == ALL_NEG_PREDICATES){
				str.append("NEG_ALL]");
			}
		}
		if(this.allowedPredicates == NO_PREDICATES){
			str.append("[ALL]");
		}
		if( (this.maxCopy != NO_RESTRICTION ) && (this.maxSize >= 1) ){
			//In case that the number of copies and clauses are n and m, respectively, the maximum number
			//of clauses in each ground hypothesis is (n+1)*m
			str.append("<=" + this.maxSize*(this.maxCopy+1));
		}
		str.append(").");
		//System.out.println(str);
		return str.toString();
	}
	
	
	public List<Clause> getTautClause()
	{
		
		//System.out.println("predicates: "+this.predicates);
		
		ArrayList<Clause> taut = new ArrayList<Clause>();
		
		for(int i = 0; i < this.predicates.size(); i++){
			Schema s_i = this.predicates.get(i);
			if(s_i.getKind() == 5){
				taut.add(s_i.createTaut());
			}
		}
		return taut;
	}
	
	
	
	public void allowAllPredicates()
	{
		allowedPredicates = ALL_PREDICATES;
	}
	
	
	public void allowAllPosPredicates()
	{
		allowedPredicates = ALL_POS_PREDICATES;
	}
	
	public void allowAllNegPredicates()
	{
		allowedPredicates = ALL_NEG_PREDICATES;
	}
	
	
	public void addPredicate(Schema predicate)
	{
		this.predicates.add(predicate);
		this.allowedPredicates = SPECIFIC_PREDICATES;
	}
	
	
	public void setMaxLength(int maxLength)
	{
		
		if(maxLength >= this.minLength){
			this.maxLength = maxLength;
		
			//if (allowedPredicates == NO_PREDICATES)
			//	allowedPredicates = SPECIFIC_PREDICATES;
		}
	}
	
	public void setMinLength(int minLength)
	{
		this.minLength = minLength;
		
	}	
	
	public void setMaxSize(int maxSize)
	{
		if(maxSize >= this.minSize){
			
			this.maxSize = maxSize;  
			//if (allowedPredicates == NO_PREDICATES)
			//	allowedPredicates = SPECIFIC_PREDICATES;
		}
		
	}
	
	public void setMinSize(int minSize)
	{
		this.minSize = minSize;  		
	}
	
	public ArrayList getPredicates()
	{
		return this.predicates;
	}
	
	public int getMaxLength()
	{
		return maxLength;
	}
	
	public int getMinLength()
	{
		return minLength;
	}
	
	public int getMaxSize()
	{
		return maxSize;
	}
	
	public int getMinSize()
	{
		return minSize;
	}
	
	public int getArrowPredicates()
	{
		return allowedPredicates;
	}
	
	public int getMaxCopy()
	{
		return this.maxCopy;
	}
	
	public int getSize()
	{
		return this.predicates.size();  
	}
	
	public void setMaxCopy(int maxCopy)
	{
		this.maxCopy = maxCopy;
		
		if (allowedPredicates == NO_PREDICATES)
			allowedPredicates = SPECIFIC_PREDICATES;
	}
	
	public boolean isEmpty()
	{
		return allowedPredicates == NO_PREDICATES;
	}
	
	public String toString(){
	
		return this.toString(0);
	}
	
	public String toString(int debagPrint) 
	{
		if (allowedPredicates == NO_PREDICATES)
			return "()";
		
		StringBuffer str = new StringBuffer(512);
		
		//str.append("[");
		
		str.append("length[");
		if (minLength != NO_RESTRICTION)
			str.append("min="+minLength);
		else{
			str.append(" ");
		}
		str.append(":");
		if (maxLength != NO_RESTRICTION)
			str.append("max="+maxLength);
		else{
			str.append(" ");
		}
		str.append("]. ");
		
		str.append("size[");
		if (minSize != NO_RESTRICTION)
			str.append("min="+minSize);
		else{
			str.append(" ");
		}
		str.append(":");
		if (maxSize != NO_RESTRICTION)
			str.append("max="+maxSize);
		else{
			str.append(" ");
		}
		str.append("] \n");
		
		if(debagPrint == 2){
		
			switch (allowedPredicates) {
				case ALL_PREDICATES:
					str.append(" predicates(all)");
					break;
				case ALL_POS_PREDICATES:
					str.append(" predicates(pos_all)");
					break;
				case ALL_NEG_PREDICATES:
					str.append(" predicates(neg_all)");
					break;
				case SPECIFIC_PREDICATES:
					str.append(" predicates(" + predicates + ")");
					break;
			}
		}
				
		return str.toString();
	}
	
	public boolean isRestricted(){
		if( this.allowedPredicates == NO_PREDICATES )
			return false;
		else
			return true;
	}
	
	public boolean checkInductionField(Schema s)
	{
		ArrayList predicates = this.getPredicates();
		int i = 0;
		while( i < predicates.size() )
		{
			Schema p = (Schema)predicates.get(i);
			if( p.getName() == s.getName() ){
				if( p.getKind() != s.getKind() ){
					//simple argument checking
					List<Schema> pArgs = p.getArgsList();
					List<Schema> sArgs = s.getArgsList();
					int j = 0;
					while(j < pArgs.size()){
						Schema p_j = (Schema)pArgs.get(j);
						Schema s_j = (Schema)sArgs.get(j);
						if(p_j.getName() != s_j.getName()){
							break;
						}
						j++;
					}
					if(j == pArgs.size()){
						return true;
					}
				}
			}
			i++;
		}
		return false;
	}
	
	private int allowedPredicates = NO_PREDICATES;
	private int maxLength = NO_RESTRICTION;
	private int minLength = NO_RESTRICTION;	
	private int maxSize = NO_RESTRICTION;
	private int minSize = NO_RESTRICTION;	
	private int maxCopy = NO_RESTRICTION;
	private ArrayList<Schema> predicates = new ArrayList<Schema>();
	private final static int NO_RESTRICTION = -1;
	private final static int NO_PREDICATES = 0;
	private final static int ALL_PREDICATES = 1;
	private final static int ALL_POS_PREDICATES = 2;
	private final static int ALL_NEG_PREDICATES = 3;
	private final static int SPECIFIC_PREDICATES = 4;
	
}