package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Queue;

import dataStructure.*;
import ui.Demo;
import core.*;

import core.*;

public class Interpreter extends Info{
	static private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	static private Lexer lexer;
	static private Syntaxer syntaxer;
	
	/*
	 *  version 3.1
	 *  adding the debug information
	 *  writting the test use case
	 */
	
	/*
	 *  version 3
	 *  give up automatic variable solution
	 *  give up cloneable server
	 *  decide to create two new classes : SyntaxerReader, Syntaxer
	 *  Syntaxer : the syntax detection and syntax-directed translation
	 *  SyntaxerReader : a rollbackable token collector
	 */
	
	
	// version 2
	// the previous backup plan but now I move it to Lexer class
	// now I use a local automatic variable as a save-point
	
//	static private Queue<Token> tokensBuf;
//	private Token getNextToken()
//	{
//		if(!tokensBuf.isEmpty())
//			return tokensBuf.poll();
//		if(lexer.hasNext())
//			return lexer.next();
//		else
//			return null;
//	}
//	private void putBackToken(Token tk)
//	{
//		tokensBuf.add(tk);
//	}
	
	
	
	public static void main ( String args [ ] ) {
		DFA[] dfas = {literalDFA, identifierDFA};
		try {
			if(args.length == 0)
			{
				System.out.println("CMM Lexer Test (reading from standard input--------- Enter ROF or enter !exit in new line to finish typing\n");
				StringBuilder inputsb = new StringBuilder();
				String buf;
				while((buf=br.readLine()) != null && !(buf.equals("!exit")))
					inputsb.append(buf + '\n');
				lexer = new Lexer(false, inputsb.toString(), dfas, reservedWords);
				syntaxer = new Syntaxer(lexer);
				//System.out.println(syntaxer.check());
				if(syntaxer.check())
				{
				Demo result = new Demo(syntaxer.getRoot());
				result.show(syntaxer.getRoot());
				}
				else
				{
					System.out.println("invalid program!");
				}
			}
			else if(args.length > 1)
		    {
		    	System.out.println("main Error --- wrong usage");
		    	return;
		    }
		    else
		    {
		    	System.out.println("CMM Lexer: reading from file: " + args[0]);
		    	System.out.println("and this file should contains more than one row.");
				lexer = new Lexer(true ,args[0], dfas, reservedWords);
				syntaxer = new Syntaxer(lexer);
				if(syntaxer.check())
				{
				Demo result = new Demo(syntaxer.getRoot());
				result.show(syntaxer.getRoot());
				}
				else
				{
					System.out.println("invalid program!");
				}

		    }
//			System.out.println("begin test:");
//			Token tmp;
//			while(lexer.hasNext())
//			{
//				tmp = lexer.next();
//			    System.out.println(tmp.getWord() + "(index:"+ tmp.getKind() + ", Token Type: " + getTokenName(tmp.getKind())+" ) ");
//			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// let's begin our syntax check
		// I use a mixture of LL and LR （准确的说是递归子程序法+LALR（1）
		// LL in a macro level
		// LR in a lower level
		// give up OO style, because I think it is better to be in procedure oriented
		
		
	}

}
