package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/*
 * version 4
 * in order to add a acuter error handling report
 * I need to let the syntaxer know the line number
 * before skipping the file
 * I need to and special key to mark the line
 * and the key should contain the character that is illegal outside of ""
 * I choose _@4
 * such a word would be added in the line 4
 * thus it would be returned in next() as words: @, _line_4
 * if skipped that is fine because the number is original
 * the modification is in skipped comment
 * and a new function called markLine
 */

/*
 * temporarily read from an file
 * Preprocessing is to skip the comment and whitespace
 * use function next to return an element to Lexer£¬ and the element should be a word
 * 
 * use Scaner instead of Tokenizer
*/

public class LexerReader {
	
	private Scanner s;
	private String buf;
	private boolean isFile; // if we want to read from file

	public  LexerReader(boolean isFile, String filepath) {
		this.isFile = isFile;
		s = new Scanner(skipComments(filepath));
		if(s.hasNext())
		    buf = s.next();
		else
			System.out.println("LexerReader.LexerReader warmming ---- empty skipped string");
	}
	
	// a new function introduced in version 4
	private String markLine(String raw)
	{
		StringBuilder result = new StringBuilder();
		int count = 1;
		String leftString = raw;
		int end = 0;
		while((end = leftString.indexOf('\n')) != -1)
		{
			result.append("_@"+count+"    "+leftString.substring(0, end+1));
			leftString = leftString.substring(end+1,leftString.length());
			end = 0;
			count++;
		}
		if(count != 1)
		{
			return result.toString();
		}
		
		// this would cause a exception
		// and hopefully never reach here
		return null;
		
	}
	
