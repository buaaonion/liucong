/** 
 * @author  LiuCong  
 * @file    CharLitNode.java 
 * @date    Date：2015年12月23日 下午9:32:10 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import cn.edu.buaa.bstar.symbolt.Type;

public class CharLitNode extends BNode {
	
	private char charValue;
	private Type type = Type.newCharType();
	
	public CharLitNode(char charValue, int line) {
		super(line);
		this.charValue = charValue;
	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		cStringBuilder.append("'"+charValue+"'");
	}

	public char getCharValue() {
		return charValue;
	}
	
	public Type getType() {
		return this.type;
	}
}
