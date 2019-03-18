package ki;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Hashtable;

import java.lang.String;

public final class Clause implements ClauseKind, SchemaConstants {
	
    public Clause()
    {
    }
	
    public Clause(Schema[] literals)
    {
		for(int i = 0; i < literals.length; i++){
			this.literals.add(literals[i]);
		}
    }
	
	
    public Clause(String name, Schema[] literals)
    {
		
		for(int i = 0; i < literals.length; i++){
			this.literals.add(literals[i]);
		}
		this.name = name;
		this.kind = CARC;
    }
	
	public Clause(String name, int kind)
	{
		this.name = name;
		this.kind = kind;
	}
	
	public Clause(List<Schema> literals)
	{
		this.name     = null;
		this.kind     = CARC;
		
		
		isPositive = true;
		isNegative = true;
		for (int i=0; i < literals.size(); i++) {
			Schema lit = (Schema)literals.get(i);
			this.literals.add(lit);
			varList.addAll( lit.getVars() );
			if (lit.getKind() != Schema.POS_PREDICATE)
				isPositive = false;
			if (lit.getKind() != Schema.NEG_PREDICATE)
				isNegative = false;
		}
	}
	
	public Clause(String name, int kind, List<Schema> literals)
	{
		this.name     = name;
		this.kind     = kind;
		
		
		isPositive = true;
		isNegative = true;
		for (int i=0; i < literals.size(); i++) {
			Schema lit = (Schema)literals.get(i);
			this.literals.add(lit);
			varList.addAll( lit.getVars() );
			if (lit.getKind() != Schema.POS_PREDICATE)
				isPositive = false;
			if (lit.getKind() != Schema.NEG_PREDICATE)
				isNegative = false;
		}
		
		this.numVars = varList.size();
	}
	
	public Hashtable<Schema, Integer> getVarType(List<Mode> modes)
	{
		//<Variable, Type name>
		Hashtable<Schema, Integer> ht = new Hashtable<Schema, Integer>();
		
		//SymbolTable symbolTable = SymbolTable.getSymbolTable();
		for(int i = 0; i < this.size(); i++)
		{
			Schema s_i = this.get(i);
			int j = 0;
			Mode mode = null;
			while(j < modes.size())
			{
				mode = (Mode)modes.get(j);
				if(mode.containsLit(s_i)){
					break;
				}
				j++;
			}
			if( (j != modes.size()) && (s_i.getNumVars() != 0) ){
				//there is a corresponding mode with the schema s_i
				List s_i_args = s_i.getArgsList();
				List mode_args = mode.getModeArgs();
				for(int k = 0; k < s_i_args.size(); k++){
					Schema s_i_k = (Schema)s_i_args.get(k);
					if(s_i_k.getKind() == VARIABLE){
						MSchema m_k = (MSchema)mode_args.get(k);
						//int m_k_n = m_k.getName();
						//String m_k_name = symbolTable.get(m_k_n);
						Integer m_k_name = new Integer(m_k.getName());
						ht.put(s_i_k, m_k_name);
					}
				}
			}
		}
		if(ht.size() == this.getNumVars()){
			return ht;
		}
		else {
			return null;
		}
	}
	
	public void addSimple(Schema s){
		this.literals.add(s);
	}
	
	public void remove(int num){
		
		Schema s = this.literals.get(num);
		VariableList varList_s = s.getVars();
		List<Schema> removedList = new ArrayList<Schema>();
		for(int i = 0; i < varList_s.size(); i++){
			Schema var_i = varList_s.get(i);
			int j = 0;
			boolean flag = false;
			while(j < this.size()-1){
				if(this.get(j).getVars().contains(var_i) ){
					flag = true;
					break;
				}
				j++;
			}
			if(flag == false){
				removedList.add(var_i);
			}
		}
		for(int j = 0; j < removedList.size(); j++){
			this.varList.remove(removedList.get(j));
		}
		this.literals.remove(num);
		
	}
	
	public void removeLast(){
		
		Schema s = this.literals.get(this.size()-1);
		VariableList varList_s = s.getVars();
		List<Schema> removedList = new ArrayList<Schema>();
		for(int i = 0; i < varList_s.size(); i++){
			Schema var_i = varList_s.get(i);
			int j = 0;
			boolean flag = false;
			while(j < this.size()-1){
				if(this.get(j).getVars().contains(var_i) ){
					flag = true;
					break;
				}
				j++;
			}
			if(flag == false){
				removedList.add(var_i);
			}
		}
		for(int j = 0; j < removedList.size(); j++){
			this.varList.remove(removedList.get(j));
		}
		this.literals.remove(this.size()-1);
	}
	
