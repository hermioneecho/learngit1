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
    private String tag;
    // Content of the current node 
    private Object contents;
    // Line number of the current node
    private int lineNum;

    //tag¡–±Ì
    public static final String Program="program";
    public static final String Function_definition="function_definition";
    public static final String Returen_node="return_node";
    public static final String While_node="while_node";
    public static final String Body="body";
    public static final String Cond="cond";
    public static final String If_else_node="if_else_node";
    public static final String If_node="if_node";
    public static final String Read="read";
    public static final String Write="write";
    public static final String Write_string="write_string";
    public static final String String_literal="striing_literal";
    public static final String Assignment="assignment";
    public static final String Add="add";
    public static final String Sub="sub";
    public static final String Mul="mul";
    public static final String Div="div";
    public static final String Address_of_identifier="address_of_identifier";
    public static final String Identifier="identifier";
    public static final String Parameter_list="parament_list";
    public static final String Parameter="parameter";
    public static final String Declaration="declaration";
    public static final String Definition="definition";
    public static final String Function_call="function_call";
    public static final String Argument_list="argument_list";
    public static final String Array_literal_node="array_literal_node";
    public static final String Left_value="left_value";
    public static final String DT="DT";
    public static final String Integer_literal="integer_literal";
    public static final String Real_integer="real_integer";
    
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
        return (SNode)super.getChildAt(index);
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
