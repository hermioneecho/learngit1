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
	}

	// hasNext
	public boolean hasNext()
	{
		return cur < count;
	}
	
	// next	
	public Token next()
	{
		if(this.hasNext())
		{
			while(tokens.get(cur).getKind()==Info.getID("new_line"))
			{
				//System.out.println("a new line");
				line = Integer.parseInt(tokens.get(cur).getWord());
				cur++;
			}
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
}

