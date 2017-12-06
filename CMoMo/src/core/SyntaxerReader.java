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
			if(cur>maxCur)
				maxCur = cur;
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
		{
			cur--;
			line--;
		}
	}
	
	// get a save-point
	public int getCur()
	{
		return cur;
	}
	
	// read a save-point
	/**
	 * @param save
	 * use for roll back (read a save) ONLY!
	 */
	public void setCur(int save)
	{
		int lines=0;
		// the new cur and the old cur cannot be new_line, new_line is in the interval
		for(int i=save; i<cur; i++)
		{
			if(Info.is(tokens.get(i),"new_line"))
				lines++;
		}
		line = line - lines;
		cur = save;
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

