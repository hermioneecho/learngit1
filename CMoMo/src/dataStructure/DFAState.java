package dataStructure;

import java.util.function.Function;

public class DFAState {
	private int state;
	private Function<Character,Integer> moveFunction;

	public int move(char c)
	{
		return moveFunction.apply(c);
	}

	public DFAState(int state, Function<Character, Integer> moveFunction) {
		super();
		this.state = state;
		this.moveFunction = moveFunction;
	}

	public int getState() {
		return state;
	}
	
}
