package core;

import dataStructure.*;

public class Lexer {
	
	/*
	 * version 4
	 * add a newline token here
	 * format is _@4
	 * @ and 4
	 * token newline, word = 4, kind = 13
	 * if read an @ but not successfully get a newline token then @ will attach to next word
	 * these mode can be a pattern that handles this kind of issue
	 */
	
	
	private LexerReader lxr;
	private DFA[] dfas;
	private String[] rws; // reserved words
		
	public Token next() {
		String word;
		String tmp;
		int rw;
		while(lxr.hasNext())
		{
			word = lxr.next();
			if(word.equals("_"))
			{
				tmp = lxr.next();
				if(!tmp.equals("@"))
					return new Token(0,"_"+tmp);
				tmp = lxr.next();
				try {
					Integer.parseInt(tmp.substring(0, tmp.length()));
					return new Token(Info.getID("new_line"), tmp.substring(0, tmp.length()));
				}
				catch(Exception e)
				{
					System.out.println("@ is forbidden to be used! --- Lexer.next()");
					word = "@" + tmp;
				}				
			}
			if(word.equals("\""))
				return makeStringToken();
			if((rw = isReservedWord(word)) != 0)
				return makeReservedWordToken(rw, word);
			for(DFA dfa : dfas)
				if(dfa.run(word) != null)
					return dfa.run(word);
			return new Token(0, word); // the unkonwn token
		}
		System.out.println("this is the unreachable area in Lexer.next()");
		return null; // unreachable
		
	}
	
	private Token makeStringToken()
	{
		StringBuilder sb = new StringBuilder();
		String word;
		while(lxr.hasNext())
		{
			word = lxr.next();
			if(word.equals("\""))
				break;
			// a little fault the multiple whitespace will be replaced by a single space
			sb.append(word + " ");
		}
		return new Token(Info.getID("string_literal"), sb.toString());		
		
	}
	
	private int isReservedWord(String word)
	{
		int i = 0; // Token.kind of reserve word begin with 1
		for(String rw : rws)
		{
			i++;
			if(rw.equals(word))
				return i;
		}
		return 0;
	}
	
	private Token makeReservedWordToken(int kind, String word)
	{
		//we can do some test here
		return new Token(kind, word);
	}
	
	public boolean hasNext() {
		return lxr.hasNext();
	}

	public Lexer(boolean isFile, String path, DFA[] dfas, String[] rws) {
		super();
		this.lxr = new LexerReader(isFile, path);
		this.dfas = dfas;
		this.rws = rws;
	}
	
	public Lexer() {
		super();
	}
	
	
}
