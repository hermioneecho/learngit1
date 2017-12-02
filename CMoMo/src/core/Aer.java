/**
 * 
 */
package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import dataStructure.ANode;
import dataStructure.SNode;

/**
 * @author daos
 * Aer is A-er, is ANode-er, who deal with ANode and do such things:
 * @ Fulfill the attribute of the ANode Tree(Attribute Tree)
 * @ detect semantics errors
 */
public class Aer {
	
	/**
	 * @ global environment. 
	 * @ environment means where the program is running on, where their methods and variables comes from.
	 * @ more information can be saw in WangYin's famous article : How to write an interpreter 
	 * @ To detect the re-declaration problem I use Map for convenience
	 */
	private Map<String,ANode> globalEnv;
	
	
	/**
	 * local environment. local variables for each function
	 */
	private Map<String, HashMap<String, ANode>> localEnvs;
	
	/**
	 * indicate which local environment it is now
	 * 0 if only global environment
	 */
	private int currentEnv;
	
	private List<String> strings;
	
	/**
	 * @ the name should be doubles but historically it is float that is to be put into the stack.
	 * @ an ArrayList for the real_literal
	 */
	private List<Double> floats;
	
	/**
	 * root of ANode tree
	 */
	private ANode A;
	
	private  getVariable()
	{
		
	}

	public Aer(SNode S) {
		super();
		A = new ANode(S);
		A.convert(S);
		globalEnv = new HashMap<String,ANode>();
		localEnvs = new HashMap<String,HashMap<String,ANode>>();
		strings = new ArrayList<String>();
		floats = new ArrayList<Double>();
	}
	
	/**
	 * @return success or not
	 * what is environment is explained in the declaration of globalEnv
	 */
	private boolean setGlobalEnv()
	{
		currentEnv = 0;
		Enumeration<ANode> round = A.children();
		ANode tmp = null;
		int variableCount = 0;
		int functionCount = 0;
		String symbol = null;
		
		while(round.hasMoreElements())
		{
			tmp = round.nextElement();
			if(tmp.getTag().equals("function_definition"))
			{
				symbol = this.AFunctionDefinition(tmp);
				globalEnv.put(symbol, tmp);
				functionCount++;
			}
			else
			{
				if(tmp.getTag().equals("declaration"))
				{
					symbol = this.ADeclaration(tmp);
					globalEnv.put(symbol, tmp);
				}
				else
				{
					//definition, needs more attentions
				}
				variableCount++;
			}
		}
		
		A.addAttribute("Function Count", functionCount);
		A.addAttribute("Global Variable Count", variableCount);		
		return true;
	}
	
	private String AFunctionDefinition(ANode a)
	{
		ANode dt = a.getChildAt(0);
		ANode id = a.getChildAt(1);
		ANode pl = a.getChildAt(2);
		
		a.addAttribute("Return Type:",(String)dt.getContents());
		a.addAttribute("Symbol", (String)id.getContents());
		int plsize = pl.getChildCount();
		a.addAttribute("Parameter Size", plsize);
		
		Enumeration<ANode> prms = pl.children();
		ANode pr = null;
		String prDT = null;
		String prID = null;
		List<String> prSymbols = new ArrayList<String>();
		List<String> prTypes = new ArrayList<String>(); 
		while(prms.hasMoreElements())
		{
			pr = prms.nextElement();
			prDT = (String) pr.getChildAt(0).getContents();
			prID = (String) pr.getChildAt(1).getContents();
			prSymbols.add(prID);
			prTypes.add(prDT);
		}
		a.addAttribute("Parameter Types", prTypes);
		a.addAttribute("Parameter Symbols", prSymbols);
		
		return (String)id.getContents();
	}
	
	
	private String ADeclaration(ANode a)
	{
		ANode dt = a.getChildAt(0);
		ANode lv = a.getChildAt(1);
		ArrayList<Object> contents = (ArrayList<Object>) lv.getContents();
		boolean isPointer = (boolean) contents.get(0);
		String ID = (String) contents.get(1);
		int arraysize = (int) contents.get(2);
		a.addAttribute("Symbol", ID);
		a.addAttribute("Data Type", (String)dt.getContents());
		if(!isPointer)
		    a.addAttribute("Pointer", false);
		else
			a.addAttribute("Pointer", true);
		if(arraysize > 0)
			a.addAttribute("Array", arraysize);
		else
			a.addAttribute("Array", null);
		return ID;
	}
	
	private String ADefinition(ANode a)
	{
		//generate the byte code to the method area? no, calculate them all
		return null;
	}
	
	
	
	
}
