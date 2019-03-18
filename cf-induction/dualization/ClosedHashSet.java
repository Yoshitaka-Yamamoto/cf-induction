import java.util.Iterator;

public class ClosedHashSet<E> {

  /**
   * Adds the specified object to this set.
   * @param num  the specified object to add.
   * @return true if the object is added to this set.
   */
  public boolean add(E obj) {
    if (size + 1 > capacity / 2)
      rehash();
    if (insert(obj)) {
      size++;
      return true;
    }
    return false;
  }
  
  /**
   * Removes the specified object.
   * @param num the specified object to be removed.
   * @return true if this set contains the specified object.
   */
  public boolean remove(E obj) {
    int index = obj.hashCode() % capacity;
    while (true) {
      if (status[index] == EMPTY)
        return false;
      if (status[index] == USED && table[index].equals(obj)) {
        table[index]  = null;
        status[index] = DELETED;
        size--;
        return true;
      }
      index = (index + 1) % capacity;
    }
  }
    
  /**
   * Clears all elements in this set.
   */
  public void clear() {
    for (int i=0; i < table.length; i++) {
      table[i]  = null;
      status[i] = EMPTY;
    }
    size = 0;
  }
  
  /**
   * Returns the size of this set.
   * @return
   */
  public int size() {
    return size;
  }

  /**
   * Returns true if this set is empty.
   * @return true if this set is empty.
   */
  public boolean isEmpty() {
    return size == 0;
  }
  
  /**
   * Returns an iterator over the elements in this set.  The elements
   * are returned in no particular order.
   *
   * @return an IntIterator over the elements in this set
   */
  public Iterator<E> iterator() {
    return new ClosedHashSetIterator();
  }  

  /**
   * Returns a string representation of this object.
   * @return a string representation of this object.
   */
  public String toString() {
    if (isEmpty())
      return "[]";
    StringBuilder str = new StringBuilder();
    str.append('[');
    Iterator<E> i = iterator();
    while (true) {
      str.append(i.next());
      if (!i.hasNext())
        return str.append(']').toString();
      str.append(' ');
    }    
  }

  /**
   * Inserts the specified object to this set without rehash.
   * @param num  the specified object to insert.
   * @return true if the object is added to this set.
   */
  private boolean insert(E obj) {
    int index = obj.hashCode() % capacity;
    while (true) {
      if (status[index] != USED)
        break;
      if (table[index].equals(obj))
        return false;
      index = (index + 1) % capacity;
    }
    table[index]  = obj;
    status[index] = USED;
    return true;    
  }
  
  /**
   * Rehash all the elements.
   */
  @SuppressWarnings("unchecked")
  private void rehash() {
    E[]   oldTable  = table;
    int[] oldStatus = status;
    if (capacity == 0)
      capacity = 7;
    else 
      capacity <<= 1;
    table  = (E[]) new Object[capacity];
    status = new int[capacity]; 
    if (oldTable != null)
      for (int i=0; i < oldTable.length; i++)   
        if (oldStatus[i] == USED) 
          insert(oldTable[i]);
  }    
  
  private class ClosedHashSetIterator implements Iterator<E> {

    public ClosedHashSetIterator() {
      if (table != null) {
        for (index=0; index < status.length; index++)
          if (status[index] == USED)
            break;
      }
    }
  
    public boolean hasNext() {
      if (table == null)
        return false;
      return index != table.length;
    }

    public E next() {
      E obj = table[index];
      prev = index;
      
      for (index++; index < status.length; index++)
        if (status[index] == USED)
          break;

      return obj;
    }
    
    public void remove() {
      assert(prev != -1); 
      table[prev]  = null;
      status[prev] = DELETED;
      size--;
    }
    
    private int index = 0;
    private int prev  = -1;
  }
  
  private final static int EMPTY    = 0;
  private final static int USED     = 1;
  private final static int DELETED  = 2;
  
  /** The list of buckets. */
  private E[] table = null;
  /** The list of status. */
  private int[] status = null;
  /** The number of buckets. */
  private int capacity = 0;
  
  /** The number of elements in this set. */
  private int size = 0;
  
}
