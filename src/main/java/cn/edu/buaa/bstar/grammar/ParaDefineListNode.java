/** 
 * @author  LiuCong  
 * @file    ParaDefineList.java 
 * @date    Date：2015年12月23日 下午9:15:42 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import java.util.Iterator;

import cn.edu.buaa.bstar.exception.SemanticsException;
import cn.edu.buaa.bstar.symbolt.FunctionType;
import cn.edu.buaa.bstar.symbolt.Type;

public class ParaDefineListNode extends BNode {

	private FunctionType functionType;
	
	public ParaDefineListNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub
		super.typeCheck();
		Iterator<BNode> iterator = getChildNodes().iterator();
		while(iterator.hasNext()) {
			TypeNode typeNode = (TypeNode)iterator.next();
			PointIdNode pointIdNode = (PointIdNode)iterator.next();
			typeNode.typeCheck();
			pointIdNode.typeCheck();
			String id = pointIdNode.getId();
			Type type;
			if(pointIdNode.isPoint()) {
				type = pointIdNode.getType();
				type.addPointType(typeNode.getType());
			} else {
				type = typeNode.getType();
			}
			if(pSymTab.containsId(id)) {
				throw SemanticsException.redefineException(getRootNode().getFileName(), line, id + " redefined!");
			} else {
				pSymTab.addVaraT(id, type);
			}
			functionType.addSubType(type, id);
			if(iterator.hasNext()) {
				iterator.next();
			}
		}
		gSymTab.addFun(functionType.getfuncName(), functionType);
	}
	
	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		Iterator<BNode> iterator = getChildNodes().iterator();
		while(true) {
			iterator.next().toCCode();
			cStringBuilder.append(" ");
			iterator.next().toCCode();
			if(iterator.hasNext()) {
				iterator.next();
				cStringBuilder.append(", ");
			} else {
				break;
			}
		}
	}

	public void setFunctionType(FunctionType functionType) {
		this.functionType = functionType;
	}
}
