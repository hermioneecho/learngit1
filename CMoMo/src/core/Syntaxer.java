package core;

import java.util.ArrayList;
import dataStructure.SNode;
import dataStructure.Token;

public class Syntaxer extends Info{
	private SyntaxerReader stxr;
	private SNode root;

	
	
	public SNode getRoot() {
		return root;
	}

	public Syntaxer(Lexer lxr) {
		super();
		stxr = new SyntaxerReader(lxr);
		root = null;
	}
	
	public boolean check()
	{
		return (root=program())!=null;
	}
	
	/*
	 * version 4
	 * rewritten those functions so that they all return a tree node
	 * and rewrite next() function to synchronize line number
	 * the main target for these version is to include the line number 
	 * and build the syntax tree
	 * to get a line number, call getLine() of SyntaxReader
	 */
	
	
	/*
	 *  after adding the following two functions, it seems no need to save and read save? ___ no I still need to
	 *  I should not provide the functions that do roll back
	 */
	
	/**
	 * added in developing version 3
	 * is the next token's word equals parameter word
	 * if false, it would roll back automatically
	 */
	private boolean isWord(String word)
	{
		Token tmp = stxr.next();
		if(tmp!=null && tmp.is(word))
			return true;
		stxr.back();
		return false;
	}
	
	private SNode xxx()
	{
		int save = stxr.getCur();
		
		if(isWord("xxx"))
		{
			SNode result = new SNode("xxx",null,stxr.getLine());
			SNode integer = Integer_Literal();
			if(integer!=null && isWord(","))
			{
				result.add(integer);
				integer = Integer_Literal();
				if(integer!=null && isWord(","))
				{
					result.add(integer);
					integer = Integer_Literal();
					if(integer != null && isWord(";"))
					{
						result.add(integer);
						return result;
					}
				}
			}
		}
		
		stxr.setCur(save);
		return null;
	}
	
	
	private SNode program()
	{
		SNode result = new SNode("program",null,stxr.getLine());
		SNode tmp = null;
		while(stxr.hasNext())
		{			
			// if the the next following tokens are FunctionDefinition or VariableDefinitionorDeclaration
		    if((tmp=FDf())!=null||(tmp=VDD())!=null)
		    {
		    	result.add(tmp);
		    	continue;
		   	}
		    //if it is the end of the program
		    else if(stxr.next()==null)
		    {
		    	continue;
		    }
		    System.out.println("Syntax Error may be at line " + stxr.getErrorLine() + ", the word \""+stxr.getErrorToken().getWord()+"\"");
		    return null;
		}
		
		// syntax direct	
		System.out.println("program");
		return result;
	}
	
	private SNode FDf() {
		// TODO Auto-generated method stub
		//
		// Function Definition
		
		int save = stxr.getCur();
		
		SNode dt = null;
		SNode identifier = null;
		SNode pl = null;
		SNode rtn = null;
		SNode statement = null;
		SNode result = new SNode("function_definition",null,stxr.getLine());
				
		if((dt=DT()) != null)
		{
			Token tmp = stxr.next();
			if(tmp != null && is(tmp,"identifier"))
			{
				identifier = mkid(tmp);
				if((pl=PL())!=null && isWord("{"))
			{		
					// do something special to guide the 
					// code generation
					result.add(dt);
					result.add(identifier);
					result.add(pl);
					while((statement=Statement())!=null)
					{
						//syntax direct
						result.add(statement);						
					}
					if((rtn=Return())!=null && isWord("}"))
					{
						//syntax direct
						System.out.println("FDf");
						result.add(rtn);
						return result;
					}
				}
		    }
		}
		// error report
		stxr.setCur(save);
		return null;
	}

	private SNode Return() {
		// TODO Auto-generated method stub
		int save = stxr.getCur();
		
		SNode rv = null;
		SNode result = null;
		if(isWord("return") && (rv=RightValue())!=null && isWord(";"))
		{
			// syntax direct
			result = new SNode("return_node",null,stxr.getLine());
			result.add(rv);
			System.out.println("Return");
			return result;
		}
		
		stxr.setCur(save);
		return null;
	}

