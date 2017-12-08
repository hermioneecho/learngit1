package core;

import java.util.ArrayList;

import bytecode.Bytecode;
import bytecode.DebugBytecode;
import bytecode.Kinds;
import cvm.DebugInfo;
import cvm.MethodArea;
import cvm.Pool;
import cvm.StackArea;

public class Loader {
	
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
	
	private Pool pool;
	
	private Aer aer;
	
	private DebugInfo debugInfo;
	
	private MethodArea methodArea;
	
	private Bytecode bytecode;
	
	private StackArea stackArea;
	
	/**
	 * get all debugByteCodes
	 */
	private ArrayList<DebugBytecode> debugCodes;
	
	public Loader(Pool pool, Aer aer, DebugInfo debugInfo, MethodArea methodArea) {
		super();
		this.pool=pool;
		this.aer=aer;
		this.debugInfo=debugInfo;
		this.methodArea=methodArea;
	}
	
	/**
	 * convert DebugByteCode to ByteCode
	 */
	public void convertDebugByteCode() {
		debugCodes=debugInfo.getDebugByteCode();
		for(DebugBytecode debugCode:debugCodes) {
			switch(debugCode.getKind()) {
			case vload:
				vload(debugCode.getSymbol());
				break;
			case vstore:
				vstore(debugCode.getSymbol());
				break;
			case astore:
				astore(debugCode.getSymbol());
				break;
			case fpush:
				fpush(debugCode.getSymbol());
				break;
			case debugBytecodePushString:
				debugBytecodePushString(debugCode.getSymbol());
				break;
			case debugBytecodeGetAddress:
				debugBytecodeGetAddress(debugCode.getSymbol());
				break;
			case invoke:
				invoke(debugCode.getSymbol());
				break;
			}
		}
	}
	
	public Bytecode vload (String symbol) {
		int op=0;
		//is a local variable?
		if(aer.isLocalVariable(symbol)) {
			op=aer.getLocalVarIndex(symbol);
			bytecode=new Bytecode(Kinds.vload,op);
		}else{
			op=-(aer.getGlobalVarIndex(symbol));
			bytecode=new Bytecode(Kinds.vload,0-op);
		}
		return bytecode;
	}
	
	public Bytecode vstore(String symbol) {
		int op=0;
		//is a local variable?
		if(aer.isLocalVariable(symbol)) {
			op=aer.getLocalVarIndex(symbol);
			bytecode=new Bytecode(Kinds.vstore,op);
		}else{
			op=-(aer.getGlobalVarIndex(symbol));
			bytecode=new Bytecode(Kinds.vstore,0-op);
		}
		return bytecode;
	}
	
	public Bytecode astore(String symbol) {
		//TODO:
		return bytecode;
	}
	
	public Bytecode invoke(String symbol) {
		int op=aer.getFunctionIndex(symbol);
		bytecode=new Bytecode(Kinds.invoke,op);
		return bytecode;
		
		
		
	}
	
	public Bytecode fpush(String symbol) {
		int op=aer.getFloatIndex(symbol);
		bytecode=new Bytecode(Kinds.fpush,op);
		return bytecode;
	}
	
	public Bytecode debugBytecodePushString(String symbol) {
		int op=aer.getStringIndex(symbol);
		bytecode=new Bytecode(Kinds.debugBytecodePushString,op);
		return bytecode;
	}
	
	public Bytecode debugBytecodeGetAddress(String symbol) {
		int op=0;
		//if in localEnvs then find the address in stack Area
		if(aer.isLocalVariable(symbol)) {
			for(int i=0;i<stackArea.getStack().size();i++) {
				if(symbol==stackArea.getValue(i)) {
					op=i;
					bytecode=new Bytecode(Kinds.vload,op);
				}
			}
		}
		//if in globalEnvs then find the address in Pool
		else {
			for(int i=0;i<pool.getGlobalVariables().length;i++) {
				if(symbol==pool.getGlobalVariable(i)) {
					op=i;
					bytecode=new Bytecode(Kinds.vload,0-op);
				}
			}
		}
		return bytecode;
	}

}
