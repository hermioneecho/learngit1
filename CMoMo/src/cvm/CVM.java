package cvm;

import java.util.ArrayList;

import bytecode.Bytecode;
import bytecode.Kinds;

/**
 * @author daos
 * controls the execution of CPU
 * contains the debug information
 */
public class CVM {
	static public void main(String[] argv)
	{
		
//      test1 : passed, Arithmetic		
//		CVM test1 = new CVM(TestArithmeticMethods());
//		test1.cpu.printAssembly();
//		test1.normalRun();
		

//      test2 : passed, IO and Jump
//		String[] test2Strings = {"negative!"};
//		Pool test2Pool = new Pool(test2Strings,(int[])null);
//		CVM test2 = new CVM(test2Pool, CVM.testIOandJumpMethods());
//		test2.normalRun();
		
//      test3 :  passed, stack, function
//		FunctionInfo test3function1 = new FunctionInfo(2,5,0,1);
//		FunctionInfo[] test3fia = {test3function1};
//		double[] test3floats = {6.5,10.0};
//		Pool test3Pool = new Pool((String[])null, test3floats, test3fia);
//		CVM test3 = new CVM(test3Pool, CVM.TestFunctionStack());
//		test3.normalRun();
		
//      final simple test: XXX and put all together
		FunctionInfo test4function1 = new FunctionInfo(1,4,0,0);
		FunctionInfo[] test4fia = {test4function1};
		Pool test4Pool = new Pool(null, null, test4fia);
		CVM test4 = new CVM(test4Pool, CVM.TestXXX());
		test4.normalRun();
	}
	
	private CPU cpu;
	private Pool pool;
	private MethodArea methodArea;
	private StackArea stackArea;
	private DebugInfo debugInfo;
	
	
	// CVM in debug mode
	// TODO: implement this mode last
	public CVM(Pool pool, MethodArea methodArea,DebugInfo debugInfo) {
		super();
		this.pool = pool;
		this.methodArea = methodArea;
		this.stackArea = new StackArea();
		this.cpu = new CPU(this.stackArea,pool,methodArea);
		this.debugInfo = debugInfo;
		
	}
	
	public void debugModeRun()
	{
		if(debugInfo == null)
			System.err.println("no debug infomation is found!");
	}
	
	// execution mode
	public CVM(Pool pool, MethodArea methodArea) {
		super();
		this.pool = pool;
		this.methodArea = methodArea;
		this.stackArea = new StackArea();
		this.cpu = new CPU(this.stackArea,pool,methodArea);	
		this.debugInfo = null;
	}
	
	/**
	 * just run ignoring any debug info
	 */
	public void normalRun()
	{
		Bytecode code = cpu.nextCode();
		try {
			while(code!=null)
			{
				cpu.printStack();
				cpu.execute(code);
				code = cpu.nextCode();
			}
			cpu.printStack();
		}
		catch(Exception e)
		{
			System.out.println("the Bytecode at "+cpu.getPc()+" in the Method Area cause a runtime error!");
		}

	}
	
	// no-global-and-string mode
	public CVM(MethodArea methodArea)
	{
		super();
		this.pool = null;
		this.methodArea = methodArea;
		this.stackArea = new StackArea();
		this.cpu = new CPU(this.stackArea,pool,methodArea);	
		this.debugInfo = null;	
	}
	

	private static MethodArea TestArithmeticMethods()
	{   //expression for int (6*(1+2)-2)/(2+2) the result should be 4
		// (/ (- (* 6 (+ 1 2)) 2) (+ 2 2)) 
		MethodArea result = new MethodArea();
		result.add(new Bytecode(Kinds.push, 1));
		result.add(new Bytecode(Kinds.push, 2));
		result.add(new Bytecode(Kinds.iadd));
		result.add(new Bytecode(Kinds.push, 6));
		result.add(new Bytecode(Kinds.imul));
		result.add(new Bytecode(Kinds.push, 2));
		result.add(new Bytecode(Kinds.isub));
		result.add(new Bytecode(Kinds.push, 2));
		result.add(new Bytecode(Kinds.push, 2));
		result.add(new Bytecode(Kinds.iadd));
		result.add(new Bytecode(Kinds.idiv));
		result.end();
		return result;	
	}
	
