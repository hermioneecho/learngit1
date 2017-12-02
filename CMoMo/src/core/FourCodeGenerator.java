package core;

import java.util.ArrayList;
import java.util.List;

import Symbol.Function;
import Symbol.TARG;
import dataStructure.FourCode;
import dataStructure.SNode;
import dataStructure.SyntaxException;
/*
 * FourCode generator
 */
public class FourCodeGenerator {
	private static int lineNo;   //line number of each Four Code, start from zero
	
	/**
	 * temporary variable
	 */
	private static TARG temp;    
	/**
	 * store the obtained symbol in corresponding symbol list respectively.
	 */
	private static ArrayList<TARG> targ;
	
	/**
	 * list for parameter in function definition
	 */
	private static ArrayList<SNode> parameterList;
	
	private static ArrayList<FourCode> codes;
	
	/**
	 * arrayList to store functions
	 */
	private static ArrayList<Function> functionList;
	
	private static ArrayList<SyntaxException> error;
	
	/**
	 * @param result
	 * @return
	 * @throws Exception
	 * The input is the TreeNode List from Syntaxes, and output is an array list of FourCode.
	 */
	public static ArrayList<FourCode> execute(SNode result) throws Exception{
		lineNo=0;     //FourCode starts from 0.
		try 
		{
			FourCodeGenerator generator=new FourCodeGenerator();
			generator.interpret(result);
			return codes;
		}
		catch(Exception e) 
		{
			throw new Exception();
		}
	}
	
	/**
	 * @param node
	 * @throws Exception
	 * Jump to the corresponding function according to the Tag of the root node. 
	 */
	public void interpret(SNode node) throws Exception {
		switch(node.getTag())
		{
	    	case "function_definition":
	    		functionDefinition(node);
	    		break;
			case "function_call":
				functionCall(node);
				break;
			case "declaration":
				declaration(node);
				break;
			case "definition":
				definition(node);
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
			case "read":
				readStatement(node);
				break;
			case "write":
				writeStatment(node);
				break;
		}
	}
	
	public void functionDefinition(SNode node) throws Exception{
		//TODO:get the lineNo of code for the appearance of each parameter.
		int i=0;
		int j=1;
		
		Function function=new Function();
		
		SNode returnValue=node.getChildAt(i);
		SNode functionName=node.getChildAt(i+1);
		SNode parameterList=node.getChildAt(i+2);
		int fromIndex=lineNo;
		
		if(returnValue.getTag()=="int")
		{
			codes.add(new FourCode(FourCode.INT,null,String.valueOf(lineNo++),functionName.getTag(),getLineNo()));
		}
		else if(returnValue.getTag()=="real")
		{
			codes.add(new FourCode(FourCode.REAL,null,String.valueOf(lineNo++),functionName.getTag(),getLineNo()));
		}
		else {
			error.add(new SyntaxException("Function<"+functionName.getTag()+"> lacks return value.",returnValue.getLineNum()));
		}
		//get the parameters from parameter list and add a FourCode for each parameter
		SNode checkPL=node.getChildAt(i+2+j);
		for(j=1;j<=node.getDepth();j++)
		{
			if(checkPL.getTag()=="parameter")
			{
				SNode tag=node.getChildAt(i+2+j+1);
				SNode identifier=node.getChildAt(i+2+j+2);
				if(tag.getContents().toString()=="int") 
				{
					codes.add(new FourCode(FourCode.INT,identifier.getContents().toString(),FourCode.ARGUMENT,null,getLineNo()));
				}
				else if(tag.getContents().toString()=="real")
				{
					codes.add(new FourCode(FourCode.REAL,identifier.getContents().toString(),FourCode.ARGUMENT,null,getLineNo()));
				}
				else {
					error.add(new SyntaxException("Parameter<"+identifier.getContents().toString()+"> lacks data type.",checkPL.getLineNum()));
				}
				
				parameterList.add(identifier);     
			}
			//if there are no parameter
			else {
				break;
			}
			j+=2;
		}
		
		codes.add(new FourCode(FourCode.JUMP0,String.valueOf(lineNo+2),null,null,getLineNo()));
		
		//set a FourCode to record the entry to function body
		codes.add(new FourCode(FourCode.IN,null,null,null,getLineNo()));
		SNode body=node.getChildAt(i+2+j);
		interpret(body);
		
		//FourCode for return statement
		for(int x=0;x<node.getDepth();x++) 
		{
			if(node.getTag()=="return_node")
			{
				codes.add(new FourCode(FourCode.RETURN,String.valueOf(node.getChildAt(x+1).getContents()),null,String.valueOf(fromIndex),getLineNo()));
			}
			else
			{
				error.add(new SyntaxException("Function<"+functionName.getTag()+"> lacks return statement.",node.getChildAt(x+1).getLineNum()));
			}
		}
		
		//set a FourCode to record the out of the function body
		int toIndex=lineNo;
		codes.add(new FourCode(FourCode.OUT,null,null,null,getLineNo()));
		
		List<FourCode> functionCode=codes.subList(fromIndex, toIndex);
		function.setFunctionCode(functionCode);
		function.setFunctionName(functionName.getTag());
		functionList.add(function);
	}
	
