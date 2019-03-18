
public abstract class MHSFinder {
  
  public MHSFinder(IFamily<IntSet> family) {
    this.family = family;
  }
  
  public abstract IFamily<HSet> findAllMHS();  
  
  protected IFamily<IntSet> family = null;

}