	public boolean isCompleteVar(List<Schema> depVars){
		VariableList varList = this.varList;
		//System.out.println("this varList:"+varList+";depVars:"+depVars);
		//varListがdepVarsと同じなら真を返す
		boolean ret = true;
		int i=0;
		while(i<varList.size()){
			Schema var_i = varList.get(i);
			if( depVars.contains(var_i) != true){
				ret = false;
				break;
			}
			i++;
		}
		return ret;
	}
	
	public void add(Schema schema){
		
		/***/
		//System.out.println("schema:"+schema);
		boolean hasTaut = false;
		int i = 0; 
		while( i < this.literals.size() ){
			Schema lit = (Schema)this.literals.get(i);
			if(lit.isTautology(schema)){
				hasTaut = true;
				break;
			}
			i++;
		}
		
		if(hasTaut != true){
			//System.out.println("hasTaut is false");
			this.literals.add(schema);
			//System.out.println(schema.getVars());
			varList.addAll( schema.getVars() );
			if (schema.getKind() != Schema.POS_PREDICATE)
				isPositive = false;
			if (schema.getKind() != Schema.NEG_PREDICATE)
				isNegative = false;
		}
		this.numVars = varList.size();
	}
	
	public void addTaut(Schema s){
		this.literals.add(s);
		this.varList.addAll(s.getVars());
		this.numVars = this.varList.size();
	}
	//Least General Generalization
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public int hit(Clause c){
		boolean unique = false;
		int i = 0;
		int index = 0;
		while(i < this.size()){
			if(c.includes((Schema)this.get(i))){
				if(unique != true){
					return -1;
				}
				else{
					index = i;
					unique = true;
				}
			}
			i++;
		}
		if(unique != true){
			return -2;
		}
		else{
			return index;
		}
	}
	
	public boolean belongsTo(InductionField indField){
		ArrayList predicates = indField.getPredicates();
		int i = 0;
		boolean flag = true;
		while(i < this.size()){
			Schema s_i = (Schema)this.get(i);
			Schema negated_s_i = s_i.negatedSchema();
			//System.out.println("negated_s_i = "+negated_s_i);
			if(predicates.contains(negated_s_i) == false){
				//System.out.println("no");
				flag = false;
				break;
			}
			i++;
		}
		return flag;
	}
	
	
	
	public boolean belongsToAtLeastOne(InductionField indField){
		ArrayList predicates = indField.getPredicates();
		int i = 0;
		while(i < this.size()){
			Schema s_i = (Schema)this.get(i);
			Schema negated_s_i = s_i.negatedSchema();
			if(predicates.contains(negated_s_i)){
				return true;
			}
			i++;
		}
		return false;
	}
	
	public Clause filtering(InductionField indField, int k)
	{
		ArrayList predicates = indField.getPredicates();
		Clause c_new = new Clause();
		//int removedNum = 0;
		//flag: true then there is a removed literal
		boolean flag = false;
		
		for(int i = 0; i < this.size(); i++){
			Schema l_i = (Schema)this.get(i);
			Schema l_i_neg = l_i.negatedSchema();
			if(predicates.contains(l_i_neg) == false){
				//removedNum++;
				flag = true;
			}
			else{
				c_new.add(l_i);
			}
		}
		if(flag){
			SymbolTable stable = SymbolTable.getSymbolTable();
			int kp = stable.put(String.valueOf(k));
			Schema schema  = Schema.createConstant(kp);
			c_new.add(schema);
			c_new.setKind(DROP);
			return c_new; 
		}
		else{
			return c_new;
		}
	}
	
	public boolean isTautology(){
		int i = 0;
		while(i < this.size()){
			Schema s_i = (Schema)this.get(i);
			int j = i+1;
			while(j < this.size()){
				Schema s_j = (Schema)this.get(j);
				if(s_i.complementaryEquals(s_j)){
					return true;
				}
				j++;
			}
			i++;
		}
		return false;
	}
	
