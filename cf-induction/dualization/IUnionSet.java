import java.util.HashMap;

@SuppressWarnings("serial")
public class IUnionSet extends HashMap<Integer, Integer> {

  public void add(int n) {
    if (containsKey(n)) 
      put(n, get(n) + 1);
    else
      put(n, 1);    
  }
  
  public void addAll(IntSet set) {
    IntIterator i = set.iterator();
    while (i.hasNext())
      add(i.next());
  }
  
  public void remove(int n) {
    assert(containsKey(n));
    assert(get(n) > 0);
    put(n, get(n) - 1);      
  }
  
  public void removeAll(IntSet set) {
    IntIterator i = set.iterator();
    while (i.hasNext())
      remove(i.next());
  }
  
  public boolean contains(int n) {
    if (containsKey(n))
      return get(n) > 0;
    return false;
  }
}
