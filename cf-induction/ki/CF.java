/*
 * $Id$
 */

package ki;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Random;

public final class CF implements ClauseKind{
	
	public CF(CFProblem cfProblem)
	{
		this.cfProblem = cfProblem;
	}
	
    public void compute()
    {
		
		CFProblem cfp = this.cfProblem;
		
		if(this.debag > 0){
			System.out.println(cfp);
			//this.log.append(cfp.toString(debag)+"\n");
		}
		
		try{
			
			ClauseSet bTheoryPos = new ClauseSet();
			ClauseSet bTheoryNeg = new ClauseSet();
			
			//Step 1. Compute the CNF of the negation of observations
			
			List<Mode> modes = cfp.getInputModes();
			Type type = cfp.getInputTypes();
			
			this.indField = cfp.getInductionField();
			
			ClauseSet obs_org = cfp.getObservations();
			ClauseSet test = null;
			ClauseSet obs = null;
			TestProblem tp = null;
			
			if(this.testRate != 0){
				test = new ClauseSet();
				obs = new ClauseSet();
				//We try a leanve-one out method 
				Random tRand = new Random(System.currentTimeMillis());
				int testIndex = tRand.nextInt(obs_org.size());
				Clause testC = (Clause)obs_org.get(testIndex);
				test.add(testC);
				obs_org.remove(testC);
				
				int obs_num = (int)(this.testRate*obs_org.size());
				for(int i = 0; i < obs_num; i++){
					int obsIndex = tRand.nextInt(obs_org.size());
					Clause cIndex = (Clause)obs_org.get(obsIndex);
					obs.add(cIndex);
					obs_org.remove(cIndex);
				}
				//System.out.println("test: "+ test);
				//System.out.println("obs: "+obs);
				//Creating the test problem
				tp = new TestProblem(test, cfp.getBackground());
				if(this.po){
					//Writing the test and traning sets in the Progol format
					tp.output(obs);
				}
			}
			
			else{
				obs = obs_org;
			}
			
			if(this.testRate == 0){
				System.out.println(">> Dualizaing observations");
			}
			
			ClauseSet neg_obs = obs.dualization();
			
			if(this.debag > 0){
				this.log.append("-O:\n"+neg_obs+"\n");
				System.out.println("-O:\n"+neg_obs);
			}
			
			ClauseSet background = cfp.getBackground();
			
			//	SkolemTable skTable = SkolemTable.getSkolemTable();
			//	System.out.println("skTable: "+skTable );			
			
			
			//Step 2. Constructing the bridge theory bTheoryPos
			
			/******** In case of CF-induction (that SOLAR is used) ***********/
			if(wos == -1){
				if(this.testRate == 0){
					System.out.println(">> Computing the new characterisitc clauses ");
				}
				//System.out.println("Length ="+indField.getMaxLength()+". Size="+indField.getMaxSize());
				
				SOLARProblem slp = new SOLARProblem(cfp.getBackground(), neg_obs, this.indField, this.maxSearchDepth);
				
				//Compute the new characteristic clauses of B and -O by SOLAR
				List newcarc = slp.getConclusions();
				if(newcarc != null){
					if(this.debag > 0){
						this.log.append("**** Newcarc ****"+"\n");
						System.out.println("**** Newcarc ****");
					}
					for(int nId = 0; nId < newcarc.size(); nId++){
						if(this.debag > 0){
							this.log.append(newcarc.get(nId)+"\n");
							System.out.println(newcarc.get(nId));
						}
						bTheoryPos.add((Clause)newcarc.get(nId));
					}
				}
				else{
					if(this.testRate == 0){
						System.out.println(" > There is no new characterisitic clause");
					}
					bTheoryPos = neg_obs;
				}
				
				List carc = background.get();
				
				if(this.testRate == 0){
					System.out.println(">> Computing the characteristic clauses");
				}
				if(background.size() != 0){
					//System.out.println("B:\n"+background);
					SOLARProblem slp2 = new SOLARProblem(cfp.getBackground(), null, this.indField, this.maxSearchDepth);
					carc = slp2.getConclusions();
				}
				if(carc != null){
					if(this.debag > 0){
						this.log.append("**** Carc ****\n");
						System.out.println("**** Carc ****");
					}
					for(int nId = 0; nId < carc.size(); nId++){
						if(this.debag > 0){
							this.log.append(carc.get(nId)+"\n");
							System.out.println(carc.get(nId));
						}
						bTheoryPos.add((Clause)carc.get(nId));
						bTheoryNeg.add((Clause)carc.get(nId));
					}
				}
				else{
					if(this.testRate == 0){
						System.out.println(" > There is no characterisitic clause");
					}
				}
			}
			/********** End of the case of CF-induction **********/
			
			/******** In case that SOLAR is not used ***********/
			else{
				
				CCcreate cc = new CCcreate(neg_obs, background, modes, type, this.indField);
				bTheoryPos = cc.getBridgeTheory(this.wos);
				for( int bPos = 0; bPos < bTheoryPos.size(); bPos++ )
				{
					System.out.println(bPos+": "+bTheoryPos.get(bPos));
				}
				
			}
			
			
			
			//Step 3. Computing the tautological clauses associated with the induction field.
			
			List<Clause> taut = indField.getTautClause();
			if(this.debag > 0){
				this.log.append("**** Taut(I_H) ****\n");
				System.out.println("**** Taut(I_H) ****");
			}
			if(this.tautNum > 0){
				//We randomly select [tautNum] tautologies 
				Random rand = new Random(System.currentTimeMillis());
				for(int nId = 0; nId < tautNum; nId++)
				{
					if(taut.size() == 0){
						break;
					}
					int id = rand.nextInt(taut.size());
					Clause taut_id = (Clause)taut.get(id);
					taut.remove(id);
					
					if(this.debag > 0){
						this.log.append(taut_id+"\n");
						System.out.println(taut_id);
					}
					
					bTheoryPos.addBySubsumed(taut_id);
					bTheoryNeg.addBySubsumed(taut_id);
				}
			}
			else if(this.tautNum == -1){
				//We add all the tautologies
				for(int nId = 0; nId < taut.size(); nId++)
				{
					Clause taut_id = (Clause)taut.get(nId);
					if(this.debag > 0){
						this.log.append(taut_id+"\n");
						System.out.println(taut_id);
					}					
					bTheoryPos.addBySubsumed(taut_id);
					bTheoryNeg.addBySubsumed(taut_id);
				}
				
			}
			
			//Step 4. Computing the positive and negative bottom theories wrt induction field.
			
			long time3 = System.currentTimeMillis();
			if(this.testRate == 0){
				System.out.println(">> Computing the positive bottom theory");
			}
			ClauseSet bottom_p = bTheoryPos.dualization();
			if(bottom_p.size() == 0){
				if(this.testRate == 0){
					System.out.println(">>> Dualization may be failed");
				}
			}
			
			if(this.debag > 0){
				this.log.append("Num of clauses in bt_pos>>>"+bottom_p.size()+"\n");
				System.out.println("Num of clauses in bt_pos>>>"+bottom_p.size());
				this.log.append("*** BOTTOM (POSTIVE) num of literals: "+bottom_p.getLitNum()+"  ***\n");
				System.out.println("Num of clauses in bt_pos>>>"+bottom_p.size());
				if( (this.debag == 2) || (this.debag == 1 && bottom_p != null && bottom_p.size() <= 20) ){
					
					for(int inst_i = 0; inst_i < bottom_p.size(); inst_i++){
						this.log.append(bottom_p.get(inst_i) +"\n");
						System.out.println(bottom_p.get(inst_i));
					}
				}
				
			}
			//GeneProblem geneProblem3 = new GeneProblem(3, bottom_p, indField);
			if(this.testRate == 0){
				System.out.println(">> Computing the negative bottom theory");
			}
			ClauseSet bottom_n = bTheoryNeg.dualization();
			
			if(this.debag > 0){
				this.log.append("Num of clauses in bt_neg>>>"+bottom_n.size()+"\n");
				System.out.println("Num of clauses in bt_neg>>>"+bottom_n.size());
				this.log.append("*** BOTTOM (NEGATIVE) num of literals: "+bottom_n.getLitNum()+"  ***\n");
				System.out.println("*** BOTTOM (NEGATIVE) num of literals: "+bottom_n.getLitNum()+"  ***");
				if( (this.debag == 2) || ( this.debag == 1 && bottom_n != null && bottom_n.size() <= 20) ){
					
					for(int inst_i = 0; inst_i < bottom_n.size(); inst_i++){
						this.log.append(bottom_n.get(inst_i) + "\n");
						System.out.println(bottom_n.get(inst_i));
					}
					
				}
				
			}
			//GeneProblem geneProblem4 = new GeneProblem(4, bottom_n, indField);
			
			long time4 = System.currentTimeMillis() -time3;
			if(this.testRate == 0){
				System.out.println("   Executing time (step: computing two bottom theories): "+time4+" [msec]" );
			}
			//System.out.println("hypo num: "+hypoNum);
			
			List<Clause> curPosBotTheory = bottom_p.getClauses();
			List<Clause> curNegBotTheory = bottom_n.getClauses();
			//From here, the generalization stage starts
			
			long time5 = System.currentTimeMillis();
			
			Generalization gene = new Generalization(curPosBotTheory, curNegBotTheory, modes, type, param, debag);
			
			gene.setParam(indField.getMinSize(), indField.getMaxSize(), indField.getMinLength(), indField.getMaxLength(), this.cBias, this.iBias, this.trials, this.constTerm, this.onInd, this.vBias);
			
            if(this.testRate != 0){
				//test mode
				gene.setTestProblem(tp);
			}
			
            if(write){
                
                System.out.println(">> Writing the generalization problem ");
                gene.write();
                return;
				
            }
			if(this.testRate == 0){
				System.out.println(">> Computing the hypotheses");
            }
			//Set the current bottom clause as the first clause in bottom_p
			gene.start(hypoNum);
			
			time5 = System.currentTimeMillis() - time5;
			
			
			if(this.debag == 0){
				System.out.println("   Size of bridge theory: "+bTheoryPos.size()+", Size of bottom theory: "+bottom_p.size() );
			}
			
			if(this.debag > 0){
				System.out.println(this.log.toString());
				FileWriter logStream = new FileWriter("ki/data/log.txt");
				logStream.write(this.log.toString());
				logStream.close(); 
			}

			if(this.testRate == 0){
				System.out.println("   Executing time (step: generalization): "+time5+" [msec]" );
			}
			
			
            if(this.testRate != 0){
				int accuracy = tp.getAccuracy();
				int average = tp.getAveAccuracy();
				System.out.println(this.testRate+","+ time4+","+time5+","+accuracy+","+average);
			}
			
			
		}catch (Exception e1) { e1.printStackTrace(); }
	}
	
