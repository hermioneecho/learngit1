package cvm;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bytecode.Bytecode;
import bytecode.DebugBytecode;
import core.Aer;
import dataStructure.ANode;

public class DebugInfo{
	/*contains the debug bytecode, which:
	 * load symbol and store symbol instead of number
	 * belong to a lineno
	 */
	
	/*
	 * the map is stored here to replace the symbol in the debug bytecode
	 * and the map is the two kind of environment in Aer
	 */
	
	/* and if we keep a new map between the address and symbol, we can display the symbols value */

	
	/* with a loader this DebugInfo can be convert into executable bytecode 
	 * the task:
	 * 1. replace the local variable symbols by the relative address 
	 * 2. replace the global variable symbols by it Pool address
	 * 3. push the real literal and string literal to the Pool and replace them by their Pool address
	 * 4. make the Pool
	 * 5. load the bytecode into the MethodArea (one rule to obey)
	 * 5. with the information of function to generate FunctionInfo[]
	 * 6. initialize the Stack(one rule to obey)
	 * 7. replace the function name by its address
	 * 8. get ready to run
	 * 9. if debug, keeps what variables we have currently and their address
	 * 10. if debug, the keeps the breakpoint location in the source file and run in CVM DebugMode 
	 * */
	
	private Map<Integer/*the line number*/, ArrayList<DebugBytecode>> debugCodes;
	private Map<ANode,Integer> codeNum;
	
	/**
	 * the index is function index, and beginLine.get(index) return the line 
	 * where the function begins(the first line inside the body)
	 */
	private List<Integer> beginLine;
	
	private List<Integer> endLine;
	
	/**
	 * contains all the information generate during the global setting and assembling
	 */
	private Aer aer;

	public DebugInfo(Aer aer) {
		super();
		this.aer = aer;
		debugCodes = new HashMap<Integer, ArrayList<DebugBytecode>>();
		beginLine = new ArrayList<Integer>();
		endLine = new ArrayList<Integer>();
		codeNum = new HashMap<ANode, Integer>();
	}
	
	public ArrayList<DebugBytecode> getCodes(int lineNo)
	{
		ArrayList<DebugBytecode> result = debugCodes.get(lineNo);
		if(result == null)
		{
			result = new ArrayList<DebugBytecode>();
			debugCodes.put(lineNo, result);
		}
		return result;
	}
	
	public void setCodeCount(ANode a)
	{
		if(codeNum.containsKey(a))
		{
		    codeNum.put(a, codeNum.get(a)+1);
		}
		else 
			codeNum.put(a, 1);
	}
	
	public int getCodeCount(ANode a)
	{
		if(codeNum.containsKey(a))
			return codeNum.get(a);
		else
			return 0;
	}

	public void pushCode(int lineNo, DebugBytecode dbc)
	{
		getCodes(lineNo).add(dbc);
	}
	
	public void setOp(int lineNo, int dbcNo, int newOp)
	{
		getCodes(lineNo).get(dbcNo).setAddress(newOp);
	}
	
	public int countCodes(ANode root)
	{
		// every debugBytecode(includeing DebugCodeGetAddress..) will be replace by one and only one bytecode
		// thus count the debugBytecode is to count the bytecode
		return getCodeCount(root) + getCodesCount(root.children());
	}

	private int getCodesCount(Enumeration children) {
		// TODO Auto-generated method stub
		ANode c;
		int count = 0;
		while(children.hasMoreElements())
		{
			c=(ANode) children.nextElement();
			count += countCodes(c);
		}
		return count;
	}
	
	public void printCodes()
	{
		System.out.println("Debug Bytecodes:");
		debugCodes.forEach(
				(i,cs)->{
					System.out.println("at line "+ i +" : ");
					cs.forEach((c)->{System.out.println("  "+c.toString());});
				}
				);
	}

	
	
	

	
}
