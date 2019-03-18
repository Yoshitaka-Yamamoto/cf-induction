/*
* $Id$
 */

package ki;

import java.io.*;
import java.util.*;
import java.lang.Math;

public final class SOLARProblem {
	

	public SOLARProblem(ClauseSet background, ClauseSet neg_obs, InductionField indField, int maxSearchDepth){
		
		this.indField = indField;
		if(maxSearchDepth != 0)
			this.maxSearchDepth = maxSearchDepth;
		
		StringBuffer str = new StringBuffer(1024);
		if(neg_obs != null){
			Iterator n_i = neg_obs.iterator();
			while(n_i.hasNext()){
				str.append("cnf(an,top_clause,"+n_i.next() + ")." + "\n");
			}
		}
		Iterator b_i = background.iterator();
		while(b_i.hasNext()){
			str.append("cnf(an,axiom,"+b_i.next() + ")." + "\n");
		}

		String prodString = this.indField.translateProd();
		
		//System.out.println(prodString);
		
		str.append(prodString);
		
		String line = str.toString();
		//System.out.println(line);
		try {
			FileWriter solInputStream = new FileWriter("ki/data/solInput.txt");
			solInputStream.write(line);
			solInputStream.close();
		}catch(IOException e){}
	}
	
  	
	public List getConclusions(){
	
		/***/
		Process solProcess = null;
		BufferedReader inStreamSol = null;
		
		//int num=0;
		if( (this.indField.getMaxLength() != -1) && (this.indField.getMaxSize() != -1)){
			double length = indField.getMaxLength();
			double size = indField.getMaxSize();
			double copy = indField.getMaxCopy();
			
			//if(copy == -1)
			//	num = (int)Math.pow(length, size);
			//else
			//	num = (int)Math.pow(length, size*copy);
			//System.out.println("Length ="+length+". Size="+size+". Copy="+copy+". Num="+num);
		}
		boolean hasConclusion = false;

		try{
			//if(num != 0 ){
				//System.out.println("java -jar jar/solar2-build138.jar ki/data/solInput.txt -dfidr "+this.maxSearchDepth+" -num "+num);
				//solProcess = Runtime.getRuntime().exec("java -jar jar/solar2-build138.jar ki/data/solInput.txt -dfidr "+this.maxSearchDepth+" -num "+num);
			//}
			//else{
				//System.out.println("java -jar jar/solar2-build138.jar ki/data/solInput.txt -df "+this.maxSearchDepth);
			solProcess = Runtime.getRuntime().exec("java -jar jar/solar2-build138.jar ki/data/solInput.txt -df "+this.maxSearchDepth);
			//}
			inStreamSol = new BufferedReader(new InputStreamReader(solProcess.getInputStream() ));
			
			StringBuffer str = new StringBuffer(1024);
			String line;
			String[] arrayline;
			while( (line = inStreamSol.readLine()) != null ){
				//System.out.println(line);
				if( (line.startsWith("[") == true) && (line.equals("[-$]") != true) ){
					
					hasConclusion = true;
					//String lineVar = line.replaceAll("_", "X_");
					str.append("cnf(an, carc, "+line +")."+ "\n");
					//System.out.println(lineVar);
				}
				else if(  (line.equals("NO CONSEQUENCES") ) || (line.equals("NOT FOUND"))  ){
					//System.out.println("No consequense");
					hasConclusion = false;
					break;
				}
			}
			
			line = str.toString();
			FileWriter solOutputStream = new FileWriter("ki/data/clsInput.txt");
			solOutputStream.write(line);
			solOutputStream.close();
		}catch(IOException e){}
		
		List list = null;
		if(hasConclusion){
			CFProblem solarOutput = CFProblem.create("ki/data/clsInput.txt");
			list = solarOutput.getInputClauses();
		}
		return list;
	}
			
	private InductionField indField = null;
 	private int maxSearchDepth = 5;		
}


