package bytecode;

/**
 * @author daos
 * the bytecode to be put into CPU to execute
 */
public class Bytecode {
	private Kinds kind;
	
	
	/**
	 * some byte code takes one operand
	 * they are:
	 * vload, aload, vstore, astore, push
	 * gto, icmpg, fcmpg, icmp, fcmp, invoke
	 * others' op is zero
	 */
	private int op;
	

	public Bytecode(Kinds kind) {
		super();
		this.kind = kind;
		this.op = 0;
	}

	public int getOp() {
		return op;
	}

	public Kinds getKind() {
		return kind;
	}

	public Bytecode(Kinds kind, int i) {
		super();
		this.kind = kind;
		this.op = i;
	}
	

	public void setOp(int op) {
		this.op = op;
	}
	

	@Override
	public String toString() {
		return kind.toString()+((op==0)?"":("_"+op));
	}


	
		
}
