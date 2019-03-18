import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.StringTokenizer;

public class Main {

  /**
   * Returns the CPU time in milliseconds.
   * @return the CPU time in milliseconds.
   */
  public static long getCPUTime() {
    return (threadMxBean.getCurrentThreadCpuTime() / 1000000);
  }

  /**
   * @param args
   */
  public static void main(String[] args) {

    try {

      if (args.length == 0) {
        System.out.println("Usage: java Main [ALG] FILE");
        System.out.println(" -simple  simple reverse search algorithm.");
        System.out.println(" -uno1    practical fast algorithm [Uno, 02]. (default)");
        System.out.println(" -uno2    test.");
        System.exit(0);
      }

      int    alg  = ALG_UNO1;
      String file = null;
      for (int i=0; i < args.length; i++) {
        if (args[i].equals("-simple"))
          alg = ALG_SIMPLE;
        else if (args[i].equals("-uno1"))
          alg = ALG_UNO1;
        else if (args[i].equals("-uno2"))
          alg = ALG_UNO2;
        else if (!args[i].startsWith("-"))
          file = args[i];
        else {
          System.out.println("Error: unknown option " + args[i]);
          System.exit(-1);
        }          
      }
      
      // Outputs the command line arguments.
      System.out.print("Command: " + Main.class.getName());
      for (String arg : args) 
        System.out.print(" " + arg);
      System.out.println();

      // Which algorithm?
      
      
      // Open the input file.
      BufferedReader reader = new BufferedReader(new FileReader(file));

      // Read the input file. The format is as follows:
      //   1 2 3   <= { 1, 2, 3 }
      //   1 5     <= { 1, 5 }
      //   3 4 5   <= { 3, 4, 5 }
      IFamily<IntSet> family = new IFamily<IntSet>();
      
      String line = null;
      while ((line = reader.readLine()) != null) { 
        if (line.startsWith("c") || line.startsWith("p"))
          continue;
        IntSet set = new IntClosedHashSet();
        StringTokenizer st = new StringTokenizer(line);
        while (st.hasMoreTokens()) {
          String token = st.nextToken();
          int num = Integer.parseInt(token);
          if (num == 0)
            break;
          set.add(num);          
        }
        family.add(set);
      }
      
      System.out.println("[Input]");
      System.out.println(" num of sets = " + family.size());
      if (family.size() <= 10) {
        for (int i=0; i < family.size(); i++)
          System.out.println(" " + family.get(i));
      }
      
      MHSFinder finder = null;
      switch (alg) {
      case ALG_SIMPLE:
        finder = new Naive(family); 
        break;
      case ALG_UNO1:
        finder = new Uno(family, Uno.USE_CRITICAL_MAP);
        break;  
      case ALG_UNO2:
        finder = new Uno(family, Uno.USE_UNION_MAP);
        break;
      }

      // Main
      long start = getCPUTime();
      IFamily<HSet> mhss = finder.findAllMHS();
      long time = getCPUTime() - start;
      
      System.out.println("[Output]");
      System.out.println(" num of mhss = " + mhss.size());
      //if (family.size() <= 10000) {
	  for (int i=0; i < mhss.size(); i++)
		  System.out.println(" " + mhss.get(i));
      //}

      System.out.println();
      System.out.println("CPU time: " + (time / 1000.0) + "sec");
      
      // Validation
      start = getCPUTime();
      int errs = 0;
      System.out.println();
      for (int i=0; i < mhss.size(); i++) {
        HSet mhs = mhss.get(i);
        if (!mhs.isMHS(family)) {
          System.out.println("Error: " + mhs + " is not mhs!");
          errs++;
        }
      }
      time = getCPUTime() - start;
      System.out.println("Validation finished (" + errs + "errors, " + (time / 1000.0) + "sec).");
      
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private final static int ALG_SIMPLE = 0;
  private final static int ALG_UNO1 = 1;
  private final static int ALG_UNO2 = 2;

  
  /** For getting CPU time. */
  private static ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();

}