	private SNode Statement() {
		// TODO Auto-generated method stub
		
		int save = stxr.getCur();
		
		SNode result = null;
		


		if((result=VDD())!=null||(result=RW())!=null||(result=IfStatement())!=null||(result=WhileStatement())!=null||((result=RightValue())!=null && isWord(";"))/*||isWord(";")*/)
		{
			System.out.println("statement");
			return result;
		}
		
		
		stxr.setCur(save);	
		if((result=xxx()) != null)
		{
			System.out.println("xxx");
			return result;
		}	
		
		
		stxr.setCur(save);	
		return null;
	}

	private SNode WhileStatement() {
		// TODO Auto-generated method stub		
		int save = stxr.getCur();
		int line = stxr.getLine();
		SNode cond = null;
		SNode body = null;
		SNode result = null;
		if(isWord("while") && (cond=Cond())!=null && (body=Body())!=null)
		{
			// syntax direct
			result = new SNode("while_node",null,line);
			result.add(cond);
			result.add(body);
			System.out.println("WhileStatement");
			return result;
		}
		
		stxr.setCur(save);
		return null;
	}

	private SNode Body() {
		// TODO Auto-generated method stub
		
		int save = stxr.getCur();
		
		SNode statement = null;
		SNode result = null;
		
		int line = 0;
				
		if((statement=Statement())!=null)
		{
			line = stxr.getLine();//to ensure the line in the body is the first line
			//syntax direct
			result = new SNode("body",null,line);
			result.add(statement);
			System.out.println("single statement body");
			return result;
		}
		if(isWord("{"))
		{
			result = new SNode("body",null,line);
			while((statement=Statement())!=null)
			{
				// syntax direct?
				result.add(statement);
			}
			if(isWord("}"))
			{
				// syntax direct?
				System.out.println("multi-statement body");
				return result;
			}
		}
		stxr.setCur(save);
		return null;
	}

	private SNode Cond() {
		// TODO Auto-generated method stub
		
		int save = stxr.getCur();
		SNode result = null;
		
		if(isWord("(") && (result=BoolExpression())!=null && isWord(")"))
		{
			System.out.println("Cond");
			return result;
		}
		stxr.setCur(save);
		return null;
	}

	private SNode BoolExpression() {
		// TODO Auto-generated method stub
		int save = stxr.getCur();
		
		Token tmp;
		
		SNode r = null;
		SNode l = null;
		String op = null;
		
		SNode result = null; 
		
		if((r=RightValue())!=null)
		{
			tmp = stxr.next();
			if(tmp!=null)
			{
				// is boolean operation?
				// is <> or <?
				// can use isWord but needs roll back
				if(tmp.is("<"))
				{
					// is <>?
					if((tmp=stxr.next())!=null && tmp.is(">"))
						op= "<>";
					else {
						stxr.back();
						op = "<";
					}
				}
				// is ==?
				else if(tmp.is("=")) {
					if ((tmp=stxr.next())!=null && tmp.is("="))
						op = "==";
					else
					{
						stxr.back();
						stxr.back();
					}
				}
				// is not boolean operation
				else
				{
					stxr.setCur(save);
					return null;
				}
				if((l=RightValue())!=null)
				{
					// syntax direct
					System.out.println("boolean expression");
					
					// to Skip the outer Cond without delete Cond()
					result = new SNode("cond",op,stxr.getLine());
					result.add(l);
					result.add(r);
					
					return result;
				}			
			}
		}
		
		
		stxr.setCur(save);
		return null;
	}

	private SNode IfStatement() {
		// TODO Auto-generated method stub
		int save = stxr.getCur();
		
		SNode result = null;
		SNode cond = null;
		SNode ifbody = null;
		SNode elsebody = null;
		
		int line = stxr.getLine();
		
		if(isWord("if") && (cond=Cond())!=null && (ifbody=Body())!=null)
		{
			if(isWord("else") && (elsebody=Body())!=null)
			{
				result = new SNode("if_else_node",null,line);
				result.add(cond);
				result.add(ifbody);
				result.add(elsebody);
				System.out.println("if-else statement block");
				return result;
			}
			else
			{
				result = new SNode("if_node",null,line);
				result.add(cond);
				result.add(ifbody);
				System.out.println("if statement block");
				return result;
			}
		}
		
		stxr.setCur(save);
		return null;
	}

