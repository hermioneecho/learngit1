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
import java.util.Map.Entry;

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
	private static Map<String,ANode> globalEnv;
	
	
	/**
	 * local environment. local variables for each function
	 */
	private static Map<String, HashMap<String, ANode>> localEnvs;
	
	/**
	 * indicate which local environment it is now
	 * -1 if only global environment
	 */
	private static int currentEnv;
	
	private static List<String> strings;
	
	/**
	 * @ the name should be doubles but historically it is float that is to be put into the stack.
	 * @ an ArrayList for the real_literal
	 */
	private static List<Double> floats;
	
	/**
	 * root of ANode tree
	 */
	private static ANode A;
	
	
	/**
	 * @param id
	 * @return Anode with the Symbol name id, null if no
	 */
	private ANode getVariable(String id)
	{
		// search the environment and return the ANode
		// variable could be anything that has a ID
		ANode result = null;
		//search local environment
		if(currentEnv != -1)
			result = localEnvs.get(currentEnv).get(id);
		if(result == null)
			result = globalEnv.get(id);
		return result;
	}
	
	/**
	 * @param rnode
	 * @return anode being referenced by rnode(rnode is a "left_value", with the same identifier)
	 */
	private ANode getVariableByNode(ANode rnode)
	{
		return getVariable((String)(((ArrayList<Object>)rnode.getContents()).get(1)));
	}
	 

	public Aer(SNode S) {
		super();
		A = ANode.convert(S);
		globalEnv = new HashMap<String,ANode>();
		localEnvs = new HashMap<String,HashMap<String,ANode>>();
		strings = new ArrayList<String>();
		floats = new ArrayList<Double>();
	}
	
	public void fillAST()
	{
		if(setGlobalEnv())
			System.out.println("fill AST successfully!");
		else
		System.out.println("fail to fill AST");
		
		//show the globalEnv

			String result = "GlobalEnv: \n";
			java.util.Iterator<Entry<String, ANode>> attrI = globalEnv.entrySet().iterator();
			Entry<String, ANode> cur = null;
			
			while(attrI.hasNext())
			{
				cur = attrI.next();
				result+=cur.getKey()+":"+ cur.getValue().toString()+"\n";
			}
			System.out.println(result);
	}
	
	public ANode getRoot()
	{
		return A;
	}
	
	/**
	 * @param symbol
	 * @param node�� the node is named as symbol
	 * if the symbol exists already then do nothing
	 */
	private static boolean addToGlobal(String symbol, ANode node)
	{
		if(!globalEnv.containsKey(symbol))
		{
			globalEnv.put(symbol, node);
			return true;
		}
		node.goodNodeComeBad("the symbol(identifier) already exists!");
		return false;
	}
	
	/**
	 * @return success or not
	 * what is environment is explained in the declaration of globalEnv
	 */
	private boolean setGlobalEnv()
	{
		currentEnv = -1;
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
				if(symbol == null)
					return false;
				if(addToGlobal(symbol, tmp))
				{
					functionCount++;
				}
			}
			else
			{
				if(tmp.getTag().equals("declaration"))
				{
					symbol = this.ADeclaration(tmp);
					if(symbol == null)
						return false;
					if(addToGlobal(symbol, tmp))
					{
						variableCount++;
					}
				}
				else
				{
					//definition, needs more attentions
					symbol = this.AGlobalDefinition(tmp);
					if(symbol == null)
						return false;
					if(addToGlobal(symbol, tmp))
					{
						variableCount++;
					}
				}
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
			a.addAttribute("Array Size", arraysize);
		else
			a.addAttribute("Array Size", (int)0);
		return ID;
	}
	
	/**
	 * @param a
	 * @return name of the global defined variable only, not local one
	 * tag "initialized value" to Anode a, to be used by the Loader
	 */
	private String AGlobalDefinition(ANode a)
	{
		String symbol = this.ADeclaration(a);
		ANode rightValue = a.getChildAt(2);
		int arraysize = 0;
		//write a function to compare the two ANodes
		
		//is Array?
		if((arraysize=(int)a.getAttribute("Array Size"))>0)
		{
			if(rightValue.getTag().equals("array_literal_node"))
			{
				String arrayType = null;
				String entryTpye = null;
				ANode entry = null;
				List<Object> valueArray = new ArrayList<Object>();
				// at least one element in the array
				if(rightValue.getChildAt(0).getTag().equals("integer_literal"))
				{
					arrayType = "int";
					entryTpye = "integer_literal";
				}
				else
				{
					arrayType = "real";
					entryTpye = "integer_literal";
				}
				Enumeration<ANode> entries = rightValue.children();
				while(entries.hasMoreElements())
				{
					if((entry=entries.nextElement()).getTag().equals(entryTpye))
					{
						valueArray.add(entry.getContents());
					}
					else
					{
						a.goodNodeComeBad("Unmatched Array Entry Type");
						return null;
					}
				}
				rightValue.addAttribute("Array Values", valueArray);
				if(arraysize != valueArray.size())
				{
					a.goodNodeComeBad("Unmatched Array Entries Numbers");
					return null;
				}
				else
					a.addAttribute("Array Values",valueArray);
				return symbol;
			}
		}
			
		//is pointer?
		// &array[1] is forbidden
		else if((boolean)a.getAttribute("Pointer"))
		{
			if(rightValue.getTag().equals("address_of_identifier"))
			{
				String pointTo = (String)rightValue.getContents();
				if(compare(getVariable(pointTo),a,"Data Type"))
				{
					a.addAttribute("Point To", pointTo);
					return symbol;
				}
				a.goodNodeComeBad("Unmatched Type");
				return null;
			}
			else
			{
				ANode id = getVariableByNode(rightValue);	
				if(id!=null)
				{
					a.goodNodeComeBad("use unexist symbol(identifier");
					return null;
				}
				if(compare(id,a,"Data Type") && (boolean)id.getAttribute("Pointer"))
				{
					a.addAttribute("Point To", id.getAttribute("Point To"));
					rightValue.setTag("right_value");
					return symbol;
				}
				a.goodNodeComeBad("Unmatched Type");
				return null;
			}
		}
		
		//is value assignment. a is not pointer or array
		else {
			// if dereference a pointer, assign the "Point To" to it
			// if dereference an array, extract and assign the value to it
			// if identifier, test data type and initialize data
			// if literal, just do it, needn't to push double to float, because just assign
			int index;
			
			// assign a literal
			if(rightValue.getTag().equals("integer_literal"))
			{
				if(a.getAttribute("Data Type").equals("int"))
				{
					a.addAttribute("Initial Value", (int)rightValue.getContents());
					return symbol;
				}
				else
				{
					a.goodNodeComeBad("Unmatched Type");
					return null;
				}
			}
			else if(rightValue.getTag().equals("real_literal"))
			{
				if(a.getAttribute("Data Type").equals("real"))
				{
					a.addAttribute("Initial Value", (double)rightValue.getContents());
					return symbol;
				}
				else
				{
					a.goodNodeComeBad("Unmatched Type");
					return null;
				}
			}
			
			// assign a "left value"
			ANode id = getVariableByNode(rightValue);
			if(id==null)
			{
				a.goodNodeComeBad("use unexist symbol");
			}
			if(compare(id, a, "Data Type")==false)
			{
				a.goodNodeComeBad("Unmatched Type");
				return null;
			}
			if((boolean)id.getAttribute("Pointer"))
			{
				rightValue.setTag("right_value");
				a.addAttribute("Dereference", (String)id.getAttribute("Point To"));
				return symbol;
			}
			else if((index=(int)((ArrayList<Object>)rightValue.getContents()).get(2))>0)
			{
				if(index >= (int)id.getAttribute("Array Size"))
				{
					a.goodNodeComeBad("Out of Range");
					return null;
				}
				rightValue.setTag("right_value");
				// get the literal 
				Object value = ((ArrayList<Object>)id.getAttribute("Array Values")).get(index);
				a.addAttribute("Initial Value", value);
				return symbol;
			}
			else
			{
				rightValue.setTag("right_value");
				a.addAttribute("Dereference", (String)id.getAttribute("Symbol"));
				return symbol;
			}
		}						
	
		return null;
	}
	
	
	private static boolean compare(ANode a, ANode b, String tag)
	{
		return a.getAttribute(tag).equals(b.getAttribute(tag));
	}
	
	
	
}
