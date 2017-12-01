package dataStructure;

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
	
	
	/**
	 * String as the name of the attribute and Object as the value
	 */
	private Map<String, Object> attributes;
	
	public ANode(SNode snode) {
		super();
		// TODO Auto-generated constructor stub
		tag = snode.getTag();
	}
		
	/**
	 * @param root
	 * convert SNode Tree to ANode tree
	 */
	public ANode convert(SNode root)
	{
		ANode result = new ANode(root);
		Enumeration<SNode> children = root.children();
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
	
	
	
	

}
