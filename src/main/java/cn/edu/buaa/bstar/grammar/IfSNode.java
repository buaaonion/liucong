/** 
 * @author  LiuCong  
 * @file    IfSNode.java 
 * @date    Date：2015年12月23日 下午9:20:37 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import java.util.Iterator;

import cn.edu.buaa.bstar.c.MachineC;
import cn.edu.buaa.bstar.exception.SemanticsException;

public class IfSNode extends BNode {

	public IfSNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub
		super.typeCheck();
		Iterator<BNode> iterator = getChildNodes().iterator();
		iterator.next();
		iterator.next();
		EleNode eleNode  = (EleNode)iterator.next();
		eleNode.typeCheck();
		if(!eleNode.getType().isNumber()) {
			throw SemanticsException.mismatchException(getRootNode().getFileName(), line, "if expression's condition should be boolean!");
		}
		iterator.next();
		StatementNode statementNode = (StatementNode)iterator.next();
		statementNode.typeCheck();
		if(iterator.hasNext()) {
			iterator.next();
			statementNode = (StatementNode)iterator.next();
			statementNode.typeCheck();
		}
	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		getChildNodeAt(2).toCCode();
		MachineC.functionStaString.append("if ("+cStringBuilder.toString()+")");
		cleanCStringBuilder();
		getChildNodeAt(4).toCCode();
		MachineC.functionStaString.append(cStringBuilder.toString());
		cleanCStringBuilder();
		if(getChildNodes().size()>5) {
			MachineC.functionStaString.append(" else ");
			getChildNodeAt(6).toCCode();
		}
		MachineC.functionStaString.append(cStringBuilder.toString());
		cleanCStringBuilder();
	}
	
}
