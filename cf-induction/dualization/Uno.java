import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class Uno extends MHSFinder {

  public Uno(IFamily<IntSet> family, int type) {
    super(family);
    this.type = type;
  }
  
  public IFamily<HSet> findAllMHS() {
    IFamily<HSet> mhss = new IFamily<HSet>();
    CriticalMap criticalMap = new CriticalMap();
    UnionMap    unionMap    = new UnionMap();
    findAllMHS(1, new HSet(), mhss, criticalMap, unionMap);
    return mhss;
  }
  
  private void findAllMHS(int index, HSet hs, IFamily<HSet> mhss, CriticalMap criticalMap, UnionMap unionMap) {
    
//   System.out.println("findAllMHS(" + index + ", " + hs + ", " + mhss + ")");
//   System.out.println(" cri" + criticalMap);
//   System.out.println(" uni" + unionMap);

//    System.out.print("index = " + index + "\r");
    
    if (index == family.size() + 1) {
      mhss.add(new HSet(hs));
//      System.out.println("Found: " + hs);
      return;
    }
    
    IntSet next = family.get(index - 1);

    int hindex = hs.getHitting(next);
    if (hindex >= 0) {
      criticalMap.get(hindex).add(next);
      if (type == USE_UNION_MAP) 
        unionMap.get(hs.getAt(hindex)).addAll(next);
      findAllMHS(index + 1, hs, mhss, criticalMap, unionMap);
      if (type == USE_UNION_MAP)
        unionMap.get(hs.getAt(hindex)).removeAll(next);
      criticalMap.get(hindex).remove(next);
    }
    else if (hindex == HSet.MULTIPLE) {
      findAllMHS(index + 1, hs, mhss, criticalMap, unionMap);
    }
    else {
      IntIterator i = next.iterator();
      while (i.hasNext()) {
        ArrayList<Pair1> removed1 = new ArrayList<Pair1>();
        ArrayList<Pair2> removed2 = new ArrayList<Pair2>();
        boolean isMin = true;
        int n = i.next();   // n will be added to hs.
        
        //if (hs.contains(-n))
          //continue;
        
        CHECK:
        for (int j=0; j < hs.size(); j++) {
          ClosedHashSet<IntSet> familyH = criticalMap.get(j);    // familyH is the set of sets that contains h and doesn't contain other than h in hs.
          IUnionSet unionH = null;
          if (type == USE_UNION_MAP) {
            unionH = unionMap.get(hs.getAt(j));
            if (!unionH.contains(n)) 
              continue;
            if (unionH.get(n) == familyH.size()) {
              isMin = false;
              break CHECK;
            }
          }
          
          Iterator<IntSet> k = familyH.iterator();
          while (k.hasNext()) {
            IntSet setH = k.next();
            if (setH.contains(n)) {    // Now, setH contains not only h but also n. Then the set becomes no-critical.
              k.remove();
              removed1.add(new Pair1(familyH, setH));
              if (type == USE_UNION_MAP) {
                unionH.removeAll(setH);
                removed2.add(new Pair2(unionH, setH));
              }
              if (familyH.isEmpty()) {
                isMin = false;
                break CHECK;
              }              
            }
          }
        }
            
        if (isMin) {
          ClosedHashSet<IntSet> critical = new ClosedHashSet<IntSet>();
          critical.add(next);
          criticalMap.add(critical);
          if (type == USE_UNION_MAP) 
            unionMap.get(n).addAll(next);          
          hs.add(n);
          findAllMHS(index + 1, hs, mhss, criticalMap, unionMap);
          hs.removeLast();
          if (type == USE_UNION_MAP)
            unionMap.get(n).removeAll(next);          
          criticalMap.remove(criticalMap.size() - 1);
        }
        
        for (int j=0; j < removed1.size(); j++) { 
          Pair1 pair1 = removed1.get(j);
          pair1.family.add(pair1.set);
          if (type == USE_UNION_MAP) {
            Pair2 pair2 = removed2.get(j);
            pair2.union.addAll(pair2.set);
          }
        }
      }
    }
  }
  
  private static class Pair1 {
    public Pair1(ClosedHashSet<IntSet> family, IntSet set) {
      this.family = family;
      this.set    = set;
    }
    public ClosedHashSet<IntSet> family = null;
    public IntSet                set    = null;
  }
  
  private static class Pair2 {
    public Pair2(IUnionSet union, IntSet set) {
      this.union = union;
      this.set   = set;
    }
    public IUnionSet union = null;
    public IntSet    set   = null;
  }
  
  
  @SuppressWarnings("serial")
  private static class CriticalMap extends ArrayList<ClosedHashSet<IntSet>> {
  }

  @SuppressWarnings("serial")
  private static class UnionMap extends HashMap<Integer, IUnionSet> {
    
    public IUnionSet get(int index) {
      if (!containsKey(index))
        put(index, new IUnionSet());
      return super.get(index);
    }
    
  }
  
  public final static int USE_CRITICAL_MAP = 0;
  public final static int USE_UNION_MAP = 1;
  private int type = USE_CRITICAL_MAP;
}
