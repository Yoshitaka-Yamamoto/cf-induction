/*
 * $Id$
 */

package ki;

import java.io.*;
import java.util.*;
import java.lang.Math;

public final class TestProblem {
	
	
	public TestProblem(ClauseSet test, ClauseSet background)
	{
		this.test = test;
		this.background = background;
	}
	
	public void add(LinkedList<Clause> hypothesis)
	{
		this.hypotheses.add(hypothesis);
	}
	
	public void write(LinkedList<Clause> hypothesis, int index)
	{
		StringBuffer str = new StringBuffer(1024);
		
		ClauseSet neg_test = this.test.dualization();
		
		if(neg_test != null){
			Iterator n_i = neg_test.iterator();
			while(n_i.hasNext()){
				str.append("cnf(an,top_clause,"+n_i.next() + ")." + "\n");
			}
		}
		Iterator b_i = this.background.iterator();
		while(b_i.hasNext()){
			str.append("cnf(an,axiom,"+b_i.next() + ")." + "\n");
		}
		
		if(hypothesis.size() != 0){
			Iterator h_i = hypothesis.iterator();
			while(h_i.hasNext()){
				str.append("cnf(an,axiom,"+h_i.next() + ")." + "\n");
			}
		}
		String line = str.toString();
		//System.out.println(line);
		
		try {
			FileWriter solInputStream = new FileWriter("ki/test/h_"+index+".txt");
			solInputStream.write(line);
			solInputStream.close();
		} catch(IOException e){}
	}
	
	public void output(ClauseSet obs)
	{
	
		this.output(obs, 1);
		this.output(this.test, 2);
		
	}
	
	public void output(ClauseSet set, int flag)
	{
	
		StringBuffer str = new StringBuffer(1024);
		
		if(set != null){
			Iterator set_i = set.iterator();
			while(set_i.hasNext()){
				String temp = (String)set_i.next().toString();
				//System.out.println(temp);
				temp = temp.replaceAll("\\[","");
				temp = temp.replaceAll("\\]",".");
				//System.out.println(temp);
				str.append(temp+"\n");
			}
		}
		
		String line = str.toString();
		//System.out.println(line);
		String fileName = "experiments/test/training.pl";
		if(flag == 2){
			fileName = "experiments/test/test.pl";
		}			
		try {
			FileWriter strInputStream = new FileWriter(fileName);
			strInputStream.write(line);
			strInputStream.close();
		} catch(IOException e){}
	}
	
	
  	public int getAccuracy()
	{
		return this.getAccuracy(-1);
	}	
	
	public int getAveAccuracy()
	{
		return this.average;
	}
	
	public int getAccuracy(int depth)
	{
		
		if(depth != -1){
			this.maxSearchDepth = depth;
		}
		
		int maxAccuracy = 0;
		int totalAverage = 0;
		int hypoIndex = 0;
		
		/***/
		
		
		while(true)
		{
			
			String fileName = "ki/test/h_"+hypoIndex+".txt";
			File testFile = new File(fileName);
			
			if(!testFile.exists()){
				//System.out.println("there is no file:" +fileName);
				break;
			}
			else{
				//System.out.println("there is a file:" +fileName);
				boolean isUnSAT = false;

				try{
					
					Process solProcess = Runtime.getRuntime().exec("java -jar jar/solar2-build138.jar ki/test/h_"+hypoIndex+".txt"+ " -df "+this.maxSearchDepth);
					//Process solProcess = Runtime.getRuntime().exec("java -jar jar/solar2-build138.jar ki/test/h.txt"+ "-df "+this.maxSearchDepth);
					BufferedReader inStreamSol = new BufferedReader( new InputStreamReader(solProcess.getInputStream() ));
					//BufferedReader errorStreamSol = new BufferedReader( new InputStreamReader(solProcess.getErrorStream() ));
					hypoIndex++;

					String line;
					while( (line = inStreamSol.readLine()) != null ){
						//System.out.println(line);
						if( line.equals("UNSATISFIABLE") == true ){
							isUnSAT = true;
						//	System.out.println("unsat");
							break;
						}
					}
				}catch(IOException e){System.out.println(e);}		
				
				
				if(isUnSAT){
					totalAverage += 100;
					//We could explain the test example
					maxAccuracy = 100;
				}
				testFile.delete();
				
			}
		}
		if(hypoIndex != 0){
			this.average = (int)totalAverage / hypoIndex;
		}
		return maxAccuracy;

	}
	
	private int average = 0;
	private ClauseSet test = null;
	private ClauseSet background = null;
 	private List<LinkedList<Clause>> hypotheses = new LinkedList<LinkedList<Clause>>();		
	int maxSearchDepth = 10;
	
}


