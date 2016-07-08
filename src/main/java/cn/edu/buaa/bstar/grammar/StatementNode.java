/** 
 * @author  LiuCong  
 * @file    StatementNode.java 
 * @date    Date：2015年12月23日 下午9:16:50 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import java.util.Iterator;

import cn.edu.buaa.bstar.c.MachineC;
import cn.edu.buaa.bstar.exception.SemanticsException;

public class StatementNode extends BNode {

	public StatementNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub
		super.typeCheck();
		Iterator<BNode> iterator = getChildNodes().iterator();
		while(iterator.hasNext()) {
			BNode bNode = iterator.next();
			if(bNode instanceof StringNode) {
				continue;
			}
			bNode.typeCheck();
		}
	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		Iterator<BNode> iterator = getChildNodes().iterator();
		BNode bNode = iterator.next();
		if(bNode instanceof StringNode) {
			if(((StringNode)bNode).getString().equals(";")) {
				MachineC.functionStaString.append(";\n");
			} else {
				MachineC.functionStaString.append("{\n");
				bNode = iterator.next();
				while(bNode instanceof StatementNode) {
					bNode.toCCode();
					bNode = iterator.next();
				}
				MachineC.functionStaString.append("}\n");
			}
		} else {
			bNode.toCCode();
			MachineC.functionStaString.append(cStringBuilder.toString());
			cleanCStringBuilder();
			if(iterator.hasNext()) {
				MachineC.functionStaString.append(";\n");
			}
		}
	}
}
