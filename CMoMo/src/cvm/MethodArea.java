package cvm;

import java.util.ArrayList;

import bytecode.Bytecode;
import bytecode.Kinds;

/**
 * bytecodes are stored here and end of the program is marked by null
 * null is stored in the first element of the bytecodes and that is where main return to
 * no symbol remains here
 * go through the MethodArea should get the right result
 * @author daos
 *
 */
public class MethodArea {
	private ArrayList<Bytecode> bytecodes;
	private int cur;
	
	public MethodArea() {
		super();
		cur = 0;
		bytecodes = new ArrayList<Bytecode>();
		bytecodes.add(null);
	}
	
	/**
	 * @return the preceding bytecode's index
	 */
	public int add(Bytecode code) {
		bytecodes.add(code);
		return cur++;
	}
	
	public int add(ArrayList<Bytecode> codes)
	{
		if(codes.isEmpty())
			return -1;
		int tmp = cur;
		cur += codes.size();
		bytecodes.addAll(codes);
		return tmp;		
	}
	
	//TODO: delete this function when no use
	/**
	 * used for executing without function call only
	 * used for testing bytecodes and CVM only
	 * @return the last bytecode's index (count of Bytecodes also)
	 */
	public int end()
	{
		bytecodes.add(null);
		return cur++;
	}
	
	public ArrayList<Bytecode> getBytecodes() {
		return bytecodes;
	}

	public Bytecode getCode(int pc)
	{
		return bytecodes.get(pc);
	}
	
	public boolean empty()
	{
		return bytecodes.size() == 1;
	}
	
	public void setBytecode(int address, int opcode, int op)
	{
		Kinds kind = Kinds.values()[opcode];
		Bytecode newbytecode = new Bytecode(kind,op);
		if(address < bytecodes.size())
		    bytecodes.set(address, newbytecode);
		return;
	}
	
	
}
