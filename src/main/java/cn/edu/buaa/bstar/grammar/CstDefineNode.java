/** 
 * @author  LiuCong  
 * @file    CstDefineNode.java 
 * @date    Date：2015年12月23日 下午9:02:00 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import java.util.Iterator;

import cn.edu.buaa.bstar.exception.SemanticsException;
import cn.edu.buaa.bstar.symbolt.Type;

public class CstDefineNode extends BNode {

	public CstDefineNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	// const 语句不能是集合操作
	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub
		super.typeCheck();
		int scope = pSymTab == null ? 0 : 1;// 作用域，全局(0)or局部(1)
		Iterator<BNode> iterator = getChildNodes().iterator();
		iterator.next();
		TypeNode typeNode = (TypeNode) iterator.next();
		typeNode.typeCheck();
		if (typeNode.getType().isSet()) {
			throw SemanticsException.mismatchException(getRootNode()
					.getFileName(), line, "const type can not be set!");
		}
		Type type;
		while (true) {
			PointIdNode pointIdNode = (PointIdNode) iterator.next();
			pointIdNode.typeCheck();
			String id = pointIdNode.getId();
			if (pointIdNode.isPoint()) {
				type = pointIdNode.getType();
				type.addPointType(typeNode.getType());
			} else {
				type = typeNode.getType();
			}
			iterator.next();
			EleNode eleNode = (EleNode) iterator.next();
			eleNode.typeCheck();
			if (eleNode.isSetOpe()) {
				throw SemanticsException.mismatchException(getRootNode()
						.getFileName(), line, "const var can not be set ope!");
			}
			if (eleNode.getType().equals(type)) {
				if (scope == 0) { // 全局
					if (!gSymTab.containsId(id) && !incSymTab.containsId(id)) {
						this.gSymTab.addConT(id, type);
					} else {
						throw SemanticsException.redefineException(
								getRootNode().getFileName(), line, id
										+ " redefine!");
					}
				} else { // 局部
					if (!pSymTab.containsId(id)) {
						this.pSymTab.addConT(id, type);
					} else {
						throw SemanticsException.redefineException(
								getRootNode().getFileName(), line, id
										+ " redefine!");
					}
				}
			} else {
				throw SemanticsException.mismatchException(getRootNode()
						.getFileName(), line,
						"const value can not fit it's type");
			}
			if (iterator.hasNext()) {
				iterator.next();
			} else {
				break;
			}
		}
	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		StringBuilder stringBuilder = new StringBuilder("const "); // 用来暂存c文件中的const语句
		int scope = pSymTab == null ? 0 : 1;
		Iterator<BNode> iterator = getChildNodes().iterator();
		iterator.next();
		BNode bNode = iterator.next();
		bNode.toCCode();
		stringBuilder.append(cStringBuilder).append(" ");
		if (scope == 0) {
			this.machineC.addhFile("extern " + stringBuilder
					.toString());
		}
		cleanCStringBuilder();
		while (true) {
			PointIdNode pointIdNode = (PointIdNode) iterator.next();
			pointIdNode.toCCode();
//			Type type;
//			type = scope == 0 ? gSymTab.getVaraT(pointIdNode.getId()) : pSymTab
//					.getVaraT(pointIdNode.getId());
			stringBuilder.append(cStringBuilder).append(" = ");
			if (scope == 0) {
				this.machineC.addhFile(cStringBuilder.toString());
			}
			cleanCStringBuilder();
			iterator.next();
			EleNode eleNode = (EleNode) iterator.next();
			eleNode.toCCode();
			if (iterator.hasNext()) {
				iterator.next();
				stringBuilder.append(cStringBuilder).append(
						", ");
				if (scope == 0) {
					this.machineC.addhFile(", ");
				}
				cleanCStringBuilder();
			} else {
				stringBuilder.append(BNode.cStringBuilder).append(
						";\n");
				cleanCStringBuilder();
				if (scope == 0) {
					this.machineC.addhFile(";\n");
				}
				break;
			}
		}
		if (scope == 0) {
			this.machineC.addcFile(stringBuilder.toString());
		} else {
			this.machineC.addFunctionString(stringBuilder.toString());
		}
	}

}