	//単一化可能なリテラルを含まない節にする
	public Clause unification(){
		Clause ret = new Clause();
		Clause temp = this;
		int[] unifList = new int[this.size()];
		for(int i=0; i < this.size(); i++){
			Schema s_i = (Schema)temp.get(i);
			//System.out.println(i+"th:s_i:"+s_i);
			int j = 0;
			while(j < this.size()){
				if( (j != i) && (unifList[j] != 1)){
					Schema s_j = (Schema)temp.get(j);
					//System.out.println(j+"th:s_j:"+s_j);
					SimpleSubstitution mgu = new SimpleSubstitution();
					mgu = this.getMgu(s_i, s_j, mgu);
					//System.out.println("mgu of"+s_i+"::"+s_j+mgu);
					if(this.unifiable(s_i,s_j,mgu)){
						//System.out.println("UNIFIABLE");
						//System.out.println("temp (before):"+temp);
						
						temp = mgu.substituteSimple(temp);
						//System.out.println("temp (after):"+temp);
						unifList[i] = 1;
						break;
					}
				}
				j++;
			}
		}
		for(int k=0; k<temp.size();k++){
			if(unifList[k] == 0){
				ret.add(temp.get(k));
			}
		}
		return ret;
	}
	
	public boolean unifiable(Schema s_i, Schema s_j, SimpleSubstitution mgu){
		if(mgu == null){
			return false;
		}
		Clause c = mgu.sustitute(this);
		//cの任意の要素がthisの節に含まれているならtrueを返す
		boolean ret = true;
		int i=0;
		while(i < c.size()){
			if(this.containsLiteral(c.get(i)) != true){
				ret = false;
				break;
			}
			i++;
		}
		return ret;
	}
	
	//x = yσとなる代入を探す
	public SimpleSubstitution getMgu(Schema x, Schema y, SimpleSubstitution mgu){
		
		//System.out.println("executing mgu[ x:"+x+"y:"+y+"]");
		//yが変数のとき
		if( y.getKind() == 1 ){
			//System.out.println("case1");
			
			//x中にｙが出現しない
			if(x.containsVar(y) != true){
				//System.out.println("x_o offset:"+this.varTable.getOffset(0));
				mgu.add(y, x);
				return mgu;
			}
			//x中にyが出現する場合は単一化できない
		}
		//else if(y.getKind() == 1){
		//this.mgu.add(y, 0, x, 0);
		//}
		//xとyがともに関数のとき
		if( (x.getKind() == 2) && (y.getKind() == 2) ) {
			
			//System.out.println("case2");
			
			if(x.getName()==y.getName()){
				List x_list = x.getArgsList();
				List y_list = y.getArgsList();
				int id = 0;
				while(id < x_list.size()){
					if(this.getMgu((Schema)x_list.get(id), (Schema)y_list.get(id),mgu) == null){
						return null;
					}
					else{
						mgu.add(this.getMgu((Schema)x_list.get(id), (Schema)y_list.get(id),mgu));
					}
					id++;
				}
				return mgu;
			}
		}
		if( (x.getName() == y.getName()) && (x.getKind() == y.getKind()) ){
			
			//System.out.println("case3");
			
			List x_list = x.getArgsList();
			List y_list = y.getArgsList();
			int id2 = 0;
			while(id2 < x_list.size()){
				//System.out.println("id2:"+id2);
				if(this.getMgu((Schema)x_list.get(id2), (Schema)y_list.get(id2),mgu) == null){
					return null;
				}
				else{
					mgu.add(this.getMgu((Schema)x_list.get(id2), (Schema)y_list.get(id2),mgu));
				}
				id2++;
			}
			return mgu;
		}
		else{
			
			//System.out.println("case4");
			
			return null;
		}
	}
	
	private boolean includes(Schema schema) {
		// TODO Auto-generated method stub
		for(int i = 0; i < this.size(); i++ ){
			Schema s = (Schema)this.get(i);
			if(s.equals(schema)){
				return true;
			}
		}
		
		return false;
	}
	
	public int getKind()
	{
		return kind;
	}
	
	public void setKind(int k){
			this.kind = k;
	}
	
	public void setKindToCarc(){
		this.kind = CARC;
	}
	public void setKindToBackground(){
		this.kind = BACKGROUND;
	}
	public void setKindToObservation(){
		this.kind = OBSERVATIONS;
	}
	public Schema get(int index)
	{
		return (Schema)literals.get(index);
	}
	
    public void negClause()
    {
		for(int i=0; i < literals.size(); i++)
		{
			Schema s = (Schema)literals.get(i);
			s.negate();
		}
    }
	
