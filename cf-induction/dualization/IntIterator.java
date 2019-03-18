
public interface IntIterator {

  /**
   * Returns {@code true} if the iteration has more elements.
   * (In other words, returns {@code true} if {@link #next} would
   * return an element rather than throwing an exception.)
   *
   * @return {@code true} if the iteration has more elements
   */
  public abstract boolean hasNext();
  
  /**
   * Returns the next element in the iteration.
   *
   * @return the next element in the iteration
   * @throws NoSuchElementException if the iteration has no more elements
   */
  public abstract int next();
  
}
