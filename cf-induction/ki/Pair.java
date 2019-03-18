package ki;

import java.util.*;


public final class Pair extends Object implements Cloneable {
    
    public Pair(Object e1, Object e2)
    {
        m_Element1 = e1;
        m_Element2 = e2;
    }

	public void setE1(Object e1){
		m_Element1 = e1;
	}
	
	public void setE2(Object e2){
		m_Element2 = e2;
	}
	
    public Object getElement1()
    {
        return m_Element1;
    }

    public Object getElement2()
    {
        return m_Element2;
    }

    public boolean equals(Pair p)
    {
        return ( m_Element1.equals( p.m_Element1 ) &&
                 m_Element2.equals( p.m_Element2 ) );
    }

    public boolean equals(Object obj)
    {
        if ( obj instanceof Pair )
            return this.equals((Pair)obj);
        else
            return false;
    }

    public String toString() 
    {
        return "<" + m_Element1 + "," + m_Element2 + ">";
    }

    public static void main(java.lang.String[] args) 
    {
    	HashMap<String, Schema> map = new HashMap<String, Schema>();
    	Schema a = Schema.createVariable(0);
    	Schema b = Schema.createVariable(0);
    	Schema c = Schema.createConstant(0);
    	Schema d = Schema.createConstant(0);
    	Schema val1 = Schema.createVariable(1);
    	Schema val2 = Schema.createVariable(2);
        Pair p1 = new Pair(a,c);
        map.put(p1.toString(), val1);
        Pair p2 = new Pair(b,d);
        if(map.containsKey(p2.toString())){
        	System.out.println("contain");
        }
        
        System.out.println(p1);
        if ( p1.equals(p2) )
            System.out.println(p1 + " equals " + p2);
        else
            System.out.println(p1 + " not equals " + p2);
        
        
    }

    private Object m_Element1;
    private Object m_Element2;
}