	private static MethodArea TestIOandJumpMethods()
	{
		/*
		 * load and store and pool operation is not finish
		 * so just use the code with the same effect as the following comments
		 * 
		 * int i;
		 * read(i);
		 * if(i < 0)
		 * {
		 * write("negative!");
		 * }
		 * else
		 * {
		 * write(i); // always write 666 because we cannot load i into operant stack
		 * }
		 * 
		 */
		MethodArea result = new MethodArea();
		result.add(new Bytecode(Kinds.iread));
		result.add(new Bytecode(Kinds.push,0));
		result.add(new Bytecode(Kinds.icmpg, 4));
		result.add(new Bytecode(Kinds.push,0));
		result.add(new Bytecode(Kinds.swrite));
		result.add(new Bytecode(Kinds.gto,3));
		result.add(new Bytecode(Kinds.push,666));
		result.add(new Bytecode(Kinds.iwrite));
		result.end();
		return result;
	}
	
    private static MethodArea TestFunctionStack() {
    	/*
    	 * test the invocation of the following function
    	 * real add10(real r)
    	 * {
    	 *     real ten = 10.0;
    	 *     r = ten + r;
    	 *     return r;
    	 * }
    	 * 
    	 * int main()
    	 * {
    	 *     add10(6.5);
    	 *     return 1;
    	 * }
    	 * 
    	 */
    	MethodArea result = new MethodArea();
    	
    	// main()
    	result.add(new Bytecode(Kinds.fpush,0));    //1
		result.add(new Bytecode(Kinds.invoke, 0));  //1+1
		result.add(new Bytecode(Kinds.push,1));     //2...
		result.add(new Bytecode(Kinds.rtn));    //3
		
		// add10()
		result.add(new Bytecode(Kinds.fpush,1));    //4+1
		result.add(new Bytecode(Kinds.vstore,2));   //5
		result.add(new Bytecode(Kinds.vload,1));    //6
		result.add(new Bytecode(Kinds.vload,2));    //7
		result.add(new Bytecode(Kinds.fadd));       //8
		result.add(new Bytecode(Kinds.rtn));    //9
		//result.end();
		return result;
    }

    private static MethodArea TestXXX()
    {
    	/*
    	 * int neg()
    	 * {
    	 *     int i;
    	 *     read(i);
    	 *     if(0<i)
    	 *       xxx 0, 9, 0;
    	 *     return = 0 + i;
    	 * }
    	 * 
    	 * int main()
    	 * {
    	 *     neg();
    	 *     return 1;
    	 * }
    	 */
    	MethodArea result = new MethodArea();
    	result.add(new Bytecode(Kinds.invoke, 0)); //1
    	result.add(new Bytecode(Kinds.push, 1));
    	result.add(new Bytecode(Kinds.rtn));
    	result.add(new Bytecode(Kinds.iread));
    	result.add(new Bytecode(Kinds.vstore, 1));
    	result.add(new Bytecode(Kinds.push, 0));   //6
    	result.add(new Bytecode(Kinds.vload, 1));
    	result.add(new Bytecode(Kinds.icmpg,5));
    	result.add(new Bytecode(Kinds.push, 15));
    	result.add(new Bytecode(Kinds.push, Kinds.isub.ordinal()));
    	result.add(new Bytecode(Kinds.push, 0));  //11
    	result.add(new Bytecode(Kinds.xxx));
    	result.add(new Bytecode(Kinds.push, 0));
    	result.add(new Bytecode(Kinds.vload, 1));
    	result.add(new Bytecode(Kinds.iadd));     //15
    	result.add(new Bytecode(Kinds.rtn));
    	return result;
    }
    
    
    
}