	private SNode RW() {
		// TODO Auto-generated method stub
		int save = stxr.getCur();
		
		Token tmp;
		
		String string_literal;
		SNode id = null;
		SNode result = null;
		
		//is read or write?
		if(isWord("read") && isWord("("))
		{		
				// is ID?
				if((tmp = stxr.next())!=null && is(tmp,"identifier")) {
					//syntax direct
					if(isWord(")") && isWord(";"))
					{
						// syntax direct
						id = mkid(tmp);
						result = new SNode("read", id, stxr.getLine());
						result.add(id);
						System.out.println("RW:read");
						return result;
					}
				}
			stxr.setCur(save);
			return null;
		}
		stxr.setCur(save);
		if(isWord("write") && isWord("("))
		{
						
				if((tmp = stxr.next())!=null) {
					// write a variable
					if(is(tmp,"identifier"))
					{
						id = mkid(tmp);
						result = new SNode("write",id,stxr.getLine());
						result.add(id);
					}
					// write a string ---> a string make it much harder ---> we need to store the string in somewhere
					else if(is(tmp,"string_literal"))
					{
						result = new SNode("write_string", null ,stxr.getLine());
						result.add(new SNode("string_literal",tmp.getWord(),stxr.getLine()));
					}
					else
					{
						stxr.setCur(save);
						return null;
					}
					if(isWord(")")&&isWord(";"))
					{
						// syntax direct
						System.out.println("RW:write");
						return result;
					}
				}				
			
		}
		stxr.setCur(save);
		return null;
	}

	private SNode Assignment()
	{
		int save = stxr.getCur();
		// is assignment?
		SNode leftvalue = null;
		SNode rightValue = null;
		SNode result = null;
		if((leftvalue = LeftValue())!=null && isWord("=") && (rightValue=RightValue())!=null)
		{
			result = new SNode("assigment",null,stxr.getLine());
			result.add(leftvalue);
			result.add(rightValue);
			return result;
		}
		stxr.setCur(save);
		return AddorSub();
		
	}
	
	private SNode AddorSub()
	{
		int save = stxr.getCur();
		//addition or subtraction
		boolean add = false;
		boolean sub = false;
		SNode lExpression = null;
		SNode rExpression = null;
		SNode result = null;
		
		if((lExpression=MulorDiv())!=null && ((add = isWord("+"))||(sub = isWord("-"))) && (rExpression=MulorDiv())!=null)
		{
			System.out.println("Expression: +-");
			result = new SNode((add?"add":"sub"),null,stxr.getLine());
			result.add(lExpression);
			result.add(rExpression);
			return result;
		}
		stxr.setCur(save);
		return MulorDiv();
	}
	
	private SNode MulorDiv()
	{
		int save = stxr.getCur();
		// division or multiplication
		boolean div = false;
		boolean mul = false;
		SNode lExpression = null;
		SNode rExpression = null;
		SNode result = null;
		
		if((lExpression = Expression_unit())!=null && ((mul = isWord("*"))||(div = isWord("/"))) && (rExpression=Expression_unit())!=null)
		{
			System.out.println("Expression: */");
			result = new SNode((mul?"mul":"div"),null,stxr.getLine());
			result.add(lExpression);
			result.add(rExpression);
			return result;
		}
		stxr.setCur(save);
		return Expression_unit();
	}
	
	private SNode Expression_unit() {
		// TODO Auto-generated method stub

		// forbit the expression such as "a;" "1;" "*p;"

		
		int save = stxr.getCur();
		SNode result = null;
		// ()?
		if(isWord("(") && (result=RightValue())!=null && isWord(")"))
		{
			// syntax direct
			System.out.println("Expression unit: ()");
			return result;
		}
		
		stxr.setCur(save);
		
		// is literal?

		if((result = Integer_Literal())!= null)
		{
			System.out.println("Expression unit: int");
			return result;
		}
		else if((result = Real_Literal())!= null)
		{
			System.out.println("Expression unit: real");
			return result;
		}
		
		//is function call?
		if((result = FunctionCall())!=null)
		{
			System.out.println("Expression unit: functionCall");
			return result;
		}
		
		if((result = LeftValue())!=null)
		{
			System.out.println("Expression unit: by identifier");
			return result;			
		}
			    
		// is address?
		Token tmp= null;
		if(isWord("&") && (tmp=stxr.next())!=null && is(tmp,"identifier"))
		{
			result = new SNode("address_of_identifier", tmp.getWord(), stxr.getLine());
			return result;
		}
		
		stxr.setCur(save);
		return null;
	}
	
	private SNode RightValue()
	{
		return Expression();
	}
	