	public void ifStatement(SNode node)throws Exception{
			int i=0;     //the subscript of the child node
			FourCode if_jmp=new FourCode(FourCode.JUMP,expression(node.getChildAt(i)),null,null,getLineNo());
			i++;
			codes.add(if_jmp);
			codes.add(new FourCode(FourCode.IN,null,null,null,getLineNo()));
			interpret(node.getChildAt(i));
			i++;
			codes.add(new FourCode(FourCode.OUT,null,null,null,getLineNo()));
			if_jmp.setResult(String.valueOf(lineNo)+1);
	}
	
	public void ifElseStatement(SNode node)throws Exception{
			int i=0;   
			FourCode if_jmp=new FourCode(FourCode.JUMP,expression(node.getChildAt(i)),null,null,getLineNo());
			i++;
			codes.add(if_jmp);
			codes.add(new FourCode(FourCode.IN,null,null,null,getLineNo()));
			interpret(node.getChildAt(i));
			i++;
			codes.add(new FourCode(FourCode.OUT,null,null,null,getLineNo()));
			FourCode else_jmp=new FourCode(FourCode.JUMP,null,null,null,getLineNo());
			codes.add(else_jmp);
			if_jmp.setResult(String.valueOf(lineNo+1));
			codes.add(new FourCode(FourCode.IN, null, null, null,getLineNo()));
            interpret(node.getChildAt(i));
            i++;
            codes.add(new FourCode(FourCode.OUT, null, null, null,getLineNo()));
            else_jmp.setResult(String.valueOf(lineNo + 1));
	}
	
	public void whileStatement(SNode node)throws Exception{
		int i=0;
		FourCode jmp=new FourCode(FourCode.JUMP,expression(node.getChildAt(i)),null,null,getLineNo());
		i++;
		codes.add(jmp);
		codes.add(new FourCode(FourCode.IN,null,null,null,getLineNo()));
		interpret(node.getChildAt(i));
		codes.add(new FourCode(FourCode.OUT,null,null,null,getLineNo()));
		codes.add(new FourCode(FourCode.JUMP,null,null,String.valueOf(lineNo+1),getLineNo()));
		jmp.setResult(String.valueOf(lineNo+1));
	}
	
	/**
	 * @param node
	 * @throws Exception
	 * variable declaration, including single value,pointer and array value.
	 */
	public void declaration(SNode node) throws Exception{
		int i=0;
		if(node.getChildAt(i).getTag()=="DT") 
		{
			if(node.getChildAt(i).getContents()=="int") 
			{
				//is a pointer?
				if(node.getChildAt(i+1).equals(true)) {
					codes.add(new FourCode(FourCode.INT,null,"*",String.valueOf(node.getChildAt(i+2).getContents()),getLineNo()));
				}
				else if(node.getChildAt(i+1).equals(false))
				{
					codes.add(new FourCode(FourCode.INT,null,node.getChildAt(i+3).getContents().toString(),String.valueOf(node.getChildAt(i+2).getContents()),getLineNo()));
		        }
				else
				{
					error.add(new SyntaxException("Variable<"+node.getChildAt(i+2).getContents()+"> is not a single value,"
							+ "a pointer or an array",node.getChildAt(i).getLineNum()));
				}
			}
			else if(node.getChildAt(i).getContents()=="real")
			{
				//is a pointer?
				if(node.getChildAt(i+1).equals(true)) {
					codes.add(new FourCode(FourCode.REAL,null,"*",String.valueOf(node.getChildAt(i+2).getContents()),getLineNo()));
				}
				else if(node.getChildAt(i+1).equals(false))
				{
					codes.add(new FourCode(FourCode.REAL,null,node.getChildAt(i+3).getContents().toString(),String.valueOf(node.getChildAt(i+2).getContents()),getLineNo()));
		        }
				else
				{
					error.add(new SyntaxException("Variable<"+node.getChildAt(i+2).getContents()+"> is not a single value,"
							+ "a pointer or an array",node.getChildAt(i).getLineNum()));
				}
		    }
		}
		else {
			error.add(new SyntaxException("Variable<"+String.valueOf(node.getChildAt(i+1).getContents())+"> is not declared data type.",node.getChildAt(i).getLineNum()));
		}
	}
	
