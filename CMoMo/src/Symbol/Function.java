package Symbol;

import java.util.List;

import dataStructure.FourCode;

/**
 * store corresponding FourCode and function name for each function;
 *
 */
public class Function {
	private List<FourCode> functionCode;
	private String functionName;
	
	public List<FourCode> getFunctionCode() {
		return functionCode;
	}
	public void setFunctionCode(List<FourCode> functionCode) {
		this.functionCode = functionCode;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
}