	private SNode Expression()
	{
		int save = stxr.getCur();
		SNode result;
		if((result = Assignment())!=null)
			return result;
		
		stxr.setCur(save);
		return null;
	}

	
	/**
	 * @param t token
	 * @return a SNode that is tagged by identifier and has contains the name
	 * @ built for convenience
	 */
	private SNode mkid(Token t)
	{
		return new SNode("identifier", t, stxr.getLine());
	}

	private SNode PL() {
		// TODO Auto-generated method stub
		
		Token tmp;
		
		int save = stxr.getCur();
		
		SNode parameter_list = new SNode("parameter_list", null, stxr.getLine());
		SNode parameter;
		SNode dt;
		SNode identifier;
		
		if(isWord("("))
		{			
			if((dt=DT())!=null && (tmp=stxr.next())!=null && is(tmp,"identifier"))
			{
				identifier = mkid(tmp);
				parameter = new SNode("parameter", null, stxr.getLine());
				parameter.add(dt);
				parameter.add(identifier);
				parameter_list.add(parameter);
				
				while(isWord(",") && (dt=DT())!=null && (tmp=stxr.next())!=null && is(tmp,"identifier"))
				{
					//syntax direct
					identifier = mkid(tmp);
					parameter = new SNode("parameter", null, stxr.getLine());
					parameter.add(dt);
					parameter.add(identifier);
					parameter_list.add(parameter);				
				}
				if(isWord(")"))
				{
					//syntax direct
					System.out.println("PL : with parameter");
					return parameter_list;
				}
			}
			
		}
		
		stxr.setCur(save);
		if(isWord("(")&&isWord(")"))
		{
			System.out.println("PL : without parameter");
			return parameter_list;
		}
		stxr.setCur(save);
		return null;
	}

	private SNode VDD() {
		// TODO Auto-generated method stub

		// Variable Declaration & Definition
		// the result of declaration and definition is similar : initiate a variable with or without specific value
		
		int save = stxr.getCur();
		
		SNode dt = null;
		SNode declarator = null;
		SNode arrayltr = null;
		SNode expression = null;
		SNode definition = null;
		SNode declaration = null;
		
			if((dt=DT())!=null && (declarator=LeftValue())!=null) {
				
				// is it definition or declaration?
				
				// is declaration?
	    		if(isWord(";"))
	    		{
	    			declaration = new SNode("declaration", null, stxr.getLine());
	    			declaration.add(dt);
	    			declaration.add(declarator);
	    		    // syntax direct  
	    			
	    			System.out.println("VDD : declaration");
	    		    return declaration;
	    		}
	    	
	    	
	    		//is definition?
			
	    		else if(isWord("="))
	    		{
	    			definition = new SNode("definition",null,stxr.getLine());
	    			definition.add(dt);
        			definition.add(declarator);
	    			// is rightValue?
	    			int save2 = stxr.getCur();
	        		if((expression = RightValue())!=null && isWord(";"))
	        		{
	        			// syntax direct
	        			definition.add(expression);
	        			System.out.println("VDD - defined by expression");
	        			return definition;
	        		}    	 
	        		stxr.setCur(save2);
	        		if((arrayltr=ArrayLiteral())!=null && isWord(";"))
	        		{
	        			// syntax direct
	        			definition.add(arrayltr);
	        			System.out.println("VDD - defined by array literal");
	        			return definition;
	        		}	
	    		}
			}
	    	stxr.setCur(save);
			return null;
	}
	

	private SNode FunctionCall() {
		// TODO Auto-generated method stub
		
		int save = stxr.getCur();
		
		Token tmp = stxr.next();
		
		SNode al;
		SNode identifier;
		SNode functioncall;
		if(tmp!=null && is(tmp, "identifier") && (al=ArgumentList())!=null)
		{
			//syntax direct
			identifier = mkid(tmp);
			functioncall = new SNode("function_call",null,stxr.getLine());
			functioncall.add(identifier);
			functioncall.add(al);
			return functioncall;			
		}
		
		stxr.setCur(save);
		return null;
	}

