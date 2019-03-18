
public class HSet extends IntArraySet { //IntClosedHashSet { // IntSet { //IntHashSet {

  public HSet() {
    super();
  }
  
  public HSet(HSet set) {
    super(set);
  }

  public boolean isHitting(IntSet set) {
    return isHitting(set, -1);
  }
  
  public boolean isHitting(IntSet set, int exIndex) {
    for (int i=0; i < size(); i++) {
      if (i == exIndex)
        continue;
      if (set.contains(getAt(i)))
        return true;
    }
    return false;      
  }
  
  public int getHitting(IntSet set) {
    int index = NONE;
    boolean first = true;
    for (int i=0; i < size(); i++) {
      if (set.contains(getAt(i))) {
        if (!first) 
          return MULTIPLE;
        index = i;
        first = false;
      }
    }
    return index;
  }
  
  public boolean isHS(IFamily<IntSet> family) {
    return isHS(family, family.size());
  }
  
  public boolean isHS(IFamily<IntSet> family, int endIndex) {
    return isHS(family, endIndex, -1);
  }    
  
  public boolean isHS(IFamily<IntSet> family, int endIndex, int exIndex) {
    if (isEmpty())
      return false;

    boolean hit = false;
    for (int i=0; i < endIndex; i++) {
      IntSet set = family.get(i);
      if (!isHitting(set, exIndex))
        return false;
      hit = true;
    }
    
    return hit;
  }
  
  public boolean isMHS(IFamily<IntSet> family) {
    return isMHS(family, family.size());
  }
  
  public boolean isMHS(IFamily<IntSet> family, int endIndex) {
      
    if (isEmpty())
      return false;
    
    for (int exIndex=0; exIndex < size(); exIndex++) {
      if (isHS(family, endIndex, exIndex))
        return false;
    }
    
    return true;
  }

  public final static int NONE = -1;
  public final static int MULTIPLE = -2;
}
