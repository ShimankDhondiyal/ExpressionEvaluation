package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
	
	/**
	 * replace spaces in expression
	 * @param expr	expression parameter
	 * @return		edited expression
	 */
	private static String replace(String expr) {
		expr = expr.replace(" ", "");
		expr = expr.trim();
		return expr;
	}
	
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	
    	//remove all whitespace from expr
    	expr = replace(expr);	// = expr = expr.replace("//s+","") doesn't work
    	
    	//create objects
    	StringTokenizer stringToken = new StringTokenizer(expr, delims, true);	//create set of tokens to represent expr, return delims (generating requirement)
    	String currentToken; 
    	
    	//loop through StringTokens
    	while(stringToken.hasMoreTokens()) {
    		//add token to string
    		currentToken = stringToken.nextToken();
    		
    		if(Character.isLetter(currentToken.charAt(0))) {	//if the string is a letter, need to use Character class to test
    			Variable temp = new Variable(currentToken);
    			if(vars.contains(temp)) continue;
    			else vars.add(temp);
    		}
    		else if(currentToken.charAt(0) == '[') {
    			//create new array object and add to arrays
    			Array temp = new Array(currentToken);
    			if(arrays.contains(temp)) continue;		//if temp already exists in arrays, skip duplicates
    			else arrays.add(temp);
    		}
    	}
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	float ret = 0;
    	
    	//replace all whitespace
    	expr = replace(expr);
    	
    	//if input is null
    	if(expr.length() == 0) return 0;
    	
    	//if input is one character and it is number
    	if(expr.length() == 1 && Character.isDigit(expr.charAt(0)) == true){
            ret = Float.parseFloat("" + expr.charAt(0));
            return ret;
        }
    	
    	//if one variable, return corresponding value
        if(expr.length() == 1 && Character.isLetter(expr.charAt(0)) == true){
        	for(int i = 0; i < vars.size(); i++) {
                if (vars.get(i).name.equals(expr))
                    ret = vars.get(i).value;
            }
            return ret;
        }
        
        //replace all variables with values from makeVariableLists
        for(int i = 0; i < vars.size(); i++) {
        	expr = expr.replace(vars.get(i).name + "", "" + vars.get(i).value);
        }
        
        

    	
    	return ret;
    }
}
