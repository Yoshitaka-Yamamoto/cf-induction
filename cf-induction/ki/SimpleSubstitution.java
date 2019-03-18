package ki;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class SimpleSubstitution {
	
	//コンストラクタ
	public SimpleSubstitution(){
	}
	//代入をセットする
	
	public void add(Schema x, Schema y){
		// 代入x/yを保持する
		if(this.bindvars.contains(x) != true){
			Pair x_y = new Pair(x,y); 
			this.substitutions.add(x_y);
			this.bindvars.add(x);
			this.bindMap.put(x, new Integer(this.substitutions.size()));
		}
	}
	
	
	public boolean isEmpty(){
		return this.bindvars.isEmpty();
	}
	
	public boolean addMGU(Schema s, Schema t){
		//System.out.println(s+":::"+t);

		if(s.getKind() == 0){
			if(s.equals(t) != true)
				return false;
		}
		else if(s.getKind() == 1){
			this.add(s, t);
		}
		else if(s.getKind() == 2){
			if(s.getName() != t.getName())
				return false;
			else{
				List<Schema> s_args = s.getArgsList();
				List<Schema> t_args = t.getArgsList();
				int i = 0;
				while(i < s_args.size()){
					Schema s_args_i = (Schema)s_args.get(i);
					Schema t_args_i = (Schema)t_args.get(i);
					if(this.addMGU(s_args_i, t_args_i) == false){
						return false;
					}
					i++;
				}
			}
		}
		return true;
	}
	
	public Clause sustitute(Clause c){
		Clause ret = new Clause();
		//cのインスタンスを与える
		for(int i=0; i < c.size(); i++){
			Schema l = this.substitute((Schema)c.get(i));
			if(ret.containsLiteral(l) != true){
				ret.add(l);
			}
		}
		return ret;
	}

	public Clause sustitute(Clause c, InductionField indField){
		Clause ret = new Clause();
		//cのインスタンスを与える
		for(int i=0; i < c.size(); i++){
			Schema l = this.substitute((Schema)c.get(i));
			if( !ret.containsLiteral(l) && indField.checkInductionField(l) )
			{
				//System.out.println("l: "+l);
				ret.add(l);
			}
		}
		if(ret.size() != c.size()){
			return null;
		}
		else {
			//System.out.println("ret: "+ret);
			return ret;
		}
	}
	
	public Clause substituteSimple(Clause c){
		Clause ret = new Clause();
		//cのインスタンスを与える
		for(int i=0; i < c.size(); i++){
			Schema l = this.substitute((Schema)c.get(i));
			//if(ret.containsLiteral(l) != true){
				ret.addSimple(l);
			//}
		}
		return ret;
		
	}
	
	
	public ArrayList<Schema> getBindVar(){
		return this.bindvars;
	}
	
	public ArrayList<Pair> getSubstitutions(){
		return this.substitutions;
	}
	
	public HashMap<Schema, Integer> getBindMap(){
		return this.bindMap;
	}
	
	
	public Schema substitute(Schema l){
		
		//System.out.println("l:\n"+l);
		//lのインスタンスを与える
		//lが変数だったら代入を加える
		if( ( (l.getKind() == 0) || (l.getKind() == 1) )  && (this.bindvars.contains(l)) ) {
			int id = this.bindMap.get(l);
			return (Schema)this.substitutions.get(id-1).getElement2();			
		}
		else if(l.getKind() == 2){
			List<Schema> schemas = l.getArgsList();
			List<Schema> schemas_ins = new ArrayList<Schema>();
			for(int i = 0; i < schemas.size(); i++){
				Schema l_child = (Schema)schemas.get(i);
				schemas_ins.add(this.substitute(l_child));
			}
			return Schema.createFunction(l.getName(), schemas_ins);	
		}
		else if(l.getKind() == 3){
			List<Schema> schemas = l.getArgsList();
			List<Schema> schemas_ins = new ArrayList<Schema>();
			for(int i = 0; i < schemas.size(); i++){
				Schema l_child = (Schema)schemas.get(i);
				schemas_ins.add(this.substitute(l_child));
			}
			return Schema.createPosPredicate(l.getName(), schemas_ins);	
		}
		else if(l.getKind() == 4){
			List<Schema> schemas = l.getArgsList();
			List<Schema> schemas_ins = new ArrayList<Schema>();
			for(int i = 0; i < schemas.size(); i++){
				Schema l_child = (Schema)schemas.get(i);
				schemas_ins.add(this.substitute(l_child));
			}
			return Schema.createNegPredicate(l.getName(), schemas_ins);	
		}
		
		else{
			return l;
		}
	}
	public void add(SimpleSubstitution sub){
		if(sub != null){
			for(int i=0; i < sub.substitutions.size(); i++){
				Pair p = sub.substitutions.get(i);
				this.add((Schema)p.getElement1(),(Schema)p.getElement2());
			}
		}
	}
	
	public String toString(){
		
		StringBuffer str = new StringBuffer("<");
		
		for(int i=0; i < this.substitutions.size(); i++){
			str.append("["+this.substitutions.get(i)+"]");
		}
		str.append(">");
		return str.toString();
	}
	
	//グローバル変数
	//束縛変数とスキーマの組のリスト
	private ArrayList<Pair> substitutions = new ArrayList<Pair>();
	//束縛変数リスト
	private ArrayList<Schema> bindvars = new ArrayList<Schema>(); 
	//束縛変数に対応する代入セットを検索するためのハッシュリスト
	private HashMap<Schema, Integer> bindMap = new HashMap<Schema, Integer>();

	
}