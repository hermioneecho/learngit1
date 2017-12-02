package dataStructure;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;

import javax.print.attribute.AttributeSetUtilities;
import javax.swing.text.html.HTMLDocument.Iterator;
import javax.swing.tree.DefaultMutableTreeNode;

public class ANode extends DefaultMutableTreeNode{


	/**
	 * 
	 */
	private static final long serialVersionUID = -5432306680515588283L;

	private String tag;
	
	private Object contents;
	
	private int lineNo;
	
	
	/**
	 * String as the name of the attribute and Object as the value
	 */
	private Map<String, Object> attributes;
	
	public ANode(SNode snode) {
		super();
		// TODO Auto-generated constructor stub
		tag = snode.getTag();
		contents = snode.getContents();
		lineNo = snode.getLineNum();
	}
		
	/**
	 * @param root
	 * convert SNode Tree to ANode tree
	 */
	public ANode convert(SNode root)
	{
		ANode result = new ANode(root);
		Enumeration<?> children = root.children();
		while(children.hasMoreElements())
		{
			result.add(convert((SNode) children().nextElement()));
		}
		
		return result;
	}
	
	/**
	 * @param string, the name of the attribute
	 * @param value, the value and it
	 */
	public void addAttribute(String string, Object value)
	{
		attributes.put(string, value);
	}

	@Override
	public String toString() {
		String result = tag;
		java.util.Iterator<Entry<String, Object>> attrI = attributes.entrySet().iterator();
		result+="{";
		while(attrI.hasNext())
		{
			result+=attrI.next().getKey()+":"+ attrI.next().getValue().toString()+"; ";
		}
		result += "}";
		return result;
	}

	public String getTag() {
		return tag;
	}

	public Object getContents() {
		return contents;
	}

	public int getLineNo() {
		return lineNo;
	}

    public ANode getChildAt(int index) {
        return (ANode) super.getChildAt(index);
    }

	public void setTag(String tag) {
		this.tag = tag;
	}
	
    
    
//	public ArrayList<ANode> getChildren() {
//		// TODO Auto-generated method stub
//		ArrayList<ANode> result = new ArrayList<ANode>();
//		for(int i=0; i<this.getChildCount(); i++)
//		{
//			result.add((ANode) this.getChildAt(i));
//		}
//		if(result.size() == 0)
//			return null;
//		return result;
//	}
	
	
	
	

}
