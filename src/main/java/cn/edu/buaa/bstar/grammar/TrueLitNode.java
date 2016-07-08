/** 
 * @author  LiuCong  
 * @file    UCharLitNode.java 
 * @date    Date：2016年1月25日 下午8:01:45 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import cn.edu.buaa.bstar.symbolt.Type;

public class TrueLitNode extends BNode {

	private Type type = Type.newPropositionType();
	
	public TrueLitNode(int line) {
		super(line);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		cStringBuilder.append("true");
	}

	public Type getType() {
		return type;
	}
}