	/* 
	 * main function
	 */
	public static void main(String[] args) 
	{
		try {
			long time1 = System.currentTimeMillis();
			ArrayList<String> problemFiles = new ArrayList<String>();
			
			//Analysis of options
			
			boolean printMoreHelp = false;
			boolean printVersion  = false; 
			
			int i = 0;
			while (i < args.length) {
				//System.out.println("args:"+args[i]);
				if ( !args[i].startsWith("-") ) {
					problemFiles.add(args[i++]);
					continue;
				}
				
				if ( args[i].equals("-h") || args[i].equals("-help") ) {
					problemFiles = null;
					break;
				}
				
				if ( args[i].equals("-d") || args[i].equals("-depth") ) {
					if(i < args.length-1){
						//System.out.println("args++:"+args[i+1]);
						maxSearchDepth = Integer.parseInt(args[i+1]);
						i++;
					}
				}
				
				if ( args[i].equals("-test") ) {
					if(i < args.length-1){
						//System.out.println("args++:"+args[i+1]);
						testRate = Double.parseDouble(args[i+1]);
						i++;
					}
				}
				
				if ( args[i].equals("-onInd") ) {
					onInd = true;
				}
				
				if ( args[i].equals("-n") || args[i].equals("-num") ) {
					if(i < args.length-1){
						//System.out.println("args++:"+args[i+1]);
						hypoNum = Integer.parseInt(args[i+1]);
						i++;
					}
				}
				
				if ( args[i].equals("-e") || args[i].equals("-enum") ) {
					//System.out.println("args++:"+args[i+1]);
					hypoNum = -1;
				}
                
                if ( args[i].equals("-w") || args[i].equals("-write") ) {
                    //System.out.println("args++:"+args[i+1]);
                    write = true;
				}
				
				if (args[i].equals("-progol_out") || args[i].equals("-po")) {
					po = true;
				}
				
				if ( args[i].equals("-r") || args[i].equals("-rate") ) {
					//System.out.println("args++:"+args[i+1]);
					if(i < args.length-1){
						//System.out.println("args++:"+args[i+1]);
						param = Double.parseDouble(args[i+1]);
						i++;
					}
				}
				
				if ( args[i].equals("-t") || args[i].equals("-trial") ) {
					//System.out.println("args++:"+args[i+1]);
					if(i < args.length-1){
						//System.out.println("args++:"+args[i+1]);
						trials = Integer.parseInt(args[i+1]);
						constTerm = true;
						i++;
					}
				}
				
				if ( args[i].equals("-tn") || args[i].equals("-tautNum") ) {
					//System.out.println("args++:"+args[i+1]);
					if(i < args.length-1){
						//System.out.println("args++:"+args[i+1]);
						tautNum = Integer.parseInt(args[i+1]);
						i++;
					}
				}				
				
				if ( args[i].equals("-cp") || args[i].equals("-coverParam") ) {
					//System.out.println("args++:"+args[i+1]);
					if(i < args.length-1){
						//System.out.println("args++:"+args[i+1]);
						cBias = Double.parseDouble(args[i+1]);
						i++;
					}
				}
				
				if ( args[i].equals("-ip") || args[i].equals("-inconsistParam") ) {
					//System.out.println("args++:"+args[i+1]);
					if(i < args.length-1){
						//System.out.println("args++:"+args[i+1]);
						iBias = Double.parseDouble(args[i+1]);
						i++;
					}
				}
				
				
				if ( args[i].equals("-vp") || args[i].equals("-variableParam") ) {
					//System.out.println("args++:"+args[i+1]);
					if(i < args.length-1){
						//System.out.println("args++:"+args[i+1]);
						vBias = Double.parseDouble(args[i+1]);
						i++;
					}
				}
				
				
				if ( args[i].equals("-wos") || args[i].equals("-wosol") ) {
					//System.out.println("args++:"+args[i+1]);
					if(i < args.length-1){
						//System.out.println("args++:"+args[i+1]);
						wos = Double.parseDouble(args[i+1]);
						i++;
					}
				}
				
				if ( args[i].equals("-l") || args[i].equals("-log") ) {
					//System.out.println("args++:"+args[i+1]);
					if(i < args.length-1){
						//System.out.println("args++:"+args[i+1]);
						debag = Integer.parseInt(args[i+1]);
						i++;
					}
				}
				
				
				if ( args[i].equals("-version") ) {
					problemFiles = null;
					printVersion = true;
					break;
				}
				
				i++;
			}
			
			CFProblem cfProblem = null;
			
			if (problemFiles != null && !problemFiles.isEmpty())
				cfProblem = CFProblem.create(problemFiles);
			
			if ( cfProblem != null ) {
				
				CF cf = new CF(cfProblem);
				
				time1 = System.currentTimeMillis()-time1;
				if(testRate == 0){
					System.out.println("   Executing time (step: loading the problem) :" + time1 + "[msec]");
				}
				if(debag == 0){
					log = new StringBuffer(500);
				}
				else if(debag == 1){
					log = new StringBuffer(1000);
				}
				else if(debag > 1){
					log = new StringBuffer(10000);
				}
				
				long time2 = System.currentTimeMillis();
				cf.compute();
				time2 = System.currentTimeMillis() - time2;
				if(testRate == 0){
					System.out.println("   Executing time (total): " + time2 + " [msec]");
				}
			}
			else {
				System.err.println(version);
				if (!printVersion){
					System.err.println("Usage: java ki.CF [options] file ");
					System.out.println(Help.print());
				}
			}
		} catch (Exception exp) {exp.printStackTrace();}
	}
	
	
	public static String readLine()
	{
		int ch;
		String r = "";
		boolean done = false;
		while (!done) {
			try {
				ch = System.in.read();
				if (ch < 0 || (char)ch == '\n')
					done = true;
				else if ((char)ch != '\r') // weird--it used to do \r\n translation
					r = r + (char) ch;
			}
			catch(java.io.IOException e) {
				done = true;
			}
		}
		return r;
	}
	
