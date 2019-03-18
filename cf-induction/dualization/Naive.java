
public class Naive extends MHSFinder {

  public Naive(IFamily<IntSet> family) {
    super(family);
  }

  public IFamily<HSet> findAllMHS() {
    IFamily<HSet> mhss = new IFamily<HSet>();
    findAllMHS(1, new HSet(), mhss);
    return mhss;
  }
  
  private void findAllMHS(int index, HSet hs, IFamily<HSet> mhss) {
    
   // System.out.println("findAllMHS(" + index + ", " + hs + ", " + mhss + ")");
    
    if (index == family.size() + 1) {
      mhss.add(new HSet(hs));
      return;
    }
    
    IntSet next = family.get(index - 1);

    if (hs.isHitting(next)) {
      findAllMHS(index + 1, hs, mhss);
    }
    else {
      IntIterator i = next.iterator();
      while (i.hasNext()) {
        int n = i.next();
        hs.add(n);
        if (hs.isMHS(family, index))
          findAllMHS(index + 1, hs, mhss);
        hs.remove(n);
      }
    }
    
  }
  
}
