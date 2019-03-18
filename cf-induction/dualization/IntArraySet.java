import java.util.Random;

public class IntArraySet extends IntSet {

  /** 
   * Constructs a empty set.
   */
  public IntArraySet() {
    this(INITIAL_SIZE);
  }
  
  /** 
   * Constructs a set with the specified initial size.
   * @param size  the initial size of this set.
   */
  public IntArraySet(int size) {
    this.elements = new int[size];
    this.size = 0;
  }
  
  /**
   *  Constructs a new set containing the elements in the specified set.
   */
  public IntArraySet(IntArraySet set) {
    this(set.size());
    addAll(set);
  }
    
  /**
   * Returns true if the specified number is contained in this set.
   * @param num  the specified number to add.
   * @return true if this set contains the specified number.
   */
  public boolean contains(int num) {
    for (int i=0; i < size; i++)
      if (elements[i] == num)
        return true;
    return false;
  }
  
  /**
   * Adds the specified number to this set.
   * @param num  the specified number to add.
   * @return true if the number is added to this set.
   */
  public boolean add(int num) {
    if (contains(num))
      return false;
    if (elements.length == size) {
      int newsz = (size == 0) ? 1 : (size << 2);
      // MEMO for J2SE1.6
      //elements = Arrays.copyOf(elements, newsz);
      int[] old = elements;
      elements = new int[newsz];
      System.arraycopy(old, 0, elements, 0, old.length);
    }
    elements[size++] = num;
    return true;
  }
  
  /**
   * Returns the value at the specified index.
   * @param index the specified index.
   * @return the value at the specified index.
   */
  public int getAt(int index) {
    assert(index < size);
    return elements[index];
  }
    
  /**
   * Removes the object at the specified position.
   * @param index the specified position to be removed.
   */
  public int removeAt(int index) {
    assert(index < size);
    int num = elements[index];
    elements[index] = elements[--size];
    return num;
  }
    
  /**
   * Removes the last elements.
   */
  public int removeLast() {
    assert(size > 0);
    return elements[--size];
  }
  
  /**
   * Removes the specified number.
   * @param num  the specified number to be removed.
   * @return true if this set contains the specified number.
   */
  public boolean remove(int num) {
    for (int i=0; i < size; i++) {
      if (elements[i] == num) {
        elements[i] = elements[--size];
        return true;
      }
    }
    return false;
  }
    
  /**
   * Clears all elements in this set.
   */
  public void clear() {
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
    return new IntArraySetIterator();
  }  
  
  private class IntArraySetIterator implements IntIterator {

    public IntArraySetIterator() {
      index = 0;
    }
    
    public boolean hasNext() {
      return index < size();
    }

    public int next() {
      return getAt(index++);
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
    IntSet set = new IntArraySet();
    
    System.out.println("Adding " + num + " random numbers to the set.");
    long time = System.currentTimeMillis();
    for (int i=0; i < num; i++) 
      set.add(rand.nextInt(num) - (num / 2));
    System.out.println("CPU time: " + ((System.currentTimeMillis() - time) / 1000.0) + "s");

  }

  /** The initial default size of a set. */
  private final static int INITIAL_SIZE = 1;
  /** The list of elements in this set. */
  private int[] elements = null;
  /** The number of elements in this set. */
  private int size = 0;
  
}

