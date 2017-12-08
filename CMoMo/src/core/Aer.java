/**
 * 
 */
package core;

import java.util.ArrayList;
import dataStructure.Token;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import bytecode.DebugBytecode;
import cvm.DebugInfo;
import dataStructure.ANode;
import dataStructure.SNode;
import bytecode.Kinds;
/**
 * @author daos
 * Aer is A-er, is ANode-er, who deal with ANode and do such A things:
 * @ ANode Tree(Attribute Tree) fulfillment
 * @ Arise semantics errors
 * @ Assembly
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
	private static List<HashMap<String/*local variable name*/, ANode/* variable's attribute*/>> localEnvs;
	
	private static List<String> functionNames;
	
	
	/**
	 * indicate which local environment it is now
	 * -1 if only global environment
	 */
	private static int currentEnv;
	
	private static Set<String> strings;
	
	/**
	 * @ the name should be doubles but historically it is float that is to be put into the stack.
	 * @ an ArrayList for the real_literal
	 */
	private static Set<Double> floats;
	
	/**
	 * root of ANode tree
	 */
	private static ANode A;
	
	private ANode lastNode;
	
	private DebugInfo out;
	
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
		{
			result = localEnvs.get(currentEnv).get(id);
		}
		if(result == null)
			result = globalEnv.get(id);
		if(result!=null&&result.getTag().equals("Bad Node"))
			return null;
		return result;
	}
	
	private boolean isLocal(String symbol)
	{
		return localEnvs.get(currentEnv).get(symbol)!=null;
	}
	
	/**
	 * @param rnode
	 * @return anode being referenced by "left_value", with the same identifier
	 */
	private ANode getVariableByNode(ANode rnode)
	{
		return getVariable((String)(((ArrayList<Object>)rnode.getContents()).get(1)));
	}
	 

	public Aer(SNode S) {
		super();
		A = ANode.convert(S);
		out = new DebugInfo(this);
		globalEnv = new HashMap<String,ANode>();
		localEnvs = new ArrayList<HashMap<String,ANode>>();
		strings = new TreeSet<String>();
		floats = new TreeSet<Double>();
		functionNames = new ArrayList<String>();
		
		currentEnv = 0;
//		functionNames.add("main");
		
	}
	
	/**
	 * @param symbol
	 * @return
	 *Get the relative position of a local variable by traversing
	 */
	public int getLocalVarIndex(String symbol) {
		int index=0;
		for(int i=0;i<localEnvs.size();i++) {
			Set<String> setKey=localEnvs.get(i).keySet();
			Iterator<String> iterator=setKey.iterator();
			//use the while loop to read the key value
			while(iterator.hasNext()){
				if(symbol==iterator.next()) {
					index=iterator.hashCode();
				}	
			};
		}
		return index;
	}
	
	/**
	 * @param symbol
	 * @return
	 * Get the relative position of a global variable by traversing
	 */
	public int getGlobalVarIndex(String symbol) {
		int index=0;
		Iterator<String> iterator=globalEnv.keySet().iterator();
		while(iterator.hasNext()) {
			if(symbol==iterator.next()) {
				index=iterator.hashCode();
			}
		}
		return index;
	}
	
	/**
	 * @param symbol
	 * @return
	 * get each float index in array floats by traversing
	 */
	public int getFloatIndex(String symbol) {
		int index=0;
		for(Double value:floats) {
			if(symbol==value.toString()) {
				index=value.hashCode();
			}
		}
		return index;
	}
	
	public int getFunctionIndex(String symbol) {
		int index=0;
		for(int i=0;i<functionNames.size();i++) {
			if(symbol==functionNames.get(i)) {
				index=i;
			}
		}
		return index;
	}
	
	/**
	 * @param symbol
	 * @return
	 * get each string index in array strings by traversing
	 */
	public int getStringIndex(String symbol) {
		int index=0;
		for(String value:strings) {
			if(symbol==value) {
				index=value.hashCode();
			}
		}
		return index;
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
		if(!Assembly())
		{
			System.out.println("Senmatic Error:");	
			printBadNodes();
		}
		else
		{
			out.printCodes();
		}
	}
	
	private void printBadNodes()
	{
		for(ANode a : ANode.illegalNode)
		{
			System.out.println(a.toString());
		}
	}
	
	public ANode getRoot()
	{
		return A;
	}
	
	/**
	 * @param symbol
	 * @param node£¬ the node is named as symbol
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
	
	private static boolean addToLocal(String symbol,ANode node)
	{
		if(!localEnvs.get(currentEnv).containsKey(symbol))
		{
			localEnvs.get(currentEnv).put(symbol, node);
			return true;
		}
		node.goodNodeComeBad("the symbol(identifier) already exist!");
		return false;
	}
	
	/**
	 * @return success or not
	 * what is environment is explained in the declaration of globalEnv
	 */
	private boolean setGlobalEnv()
	{
		Enumeration<ANode> round = A.children();
		ANode tmp = null;
		currentEnv = 0;
		int variableCount = 0;
		int functionCount = 0;
		String symbol = null;
		
		while(round.hasMoreElements())
		{
			tmp = round.nextElement();
			if(tmp.getTag().equals("function_definition"))
			{
				symbol = this.AFunctionDeclaration(tmp);
				if(symbol == null)
					return false;
				// so add a isFunction()
				if(addToGlobal(symbol, tmp))
				{
					functionCount++;
					functionNames.add(symbol);
					currentEnv++;
				}
			}
			else
			{
				int oldEnv = currentEnv;
				currentEnv = -1;
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
					symbol = this.ADefinition(tmp);
					if(symbol == null)
						return false;
					if(addToGlobal(symbol, tmp))
					{
						variableCount++;
					}
				}
				currentEnv=oldEnv;
			}
		}
		
		A.addAttribute("Function Count", functionCount);
		A.addAttribute("Global Variable Count", variableCount);		
		return true;
	}
	
	/**
	 * @param id, the node that generated by mkid in syntaxer...
	 * @return symbol of id node
	 */
	private static String getSymbolFromMkid(ANode id)
	{
		return (String)((Token)(id.getContents())).getWord();
	}
	
	/**
	 * @param id, a ANode that comes from mkid in syntaxer
	 * @return
	 */
	private ANode getDefinitionOfID(ANode id)
	{
		return getVariable(getSymbolFromMkid(id));
	}
	
	/**
	 * @param id, the definition of the identifier in the environment
	 * @return
	 * use for id only
	 */
	private static boolean isPointer(ANode id)
	{
		return (boolean)id.getAttribute("Pointer");
	}
	/**
	 * @param id
	 * @return
	 * use for id only
	 */
	private static boolean isArray(ANode id)
	{
		return (int)id.getAttribute("Array Size") > 0;
	}
	private static boolean isFunction(ANode id)
	{
		return id.getTag().equals("function_definition");
	}
	
	public boolean isLocalVariable(String symbol) {
		for(int i=0;i<localEnvs.size();i++) {
			Set<String> setKey=localEnvs.get(i).keySet();
			Iterator<String> iterator=setKey.iterator();
			//use the while loop to read the key value
			while(iterator.hasNext()){
				if(symbol==iterator.next()) {
					return true;
				}
			}
		}
		return false;
	}
	
	private String AFunctionDeclaration(ANode a)
	{
		localEnvs.add(new HashMap<String,ANode>());
		
		ANode dt = a.getChildAt(0);
		ANode id = a.getChildAt(1);
		ANode pl = a.getChildAt(2);
		
		a.addAttribute("Data Type",(String)dt.getContents());
		a.addAttribute("Symbol", getSymbolFromMkid(id));
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
			prID = (String) getSymbolFromMkid(pr.getChildAt(1));
			prSymbols.add(prID);
			prTypes.add(prDT);
			pr.addAttribute("Data Type", prDT);
			pr.addAttribute("Symbol", prID);
			pr.addAttribute("Pointer", false);
			pr.addAttribute("Array Size", 0);
			if(!addToLocal(prID,pr))
				;//seems no need to handle here because it would be unexisted node if we use the parameter
			
		}
		a.addAttribute("Parameter Types", prTypes);
		a.addAttribute("Parameter Symbols", prSymbols);
		
		int beginLine = dt.getLineNo();
		int endLine = ((ANode)a.getLastChild()).getLineNo();
		
		a.addAttribute("Begin Line", beginLine);
		a.addAttribute("End Line", endLine);
		
		return getSymbolFromMkid(id);
	}
	
	private boolean isUsedSymbol(String symbol)
	{
		return getVariable(symbol)!=null;
	}
	
	static int globalOrder = 1;
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
		a.addAttribute("Global Order", globalOrder++);
		return ID;
	}
	
	/**
	 * @param a
	 * @return name of the global defined variable only, not local one
	 * tag "initialized value" to Anode a, to be used by the Loader
	 */
	private String ADefinition(ANode a)
	{
//********************try to let the right value of the definition to be Expression but failed, let it inperfect
//		String symbol = this.ADeclaration(a);
//		ANode rightValue = a.getChildAt(2);
//		int arraysize = 0;
//		AExpression(rightValue);
//		if(a.getTag().equals("Bad Node"))
//			return null;
//		if(compare(rightValue,a,"Data Type")&&!isArray(a))
//		{
//			if(rightValue.getTag().equals("function_call")&&!isPointer(a)
//			||(rightValue.getTag().equals("address_of_identifier")&&isPointer(a))
//			||(!isArray(rightValue)))
//			{
//				add(a,new DebugBytecode(Kinds.vstore,symbol));
//				return symbol;
//			}
//			else
//			{
//				return null;
//			}
//		}
//		else if(isArray(a))
//		{
//			if((arraysize=(int)a.getAttribute("Array Size"))>0)
//			{
//				if(rightValue.getTag().equals("array_literal_node"))
//				{
//					String arrayType = null;
//					String entryTpye = null;
//					ANode entry = null;
//					List<Object> valueArray = new ArrayList<Object>();
//					// at least one element in the array
//					if(rightValue.getChildAt(0).getTag().equals("integer_literal"))
//					{
//						arrayType = "int";
//						entryTpye = "integer_literal";
//					}
//					else
//					{
//						arrayType = "real";
//						entryTpye = "integer_literal";
//					}
//					Enumeration<ANode> entries = rightValue.children();
//					while(entries.hasMoreElements())
//					{
//						if((entry=entries.nextElement()).getTag().equals(entryTpye))
//						{
//							valueArray.add(entry.getContents());
//						}
//						else
//						{
//							a.goodNodeComeBad("Unmatched Array Entry Type");
//							return null;
//						}
//					}
//					rightValue.addAttribute("Array Values", valueArray);
//					if(arraysize != valueArray.size())
//					{
//						a.goodNodeComeBad("Unmatched Array Entries Numbers");
//						return null;
//					}
//					else
//						a.addAttribute("Array Values",valueArray);
//					return symbol;
//				}
//			}			
//		}
//		return null;
//***************************		
		
	
		//it would work well at the first two children, symbol is not null
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
					a.goodNodeComeBad("use unexist symbol(identifier)");
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
		// is functioncall
		else if(rightValue.getTag().equals("function_call"))
		{
			a.goodNodeComeBad("Can not initialized a variable with function call");
			return null;
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
			else if(rightValue.getTag().equals("address_of_identifier"))
			{
				a.goodNodeComeBad("Unmatched Type");
				return null;
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
	
	/*
	 * it is time to cope with the local environment, the task is only focus on:
	 * 1. use the unmatched variable, the type check
	 * 2. use the unexist variable
	 * 3. generate bytecode...
	 * 4. done by the following function Compile
	 */
	
	/*
	 * How to:
	 * 1. Walk through and the types of operation are limited
	 * 2. for Arithmetic operations, finish the unit_expression first(using the stack bytecode) the backup and just generate code
	 * 3. for IO, just generate it
	 * 4. for function, just generate it
	 * 5. for jump, fill back. keep some local number in the stack and when comes back we fill
	 * 6. xxx, with a stack operation, just generate it 
	 */
	
	private boolean Assembly(){
		currentEnv = 0;
		globalEnv.forEach((string,anode)->
		{
			if(anode.getTag().equals("function_definition"))
			{
				//the parameter is added to LocalEnv in AFunction definition
				//the function name is added in SetGlobal
				Enumeration<ANode> e = anode.children();
				ANode c;

				while(e.hasMoreElements())
				{
					c=e.nextElement();
					AStatement(c);
				}				
				currentEnv++;
			}

		});
		//TODO check the main function : return int and no parameter
		return ANode.illegalNode.isEmpty();
	}
	
    private void AStatement(ANode c)
    {
		String symbol;
    	if(c.getTag().equals("declaration"))
		{
			// if null, the code is set as bad automatically
			if((symbol = this.ADeclaration(c))!=null)
			{
				addToLocal(symbol, c);			
			}
			
		}
		else if(c.getTag().equals("definition"))
		{
			// if null, the code is set as bad automatically
			if((symbol = this.ADefinition(c))!=null)
			{
				if(addToLocal(symbol,c))
				{
				    if(isPointer(c))
				    {
				    	add(c, new DebugBytecode(Kinds.push,(String)c.getAttribute("Point To")));
				    }
				    else {

						String dataType = getDataType(symbol);
						
						if(dataType.equals("int"))
						{	if(!isPointer(c)&&!isArray(c))
						    {
							    add(c, new DebugBytecode(Kinds.push,(int)c.getAttribute("Initial Value")));
							}
						    else if(isArray(c))
						    {
						    	ArrayList<Object> values = (ArrayList<Object>)c.getAttribute("Array Values");
						    	int size = values.size();
						    	for(Object v:values)
						    	{
						    		add(c, new DebugBytecode(Kinds.push,(int)v));
						    	}
						    	for(int i=1;i<=size;i++)
						    	{
						    		// our stack grows from lower address to high address so the first value is to be store in the heighest address
						    		// symbol of an array points to the lowest address of the array
						    		add(c, new DebugBytecode(Kinds.vstore,size-i,symbol));
						    	}
						    	return;
						    }
						}
						else if(dataType.equals("real"))
						{
							if(!isPointer(c)&&!isArray(c))
							{
								double value = (double)c.getAttribute("Initial Value");
								floats.add(value);
								add(c,new DebugBytecode(Kinds.fpush,String.valueOf(value)));
							}
							else if(isArray(c))
						    {
						    	ArrayList<Object> values = (ArrayList<Object>)c.getAttribute("Array Values");
						    	int size = values.size();
						    	for(Object v:values)
						    	{
						    		floats.add((double)v);
						    		add(c, new DebugBytecode(Kinds.fpush,String.valueOf((double)v)));
						    	}
						    	for(int i=1;i<=size;i++)
						    	{
						    		add(c, new DebugBytecode(Kinds.vstore,size-i,symbol));
						    	}
						    	return;
						    }
						}
				    }
					add(c, new DebugBytecode(Kinds.vstore,symbol));
				}
				
			}					
		}
		else if(c.getTag().equals("assignment"))
		{
			ANode left = c.getChildAt(0);
			symbol=getSymbolFromLeftValue(left);
			ALeftValue(left);
			ANode d = getVariable(symbol);
			ANode right = c.getChildAt(1);
			//deal with the right value first, the value is pushed to the stack
			AExpression(right);
			
			//check left
			if(d == null)
			{
				c.goodNodeComeBad("Unexisted Symbol");
				return;
			}
			
			//check right
			if(isBad(right))
			{
				c.goodNodeComeBad("Illegal Expression");
				return;
			}
			
			if(right.getTag().equals("address_of_identifier"))
			{
				
				right = getVariable((String)right.getContents());
			}
			
			//check type
			if(compare(d,right,"Data Type"))
			{
				c.addAttribute("Data Type", getDataType(d));
				//pointer
				if((boolean)d.getAttribute("Pointer") == true && c.getChildAt(1).getTag().equals("address_of_identifier"))
					add(c, new DebugBytecode(Kinds.vstore,0,symbol));
				//value and array
				if((boolean)d.getAttribute("Pointer") == false && !c.getChildAt(1).getTag().equals("address_of_identifier"))
				{
					//array
					if((int)d.getAttribute("Array Size")>0)
					{
						add(c, new DebugBytecode(Kinds.vload,(int)left.getAttribute("Array Size"),symbol));
					}
					else
					{
						add(c, new DebugBytecode(Kinds.vstore,0,symbol));
					}
				}
			}
			else
			{
				c.goodNodeComeBad("Unmatched Type");
			}
		}
		else if(c.getTag().equals("read")||c.getTag().equals("write"))
		{
			symbol = getSymbolFromMkid(c.getChildAt(0));
			ANode id = getVariable(symbol);
			if(id==null)
			{
				c.goodNodeComeBad("Unexisted Symbol");
				return;
			}
			if(isPointer(id)||isArray(id))
			{
				c.goodNodeComeBad("Unmatched Symbol");
				return;
			}
			String dataType = getDataType(id);
			if(c.getTag().equals("read"))
			{
				if(dataType.equals("int"))
					add(c, new DebugBytecode(Kinds.iread));
				else if(dataType.equals("real"))
					add(c, new DebugBytecode(Kinds.fread));
				add(c, new DebugBytecode(Kinds.vload,0, symbol));
			}
			else
			{
				add(c, new DebugBytecode(Kinds.push,0, symbol));
				if(dataType.equals("int"))
					add(c, new DebugBytecode(Kinds.iwrite));
				else if(dataType.equals("real"))
					add(c, new DebugBytecode(Kinds.fwrite));
			}
		}
		else if(c.getTag().equals("write_string"))
		{
			String string_lteral = (String)c.getChildAt(0).getContents();
			strings.add(string_lteral);
			add(c,new DebugBytecode(Kinds.debugBytecodePushString,0,string_lteral));
			add(c,new DebugBytecode(Kinds.swrite));
		}
		else if(c.getTag().equals("xxx"))
		{
			int aLine = (int)c.getChildAt(0).getContents();
			int codeNo = (int)c.getChildAt(1).getContents();
			int op = (int)c.getChildAt(2).getContents();
			add(c,new DebugBytecode(Kinds.push,aLine));
			add(c,new DebugBytecode(Kinds.push,codeNo));
			add(c,new DebugBytecode(Kinds.push,op));
			add(c,new DebugBytecode(Kinds.xxx));
		}
		else if(c.getTag().equals("return_node"))
		{
			this.AExpression(c.getChildAt(0));
			add(c, new DebugBytecode(Kinds.rtn));
		}
		else if(c.getTag().equals("while_node"))
		{
			DebugBytecode condDBC = null;
			ANode cond = c.getChildAt(0);
			
			condDBC = ACond(cond);
			add(c,condDBC);			
			// cope with the bytecode count:Add a new map in debugInfo
			ANode body = c.getChildAt(1);
			Enumeration e = body.children();
			while(e.hasMoreElements())
			{
				AStatement((ANode)e.nextElement());
			}
			int count = out.countCodes(body);
			int whileCount = out.countCodes(c);
			add(lastNode, new DebugBytecode(Kinds.gto,0-whileCount));
			condDBC.setAddress(count+2);
		}
		else if(c.getTag().equals("if_node"))
		{
			DebugBytecode condDBC = null;
			ANode cond = c.getChildAt(0);
			
			condDBC = ACond(cond);
			add(c,condDBC);			
			// cope with the bytecode count:Add a new map in debugInfo
			ANode body = c.getChildAt(1);
			Enumeration e = body.children();
			while(e.hasMoreElements())
			{
				AStatement((ANode)e.nextElement());
			}
			int count = out.countCodes(body);
			condDBC.setAddress(count+1);
		}
		else if(c.getTag().equals("if_else_node"))
		{
			DebugBytecode condDBC = null;
			DebugBytecode ifgoto = new DebugBytecode(Kinds.gto);
			ANode cond = c.getChildAt(0);
			
			condDBC = ACond(cond);
			add(c,condDBC);			
			// cope with the bytecode count:Add a new map in debugInfo
			ANode ifbody = c.getChildAt(1);
			ANode elsebody = c.getChildAt(2);
			Enumeration e = ifbody.children();
			while(e.hasMoreElements())
			{
				AStatement((ANode)e.nextElement());
			}
			int count = out.countCodes(ifbody);
			add(lastNode,ifgoto);
			condDBC.setAddress(count+2);
			Enumeration e2 = elsebody.children();
			while(e2.hasMoreElements())
			{
				AStatement((ANode)e2.nextElement());
			}
			count = out.countCodes(elsebody);
			ifgoto.setAddress(count+1);
		}
		else
		{
			;// the pl and dt would reach here
		}

    }

	/**
	 * @param a
	 * @return isUnit
	 */
	private boolean AUnit(ANode a)
	{
		if(a.getTag().equals("integer_literal"))
		{
			addDataType(a, "int");
			add(a,new DebugBytecode(Kinds.push,(int)a.getContents()));
		}
		else if(a.getTag().equals("real_literal"))
		{
			addDataType(a, "real");
			floats.add((double)a.getContents());
			add(a,new DebugBytecode(Kinds.fpush,0,((Double)a.getContents()).toString()));
		}
		else if(a.getTag().equals("function_call"))
		{
			//set type
			String symbol= getSymbolFromMkid(a.getChildAt(0));			
			ANode d = getVariable(symbol);
			String returnType = getDataType(d);
			if(d == null)
			{
				a.goodNodeComeBad("Unexisted Symbol");
				return true;
			}
			if(!isFunction(d))
			{
				a.goodNodeComeBad("Unmatch Type");
				return true;
			}
			addDataType(a,returnType);
			
			//push the arguments to the stack
			ANode al = a.getChildAt(1);
			int argumentCount = 0;
			Enumeration<ANode> ale = al.children();
			ANode argument;
			String argumentType;
			if(al.getChildCount() != (int)d.getAttribute("Parameter Size"))
			{
				a.goodNodeComeBad("Unmatched Parameters Number");
				return true;
			}
			while(ale.hasMoreElements())
			{
				AExpression((argument=ale.nextElement()));
				argumentType=getDataType(argument);
				if(argumentType==null)
				{
					argument.goodNodeComeBad("Unexisted Symbol");
				}
				else if(!argumentType.equals(((ArrayList<String>)d.getAttribute("Parameter Types")).get(argumentCount)))
				{
					argument.goodNodeComeBad("Unmatched Type");
				}
				argumentCount++;
			}
			//check argument count

			
			//then invoke
			add(a,new DebugBytecode(Kinds.invoke,((Token)a.getChildAt(0).getContents()).getWord()));
		}
		else if(a.getTag().equals("left_value"))
		{
			ArrayList<Object> contents = (ArrayList<Object>) a.getContents();
			boolean isPointer = (boolean) contents.get(0);
			String symbol = (String) contents.get(1);
			int arraysize = (int) contents.get(2);
			a.setTag("right_value");
			//check and set the type
			ANode d = getVariable(symbol);
			if(d==null)
			{
				a.goodNodeComeBad("Unexisted Symbol");
				return true;
			}
			
			if(isFunction(d))
			{
				a.goodNodeComeBad("Unmatched Type");
				return true;
			}
			a.addAttribute("Data Type", getDataType(d));			
			
			//check array, dereference, identifier
			if(arraysize>0)
			{
				if((int)d.getAttribute("Array Size")==0)
				{
					a.goodNodeComeBad("Unmatched Type");
					return true;
				}
				add(a,new DebugBytecode(Kinds.vload,arraysize,(String)symbol));					
				a.addAttribute("Data Type", "int");
			}
			else if(isPointer)
			{
				if((boolean)d.getAttribute("Pointer")==false)
				{
					a.goodNodeComeBad("Unmatched Type");
					return true;
				}
				add(a,new DebugBytecode(Kinds.vload,symbol));
			}
			else if(arraysize==0 && isPointer==false)
			{
				if((boolean)d.getAttribute("Pointer")&&(int)d.getAttribute("Array Size")>0)
				{
					a.goodNodeComeBad("Unmatched Type");
					return true;
				}
				add(a,new DebugBytecode(Kinds.vload,symbol));
			}
			else
			{
				a.goodNodeComeBad("Unmatched Type");
				return true;
			}
		}
		else if(a.getTag().equals("address_of_identifier"))
		{
			//check and add
			ANode d = getVariable((String)a.getContents());
			if(d==null)
			{
				a.goodNodeComeBad("Unexisted Symbol");
				return true;
			}
			// ensure it is a pure identifier
			if((boolean)d.getAttribute("Pointer")==true||(int)d.getAttribute("Array Size")>0||isFunction(d))
			{
				a.goodNodeComeBad("Unmatched Type");
				return true;
			}
			add(a,new DebugBytecode(Kinds.debugBytecodeGetAddress,(String)a.getContents()));
			return true;
		}
		else
			return false;
		return true;
	}
	
	/**
	 * @param a Cond
	 * @return a DebugBytecode to be modified, and the DebugBytecode has the logical operation in its symbol
	 */
	private DebugBytecode ACond(ANode a)
	{
		String logicalOp = (String)a.getContents();
		ANode left = a.getChildAt(0);
		ANode right = a.getChildAt(1);		
		AExpression(left);
		AExpression(right);
		String leftDataType = (String)left.getAttribute("Data Type");
		String rightDataType = (String)right.getAttribute("Data Type");
		String type;
		
		if(leftDataType!=null)
			type = leftDataType;
		else if(rightDataType!=null)
			type = rightDataType;
		else
		{
			a.goodNodeComeBad("Unmatched Type");
			type = "int"; //continue
		}
		
		if(type.equals("int"))
		{
			if(logicalOp.equals("=="))
			    return new DebugBytecode(Kinds.icmp,"==");
			else if(logicalOp.equals("<>"))
				return new DebugBytecode(Kinds.icmpi,"<>");
			else if(logicalOp.equals("<"))
				return new DebugBytecode(Kinds.icmpg,"<");
		}
		else if(type.equals("real"))
		{
			if(logicalOp.equals("=="))
			    return new DebugBytecode(Kinds.fcmp,"==");
			else if(logicalOp.equals("<>"))
				return new DebugBytecode(Kinds.fcmpi,"<>");
			else if(logicalOp.equals("<"))
				return new DebugBytecode(Kinds.fcmpg,"<");
		}
		else
			return null;
		return null;
	}
	
	private void AExpression(ANode a)
	{
		if(AUnit(a))
		{	
			return;
		}
		
		ANode c1 = a.getChildAt(0);
		ANode c2 = a.getChildAt(1);
		
		AExpression(c1);
		AExpression(c2);
		
		//test the type
		
		// first, if the code is bad, we assume it is good
		// just check the type and not other checking? - no, the left to be runtime error
		
		if(isBad(c1)&&isBad(c2))
		{
			//look for the c2 for information, if both bad them this node is bad
			c1.addAttribute("Data Type", getDataType(c2));
		}
		else if((!isBad(c1)&&isBad(c2)))
		{
			c2.addAttribute("Data Type", getDataType(c1));
		}
		else if(isBad(c1)&&isBad(c2))
		{
			a.goodNodeComeBad("Still Bad");
			return;
		}
		
		String dataType = getDataType(c1);
		
		if(dataType.equals("int")) {
			if(a.getTag().equals("add"))
			{				
				add(a,new DebugBytecode(Kinds.iadd));
			}
			else if(a.getTag().equals("sub"))
			{
				add(a,new DebugBytecode(Kinds.isub));
			}
			else if(a.getTag().equals("mul"))
			{
				add(a,new DebugBytecode(Kinds.imul));
			}
			else if(a.getTag().equals("div"))
			{
				add(a,new DebugBytecode(Kinds.idiv));
			}
			else
			{
				System.out.println("in AExpression Error");
			}
			a.addAttribute("Data Type", "int");
		}
		else if(dataType.equals("real"))
		{
			if(a.getTag().equals("add"))
			{				
				add(a,new DebugBytecode(Kinds.fadd));
			}
			else if(a.getTag().equals("sub"))
			{
				add(a,new DebugBytecode(Kinds.fsub));
			}
			else if(a.getTag().equals("mul"))
			{
				add(a,new DebugBytecode(Kinds.fmul));
			}
			else if(a.getTag().equals("div"))
			{
				add(a,new DebugBytecode(Kinds.fdiv));
			}
			else
			{
				System.out.println("in AExpression Error");
			}
			a.addAttribute("Data Type", "real");
		}
		else
		{
			System.out.println("in AExpression Error");
		}
		
	}
	
	
	
	/**
	 * @param a, knowing which node we put code to
	 * @param dbc
	 */
	private void add(ANode a,DebugBytecode dbc)
	{
		out.pushCode(a.getLineNo(), dbc);
		out.setCodeCount(a);
		lastNode = a;
	}
	
	private String getAttribute(String symbol, String attribute)
	{
		ANode a =  getVariable(symbol);
		if(a != null)
		{
			return (String) a.getAttribute(attribute);
		}
		return null;
	}
	
	/**
	 * @param symbol
	 * @param attribute
	 * use to change the variable in the environment's attribute, maybe useless
	 */
	private void setAttribute(String symbol, String attribute, Object value)
	{
		ANode a =  getVariable(symbol);
		if(a != null)
		{
			a.addAttribute(attribute, value);
		}
	}
	
	private String ALeftValue(ANode a)
	{
		ArrayList<Object> contents = (ArrayList<Object>) a.getContents();
		boolean isPointer = (boolean) contents.get(0);
		String ID = (String) contents.get(1);
		int arraysize = (int) contents.get(2);
		a.addAttribute("Symbol", ID);
		String dataType = getDataType(ID);
		if(dataType == null)
		{
			a.goodNodeComeBad("Unexited Symbol");
			return null;
		}
		a.addAttribute("Data Type", dataType);
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
	 * @param symbol
	 * @return Data Type of that symbol
	 */
	private String getDataType(String symbol)
	{
		return getAttribute(symbol, "Data Type");
	}
	
	/**
	 * @param a
	 * @return Data Type of a
	 */
	private String getDataType(ANode a)
	{
		return (String)a.getAttribute("Data Type");
	}
	
	/**
	 * @param a
	 * @param type
	 */
	private void addDataType(ANode a ,String type)
	{
		a.addAttribute("Data Type", type);
	}
	
	private boolean isBad(ANode a)
	{
		return a.getTag().equals("Bad Node");
	}
	
	private String getSymbolFromLeftValue(ANode a)
	{
		ArrayList<Object> contents = (ArrayList<Object>) a.getContents();
		String symbol = (String) contents.get(1);
		return symbol;
	}
	
	
}