	public void definition(SNode node) throws Exception{
		//TODO
		int i=0;
		String value=expression(node.getChildAt(i+2));
		//if not an array
		if(node.getChildAt(i).getTag()=="assignment")
		{
			codes.add(new FourCode(FourCode.ASSIGN,value,null,String.valueOf(node.getChildAt(i+1).getContents()),getLineNo()));
		}
		//if an array
		if(node.getChildAt(i+2).getTag()=="array_literal_node")
		{
			codes.add(new FourCode(FourCode.ASSIGN,value,null,(String.valueOf(node.getChildAt(i+1).getContents())+"["+"]"),getLineNo()));
		}
		if(no)
	}

	public void writeStatment(SNode node) throws Exception{
		int i=0;
		SNode exp=node.getChildAt(i);
		codes.add(new FourCode(FourCode.WRITE,expression(exp),null,temp.getTemp(),getLineNo()));
		targ.add(temp);
	}
	
	public void readStatement(SNode node)throws Exception{
		int i=0;
		SNode var=node.getChildAt(i);
		codes.add(new FourCode(FourCode.READ,var.getContents().toString(),null,temp.getTemp(),getLineNo()));
		targ.add(temp);
	}

	/**
	 * @param node
	 * @return
	 * @throws Exception
	 * do with expression, including logical expression, term expression,variable, left value and normal expression
	 */
	public String expression(SNode node) throws Exception{
		switch(node.getTag())
		{
		case "cond":
			logicalExpression(node);
			break;
		case "add":
			addExpression(node);
			break;
		case "sub":
			subExpression(node);
			break;
		case "mul":
			mulExpression(node);
			break;
		case "div":
			divExpression(node);
			break;
		case "read":
			return (String)node.getChildAt(0).getContents();
		case "write":
			return (String)node.getChildAt(0).getContents();
		case "write_string":
			return (String)node.getChildAt(0).getContents();
		case "integer_literal":
			return (String) node.getContents();
		case "real_literal":
			return (String) node.getContents();
		case "address_of_identifier":
			return (String) node.getContents();
//		case "left_value":
//			leftValue(node);
//			break;
		}
		throw new Exception("Error in expression!");
	}
	
	/**
	 * @param node
	 * @return
	 * @throws Exception
	 * do with function call, find the lineNo of FourCode of the function definition according to its name and jump to the
	 * lineNo and execute. 
	 */
	public void functionCall(SNode node)throws Exception {
		int i=0;
		String functionName=node.getChildAt(i).getContents().toString();
		for(Function function:functionList)
		{
			if(function.getFunctionName()==functionName)
			{
				//get the lineNo of FourCode of the function definition
				int jumpTo=function.getFunctionCode().get(0).getLineN();
				for(int x=1;x<=node.getDepth();x++) 
				{
					if(node.getChildAt(i+1+x).getTag()=="parameter") 
					{
						SNode tag=node.getChildAt(i+1+x+1);
						SNode identifier=node.getChildAt(i+1+x+2);
						codes.add(new FourCode(tag.getContents().toString(),identifier.getContents().toString(),FourCode.PARAMETER,null,getLineNo()));     
					}
					//if there are no parameter list
					else {
						break;
					}
				}
				codes.add(new FourCode(FourCode.INVOKE,String.valueOf(jumpTo),null,String.valueOf(functionName),getLineNo()));
			}
			else
			{
				error.add(new SyntaxException("Function<"+String.valueOf(functionName)+"> is not defined.",node.getChildAt(i).getLineNum()));
			}
				
		  }
		}
	
