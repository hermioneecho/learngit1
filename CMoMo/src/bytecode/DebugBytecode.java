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
	
	public DebugBytecode(Kinds kind, int op, String symbol) {
		this.bytecode = new Bytecode(kind,op);
		this.symbol = symbol;
	}
	
	
	
	@Override
	public String toString() {
		return bytecode.toString()+ " " + ((symbol==null)?"":symbol);
	}

	/**
	 * @param op
	 * @ use for loading time, when replace the symbol by an address op
	 * @ can be used to change the op too
	 */
	public void setAddress(int op)
	{
		bytecode.setOp(op);
	}
	
	public Bytecode getByteCode()
	{
		return bytecode;
	}

}
