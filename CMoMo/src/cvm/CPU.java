package cvm;

import java.io.IOException;
import java.security.Policy.Parameters;
import java.util.ArrayList;
import java.util.function.Consumer;

import bytecode.Bytecode;
import bytecode.Kinds;

/**
 * where the Bytecode is executed
 * and the control flow is changed here
 * and registers are store here
 * @author daos
 *
 */
public class CPU {
	
	/**
	 * points to next Bytecode to be executed
	 * the first pc is one, because zero is set to be null to mark the end of program
	 */
	private int pc;
	
	/**
	 * no real-time sp, the real-time sp is in stackArea
	 * updates per bytecode execution
	 * has its usage in invoke
	 */
	private int sp;
	
	/**
	 * point to the bottom of the current stack frame
	 * first bp = 0;
	 */
	private int bp;
	
	/**
	 * point to the beginning of the variable and parameter section
	 * of the current stack frame
	 * first vars = 0;
	 */
	private int vars;
	
	/**
	 * first element in stack is 0, which leads the pc to be zero
	 * the first function to execute is main
	 */
	private StackArea stackArea;
	
	private Pool pool;
	
	/**
	 * the first methodArea must belong to main()
	 */
	private MethodArea methodArea;
		
	/**
	 * for simplifying the execute()
	 * otherwise a massive number of cases will occur in the code and looks ugly
	 * and the mechanism is basically the same but looks better
	 */
	private static ArrayList<Consumer<Integer>> FunctionalCircuits = null;
	
	public CPU(StackArea stackArea, Pool pool, MethodArea methodArea) {
		super();
		if(this.FunctionalCircuits == null)
			initializeExecutors();
		this.stackArea = stackArea;
		this.pool = pool;
		this.methodArea = methodArea;
		resetCPU();

	}

	public void resetCPU()
	{
		pc = 1;
		sp = 1;
		bp = 0;
		vars = 0;
		stackArea.empty();
	}
			
	public void setPool(Pool pool) {
		this.pool = pool;
	}

	public void setMethodArea(MethodArea methodArea) {
		this.methodArea = methodArea;
		this.stackArea = new StackArea();
	}
	public Bytecode nextCode()
	{
		return methodArea.getCode(pc);
	}

	public int getPc() {
		return pc;
	}

	public int getSp() {
		return sp;
	}

	public int getBp() {
		return bp;
	}

	public int getVars() {
		return vars;
	}


	public void printStack()
    {
    	for(int i=1; i<stackArea.getSp();i++)
    	{
    		System.out.println(stackArea.getValue(i));
    	}
    	System.out.println("---------------------------------------------------------");
    }
	
    public void printAssembly()
    {
    	for(Bytecode code : methodArea.getBytecodes())
    	{
    		System.out.println(code);
    	}
    }
	

	void execute(Bytecode code) {
		int op = code.getOp();
		FunctionalCircuits.get(code.getKind().ordinal()).accept(op);
		sp = stackArea.getSp();
	}
	
