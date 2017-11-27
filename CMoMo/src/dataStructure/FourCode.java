package dataStructure;

public class FourCode {
	private String op;
	private String arg1;
	private String arg2;
	private String result;
	
	public static final String JMP = "jmp";
	public static final String READ = "read";
	public static final String WRITE = "write";
	public static final String IN = "in";
	public static final String OUT = "out";
	public static final String INT = "int";
	public static final String REAL = "real";
	public static final String ASSIGN = "assign";
	public static final String PLUS = "+";
	public static final String MINUS = "-";
	public static final String MUL = "*";
    public static final String DIV = "/";
	public static final String GT = ">";
	public static final String LT = "<";
	public static final String GET = ">=";
	public static final String LET = "<=";
	public static final String EQ = "==";
	public static final String NEQ = "<>";
	
	public FourCode(String op, String arg1, String arg2, String result) {
		this.op=op;
		this.arg1=arg1;
		this.arg2=arg2;
		this.result=result;
	}
	
	public String getOp() {
		return op;
	}
	public void setOp(String op) {
		this.op = op;
	}
	public String getArg1() {
		return arg1;
	}
	public void setArg1(String arg1) {
		this.arg1 = arg1;
	}
	public String getArg2() {
		return arg2;
	}
	public void setArg2(String arg2) {
		this.arg2 = arg2;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
}
