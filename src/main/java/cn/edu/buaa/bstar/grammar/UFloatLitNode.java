/** 
 * @author  LiuCong  
 * @file    UFloatLitNode.java 
 * @date    Date：2015年12月23日 下午9:30:41 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import cn.edu.buaa.bstar.symbolt.Type;

public class UFloatLitNode extends BNode {
	
	private float floatValue;
	private Type type = Type.newUFloatType();
	
	public UFloatLitNode(float floatValue, int line) {
		super(line);
		this.floatValue = floatValue;
	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		cStringBuilder.append(floatValue);
	}

	public float getFloatValue() {
		return floatValue;
	}
	
	public Type getType(){
		return this.type;
	}
}
