package dataStructure;

import javax.swing.tree.DefaultMutableTreeNode;
import core.Info;
/**
 * @author 0050
 * @version 4.0
 * @ 
 * the node in a syntax tree, the purpose is simple - a tree with tag to describe the node
 * and if the node is terminate node then it's token isn't null
 */
public class SNode extends DefaultMutableTreeNode{
    private static final long serialVersionUID = 123232323L;
    // Type of the current node
    protected String tag;
    // Content of the current node 
    private Object contents;
    // Line number of the current node
    private int lineNum;
    
    public boolean isTN()
    {
    	for(String s : Info.kindToKindName)
    		if(tag.equals(s))
    			return true;
    	return false;
    }

    public SNode(String tag, Object contents, int lineNum) {
		super();
		this.tag = tag;
		this.contents = contents;
		this.lineNum = lineNum;
	}
    
    

	public SNode(String tag, int lineNum) {
		super();
		this.tag = tag;
		this.contents = null;
		this.lineNum = lineNum;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}



	public String getTag() {
		return tag;
	}



	public Object getContents() {
		return contents;
	}



	public int getLineNum() {
		return lineNum;
	}



	public void add(SNode childNode) {
        super.add(childNode);
    }

    public SNode getChildAt(int index) {
        return (SNode) super.getChildAt(index);
    }



	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String result = this.getTag();
		if(isTN()) {
			try {
			    result += " : " + ((Token) this.contents).getWord();
			}
			catch(ClassCastException e)
			{
				result += " : " + this.contents.toString();
			}
		}
		return result;
	}

    


}
