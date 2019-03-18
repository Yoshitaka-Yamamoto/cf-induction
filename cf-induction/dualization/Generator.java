import java.util.Random;


public class Generator {

  /**
   * @param args
   */
  public static void main(String[] args) {

    if (args.length != 3) {
      System.out.println("Usage: java Generator PROB E F");
      System.out.println(" PROB  the occurence probability of each number in each set.");
      System.out.println(" E     the kinds of elements.");
      System.out.println(" F     the number of sets.");
      System.exit(0);
    }
    
    double prob  = Double.parseDouble(args[0]);
    int    kinds = Integer.parseInt(args[1]);
    int    sets  = Integer.parseInt(args[2]);

    System.out.print("c Cmd: " + Generator.class.getName());
    for (String arg : args) 
      System.out.print(" " + arg);
    System.out.println();
    System.out.println("c Prob=" + prob + ", Kinds=" + kinds + ", Sets" + sets);
    
    Random rand = new Random(System.currentTimeMillis());
    
    for (int i=0; i < sets; i++) {
      HSet set = new HSet();
      for (int j=1; j <= kinds; j++) 
        set.add(j);
      for (int j=0; j < kinds * prob; j++) {
        int index = rand.nextInt(set.size());
        int n = set.removeAt(index);
        System.out.print(n + " ");
      }
      System.out.println("0");
    }
  }

}
