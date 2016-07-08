/** 
 * @author  LiuCong  
 * @file    EnumTypeNode.java 
 * @date    Date：2015年12月23日 下午9:03:56 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import java.util.Iterator;

import cn.edu.buaa.bstar.exception.SemanticsException;
import cn.edu.buaa.bstar.symbolt.Type;

public class EnumTypeNode extends BNode {

	private String image; // enum类型的名称

	public EnumTypeNode(int line) {
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
		BNode bNode = iterator.next();
		if (bNode instanceof StringNode) {
			return;
		} else {
			while (true) {
				Type type = Type.newEnumType(image);
				IdNode idNode = (IdNode) bNode;
				String id = idNode.getId();
				StringNode stringNode = (StringNode) iterator.next();
				if (stringNode.getString().equals("=")) {
					UnaryENode unatyENode = (UnaryENode) iterator.next();
					unatyENode.typeCheck();
					if (!unatyENode.getType().isInteger()) {
						throw SemanticsException.mismatchException(
								getRootNode().getFileName(), line,
								"enum type value should be Integer");
					} else if (unatyENode.isSetOpe()) {
						throw SemanticsException.mismatchException(
								getRootNode().getFileName(), line,
								"enum type value can not be complex operation!");
					}
					stringNode = (StringNode) iterator.next();
				}
				if (pSymTab == null) {
					if (gSymTab.containsId(id) || incSymTab.containsId(id)) {
						throw SemanticsException.redefineException(
								getRootNode().getFileName(), line, "enum type "
										+ image + " subType " + id
										+ " redefined!");
					} else {
						gSymTab.addEnumT(id, type);
					}
				} else {
					if (pSymTab.containsId(id)) {
						throw SemanticsException.redefineException(
								getRootNode().getFileName(), line, "enum type "
										+ image + " subType " + id
										+ " redefined!");
					} else {
						pSymTab.addEnumT(id, type);
					}
				}
				if (stringNode.getString().equals(",")) {
					bNode = iterator.next();
				} else {
					break;
				}
			}
		}
	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		StringBuilder stringBuilder = new StringBuilder();
		Iterator<BNode> iterator = getChildNodes().iterator();
		iterator.next();
		iterator.next();
		BNode bNode = iterator.next();
		stringBuilder.append("{\n");
		if (bNode instanceof StringNode) {
			stringBuilder.append("} ");
			cStringBuilder.append(stringBuilder);
			return;
		}
		while (true) {
			bNode.toCCode();
			stringBuilder.append(cStringBuilder);
			cleanCStringBuilder();
			StringNode stringNode = (StringNode) iterator.next();
			if (stringNode.getString().equals("=")) {
				stringBuilder.append(" = ");
				iterator.next().toCCode();
				stringBuilder.append(cStringBuilder);
				cleanCStringBuilder();
				stringNode = (StringNode) iterator.next();
			}
			if (stringNode.getString().equals(",")) {
				stringBuilder.append(",\n");
				bNode = iterator.next();
			} else {
				break;
			}
		}
		stringBuilder.append("\n} ");
		cStringBuilder.append(stringBuilder);
	}

	public void setImage(String image) {
		this.image = image;
	}
}
