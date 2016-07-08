/** 
 * @author  LiuCong  
 * @file    FunctionDefineHeadNode.java 
 * @date    Date：2016年5月8日 下午4:52:09 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import java.util.Iterator;

import cn.edu.buaa.bstar.exception.SemanticsException;
import cn.edu.buaa.bstar.symbolt.FunctionType;
import cn.edu.buaa.bstar.symbolt.SymbolTables;

public class FunctionDefineHeadNode extends BNode {

	public FunctionDefineHeadNode(int line) {
		super(line);
	}

	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub
		super.typeCheck();
		pSymTab = new SymbolTables();
		FunctionType functionType;
		Iterator<BNode> iterator = getChildNodes().iterator();
		TypeNode typeNode = (TypeNode)iterator.next();
		typeNode.typeCheck();
		PointIdNode pointIdNode = (PointIdNode)iterator.next();
		pointIdNode.typeCheck();
		if(gSymTab.containsId(pointIdNode.getId())||incSymTab.containsId(pointIdNode.getId())) {
			throw SemanticsException.redefineException(getRootNode().getFileName(), line, pointIdNode.getId()+" redefined!");
		}
		if(pointIdNode.isPoint()) { 
			retType = pointIdNode.getType();
			retType.addPointType(typeNode.getType());
		} else {
			retType = typeNode.getType();
		}
		functionType = new FunctionType(retType, pointIdNode.getId(), true);
		iterator.next();
		BNode bNode = iterator.next();
		if(bNode instanceof StringNode) {
			gSymTab.addFun(pointIdNode.getId(), functionType);
		} else {
			ParaDefineListNode paraDefineList = (ParaDefineListNode)bNode;
			paraDefineList.setFunctionType(functionType);
			paraDefineList.typeCheck();
			iterator.next();
		}
	}

	@Override
	public void toCCode() {
		// 这个函数不需要实现，因为后面的函数定义会生成对应的头文件
		super.toCCode();
	}

	
}