	private SNode ArgumentList() {
		// TODO Auto-generated method stub
		
		int save = stxr.getCur();
		SNode argument = null;
		ArrayList<Object> result = new ArrayList<Object>();
		
		Token tmp;
		SNode argumentlist = new SNode("argument_list",null,stxr.getLine());
		
		if(isWord("(") && (argument=RightValue())!=null)
		{
			argumentlist.add(argument);
			while(isWord(",") && (argument=RightValue())!=null)
			{
				argumentlist.add(argument);
			}
			if(isWord(")"))
			{
				// syntax direct
				return argumentlist;
			}
			return null;
		}
		stxr.setCur(save);
		if(isWord("(") && isWord(")"))
		{
			//syntax direct
			return new SNode("argument_list", null, stxr.getLine());
		}
		stxr.setCur(save);

		return null;
	}

	private SNode ArrayLiteral() {
		// TODO Auto-generated method stub
		int save = stxr.getCur();
		SNode real = null;
		SNode integer = null;
		SNode result = new SNode("array_literal_node", null, stxr.getLine());
		if(isWord("{"))
		{
			if((integer=Integer_Literal()) != null)
			{
				result.add(integer);
			}
			else if((real = Real_Literal()) != null)
			{
				result.add(real);
			}
			else 
			{
				stxr.setCur(save);
	    		return null;
			}
			while(true)
			{
			    if(isWord("}"))
				{
					//syntax direct
					return result;
				}
				
				if(!isWord(","))
				{
					stxr.setCur(save);
		    		return null;
				}
				if((integer=Integer_Literal()) != null)
				{
					result.add(integer);
					continue;
				}
				else if((real = Real_Literal()) != null)
				{
					result.add(real);
					continue;
				}
				
				stxr.setCur(save);
				return null;
			}
		}
		stxr.setCur(save);
		return null;
	}

	private SNode LeftValue() {
		// TODO Auto-generated method stub
		int save = stxr.getCur();
		
		boolean pointer = false;
		int arraysize = 0;
		String ID = null;
		ArrayList<Object> result = new ArrayList<Object>();
		
		Token tmp = stxr.next();
		if(tmp != null)
			// is pointer?
			if(tmp.getWord().equals("*"))
			{
				pointer = true;
				tmp = stxr.next();
			}
			if(tmp != null && is(tmp,"identifier")) {
				ID = tmp.getWord();	
				
				// is an array?
				arraysize= stxr.getCur(); // temporally served as save
				tmp = stxr.next();
				Token tmp2 = stxr.next();
				Token tmp3 = stxr.next();
				if(tmp3!=null && tmp2!=null && tmp!=null && tmp.getWord().equals("[") && is(tmp2,"integer_literal") && tmp3.getWord().equals("]") )
				{
					arraysize = Integer.parseInt(tmp2.getWord());
				}
				else
				{
					stxr.setCur(arraysize);
					arraysize = 0;
				}
				
			}
			if(ID == null){
				stxr.setCur(save);
				return null;
			}

		result.add(pointer);
		result.add(ID);
		result.add(arraysize);
		if(pointer)
		    System.out.println("left_value - *");
		if(arraysize != 0)
			System.out.println("left_value - ["+arraysize+"]");
		return new SNode("left_value", result, stxr.getLine());
	}
	
    private SNode DT() {
		// TODO Auto-generated method stub
		int save = stxr.getCur();	
		Token tmp;
		tmp = stxr.next();
		if(tmp !=null && (is(tmp,"int") || is(tmp,"real")))
		{
			System.out.println("DT");
			return new SNode("DT", tmp.getWord(), stxr.getLine());
		}
		stxr.setCur(save);
		return null;
	}
    
    private SNode Integer_Literal()
    {
    	int save = stxr.getCur();
    	
    	boolean negative = isWord("-");
    	Token tmp = stxr.next();
    	Integer result = null;
    	if(tmp != null && is(tmp, "integer_literal"))
    	{
    		result = Integer.parseInt(tmp.getWord());
    		if(negative)
    		{
    			return new SNode("integer_literal",0 - result, stxr.getLine());
    		}		
    		return new SNode("integer_literal",result, stxr.getLine());

    	}
    	stxr.setCur(save);
    	return null;
    }
    
    private SNode Real_Literal()
    {
    	int save = stxr.getCur();
    	boolean negative = isWord("-");
    	Token tmp = stxr.next();
    	Double result = null;
    	if(tmp != null && is(tmp, "real_literal"))
    	{
    		result = Double.parseDouble(tmp.getWord());
    		if(negative)
    		{
    			return new SNode("real_literal",0 - result, stxr.getLine());
    		}
    		return new SNode("real_literal",result, stxr.getLine());
    	}
    	stxr.setCur(save);
    	return null;
    }
	
}