	/**
	 * @param node
	 * @return
	 * @throws Exception
	 * deal with logical expression, including LT,GT,LET,GET....
	 */
	public String logicalExpression(SNode node) throws Exception{
		int i=0;
		String tempName=temp.getTemp();
		SNode arg1=node.getChildAt(i);
		SNode arg2=node.getChildAt(i+1);
		switch(String.valueOf(node.getContents()))
		{
		case "<>":
			codes.add(new FourCode(FourCode.NEQ,expression(arg1),expression(arg2),tempName,getLineNo()));
			targ.add(temp);
			break;
		case "<":
			codes.add(new FourCode(FourCode.LT,expression(arg1),expression(arg2),tempName,getLineNo()));
			targ.add(temp);
			break;
		case "==":
			codes.add(new FourCode(FourCode.EQ,expression(arg1),expression(arg2),tempName,getLineNo()));
			targ.add(temp);
			break;
		case ">":
			codes.add(new FourCode(FourCode.GT,expression(arg1),expression(arg2),tempName,getLineNo()));
			targ.add(temp);
			break;
		case "<=":
			codes.add(new FourCode(FourCode.LET,expression(arg1),expression(arg2),tempName,getLineNo()));
			targ.add(temp);
			break;
		case ">=":
			codes.add(new FourCode(FourCode.GET,expression(arg1),expression(arg2),tempName,getLineNo()));
			targ.add(temp);
			break;
			default:
				throw new Exception("The input is not a logical symbol!");
		}
		return tempName;
	}
	
	public String addExpression(SNode node) throws Exception{
		int i=0;
		String tempName=temp.getTemp();
		SNode arg1=node.getChildAt(i);
		SNode arg2=node.getChildAt(i+1);
		codes.add(new FourCode(FourCode.ADD,expression(arg1),expression(arg2),temp.getTemp(),lineNo));
		lineNo++;
		return tempName;
	}
	
	public String subExpression(SNode node) throws Exception{
		int i=0;
		String tempName=temp.getTemp();
		SNode arg1=node.getChildAt(i);
		SNode arg2=node.getChildAt(i+1);
		codes.add(new FourCode(FourCode.SUB,expression(arg1),expression(arg2),temp.getTemp(),lineNo));
		lineNo++;
		return tempName;
	}

	public String mulExpression(SNode node) throws Exception{
		int i=0;
		String tempName=temp.getTemp();
		SNode arg1=node.getChildAt(i);
		SNode arg2=node.getChildAt(i+1);
		codes.add(new FourCode(FourCode.MUL,expression(arg1),expression(arg2),temp.getTemp(),lineNo));
		lineNo++;
		return tempName;
	}
	
	public String divExpression(SNode node) throws Exception{
		int i=0;
		String tempName=temp.getTemp();
		SNode arg1=node.getChildAt(i);
		SNode arg2=node.getChildAt(i+1);
		codes.add(new FourCode(FourCode.DIV,expression(arg1),expression(arg2),temp.getTemp(),lineNo));
		lineNo++;
		return tempName;
	}
	
//	public String leftValue(SNode node) throws Exception{
//		
//	}
	
	public int getLineNo() {
		lineNo++;
		return (lineNo-1);
	}

	public static void setLineNo(int lineNo) {
		FourCodeGenerator.lineNo = lineNo;
	}

	public static ArrayList<FourCode> getCodes() {
		return codes;
	}

	public static void setCodes(ArrayList<FourCode> codes) {
		FourCodeGenerator.codes = codes;
	}

	public static ArrayList<SNode> getParamterList() {
		return parameterList;
	}

	public static void setParamterList(ArrayList<SNode> paramterList) {
		FourCodeGenerator.parameterList = paramterList;
	}

	public static ArrayList<SyntaxException> getError() {
		return error;
	}

	public static void setError(ArrayList<SyntaxException> error) {
		FourCodeGenerator.error = error;
	}

}