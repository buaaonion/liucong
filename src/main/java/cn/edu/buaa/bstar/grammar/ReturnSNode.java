/** 
 * @author  LiuCong  
 * @file    ReturnSNode.java 
 * @date    Date：2015年12月23日 下午9:19:06 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import cn.edu.buaa.bstar.c.MachineC;
import cn.edu.buaa.bstar.exception.SemanticsException;

public class ReturnSNode extends BNode {

	public ReturnSNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub
		super.typeCheck();
		if(getChildNodes().size() == 2) {
			EleNode eleNode = (EleNode)getChildNodeAt(1);
			eleNode.typeCheck();
			if(!retType.equals(eleNode.getType())) {
				throw SemanticsException.mismatchException(getRootNode().getFileName(), line, "returnType misMatch!");
			}
		} else {
			if(!retType.isVoid()) {
				throw SemanticsException.mismatchException(getRootNode().getFileName(), line, "returnType misMatch!");
			}
		}
	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		if(getChildNodes().size() == 2) {
			EleNode eleNode = (EleNode)getChildNodeAt(1);
			if(eleNode.isSetOpe()) {
				eleNode.setLeftType(this.retType);
			}
			eleNode.toCCode();
			MachineC.functionStaString.append("return "+cStringBuilder.toString());
			cleanCStringBuilder();
		} else {
			MachineC.functionStaString.append("return");
		}
	}
	
}
