package cvm;

import java.util.ArrayList;

public class Pool {
	private String[] strings;
	private FunctionInfo[] methods;
	private double[] floats;
	private Object[] globalVariables;
	
	public Pool(String[] strings, FunctionInfo[] methods) {
		super();
		this.strings = strings;
		this.methods = methods;
		this.floats = null;
	}
	

	public Pool(String[] strings, double[] floats, FunctionInfo[] methods) {
		super();
		this.strings = strings;
		this.methods = methods;
		this.floats = floats;
	}
	
	public double getFloatLiteral(int index)
	{
		return floats[index];
	}


	public String getString(int index)
	{
		return strings[index];
	}
	public FunctionInfo getMethod(int index)
	{
		return methods[index];
	}

	public int getPc(int index)
	{
		return methods[index].getPc();
	}
	/**
	 * @param index of the function
	 * @return the number of local variables
	 */
	public int getVariableCount(int index)
	{
		return methods[index].getVariableCount();
	}
	
	public int getParameterCOunt(int index)
	{
		return methods[index].getParamCount();
	}
	
	public Object getGlobalVariable(int index)
	{
		return globalVariables[index];
	}
	
	public void setGlobalVariable(int index, Object o)
	{
		globalVariables[index] = o;
	}
	
	public void printPool()
	{
		System.out.println("Pool:");
		int j = 1;
		for(int i = 0; i<strings.length; i++)
			System.out.println("    #"+(i+(j++)+" "+strings[i]));
		for(int i = 0; i<floats.length; i++)
			System.out.println("    #"+(i+(j++)+" "+floats[i]));
		for(int i = 0; i<globalVariables.length; i++)
			System.out.println("    #"+(i+(j++)+" "+globalVariables[i]));
		for(int i = 0; i<methods.length; i++)
			System.out.println("    #"+(i+(j++)+" "+methods[i].toString()));
		System.out.println();
	}

}
