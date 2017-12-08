package cvm;

import java.util.ArrayList;

/**
 * @author daos
 * first element is zero
 */
public class StackArea {
	private ArrayList<Object> stack;
	
	/**
	 * next bigger index to the last added element
	 */
	private int sp;

	public void setSp(int sp) {
		this.sp = sp;
	}

	public void alloca(int i)
	{
		for(int j=0;j<i;j++)
		{
			push(null);
		}
	}
	
	public Object getTop()
	{
		return stack.get(sp-1);
	}
	
	public StackArea() {
		super();
		this.stack = new ArrayList<Object>();
		stack.add(0);
		sp = 1;
	}
	
	public ArrayList<Object> getStack(){
		return stack;
	}
	
	public Object getValue(int address)
	{
		if(address>=sp)
			return null;
		return stack.get(address);
	}
	
	public void setValue(int address, Object value)
	{
		if(address<=sp)
		stack.set(address, value);
	}
	
	public boolean isEmpty()
	{
		return sp == 1;
	}
	
	public void push(Object value)
	{
		if(sp>=stack.size())
		   stack.add(value);
		else
			stack.set(sp,value);
		sp++;
	}
	
	public Object pop()
	{
		if(isEmpty())
			return null;
		return stack.get(--sp);
	}
	
	public void empty()
	{
		sp = 1;
	}

	public int getSp() {
		return sp;
	}
	

}
