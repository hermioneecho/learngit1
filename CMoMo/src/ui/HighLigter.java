package ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import core.Lexer;
import core.SyntaxerReader;
import dataStructure.Token;

public class HighLigter implements DocumentListener{
	private static HashSet<String> keywords;
	private static StyleContext styleContext=new StyleContext();
	
	private static int CharHeight;
	private static int CharWidth;
	
	private Lexer lexer=new Lexer();
	private static ArrayList<Token> tokens=new ArrayList<Token>();
	
	public HighLigter() {
		addStyle("keyword",new Color(127, 0, 85));
		addStyle("comment",new Color(0, 0, 255));
		addStyle("comment", new Color(150, 150, 150));
		addStyle("String", new Color(57, 150, 48));
		addStyle("default", new Color(0, 0, 0));
		
		keywords = new HashSet<String>();
		keywords.add("int");
	    keywords.add("double");
	    keywords.add("bool");
	    keywords.add("string");
	    keywords.add("if");
	    keywords.add("else");
	    keywords.add("while");
	    keywords.add("read");
	    keywords.add("write");
	    keywords.add("true");
	    keywords.add("false");
	}
	
//	public void coloring(String text, StyledDocument document,boolean CMM) {
//	    if(!CMM) {
//	    	try {
//	    		StringTokenizer tokens=new StringTokenizer(text,"[] (); {}. \n\t",true);
//	    		while(tokens.hasMoreTokens()) {
//	    			String s=tokens.nextToken();
//	    			Style style;
//	    			if(keywords.contains(s.trim())) {
//	    				style=styleContext.getStyle("keyword");
//	    			} else if (s.trim().matches("^\\w+$") && s.trim().substring(0,1).matches("[A-Za-z]")) {
//	                    style = styleContext.getStyle("variable");
//	                } else {
//	                    style = styleContext.getStyle("none");
//	                }
//	                document.insertString(document.getLength(), s, style);
//	    		}
//	    	}catch(BadLocationException e) {
//	    		e.printStackTrace();
//	    }
//	  }else {
//		  try {
//		  SyntaxerReader sr=new SyntaxerReader(lexer);
//		  tokens=sr.getTokens();
//		  for (Token token : tokens) {
//              Style style = null;
//              if (keywords.contains(token.getContent()) && token.getType().equals(VarSet.KEY)) {
//                  style = styleContext.getStyle("keywords");
//                  StyleConstants.setBold(style, true);
//              } else if (token.getType().equals(VarSet.ID)) {
//                  style = styleContext.getStyle("variable");
//                  StyleConstants.setBold(style, true);
//              } else if (token.getContent().equals("//") || token.getType().equals(VarSet.COM) ||
//                      token.getContent().equals("/*") || token.getContent().equals("*/")) {
//                  style = styleContext.getStyle("comment");
//                  StyleConstants.setBold(style, true);
//              } else {
//                  style = styleContext.getStyle("none");
//              }
//              document.insertString(document.getLength(), token.getContent(), style);
//          }
//      } catch (BadLocationException e) {
//          e.printStackTrace();
//      }
//	}
//	    
//	}
	public void addStyle(String key, Color color) {
	    addStyle(key,color,14,"Courier New");
	}
	    
	public void addStyle(String key, Color color, int size, String fontStyle) {
	    Style style = styleContext.addStyle(key, null);
	    if (color != null)
	        StyleConstants.setForeground(style, color);
	    if (size > 0)
	        StyleConstants.setFontSize(style, size);
	    if (fontStyle != null)
	        StyleConstants.setFontFamily(style, fontStyle);
	 }

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public static int getCharHeight() {
		return CharHeight;
	}

	public static void setCharHeight(int charHeight) {
		CharHeight = charHeight;
	}

	public static int getCharWidth() {
		return CharWidth;
	}

	public static void setCharWidth(int charWidth) {
		CharWidth = charWidth;
	}	
}

