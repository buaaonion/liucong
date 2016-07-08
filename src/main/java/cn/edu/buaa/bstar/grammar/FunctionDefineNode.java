/** 
 * @author  LiuCong  
 * @file    FunctionDefineNode.java 
 * @date    Date：2015年12月23日 下午9:05:27 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import java.util.Iterator;

import cn.edu.buaa.bstar.c.MachineC;
import cn.edu.buaa.bstar.exception.SemanticsException;
import cn.edu.buaa.bstar.symbolt.FunctionType;
import cn.edu.buaa.bstar.symbolt.SymbolTables;

public class FunctionDefineNode extends BNode {

	public FunctionDefineNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub

		super.typeCheck();
		pSymTab = new SymbolTables();
		FunctionType functionType, funcHeadType = null;
		Iterator<BNode> iterator = getChildNodes().iterator();
		TypeNode typeNode = (TypeNode) iterator.next();
		typeNode.typeCheck();
		PointIdNode pointIdNode = (PointIdNode) iterator.next();
		pointIdNode.typeCheck();
		if (incSymTab.containsId(pointIdNode.getId())) {
			throw SemanticsException.redefineException(getRootNode()
					.getFileName(), line, pointIdNode.getId() + " redefined!");
		} else if (gSymTab.containsId(pointIdNode.getId())) {
			if (gSymTab.containsFun(pointIdNode.getId())) {
				funcHeadType = gSymTab.getFuncT(pointIdNode.getId());
				if (!funcHeadType.isHeadDef()) {
					throw SemanticsException.redefineException(getRootNode()
							.getFileName(), line, pointIdNode.getId()
							+ " redefined!");
				}
			} else {
				throw SemanticsException.redefineException(getRootNode()
						.getFileName(), line, pointIdNode.getId()
						+ " redefined!");
			}
		}
		if (pointIdNode.isPoint()) {
			retType = pointIdNode.getType();
			retType.addPointType(typeNode.getType());
		} else {
			retType = typeNode.getType();
		}
		functionType = new FunctionType(retType, pointIdNode.getId(), false);
		iterator.next();
		BNode bNode = iterator.next();
		if (bNode instanceof StringNode) {
			gSymTab.addFun(pointIdNode.getId(), functionType);
		} else {
			ParaDefineListNode paraDefineList = (ParaDefineListNode) bNode;
			paraDefineList.setFunctionType(functionType);
			paraDefineList.typeCheck();
			iterator.next();
		}
		if (funcHeadType != null && !functionType.equals(funcHeadType)) {
			throw SemanticsException.redefineException(getRootNode()
					.getFileName(), line, "function " + pointIdNode.getId()
					+ " misMatch the function head you defined ahead!");
		} else if(funcHeadType != null){
			funcHeadType.setIsHeadDef(false); //防止后面还有相同的函数实现
		}
		iterator.next();
		bNode = iterator.next();
		if (bNode instanceof StringNode) {
			return;
		}
		ComStatementNode comStatement = (ComStatementNode) bNode;
		comStatement.typeCheck();
	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		Iterator<BNode> iterator = getChildNodes().iterator();
		StringBuilder stringBuilder = new StringBuilder();
		iterator.next().toCCode();
		cStringBuilder.append(" ");
		iterator.next().toCCode();
		stringBuilder.append(cStringBuilder.toString()).append("(");
		cleanCStringBuilder();
		iterator.next();
		BNode bNode = iterator.next();
		if (bNode instanceof StringNode) {
			stringBuilder.append(")");
		} else {
			bNode.toCCode();
			stringBuilder.append(cStringBuilder.toString()).append(")");
			cleanCStringBuilder();
			iterator.next();
		}
		iterator.next();
		this.machineC.addFunctionString(stringBuilder.toString() + "\n{\n");
		this.machineC.addhFile("extern " + stringBuilder.toString() + ";\n");
		bNode = iterator.next();
		if (!(bNode instanceof StringNode)) {
			bNode.toCCode();
		}
		this.machineC.addFunctionString(MachineC.functionStaString.toString());
		MachineC.functionStaString.delete(0, MachineC.functionStaString.length());
		this.machineC.addFunctionString("}\n");
	}

}
