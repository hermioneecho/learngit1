package core;

import java.util.LinkedList;

import dataStructure.FourCode;
import dataStructure.SNode;
import dataStructure.SymbolTable;

//四元式生成
public class FourCodeGenerator {
	private static int lineNo;   //行号
	private static int level;
	private static LinkedList<FourCode> codes;
	private static SymbolTable symboltable;
	private static LinkedList<SNode> nodeList;
	
	public static LinkedList<FourCode> execute(SNode result) throws Exception{
		lineNo=0;
		level=0;
		try {
			symboltable=new SymbolTable();
			FourCodeGenerator generator=new FourCodeGenerator();
			symboltable.createTable();
			generator.interpret(result);
			symboltable.deleteTable();
		}catch(Exception e) {
			throw new Exception();
		}
	}
	
	public void interpret(SNode node) {
		switch(node.getTag()) {
			case "declaration":
				declaration(node);
				break;
			case "definition":
				definition(node);
				break;
			case "function-definition":
				function(node);
				break;
			case "if_node":
				ifStatement(node);
				break;
			case "if_else_node":
				ifElseStatement(node);
				break;
			case "while_node":
				whileStatement(node);
				break;
			case "assignment":
				assignment(node);
				break;
			case "read":
				readStatement(node);
				break;
			case "write":
				writeStatment(node);
				break;
		}
	}
	
	public void ifStatement(SNode node)throws Exception{
		if(node.getTag()==SNode.If_node) {
			int i=0;    //SNode的子树结点下标
			FourCode if_jmp=new FourCode(FourCode.JMP,expression(node.getChildAt(i)),null,null);
			i++;
			codes.add(if_jmp);
			lineNo++;
			codes.add(new FourCode(FourCode.IN,null,null,null));
			lineNo++;
			level++;
			interpret(node.getChildAt(i));
			i++;
			SymbolTable.getSymbolTable().deleteFromTable(level);
			level--;
			codes.add(new FourCode(FourCode.OUT,null,null,null));
			lineNo++;
			if_jmp.setResult(String.valueOf(lineNo)+1);
		}else {
			throw new Exception("输入的语法树结点有误！");
		}
	}
	
	public void ifElseStatement(SNode node)throws Exception{
		if(node.getTag()==SNode.If_else_node) {
			int i=0;    //SNode的子树结点下标
			FourCode if_jmp=new FourCode(FourCode.JMP,expression(node.getChildAt(i)),null,null);
			i++;
			codes.add(if_jmp);
			lineNo++;
			codes.add(new FourCode(FourCode.IN,null,null,null));
			lineNo++;
			level++;
			interpret(node.getChildAt(i));
			i++;
			SymbolTable.getSymbolTable().deleteFromTable(level);
			level--;
			codes.add(new FourCode(FourCode.OUT,null,null,null));
			lineNo++;
			FourCode else_jmp=new FourCode(FourCode.JMP,null,null,null);
			codes.add(else_jmp);
			lineNo++;
			if_jmp.setResult(String.valueOf(lineNo+1));
			codes.add(new FourCode(FourCode.IN, null, null, null));
            lineNo++;
            level++;
            interpret(node.getChildAt(i));
            i++;
            codes.add(new FourCode(FourCode.OUT, null, null, null));
            lineNo++;
            SymbolTable.getSymbolTable().deleteFromTable(level);
            level--;
            else_jmp.setResult(String.valueOf(lineNo + 1));
		}else {
			throw new Exception("输入的语法树结点有误！");
		}
	}
	
	public void whileStatement(SNode node)throws Exception{
		
	}
	
	public void declaration(SNode node) throws Exception{
	}
	
	public void definition(SNode node) throws Exception{
		
	}
	
	public void function(SNode node) throws Exception{
		
	}
	
	public void assignment(SNode node)throws Exception{
		
	}

	public void writeStatment(SNode node) throws Exception{
		
	}
	
	public String expression(SNode node) throws Exception{
		
	}
	
	public void readStatement(SNode node)throws Exception{
		
	}

	public static int getLineNo() {
		return lineNo;
	}

	public static void setLineNo(int lineNo) {
		FourCodeGenerator.lineNo = lineNo;
	}

	public static int getLevel() {
		return level;
	}

	public static void setLevel(int level) {
		FourCodeGenerator.level = level;
	}

	public static SymbolTable getSymboltable() {
		return symboltable;
	}

	public static void setSymboltable(SymbolTable symboltable) {
		FourCodeGenerator.symboltable = symboltable;
	}

	public static LinkedList<FourCode> getCodes() {
		return codes;
	}

	public static void setCodes(LinkedList<FourCode> codes) {
		FourCodeGenerator.codes = codes;
	}
}
