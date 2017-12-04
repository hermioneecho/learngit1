package dataStructure;

import java.util.function.Function;

public class DFA{
	private int state;
	private DFAState[] states;
	Function<Integer, Integer> map;

	
	public DFA(DFAState[] states, Function<Integer, Integer> map) {
		super();
		this.state = 0;
		this.states = states;
		this.map = map;
	}
	
	public Token run(String word)
	{ 
		state = 0;
		int i = 0;
		char c;
		while(i != word.length())
		{
			if(state == -1)
				return null;
			c = word.charAt(i++);
			state = states[state].move(c);
		}
		return makeToken(state, word);
	}
	
	private Token makeToken(int kind, String word)
	{
		int i = map.apply(kind);
		if(i == -1)
			return null;
		return new Token(i, word);
	}
	

}
