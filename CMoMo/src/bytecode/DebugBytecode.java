package bytecode;

public class DebugBytecode /*extends Bytecode*/{
	private String symbol;
	
	private Bytecode bytecode;

	public String getSymbol() {
		return symbol;
	}

	public DebugBytecode(Kinds kind, String symbol) {
		this.bytecode = new Bytecode(kind,0);
		this.symbol = symbol;
	}
	
	public DebugBytecode(Kinds kind, int op) {
		this.bytecode = new Bytecode(kind,op);
		this.symbol = null;
	}
	
	
	
	@Override
	public String toString() {
		return bytecode.toString()+ " " + ((symbol==null)?"":symbol);
	}

	public void setAddress(int op)
	{
		bytecode.setOp(op);
	}
	
	public Bytecode getByteCode()
	{
		return bytecode;
	}

}
