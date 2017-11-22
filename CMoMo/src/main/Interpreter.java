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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
