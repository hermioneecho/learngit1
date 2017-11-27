package dataStructure;

import java.util.LinkedList;

public class SymbolTable {
	private static SymbolTable symbolTable=new SymbolTable();
	private static LinkedList<Symbol> symbolList;
	
	public static SymbolTable getSymbolTable() {
		return symbolTable;
	}

	public static void setSymbolTable(SymbolTable symbolTable) {
		SymbolTable.symbolTable = symbolTable;
	}

	public static LinkedList<Symbol> getSymbolList() {
		return symbolList;
	}

	public static void setSymbolList(LinkedList<Symbol> symbolList) {
		SymbolTable.symbolList = symbolList;
	}
	
	public void createTable() {
		symbolList=new LinkedList<Symbol>();
	}
	
	public void deleteTable() {
		if(symbolList!=null) {
			symbolList.clear();
			symbolList=null;
		}
	}
	
	//将符号添加到符号表中
	public void addToTable(Symbol symbol) throws Exception{
		for(int i=0;i<symbolList.size();i++) {
			if(symbolList.get(i).getName().equals(symbol)) {
				if(symbolList.get(i).getLevel()<symbol.getLevel()) {
				symbol.setNext(symbolList.get(i));
				symbolList.set(i, symbol);
				return;
			}else {
				throw new Exception("符号"+symbol.getName()+"已经存在于符号表中!");
			}
		  }
		}
		symbolList.add(symbol);
	}
	
	//将符号从符号表中删除并返回上一层
	public void deleteFromTable(int level) throws Exception{
		for(int i=0;i<symbolList.size();i++) {
			if(symbolList.get(i).getLevel()==level) {
				symbolList.set(i,symbolList.get(i).getNext());
			}
		}
		for (int i=symbolList.size()-1 ; i>=0; i--) {
            if (symbolList.get(i) == null) {
                symbolList.remove(i);
            }
        }
	}

}
