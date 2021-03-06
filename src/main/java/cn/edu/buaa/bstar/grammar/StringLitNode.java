/** 
 * @author  LiuCong  
 * @file    StringLitNode.java 
 * @date    Date：2015年12月23日 下午9:32:24 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import cn.edu.buaa.bstar.symbolt.Type;

public class StringLitNode extends BNode {
	
	private String stringValue;
	private Type type = Type.newStringType();
	
	public StringLitNode(String stringValue, int line) {
		super(line);
		this.stringValue = stringValue;
	}
	
	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		cStringBuilder.append("\""+stringValue+"\"");
	}

	public String getStringValue() {
		return stringValue;
	}
	
	public Type getType() {
		return this.type;
	}
}
