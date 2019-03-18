package ki;

import java.io.*;

public final class Help {


	public static String print(){
	
	
		StringBuffer str = new StringBuffer(1024);
		
		str.append("\t -h: displying the options\n ");
		str.append("\t -w: writing the generalization problem as the text file\n");
		str.append("\t     (see [ki/data/geneProblem.txt])\n");
		str.append("\t      ---->> this option may be useful in case that the SOLAR computation cost\n");
		str.append("\t             is relatively high\n");
		str.append("\t -l [num]: displaying the intermidiate process of CF computation for debagging,\n");
		str.append("\t           and writing it as the text file (see [ki/data/log.txt])\n");
		str.append("\t      ---->> [num] in {0, 1, 2, 3, 4, 5} \n");
		str.append("\t -d [num]: the search depth in SOLAR finding the (new) characteristic clauses\n");
		str.append("\t -wot: we do not allow to add any tautologies to the bridge theory\n");
		str.append("\t -tn [num]: the number of tautologies to be included in the bridge theory\n");
		str.append("\t      ---->> the tautologies whose number is [num] are randomly selected\n");
		str.append("\t -e: enumerating all the hypotheses\n");
		str.append("\t -n [num]: computing [num] hypotheses\n");
		str.append("\t -r [ratio]: the pruning parameter detemining if the refinement process stops\n");
		str.append("\t      ---->> setting a double number in [0-1] if any hypothesis cannot be found\n");
		str.append("\t -t [trials]: the trial number until the refinement process can be repeated\n");
		str.append("\t -cp [bias]: setting the coverage bias parameter in the evaluation function\n");
		str.append("\t -ip [bias]: setting the inconsistency bias parameter in the evaluation function\n");
		str.append("\t -vp [bias]: setting the paremeter for penelizing independent variables\n");
		str.append("\t -test [ratio]: performing a leave-one-out strategy.\n");
		str.append("\t             The program first randomly selects one test example as well as\n");
		str.append("\t             training examples whose ratio to all the examples is [ratio],\n");
		str.append("\t             next generates one hypothesis from the training examples, and\n");
		str.append("\t             then checks if the hypothesis can explain the test example\n");
		str.append("\t      ---->> please set [ratio] as some double number in the range [0-1].\n");
		str.append("\t             The output is of form #Ratio #A #B #C #D, where\n");
		str.append("\t             #A (resp. #B) is the dualization (resp. generalization) time [msec]\n");
		str.append("\t             #C is the predicate accuracy, and if -e option is used, \n");
		str.append("\t             #D is the average of accuracies among the hypotheses\n");
		str.append("\t -po: outputing the test and traning files in the Progol format.\n");
		str.append("\t      Please use this option together with -test option.\n");
		str.append("\t      The test and training file correspond to test.pl and animal.pl\n");
		str.append("\t      in the directory experiments/test.\n");
		str.append("\t -onInd: adding the condition that there is no independent variale\n");
		str.append("\t         for the terminating the refinement process\n");
		str.append("\t -version: displaying the version of this implementation\n");
		
			
		return str.toString();
	
	}



}