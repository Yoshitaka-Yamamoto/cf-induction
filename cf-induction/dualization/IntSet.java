
public abstract class IntSet {

  /**
   * Returns true if the specified number is contained in this set.
   * @param num  the specified number to add.
   * @return true if this set contains the specified number.
   */
  public abstract boolean contains(int num);
  
  /**
   * Adds the specified number to this set.
   * @param num  the specified number to add.
   * @return true if the number is added to this set.
   */
  public abstract boolean add(int num); 
  
  /**
   * Adds the elements in the specified set to this set.
   * @param set  the specified set.
   * @return true if a number is added to this set.
   */
  public boolean addAll(IntSet set) {
    boolean added = false;
    IntIterator i = set.iterator();
    while (i.hasNext())
      added |= add(i.next());
    return added;
  }
  
  /**
   * Removes the specified number.
   * @param num  the specified number to be removed.
   * @return true if this set contains the specified number.
   */
  public abstract boolean remove(int num); 
    
  /**
   * Removes the elements in the specified set from this set.
   * @param set  the specified set.
   * @return true if a number is removed from this set.
   */
  public boolean removeAll(IntSet set) {
    boolean removed = false;
    IntIterator i = set.iterator();
    while (i.hasNext())
      removed |= remove(i.next());
    return removed;
  }
  
  /**
   * Clears all elements in this set.
   */
  public abstract void clear();
  
  /**
   * Returns the size of this set.
   * @return
   */
  public abstract int size();

  /**
   * Returns true if this set is empty.
   * @return true if this set is empty.
   */
  public abstract boolean isEmpty();
  
  /**
   * Returns an iterator over the elements in this set.  The elements
   * are returned in no particular order.
   *
   * @return an IntIterator over the elements in this set
   */
  public abstract IntIterator iterator();
  
  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    if (isEmpty())
      return "[]";
    StringBuilder str = new StringBuilder();
    str.append('[');
    IntIterator i = iterator();
    while (true) {
      str.append(i.next());
      if (!i.hasNext())
        return str.append(']').toString();
      str.append(' ');
    }    
  }
  
}
