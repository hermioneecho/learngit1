package core;
import java.util.ArrayList;
import java.util.function.Function;

import dataStructure.*;
// This class is a package containing all the specific objects for this CMM Interpreter
public class Info {
	// first we want three DFAs
	// literalDFA is used to identify integer and real
	// identifierDFA for identifier 
	// string literal is identifier by Lexer, because my DFA is defined as a auto machine that takes only one word per input
	
	//States for literalDFA
    static DFAState ltr0 = new DFAState(0, (c) ->
	{
		if(c == '0')
			return 1;
		if(Character.isDigit(c))
			return 2;
	    return -1;
	}
	);
	static DFAState ltr1 = new DFAState(1, (c)->
	{
		if(c == '.')
			return 3;
		if(c == '0')
			return 2;
		return -1;
	}
	);
	static DFAState ltr2 = new DFAState(2, (c)->
	{
		if(Character.isDigit(c))
			return 2;
		if(c == '.')
			return 3;
		return -1;
	}
	);
	static DFAState ltr3 = new DFAState(3, (c)->
	{
		if(Character.isDigit(c))
			return 3;
		return -1;
	}
	);
	
	// try to build the first DFA
	static DFAState[] ltrs = {ltr0, ltr1, ltr2, ltr3}; 
	protected static DFA literalDFA = new DFA(ltrs, i->
	{
		if(i == 1)
			return getID("integer_literal");
		if(i == 2)
			return getID("integer_literal");
		if(i == 3)
			return getID("real_literal"); 
		return -1;
	}
	);
	
	//States for identifier
	static DFAState identifier0 = new DFAState(0, c->
	{
		if(Character.isLetter(c))
			return 1;
		return -1;		
	}
	)
	;
	static DFAState identifier1 = new DFAState(1, c->
	{
		if(c == '_')
			return 2;
		else if(Character.isDigit(c)||Character.isLetter(c))
			return 3;
		return -1;
	}
	)
	;
	static DFAState identifier2 = new DFAState(2, c->
	{
		if(Character.isLetter(c) || Character.isDigit(c))
			return 3;
		if(c == '_')
			return 2;
		return -1;
	}
	)
	;
	static DFAState identifier3 = new DFAState(3, c->
	{
		if(Character.isLetter(c) || Character.isDigit(c))
			return 3;
		if(c == '_')
			return 2;
		return -1;
	}
	)
	;
	
	static DFAState[] identifiers = {identifier0, identifier1, identifier2, identifier3};
	protected static DFA identifierDFA = new DFA(identifiers, i->
	{
		if(i == 3 || i == 1)
			return getID("identifier");
		return -1;
	}
	);
	
	protected static String[] reservedWords = {"int","real","if","else","while","read","write","return"};
	public static String[] kindToKindName = {"undefined", "int", "real", "if", "else", "while", "read", "write", "return", "integer_literal", "real_literal", "string_literal", "identifier", "new_line"};
	protected static String getName(int kind) {
		return kindToKindName[kind];
	}
	public static int getID(String name){
		int i = 0;
		boolean notMatch = true;
		for(String s : kindToKindName)
		{
			if(name.equals(s))
			{
				notMatch = false;
				break;
			}
			i++;
		}
		if(notMatch) 
		{
			Exception e = new Exception("Wrong Token name : " + name);
			e.printStackTrace();
			return -1;
		}
		return i;
	}
	
	protected static boolean is(Token t, String name) {
		return t.getKind() == getID(name);
	}

	
	
}
