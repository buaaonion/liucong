/** 
 * @author  LiuCong  
 * @file    VarDefineNode.java 
 * @date    Date：2015年12月23日 下午9:01:23 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cn.edu.buaa.bstar.exception.SemanticsException;
import cn.edu.buaa.bstar.symbolt.Type;

public class VarDefineNode extends BNode {

	// 未初始化的变量，为了子类型中是集合类型的初始化（暂时不做初始化，因为涉及空间的申请，会造成内存的混乱）
	private List<String> ids = new LinkedList<String>(); 

	public VarDefineNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub
		super.typeCheck();
		int scope = pSymTab == null ? 0 : 1;// 作用域，全局(0)or局部(1)
		Iterator<BNode> iterator = getChildNodes().iterator();
		BNode bNode = iterator.next();
		if (bNode instanceof StringNode) {
			bNode = iterator.next();
			if (bNode instanceof EnumTypeNode) {
				EnumTypeNode enumTypeNode = (EnumTypeNode) bNode;
				String id = ((IdNode) iterator.next()).getId();
				Type type = Type.newEnumType(id);
				enumTypeNode.setImage(id);
				enumTypeNode.typeCheck();
				if (scope == 0) {
					gSymTab.addTypeT(id, type);
				} else {
					pSymTab.addTypeT(id, type);
				}
			} else if (bNode instanceof TypeNode) {
				TypeNode typeNode = (TypeNode) bNode;
				typeNode.typeCheck();
				PointIdNode pointIdNode = (PointIdNode) iterator.next();
				pointIdNode.typeCheck();
				String id = pointIdNode.getId();
				Type type;
				if (typeNode.getType().isSet() && pointIdNode.isPoint()) {
					throw SemanticsException.mismatchException(getRootNode()
							.getFileName(), line, "set type can not be point!");
				}
				if (pointIdNode.isPoint()) {
					type = pointIdNode.getType();
					type.addPointType(typeNode.getType());
				} else {
					type = typeNode.getType();
				}
				type.setImage(pointIdNode.getId());// typedef后image需要重新修改，并不是指针，方便强制类型转换
				// if (!type.isSet()) {
				// type.setImage(id);
				// } //集合中的image依然是存储的类型的名称，其他的类型因为是set的操作可能需要定义临时变量
				if (scope == 0) {
					if (gSymTab.containsId(id) || incSymTab.containsId(id)) {
						throw SemanticsException.redefineException(
								getRootNode().getFileName(), line, id
										+ " redefined!");
					}
					gSymTab.addTypeT(id, type);
				} else {
					if (pSymTab.containsId(id)) {
						throw SemanticsException.redefineException(
								getRootNode().getFileName(), line, id
										+ " redefined!");
					}
					pSymTab.addTypeT(id, type);
				}
			} else if (bNode instanceof TupleTypeNode) {
				TupleTypeNode tupleTypeNode = (TupleTypeNode) bNode;
				tupleTypeNode.typeCheck();
				IdNode idNode = (IdNode) iterator.next();
				idNode.typeCheck();
				String id = idNode.getId();
				Type type;
				type = tupleTypeNode.getType();
				type.setImage(id);
				if (scope == 0) {
					if (gSymTab.containsId(id) || incSymTab.containsId(id)) {
						throw SemanticsException.redefineException(
								getRootNode().getFileName(), line, id
										+ " redefined!");
					}
					gSymTab.addTypeT(id, type);
				} else {
					if (pSymTab.containsId(id)) {
						throw SemanticsException.redefineException(
								getRootNode().getFileName(), line, id
										+ " redefined!");
					}
					pSymTab.addTypeT(id, type);
				}
			} else {
				IdNode idNode = (IdNode) getChildNodeAt(2);
				PointIdNode pointIdNode = (PointIdNode) getChildNodeAt(3);
				pointIdNode.typeCheck();
				if (!pointIdNode.isPoint()) {
					throw SemanticsException.mismatchException(getRootNode()
							.getFileName(), line,
							"After typedef struct name must be point!");
				}
				Type type = Type.newPointType();
				((CVDefineNode) this.fatherNode).addNullStruct(
						pointIdNode.getId(), idNode.getId());
				if (scope == 0) {
					gSymTab.addTypeT(pointIdNode.getId(), type);
				} else {
					pSymTab.addTypeT(pointIdNode.getId(), type);
				}
			}
		} else {
			TypeNode typeNode = (TypeNode) bNode;
			typeNode.typeCheck();
			while (iterator.hasNext()) {
				PointIdNode pointIdNode = (PointIdNode) iterator.next();
				pointIdNode.typeCheck();
				String id = pointIdNode.getId();
				
				Type type;
				if (typeNode.getType().isSet() && pointIdNode.isPoint()) {
					throw SemanticsException.mismatchException(getRootNode()
							.getFileName(), line, "set type can not be point!");
				}
				if (pointIdNode.isPoint()) {
					type = pointIdNode.getType();
					type.addPointType(typeNode.getType());
				} else {
					type = typeNode.getType();
				}
				if (iterator.hasNext()) {
					StringNode node = (StringNode) (iterator.next());
					if (node.getString().equals("=")) {
						EleNode eleNode = (EleNode) iterator.next();
						eleNode.typeCheck();
						if (!eleNode.getType().equals(type)) {
							throw SemanticsException
									.mismatchException(
											getRootNode().getFileName(),
											line,
											id
													+ "'s type dismatch the right expresion!");
						}
						if (iterator.hasNext()) {
							iterator.next();
						}
					} else {
						ids.add(id);
					}
				}
				if (scope == 0) {
					if (gSymTab.containsId(id) || incSymTab.containsId(id)) {
						throw SemanticsException.redefineException(
								getRootNode().getFileName(), line, id
										+ " redefined!");
					}
					gSymTab.addVaraT(id, type);
				} else {
					if (pSymTab.containsId(id)) {
						throw SemanticsException.redefineException(
								getRootNode().getFileName(), line, id
										+ " redefined!");
					}
					pSymTab.addVaraT(id, type);
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
		int scope = pSymTab == null ? 0 : 1;
		BNode bNode = iterator.next();
		if (bNode instanceof StringNode) {
			stringBuilder.append("typedef ");
			bNode = iterator.next();
			if (bNode instanceof TupleTypeNode) {
				stringBuilder.append("struct ");
				iterator.next().toCCode();
				String name = cStringBuilder.toString();
				cleanCStringBuilder();
				stringBuilder.append(name);
				bNode.toCCode();
				stringBuilder.append(cStringBuilder);
				stringBuilder.append(name);
				cleanCStringBuilder();
			} else if (bNode instanceof EnumTypeNode) {
				stringBuilder.append("enum ");
				bNode.toCCode();
				stringBuilder.append(cStringBuilder);
				cleanCStringBuilder();
				iterator.next().toCCode();
				stringBuilder.append(cStringBuilder);
				cleanCStringBuilder();
			} else {
				bNode.toCCode();
				stringBuilder.append(cStringBuilder);
				stringBuilder.append(" ");
				cleanCStringBuilder();
				iterator.next().toCCode();
				stringBuilder.append(cStringBuilder);
				cleanCStringBuilder();
			}
			if (iterator.hasNext()) {
				stringBuilder.append(" ");
				iterator.next().toCCode();
				stringBuilder.append(cStringBuilder);
				cleanCStringBuilder();
			}
			stringBuilder.append(";\n");
			if (scope == 0) {
				this.machineC.addhFile(stringBuilder.toString());
			} else {
				this.machineC.addFunctionString(stringBuilder.toString());
			}
		} else {

			bNode.toCCode();
			Type type = ((TypeNode) bNode).getType();
			stringBuilder.append(cStringBuilder).append(" ");
			if (scope == 0) {
				this.machineC.addhFile("extern " + cStringBuilder.toString()
						+ " ");
			}
			cleanCStringBuilder();
			while (true) {
//				if (type.isSet()) {
//					stringBuilder.append("*");
//					if (scope == 0) {
//						this.machineC.addhFile("*");
//					}
//				}
				PointIdNode pointIdNode = (PointIdNode) iterator.next();
				pointIdNode.toCCode();
				stringBuilder.append(cStringBuilder.toString());
				if (scope == 0) {
					this.machineC.addhFile(cStringBuilder.toString());
				}
				cleanCStringBuilder();
				if (iterator.hasNext()) {
					StringNode stringNode = (StringNode) iterator.next();
					if (stringNode.getString().equals("=")) {
						stringBuilder.append(" = ");
						EleNode eleNode = (EleNode) iterator.next();
						if (eleNode.isSetOpe()) { // 如果左边是<>{}类型
							if (pointIdNode.isPoint()) {
								eleNode.setLeftType(Type.newPointType(type));
							} else {
								eleNode.setLeftType(type);
							}
						}
						if (eleNode.getType().isPoint()
								&& eleNode.getType().getSubType(0).isVoid()) {
							stringBuilder.append("(" + type.getImage() + "*)");
						}
						if (eleNode.isSetOpe()) {
							eleNode.setLeftType(type);
						}
						eleNode.toCCode();
						stringBuilder.append(cStringBuilder.toString());
						cleanCStringBuilder();
						if (iterator.hasNext()) {
							iterator.next();
						} else {
							stringBuilder.append(";\n");
							if (scope == 0) {
								this.machineC.addhFile(";\n");
							}
							break;
						}
					} else {
						if (type.isArraySet() && scope == 1) {
							stringBuilder.append(" = Array_new(256, BStar_equal)");
						} else if (type.isListSet() && scope == 1) {
							stringBuilder.append(" = List_new(BStar_equal)");
						}
					}
					stringBuilder.append(", ");
					if (scope == 0) {
						this.machineC.addhFile(", ");
					}
				} else {
					if (type.isListSet() && scope == 1) {
						stringBuilder.append(" = List_new(BStar_equal)");
					} else if (type.isArraySet() && scope == 1) {
						stringBuilder.append(" = Array_new(256, BStar_equal)");
					}
					stringBuilder.append(";\n");
					if (scope == 0) {
						this.machineC.addhFile(";\n");
					}
					break;
				}
			}
			/*
			 * 需要考虑一下，因为定义的时候如果申请了空间，但是在后面赋值的时候指向另一个同类型的变量，设计到申请空间需要释放的一个问题
			for (String id : ids) {
				stringBuilder.append(setInit(type, id));
			}*/

			if (scope == 0) {
				this.machineC.addcFile(stringBuilder.toString());
			} else {
				this.machineC.addFunctionString(stringBuilder.toString());
			}
		}
	}
	
	/*
	// 结构体类型如果当中存在集合子类型，需要初始化(注意：指针类型定义时候)
	private String setInit(Type type, String id) {
		StringBuilder stringBuilder = new StringBuilder();
		if (type.isStruct()) {
			if (type.isPoint()) {
				for (int i = 0; i < type.getSubSize(); i++) {
					if (type.getSubType(i).isListSet()) {
						stringBuilder.append(id + "->" + type.getSubName(i)
								+ " = List_new(BStar_equal);\n");
					} else if (type.getSubType(i).isArraySet()) {
						stringBuilder.append(id + "->" + type.getSubName(i)
								+ " = Array_new(256);\n");
					}
				}
			} else {
				for (int i = 0; i < type.getSubSize(); i++) {
					if (type.getSubType(i).isListSet()) {
						stringBuilder.append(id + "." + type.getSubName(i)
								+ " = List_new(BStar_equal);\n");
					} else if (type.getSubType(i).isArraySet()) {
						stringBuilder.append(id + "." + type.getSubName(i)
								+ " = Array_new(256);\n");
					}
				}
			}
		}
		return stringBuilder.toString();
	}*/
}