	/**
	 * get every circuit ready
	 */
	private void initializeExecutors() {
		FunctionalCircuits = new ArrayList<Consumer<Integer>>();
		for(int i=0; i<Kinds.values().length; i++)
			FunctionalCircuits.add(null);
		FunctionalCircuits.set(Kinds.push.ordinal(), (op)->
		{
			stackArea.push(op);
			pc++;
		});
		FunctionalCircuits.set(Kinds.fpush.ordinal(), (op)->
		{
			stackArea.push(pool.getFloatLiteral(op));
			pc++;
		});
		FunctionalCircuits.set(Kinds.idiv.ordinal(), (op)->
		{
			int op2 = (int) stackArea.pop();
			int op1 = (int) stackArea.pop();
			if(op2 == 0)
				return;
			stackArea.push(op1/op2);
			pc++;
		});
		FunctionalCircuits.set(Kinds.imul.ordinal(), (op)->
		{
			int op2 = (int) stackArea.pop();
			int op1 = (int) stackArea.pop();
			stackArea.push(op1*op2);
			pc++;
		});
		FunctionalCircuits.set(Kinds.isub.ordinal(), (op)->
		{
			int op2 = (int) stackArea.pop();
			int op1 = (int) stackArea.pop();
			stackArea.push(op1-op2);
			pc++;
		});	
		FunctionalCircuits.set(Kinds.iadd.ordinal(), (op)->
		{
			int op2 = (int) stackArea.pop();
			int op1 = (int) stackArea.pop();
			stackArea.push(op1+op2);
			pc++;
		});
		FunctionalCircuits.set(Kinds.fdiv.ordinal(), (op)->
		{
			double op2 = (double) stackArea.pop();
			double op1 = (double) stackArea.pop();
			stackArea.push(op1/op2);
			pc++;
		});
		FunctionalCircuits.set(Kinds.fmul.ordinal(), (op)->
		{
			double op2 = (double) stackArea.pop();
			double op1 = (double) stackArea.pop();
			stackArea.push(op1*op2);
			pc++;
		});
		FunctionalCircuits.set(Kinds.fsub.ordinal(), (op)->
		{
			double op2 = (double) stackArea.pop();
			double op1 = (double) stackArea.pop();
			stackArea.push(op1-op2);
			pc++;
		});
		FunctionalCircuits.set(Kinds.fadd.ordinal(), (op)->
		{
			double op2 = (double) stackArea.pop();
			double op1 = (double) stackArea.pop();
			stackArea.push(op1+op2);
			pc++;
		});	
		FunctionalCircuits.set(Kinds.iread.ordinal(), (op)->
		{
			try {
				System.out.println("read from console to get an integer, but later we should should have a console class to interact with this assembly code:");
				int i = System.in.read();			
				i = Integer.parseInt(String.format("%c", i));
				stackArea.push(i);
				pc++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		});
		FunctionalCircuits.set(Kinds.fread.ordinal(), (op)->
		{
			try {
				System.out.println("read from console to get an integer, but later we should have a console class to interact with this assembly code:");
				int i = System.in.read();
				i = Integer.parseInt(""+((char)i));
				stackArea.push(i);
				pc++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		});
		FunctionalCircuits.set(Kinds.swrite.ordinal(), (op)->
		{
			System.out.println("later we should have a Console to interact with this assembly code:");
			System.out.println(pool.getString((int)stackArea.pop()));
			pc++;
		});
		FunctionalCircuits.set(Kinds.iwrite.ordinal(), (op)->
		{
			System.out.println("later we should have a Console to interact with this assembly code:");
			System.out.println((int)stackArea.pop());
			pc++;
		});
		FunctionalCircuits.set(Kinds.fwrite.ordinal(), (op)->
		{
			System.out.println("later we should have a Console to interact with this assembly code:");
			System.out.println((double)stackArea.pop());
			pc++;
		});
		FunctionalCircuits.set(Kinds.gto.ordinal(), (op)->
		{
			pc = pc+op;
		});
		FunctionalCircuits.set(Kinds.icmpg.ordinal(), (op)->
		{
			int op2 = (int) stackArea.pop();
			int op1 = (int) stackArea.pop();
			if(op1 < op2)
				pc++;
			else
				pc+=op;
		});	
		FunctionalCircuits.set(Kinds.fcmpg.ordinal(), (op)->
		{
			double op2 = (double) stackArea.pop();
			double op1 = (double) stackArea.pop();
			if(op1 < op2)
				pc++;
			else
				pc+=op;
		});	
		FunctionalCircuits.set(Kinds.icmp.ordinal(), (op)->
		{
			int op2 = (int) stackArea.pop();
			int op1 = (int) stackArea.pop();
			if(op1 == op2)
				pc++;
			else
				pc+=op;
		});	
		FunctionalCircuits.set(Kinds.fcmp.ordinal(), (op)->
		{
			double op2 = (double) stackArea.pop();
			double op1 = (double) stackArea.pop();
			if(op1 == op2)
				pc++;
			else
				pc+=op;
		});	
		FunctionalCircuits.set(Kinds.invoke.ordinal(), (index)->
		{
			ArrayList<Object> params = new ArrayList<Object>();
			int psize = pool.getParameterCOunt(index);
			for(int i=0;i<psize;i++)
			{
				params.add(stackArea.pop());
			}
			// return value
			stackArea.push(null);
			stackArea.push(bp);
			stackArea.push(sp-psize+1); // use sp rather than stackArea.getSP()
			stackArea.push(vars);
			stackArea.push(pc+1);
			// var points to the pc
			vars = stackArea.getSp()-1;
			//bp serves as the bottom of the operand stack
			bp = vars;
			for(int i=0; i < psize; i++)
			{
				stackArea.push(params.get(i));
				bp++;
			}
			// pool should contains the size of local variable 
			stackArea.alloca(pool.getVariableCount(index)-psize);
			pc = pool.getPc(index);
			
		});
		FunctionalCircuits.set(Kinds.rtn.ordinal(), (op)->
		{
			if((int)stackArea.getValue(vars)==0)
			{
				pc = 0;
			}
			else
			{
				stackArea.setValue(vars-4, stackArea.getTop());
				pc = (int)stackArea.getValue(vars);
				sp = (int)stackArea.getValue(vars-2);
				bp = (int)stackArea.getValue(vars-3);
				vars = (int)stackArea.getValue(vars-1);
				stackArea.setSp(sp);
			}
		});

		FunctionalCircuits.set(Kinds.vload.ordinal(), (op)->
		{
			stackArea.push(stackArea.getValue(vars+op));
			pc++;
		});
		FunctionalCircuits.set(Kinds.vstore.ordinal(), (op)->
		{
			stackArea.setValue(vars+op, stackArea.pop());;
			pc++;
		});
		FunctionalCircuits.set(Kinds.xxx.ordinal(), (op)->
		{
			int operand = (int)stackArea.pop();
			int opcode = (int)stackArea.pop();
			int address = (int)stackArea.pop();
			methodArea.setBytecode(address, opcode, operand);			
			pc++;
		});
		
		
	}
	
}
