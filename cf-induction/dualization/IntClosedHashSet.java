import java.util.Random;


public class IntClosedHashSet extends IntSet {

  /**
   * Constructs a empty set.
   */
  public IntClosedHashSet() {    
  }
  
  /**
   *  Constructs a new set containing the elements in the specified set.
   */
  public IntClosedHashSet(IntClosedHashSet set) {
    addAll(set);
  }
  
  /**
   * Returns true if the specified number is contained in this set.
   * @param num  the specified number to add.
   * @return true if this set contains the specified number.
   */
  public boolean contains(int num) {
    int index = hash(num) << 1;
    while (true) {
      int status = table[index];
      if (status == EMPTY)
        return false;
      if (status == USED && table[index+1] == num)
        return true;
      index = (index + 2) % (capacity << 1);
    }
  }  
  
  /**
   * Adds the specified number to this set.
   * @param num  the specified number to add.
   * @return true if the number is added to this set.
   */
  public boolean add(int num) {
    if (size + 1 > capacity / 2)
      rehash();
    if (insert(num)) {
      size++;
      return true;
    }
    return false;
  }

  /**
   * Removes the specified number.
   * @param num the specified number to be removed.
   * @return true if this set contains the specified number.
   */
  public boolean remove(int num) {
    int index = hash(num) << 1;
    while (true) {
      int status = table[index];
      if (status == EMPTY)
        return false;
      if (status == USED && table[index+1] == num) {
        table[index] = DELETED;
        size--;
        return true;
      }
      index = (index + 2) % (capacity << 1);
    }
  }
    
  /**
   * Clears all elements in this set.
   */
  public void clear() {
    for (int i=0; i < table.length; i+=2)
      table[i] = EMPTY;
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
  public IntIterator iterator() {
    return new IntClosedHashSetIterator();
  }  
  
  /**
   * Inserts the specified number to this set without rehash.
   * @param num  the specified number to insert.
   * @return true if the number is added to this set.
   */
  private boolean insert(int num) {
    int index = hash(num) << 1;
    while (true) {
      int status = table[index];
      if (status != USED)
        break;
      if (table[index+1] == num)
        return false;
      index = (index + 2) % (capacity << 1);
    }
    table[index  ] = USED;
    table[index+1] = num;
    return true;    
  }
  
  /**
   * Rehash all the elements.
   */
  private void rehash() {
    int[] old = table;
    if (capacity == 0)
      capacity = 7;
    else 
      capacity <<= 1;
    table = new int[capacity << 1];
    if (old != null)
      for (int i=0; i < old.length; i+=2)  
        if (old[i] == USED) 
          insert(old[i+1]);
  }    
  
  /**
   * Return the hash code of the specified number.
   * @param num   the specified number.
   * @return the hash code of the specified number.
   */
  private int hash(int num) {
    if (num > 0)
      return (31 * num) % capacity;
    return (13 * -num) % capacity;
  }
  
  private class IntClosedHashSetIterator implements IntIterator {

    public IntClosedHashSetIterator() {
      if (table != null) {
        for (index=0; index < table.length; index+=2)
          if (table[index] == USED)
            break;
      }
    }
    
    public boolean hasNext() {
      if (table == null)
        return false;
      return index != table.length;
    }

    public int next() {
      int num = table[++index];
      
      for (index++; index < table.length; index+=2)
        if (table[index] == USED)
          break;

      return num;
    }
    
    private int index = 0;
  }

  /**
   * Main method for test.
   */
  public static void main(String[] args) {
    
    int num = 100000;
    if (args.length == 1)
      num = Integer.parseInt(args[0]);
      
    Random rand = new Random();
    IntClosedHashSet set  = new IntClosedHashSet();
    
    System.out.println("Adding " + num + " random numbers to the set.");
    long time = System.currentTimeMillis();
    for (int i=0; i < num; i++) 
      set.add(rand.nextInt(num) - (num / 2));
    System.out.println("CPU time: " + ((System.currentTimeMillis() - time) / 1000.0) + "s");

//    System.out.println(set);
    
//    IntIterator i = set.iterator();
//    while (i.hasNext())
//      System.out.println(i.next());
  }

  private final static int EMPTY    = 0;
  private final static int USED     = 1;
  private final static int DELETED  = 2;
  
  /** The list of buckets. */
  private int[] table = null;
  /** The number of buckets. */
  private int capacity = 0;
  
  /** The number of elements in this set. */
  private int size = 0;

}