	private String skipComments(String filePath) {
		
		// first we convert the whole file into a big string
		// then we use indexof() to skip the comments and put the skipped into the result
		 Path path;	 
		 BufferedReader reader;
		 String line;
		 String wholeFile;
		 StringBuilder sb = new StringBuilder();
		 StringBuilder skipped = new StringBuilder();
		 int begSkip = 0;
		 int endSkip = -1;
		 int begQuote = 0;
		 int endQuote = -1;
		 int cur = 0;
		 
		 //validate the filename first
		 // if...
		 
		try {
			if(isFile)
			{
				int count = 1;
				path = Paths.get(filePath);	 
			    reader = Files.newBufferedReader(path);
			
		    	// convert the file into a string
			    // modification of version 4
	            while((line=reader.readLine())!=null) {
	        	    sb.append("_@"+ count + "    " + line + "\n");
	        	    count++;
	            }
	            wholeFile = sb.toString();
			}
			else
			{
				wholeFile = filePath;  // if we don't want to read from file than we read filePath 
				// modification of version 4
				System.out.println(wholeFile);
				wholeFile = markLine(wholeFile);
			}
//	        System.out.print(wholeFile);
//			System.out.print("\n\n\nready to begin skipping\n\n");
			
			
	        while(true)
	        {
	        	// to prevent what // and /* inside a string literal to be skipped
	        	// it is OK that inside a comment we have " // "
	        	// we protect the comment only and only if begQuote < begSkip and endQuote > endSkip.
	        	
	        	// get begQuote and endQuote, doesn't matter if begQuote == -1 
	        	if((begQuote=wholeFile.indexOf('"', cur)) != -1)
	        	{   
	        		endQuote =  wholeFile.indexOf('"', begQuote+1) + 1;

	        		while(endQuote != -1 && wholeFile.charAt(endQuote-2) == '\\')
	        		{
	        			endQuote = wholeFile.indexOf('"', endQuote);
	        		}
	        		if(endQuote == -1) {
	        			System.out.println("LexerReader.SkipComents---error: missing \" at the end of file");
	        			break;
	        		}
	        	    
	        	}
	        	else
	        		endQuote = -1;
	        	
	        	
	        	// get begSkip and endSkip for the first glance without consideration that it may be included inside ""
	        	begSkip = wholeFile.indexOf('/',cur);
	        	if(begSkip == -1) 
	        	{
	        		skipped.append(wholeFile.substring(cur));
	        		break;
	        	}        	
        	    if(wholeFile.charAt(begSkip + 1) == '/')
        	    {
        		   endSkip = wholeFile.indexOf('\n',begSkip) + 1;
        		   if(endSkip == -1 && begSkip > endQuote) // the last line
        		   {
        			   skipped.append(wholeFile.substring(cur, begSkip));
        			   break;
        		   }
//        		   if( endQuote != -1 && endSkip >= wholeFile.indexOf('\n', endQuote-1))
//        			   endSkip = endQuote-1; // in order to deal with this situation: "hello // world"  \n
        	    }
                else if(wholeFile.charAt(begSkip + 1) == '*')
                {
        	      endSkip = wholeFile.indexOf("*/", begSkip) + 2;
        	      if(endSkip == -1 && begSkip > endQuote)
       		      {
        	       System.out.println("LexerReader.SkipComments---error: missing */ at the end of the file");
       			   break;
       		       }
                }
                else
                {
                	skipped.append(wholeFile.charAt(cur));
                	cur++;
                	continue;
                }
        	    
	        	// if begskip inside "", then we add the substring ended with the last " to the result
	        	if(begQuote < begSkip && endQuote > begSkip)
	        	{
//	        		System.out.println("begQuote: " + begQuote + " endQuote: " + endQuote + " cur: " + cur + " begSkip: " + begSkip + " endSkip: " + endSkip);
	        		skipped.append(wholeFile.substring(cur, endQuote));
	        		cur = endQuote;
	        	}
	        	else
	        	{
	        		skipped.append(wholeFile.substring(cur,begSkip));
	        		cur = endSkip;
	        	}       	

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	        	
	        	// the following is the old idea to add a substring that ends at begSkip to the result, the new idea is to add
	        	// a substring that ends at endQuote
	        	
//	        	{
//	        		begSkip = wholeFile.indexOf('/',begSkip);	        		
//	        	    if(wholeFile.charAt(begSkip + 1) == '/')
//	        	    {
//	        		   if((endSkip = wholeFile.indexOf('\n')) == -1)
//	        		   {
//	        			   if(begSkip < endQuote) 
//	        			   {
//	        			       skipped.append(wholeFile.substring(0, endQuote));
//	        			       // no match \n means it may be the last line, but we still need to take care of the rest of the code that outside of ""
//	        			       wholeFile = wholeFile.substring(endQuote, wholeFile.length());
//	        			       break;
//	        			   }
//	        			   else
//	        			   {
//	        				   skipped.append(wholeFile.substring(0, begSkip));
//	        				   break;
//	        				   //else we have comment at the end
//	        			   }
//	        		   }
//	        	    }
//	                else if(wholeFile.charAt(begSkip + 1) == '*')
//	                {
//	        	      if((endSkip = wholeFile.indexOf("*/")) == -1)
//	        	    	  if(begSkip < endQuote)
//	        	    	  {
//	        	    		  skipped.append(wholeFile.substring(0,endQuote));
//	        	    		  wholeFile = wholeFile.substring(endQuote, wholeFile.length());
//	        	    	  }
//	        	    	  else
//	        	    	  {
//	        	    		  System.out.println("LexerReader.SkipComments---error: missing */ at the end of the file");
//	        	    	  }
//	                }
//     			   begSkip = endSkip;        	    
//	        	}
	        	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	        	
	        	
	        }
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		System.out.print(skipped.toString());
		return skipped.toString();
	}
	
	public String next() {
		// should return a word
		// scan buf to see if there is character that can be delimeter
		// if no word left, return null
		
		if(!this.hasNext())
			return null;
		
		int end = 0;
		String result;
		while(end < buf.length())
		{
			 if (!(Character.isDigit(buf.charAt(end)) || Character.isLetter(buf.charAt(end)) || buf.charAt(end) == '_'|| buf.charAt(end)== '.'))
			{
				 if(end == 0)
					 end++;
				 break;
			}
			 end++;
		}
		result=buf.substring(0, end);
		if(end == buf.length())
		{
			if(s.hasNext())
			{
				buf=s.next();
			}
			else
			{
				buf = null; // the last word is returned
			}
		}
		else
		{
			buf=buf.substring(end); //buf will not be empty for end != buf.length
		}
		
		return result;
	}
	
	public boolean hasNext() {
		return buf != null;
		
	}
			 
}
