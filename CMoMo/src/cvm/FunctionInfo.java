package cvm;

/**
 * @author daos
 */
public class FunctionInfo {
	
	@Override
	public String toString() {
		return "FunctionInfo [variableCount=" + variableCount + ", pc=" + pc + ", line=" + line + ", paramCount="
				+ paramCount + "]";
	}

	/**
	 * the number of variables
	 */
	private int variableCount;
	
	/**
	 * the pc of the first assembly code in the methodArea
	 */
	private int pc;
	
	/**
	 * the line where this function is defined in the source file
	 */
	private int line;
	
	/**
	 * parameter count
	 */
	private int paramCount;

	/**
	 * @param variableCount
	 * @param pc
	 * @param line
	 * @param paramCount
	 */
	public FunctionInfo(int variableCount, int pc, int line, int paramCount) {
		super();
		this.variableCount = variableCount;
		this.pc = pc;
		this.line = line;
		this.paramCount = paramCount;
	}

	public int getParamCount() {
		return paramCount;
	}

	public int getVariableCount() {
		return variableCount;
	}

	public int getPc() {
		return pc;
	}

	public int getLine() {
		return line;
	}


	

}