	public int size()
	{
		return literals.size();
	}
	public int getNumVars()
	{
		return numVars;
	}
	public boolean containsVar(int offset, int name)
	{
		if (offset <= name && name < offset + numVars)
			return true;
		
		return false;
	}
	
	public boolean containsLiteral(Schema s){
		if(this.getLiterals().isEmpty()){
			return false;
		}
		for(int i= 0; i < this.size(); i++){
			Schema t = (Schema)this.get(i);
			if(t.equals(s)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isPositive()
	{
		return isPositive;
	}
	
	public boolean isNegative()
	{
		return isNegative;
	}
	
	
	public boolean isContained(Clause c){
		if(this.size() > c.size()){
			return false;
		}
		boolean flag = true;
		List literals = c.getLiterals();
		int i = 0;
		while(i < this.size()){
			Schema this_i = (Schema)this.get(i);
			if(literals.contains(this_i) == false){
				flag = false;
				break;
			}
			i++;
		}
		return flag;
	}
	public boolean isContained(List<Clause> s){
		Iterator i = s.iterator();
		while(i.hasNext()){
			Clause c_i = (Clause)i.next();
			if(this.isContained(c_i))
				return true;
		}
		return false;
	}
	
	public Clause getInstance(int num, ConstantTable conTable){
		//numを組み合わせに変換する
		SimpleSubstitution sub = new SimpleSubstitution();
		int varNum = this.varList.size();
		int conNum = conTable.size();
		int[] combine = new int[varNum];
		//System.out.println("num:"+num+":varNum:"+varNum+":conNum"+conNum);
		for(int id = varNum-1; id >= 0; id--){
			int con_id = num / (int)Math.pow((double)conNum,(double)id);
			num  = num % (int)Math.pow((double)conNum,(double)id);
			// System.out.println("con_id"+con_id);
			combine[id] = con_id;
			Schema s_con = conTable.get(con_id);
			//System.out.println("con:"+s_con);
			
			Schema s_var = this.varList.get(id);
			sub.add(s_var,s_con);
		}
		//System.out.println("ins:"+sub.sustitute(this));
		return sub.sustitute(this);
		
	}
	
	public boolean isEmpty(){
		return this.literals.isEmpty();
	}
	
	
	public Clause clone(){
		Clause c = new Clause();
		for(int i = 0; i < this.size(); i++){
			Schema s = this.get(i);
			c.add((Schema)s.clone());
		}
		return c;
	}
	
	public Clause copy(int num){
		
		Clause c = new Clause();
		c.name     = this.name;
		c.kind     = this.kind;
		
		int varNum = this.getNumVars();
		
		c.isPositive = true;
		c.isNegative = true;
		
		
		for (int i = 0; i < this.literals.size(); i++) {
			Schema lit = (Schema)this.literals.get(i);
			//System.out.println("lit:"+lit);
			Schema lit_copy = lit.copy(varNum, num);
			//lit_copy.changeVarName(varNum, num);
			//System.out.println("lit:"+lit+"･n"+"lit_copy:"+lit_copy);
			c.literals.add(lit_copy);
			c.varList.addAll( lit_copy.getVars() );
			if (lit_copy.getKind() != Schema.POS_PREDICATE)
				c.isPositive = false;
			if (lit_copy.getKind() != Schema.NEG_PREDICATE)
				c.isNegative = false;
		}
		c.numVars = c.varList.size();
		
		return c;
		
	}
	public boolean equals(Clause c)
	{
		if ( literals.size() != c.literals.size() )
			return false;
		if ( numVars != c.numVars )            
			return false;
		for (int i=0; i < literals.size(); i++){
			Schema s1 = (Schema)this.literals.get(i);
			Schema s2 = (Schema)c.literals.get(i);
			if ( ! s1.equals( s2 ) )
				return false;
		}
		return true;
	}
	
	public String toString() 
	{
		if (literals == null)
			return "[]";
		
		StringBuffer str = new StringBuffer();
		
		int i = 0;
		str.append("[");
		for (; i < literals.size() - 1; i++){
			Schema s_i = (Schema)literals.get(i); 
			str.append(s_i.toString());
			str.append(", ");
        }
        if(literals.size() != 0){
			Schema s_last = (Schema)literals.get(i);
			str.append(s_last.toString());
        }
        str.append("]");
		
		return str.toString();
	}
	
	
    public String getName()
    {
        return name;
    }
	
    public List<Schema> getLiterals()
    {
		return literals;
    }
    
	public int getDif(Clause cls) {
		
		
		//System.out.println("this"+this+"size:"+this.size()+"･n"+"cls"+cls+"size"+cls.size());
		if(this.size() == 0){
			return cls.size()-1;
		}
		
		else if(this.size()+1 != cls.size()){
			//System.out.println("1");
			return -1;
		}
		else{
			boolean flag = true;
			int i = 0;
			while(i < this.size()){
				Schema cls_s = (Schema)cls.get(i);
				Schema this_s = (Schema)this.get(i);
				if(cls_s.equals(this_s) != true){
					flag = false;
					break;
				}
				i++;
			}
			if(flag == true){
				return cls.size()-1;
			}
			else{
				//System.out.println("2");
				return -1;
			}
		}
	}
	
	public static void main(String[] args){
		
		//単一可チェック
		SymbolTable sb = SymbolTable.getSymbolTable();
		sb.put("odd");
		sb.put("even");
		sb.put("s");
		sb.put("0");
		Schema x = Schema.createVariable(0);
		Schema y = Schema.createVariable(1);
		List<Schema> x_list = new ArrayList<Schema>();
		List<Schema> y_list = new ArrayList<Schema>();
		x_list.add(x);
		y_list.add(y);
		Schema s_x = Schema.createFunction(sb.get("s"), x_list);
		Schema s_y = Schema.createFunction(sb.get("s"), y_list);
		List<Schema> e_s_x_list = new ArrayList<Schema>();
		List<Schema> e_s_y_list = new ArrayList<Schema>();
		
		e_s_x_list.add(s_x);
		e_s_y_list.add(s_y);
		Schema e_y = Schema.createPosPredicate(sb.get("odd"), x_list);
		Schema e_x = Schema.createPosPredicate(sb.get("odd"), y_list);
		Schema e_s_x = Schema.createPosPredicate(sb.get("even"), e_s_x_list);
		Schema e_s_y = Schema.createPosPredicate(sb.get("even"), e_s_y_list);
		//if(e_s_x.containsVar(x)){
		//System.out.println("yes");
		//}
		Schema zero = Schema.createConstant(sb.get("0"));
		
		List<Schema> test1 = new ArrayList<Schema>();
		List<Schema> test2 = new ArrayList<Schema>();
		List<Schema> test3 = new ArrayList<Schema>();
		
		test1.add(zero);
		Schema s_zero = Schema.createFunction(sb.get("s"),test1);
		test2.add(s_zero);
		Schema e_s_zero = Schema.createPosPredicate(sb.get("odd"),test1);
		
		Clause cls = new Clause();
		cls.add(e_y);
		cls.add(e_x);
		cls.add(e_s_x);
		cls.add(e_s_y);
		cls.add(e_s_zero);
		SimpleSubstitution subtest = new SimpleSubstitution();
		//System.out.println("MGU of"+e_x+"::"+e_s_x+":");
		//System.out.println(cls.getMgu(e_x, e_s_x, subtest));
		//System.out.println("MGU of"+e_s_x+"::"+e_x+":");
		//System.out.println(cls.getMgu(e_s_x, e_x, subtest));
		
		
		System.out.println("*****************");
		
		//              SimpleSubstitution sub = new SimpleSubstitution();
		
		
		//sub.add(x, zero);
		//sub.add(y, s_zero);
		
		//System.out.println("sub:"+sub);
		//cls = sub.sustitute(cls);
		//cls.unification();
		//System.out.println("cls (after substitution) :･n"+cls);
		
		System.out.println("cls:･n"+cls);
		Clause clsUnif = cls.unification();
		System.out.println("cls after unification"+clsUnif);
		
	}
	
	public VariableList getVarList(){
		return this.varList;
	}
	
	public void replacedBy(Clause c){
		this.name = c.getName();
		this.kind = c.getKind();
		this.literals = c.getLiterals();
		this.varList = c.getVarList();
		this.numVars = this.varList.size();
	}
	//private SimpleSubstitution mgu = null;
	private String name = null;
	private int kind = CARC;
	private List<Schema> literals = new ArrayList<Schema>();
	private VariableList varList = new VariableList();
	private int numVars = 0;
	private boolean isPositive = false;
	private boolean isNegative = false;
	private final static int INITIAL_CAPACITY = 10;
	private static MetaVarTable cVarTable = new MetaVarTable(INITIAL_CAPACITY);
	private final static VariableTable varTable = VariableTable.getVariableTable();
	
}