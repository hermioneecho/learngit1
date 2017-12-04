package Symbol;

import java.util.ArrayList;

//temporary variable
public class TARG {
	private static final String Kind="temp";
	private int index;
	private ArrayList<TARG> tempName;
	
	public TARG(int index) {
		this.index=index;
	}
	
	/**
	 * @return
	 * get an unused tenpName for a new temporary variable
	 */
	public String getTemp() {
		int i=0;
		String temp=null;
		for(i=0; ;i++) {
			temp="T"+i;
			for(TARG t:tempName) {
				if(t.getIndex()==i) {
					break;
				}
			}
			tempName.add(new TARG(i));
			return temp;
		}
	}
	
	public String getKind() {
		return Kind;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public ArrayList<TARG> getTempName() {
		return tempName;
	}

	public void setTempName(ArrayList<TARG> tempName) {
		this.tempName = tempName;
	}
	
	
}