	/** Problem of CF-induction */
	private CFProblem cfProblem = null;
	/** The set of inputclauses */
	private List inputclauses = null;
	/** Induction field */
	private InductionField indField = null;
	/** Mode declarations */
	private List<Mode> modes = null;
	/** Type */
	private Type type = null;
	/** Observations */
	private ClauseSet observations = null;
	/** Background theory */
	private ClauseSet background = null;
	/** Hypothesis */
	private ClauseSet hypothesis = null;
	/** Search Depth */
	private static int maxSearchDepth = 0;
	/** Hypotheses number */
	private static int hypoNum = 1;	
	/** Discount rate */
	private static double param = 1; 
	/** Coverage bias parameter in Evaluation Function */
	private static double cBias = 10;
	/** Inconsistency bias parameter in Evaluation Function */
	private static double iBias = 1;
	/** Trials in each refinement step*/
	private static int trials = -1;
	/** Termination condition */
	private static boolean constTerm = false;
	/** Debagging type */
	private static int debag = -1; 
	/** Debagging log */
	private static StringBuffer log = null;
    /** Debagging flag for writing the current generalization problem */
    private static boolean write = false;
	/** Outputing Test and Traing sets in the Progol form */
	private static boolean po = false;
	/** Randomly selecting some instances whose ratio to all the instances 
	 * from B and -E to be incuded in the bridge theory 
	 * in this option, we do not use SOLAR 
	 **/
	private static double wos = -1;
	/** Without tautologies **/
	private static int tautNum = -1;
	/** Ration of the num. of examples for testing wrt the num of the whole examples*/
	private static double testRate = 0;
	/** Strings of the current version */
	private static boolean onInd = false;
	/** Bias for peneralizing independent parameters */
	private static double vBias = 1;
	
	public static final String version = "CF-induction version 0.45 (2013/3/14)";
}










