package core;


import java.util.ArrayList;

//import dataStructure.Ssr;
import dataStructure.Token;

public class SyntaxerReader{
	

	private ArrayList<Token> tokens;
	
	// the current position
	private int cur;
	// the number of tokens
	private int count;
	// the line number
	private int line;
	
	private int maxLine;
	private int maxCur;
	
	public SyntaxerReader(Lexer lexer) {
		super();
		this.cur = 0;
		this.line = 0;
		tokens = new ArrayList<Token>();
		// load
		
		while(lexer.hasNext())
    	{
    		tokens.add(lexer.next());
    	}
		this.count = tokens.size();
		maxLine = 0;
		maxCur = 0;
	}
	
	public ArrayList<Token> getTokens(){
		return tokens;
	}

	// hasNext
	public boolean hasNext()
	{
		return cur < count;
	}
	
	// next	
	public Token next()
	{

		// skip the new_line
		while(this.hasNext() && Info.is(tokens.get(cur),"new_line"))
		{
			line = Integer.parseInt(tokens.get(cur).getWord());
			if(line>maxLine)
				maxLine = line;
			cur++;
		}
		
		if(this.hasNext())
		{
			if(cur+1>maxCur)
				maxCur = cur+1;
			return tokens.get(cur++);
		}
		return null;
	}
	
	// back
	public void back()
	{
		if(cur>0)
			cur--;
		while(Info.is(tokens.get(cur),"new_line"))
			cur--;
	}
	
	// get a save-point
	public int getCur()
	{
		return cur;
	}
	
	// read a save-point
	public void setCur(int save)
	{
		this.cur = save;
	}
	
//	public Ssr save()
//	{
//		return new Ssr(line, cur);
//	}
//	
//	public void load(Ssr save)
//	{
//		this.line = save.getLine();
//		this.cur = save.getCur();
//	}
	
	public int getLine()
	{
		return line;
	}
	
	public int getErrorLine()
	{
		return maxLine;
	}
	
	public Token getErrorToken()
	{
		return tokens.get(maxCur);
	}
}

