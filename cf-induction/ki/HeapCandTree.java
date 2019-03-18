
package ki;

import java.io.*;
import java.util.ArrayList;

public final class HeapCandTree{

	//Constructer
	
	public HeapCandTree(){
	
	}
	
	//Method
	
	public void insert(int index, int value){
	
		Integer index_int = new Integer(index);
		Integer value_int = new Integer(value);
		Pair newPair = new Pair(index_int, value_int);
		this.insert(newPair);
		
	}
	
	public void insert(Pair pair)
	{
		//Insert this pair into the last index
		this.heap.add(pair);
		int child = heap.size();
		int parent = child / 2;
		while(parent > 0)
		{
			Pair heap_child = (Pair)this.heap.get(child - 1);
			Pair heap_parent = (Pair)this.heap.get(parent - 1);
			
			Integer heap_child_val = (Integer)heap_child.getElement2();
			Integer heap_parent_val = (Integer)heap_parent.getElement2();
			
			if(heap_child_val.intValue() > heap_parent_val.intValue()){
				this.swap(child-1, parent-1, heap_child, heap_parent);
			}
			else {
				break;
			}
			child = parent;
			parent = child / 2;
		}
	}
	
	public Pair delete_max()
	{
		
		//System.out.println("size = "+this.heap.size());
		
		if(this.heap.size() == 0){
			return null;
		}
		
		Pair ret = this.heap.get(0);
		Pair last = this.heap.get(this.heap.size() - 1);
		this.heap.remove(0);
		this.heap.add(0, last);
		this.heap.remove(this.heap.size() - 1);
		
		int parent = 1;
		int child = parent*2;
		
		while( child <= this.heap.size() )
		{
			Pair p_pair = this.heap.get(parent - 1);
			Pair c_pair_l = this.heap.get(child - 1);
			Pair c_pair = c_pair_l;
			Integer p_int = (Integer)p_pair.getElement2();
			Integer c_l_int = (Integer)c_pair_l.getElement2();
			Integer c_int = c_l_int;

			if( child != this.heap.size()){
				
				Pair c_pair_r = this.heap.get(child);			
				Integer c_r_int = (Integer)c_pair_r.getElement2();
			
				if( c_l_int.intValue() < c_r_int.intValue() ){
					c_int = c_r_int;
					c_pair = c_pair_r;
					child++;
				}
			}
			
			//System.out.println("c_pair: "+c_pair+". p_pair: "+p_pair);
			
			if( c_int.intValue() > p_int.intValue() )
			{
				swap(child-1, parent-1, c_pair, p_pair);
			}
			else {
				//System.out.println("break");
				break;
			}
			
			parent = child;
			child = parent * 2;
		}
		
		return ret;
	}
	
	public void swap(int child, int parent, Pair h_c, Pair h_p)
	{
		
		this.heap.remove(child);
		this.heap.add(child, h_p);
		this.heap.remove(parent);
		this.heap.add(parent, h_c);
		
	}
	
	public int getSize(){
		return this.heap.size();	
	}
	
	public String toString(){
	
		StringBuffer str = new StringBuffer(1024);
		str.append("###### HEAP list ###### \n");
		for(int i = 0; i < heap.size(); i++){
		
			str.append(i +":" + heap.get(i) + "\n");
			
		}
		
		return str.toString();
		
	}
	
	
	
	//heap_insert
	//heap_delete
	
	//Field
	/*
	   Heap list of pairs each of which consists of a candidate index and 
	   the value of the evaluation function of its candidate
	*/
	ArrayList<Pair> heap = new ArrayList<Pair>();  
		
}


