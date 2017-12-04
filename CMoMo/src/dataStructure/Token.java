package dataStructure;

public class Token{
	private int kind;
	private String word;
	public Token(int kind, String word) {
		super();
		this.kind = kind;
		this.word = word;
	}
	public int getKind() {
		return kind;
	}

	public String getWord() {
		return word;
	}
	
	public boolean is(String word)
	{
		return this.word.equals(word);
	}

	
}
