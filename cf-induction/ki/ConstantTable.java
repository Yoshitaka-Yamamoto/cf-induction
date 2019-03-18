package ki;

import java.util.ArrayList;

public final class ConstantTable{
	
	public ConstantTable(){
	
	}
	
	public void put(Clause c){
		for(int i=0; i < c.size(); i++){
			Schema s_i = c.get(i);
			this.put(s_i);
		}
	}
	
	public int size(){
		return this.conTable.size();
	}
	public void put(Schema s){
		//System.out.println("schema:"+s);
		if(s.getKind() == 0){
			if(conTable.contains(s) != true){
				this.conTable.add(s);
			}
			if(s.getNextSchema()!=null){
				this.put(s.getNextSchema());
			}	
		}
		else if(s.getKind() == 2){
			if(s.getVars().isEmpty() == true){
				if(conTable.contains(s) != true){
					this.conTable.add(s);
				}
				if(s.getNextSchema()!=null){
					this.put(s.getNextSchema());
				}
			}
			if(s.getNextSchema() != null){
				this.put(s.getNextSchema());
			}
		}
		else if( (s.getKind() == 1) || (s.getKind() == 3) || (s.getKind() == 4) || (s.getKind() == 5) ){
			if(s.getNextSchema() != null){
				this.put(s.getNextSchema());
			}			
		}
	}
	
	public Schema get(int id){
		return this.conTable.get(id);
	}
	
	public String toString(){
		return this.conTable.toString();
	}
	
	private ArrayList<Schema> conTable = new ArrayList<Schema>();
}