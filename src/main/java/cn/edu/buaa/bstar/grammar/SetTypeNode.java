/** 
 * @author  LiuCong  
 * @file    SetTypeNode.java 
 * @date    Date：2015年12月23日 下午9:27:28 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import cn.edu.buaa.bstar.exception.SemanticsException;
import cn.edu.buaa.bstar.symbolt.Type;

public class SetTypeNode extends BNode {

	private Type type;

	public SetTypeNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub
		super.typeCheck();
		TypeNode typeNode = (TypeNode) getChildNodeAt(2);
		typeNode.typeCheck();
		if (typeNode.getType().isSet()) {
			throw SemanticsException.mismatchException(getRootNode()
					.getFileName(), line, "elements  in set can not be set!");
		}
		this.type = Type.newSetType(typeNode.toString(), typeNode.getType());
		AtLitNode atLitNode = (AtLitNode) getChildNodeAt(4);
		atLitNode.typeCheck();
		this.type.setSetI(atLitNode.getSetImplement());
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		cStringBuilder.append(this.type.setIString()).append("*");
	}

}
