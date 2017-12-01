package dataStructure;

/**
 *store syntax errors in FourCodeGenerator.
 */
public class SyntaxException {
	private String errorMsg=new String();
	private int lineNo;       //code line number for error
	
	public SyntaxException(String errorMsg, int lineNo) {
		this.setErrorMsg(errorMsg);
		this.lineNo=lineNo;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public int getLineNo() {
		return lineNo;
	}

	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}
}
