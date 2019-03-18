/*
 * $Id$
 */

package ki;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import parser.CFParser;


public final class CFProblem {
	
	
	public CFProblem(String name, List inputClauses, List<Mode> modes, InductionField inductionField, Type type)
	{
		
		// Parameters
		this.name              = name;
		this.inputClauses      = inputClauses;
		this.modes             = modes;
		this.type              = type;
		
		if(inductionField == null){
			this.indField.update(modes, type);
		}
		else 
		{
			ArrayList predicates = inductionField.getPredicates();
			if(predicates.size() == 0){
				this.indField.update(modes, type, inductionField);
				//this.indField.update(modes, type);
				//System.out.println("predicates 0: "+this.indField.toString(2));

			}
			else {
				//System.out.println("predicates "+predicates.size());
				//this.indField.update(predicates);
				this.indField = inductionField;
			}
		}
		
		// Extracting observations, background theory and target hypothesis 
		
		for (int i = 0; i < inputClauses.size(); i++){
			
			Clause cl = new Clause();
			cl = (Clause)inputClauses.get(i);
			
			this.conTable.put(cl);	
			if(cl.getKind() == ClauseKind.OBSERVATIONS){
				obs.add(cl);
			}
			else if (cl.getKind() == ClauseKind.BACKGROUND){
				bg.add(cl);
			}
			else if (cl.getKind() == ClauseKind.HYPOTHESIS){
				hypo.add(cl);
			}
			else if (cl.getKind() == ClauseKind.POSITIVE){
				bottom_p.add(cl);
			}
			else if (cl.getKind() == ClauseKind.NEGATIVE){
				bottom_n.add(cl);
			}
		}
	}
	
	
   /**
	* Constructing the problem on hypothesis finding
	* @param fileName
	* @return the problem on hypothesis finding
	**/
	
	public static CFProblem create(String fileName) 
	{
		return CFParser.parse(fileName);
	}
	
	/**
	 * Constructing the list of problems on hypothesis finding
	 * @param fileNames
	 * @return the problem on hypothesis finding
	 */
	
	public static CFProblem create(List fileNames) 
	{
		return CFParser.parse(fileNames);
	}
	
	/**
	 * Getting the name
	 * @return the name of the problem
	 */
	
	public String getName()
	{
		return name;
	}
	
	/**
	 * Getting the top clause
	 * @return the name of the top clause
	 */
	
	public Clause getTopClause()
	{
		return topClause;
	}
	
	/**
	 * Getting observations
	 * @return observation
	 */
	
	public ClauseSet getObservations()
	{
		return obs;
	}
	
	/**
	 * Getting the background theory
	 * @return the background theory
	 */
	
	public ClauseSet getBackground()
	{
		return bg;
	}
	
	public ClauseSet getHypothesis()
	{
		return hypo;
	}
	
	public ClauseSet getPosBottom()
	{
		return bottom_p;
	}
	
	public ClauseSet getNegBottom()
	{
		return bottom_n;
	}
	
	/**
	 * Getting the input clauses
	 * @return the set of input clauses
	 */
	
	public List getInputClauses()
	{
		return inputClauses;
	}
	
	/** 
	 * Getting the induction field
	 * @return the induction field
	 */
	public InductionField getInductionField()
	{
		return indField;	
	}
	public List<Mode> getInputModes()
	{
		return modes;
	}
	
	public Type getInputTypes()
	{	  
		return type;  
	}
	
	public ConstantTable getConstantTable()
	{
		return conTable;
	}
	
	public String toStringRefine()
	{
		
		StringBuffer str = new StringBuffer(1024);
		str.append("======================================\n");
		
		str.append("Problem Name:\n");  
		str.append("Postive bottom theory:\n");
		Iterator i = bottom_p.iterator();
		while (i.hasNext())
			str.append("  " + i.next() + "\n");
		str.append("Negative bottom theory:\n");
		
		Iterator j = bottom_n.iterator();
		while (j.hasNext())
			str.append("  " + j.next() + "\n");
		str.append("Mode declarations:\n");
		str.append("  " + modes + "\n");
		str.append("======================================\n");
		
		return str.toString();
		
	}  
  	
	public String toString(){
		return this.toString(0);
	}
	
	/**
	 * return the strings representing this object
	 * @return displaying the strings
	 */
	public String toString(int debag) 
	{
		StringBuffer str = new StringBuffer(1024);
		
		str.append("======================================\n");
		
		str.append("Problem Name:\n");
		str.append("  " + name + "\n");
		
		
		str.append("Observations:\n");
		Iterator j = obs.iterator();
		while (j.hasNext())
			str.append("  " + j.next() + "\n");
		
		
		str.append("Backgroud theory:\n");
		Iterator k = bg.iterator();
		while (k.hasNext())
			str.append("  " + k.next() + "\n");
		
		str.append("Target hypothesis:\n");
		Iterator kk = hypo.iterator();
		while (kk.hasNext())
			str.append("  " + kk.next() + "\n");
		
		str.append("Mode declarations:\n");
		Iterator m = modes.iterator();
		while (m.hasNext())
			str.append("  " + m.next() + "\n");
		
		str.append("Type:\n");
		str.append("  " + type + "\n");
		
		//if(indField != null){
		str.append("Induction field: \n");
		str.append("  " + indField.toString(debag) + "\n");
		//}
		str.append("======================================\n");
		
		return str.toString();
	}
	
	
	
	static public void main(String[] args) 
	{
	}
	
	/** The name of the problem */
	private String name = null;
	/** The top clause */
	private Clause topClause = null;
	/** The set of input clauses */
	private List inputClauses = null;
	/** Observations */
	private ClauseSet obs = new ClauseSet();
	/** The background theory */
	private ClauseSet bg = new ClauseSet();
	/** Hypothesis */
	private ClauseSet hypo = new ClauseSet();
	/** The mode declarations */
	private List<Mode> modes = null;
	/** Type */	
	private Type type = null;
	/** Indution field */
	private InductionField indField = new InductionField();	
	/** Constant table */
	private ConstantTable conTable = new ConstantTable();
	/** Positive bottom theory */
	private ClauseSet bottom_p = new ClauseSet();
	/** Negative bottom theory */
	private ClauseSet bottom_n = new ClauseSet();	
	
	
	
}


