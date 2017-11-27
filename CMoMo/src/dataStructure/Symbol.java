package dataStructure;

//符号表
public class Symbol {
    private String name;
    private int symbolType;
    private Value value;
    private int level;
    private Symbol next;    //符号表下一个符号
    
    public Symbol(String name, int symbolType, int level) {
        this.setName(name);
        this.setSymbolType(symbolType);
        this.level=level;
        this.setNext(null);
//        this.setValue(new Value(symbolType));
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSymbolType() {
		return symbolType;
	}

	public void setSymbolType(int symbolType) {
		this.symbolType = symbolType;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	public Symbol getNext() {
		return next;
	}

	public void setNext(Symbol next) {
		this.next = next;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
    
}
