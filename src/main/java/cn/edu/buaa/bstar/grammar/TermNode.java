/** 
 * @author  LiuCong  
 * @file    TermNode.java 
 * @date    Date：2015年12月23日 下午9:30:14 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import java.util.Iterator;

import cn.edu.buaa.bstar.c.MachineC;
import cn.edu.buaa.bstar.exception.SemanticsException;
import cn.edu.buaa.bstar.symbolt.SetImplement;
import cn.edu.buaa.bstar.symbolt.Type;

public class TermNode extends BNode {

	private Type type;
	private boolean setOpe = false;
	private Type leftType;

	public TermNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub
		super.typeCheck();
		Iterator<BNode> iterator = getChildNodes().iterator();
		BNode bNode = iterator.next();
		if (bNode instanceof StringNode) {
			StringNode stringNode = (StringNode) bNode;
			if (stringNode.getString().equals("nil")) {
				this.type = Type.newNilType();
				return;
			} else if (stringNode.getString().equals("{")) {
				this.setOpe = true;
				EleNode eleNode = (EleNode) iterator.next();
				eleNode.typeCheck();
				this.type = Type.newSetType(eleNode.getType());
				stringNode = (StringNode) iterator.next();
				while (!stringNode.getString().equals("}")) {
					eleNode = (EleNode) iterator.next();
					eleNode.typeCheck();
					if (!this.type.getSubType(0).equals(eleNode.getType())) {
						throw SemanticsException.mismatchException(
								getRootNode().getFileName(), line,
								"element in set should have the same type!");
					} else if (!eleNode.getType().isPoint()
							&& !(eleNode.isSetOpe())) {// <>默认为是指针
						throw SemanticsException.mismatchException(
								getRootNode().getFileName(), line,
								"element in {} should be point type!");
					}
					stringNode = (StringNode) iterator.next();
				}
				if (iterator.hasNext()) {
					AtLitNode atLitNode = (AtLitNode) iterator.next();
					atLitNode.typeCheck();
					this.type.setSetI(atLitNode.getSetImplement());
				}
			} else if (stringNode.getString().equals("<")) {
				this.setOpe = true;
				this.type = Type.newStructTypeBlank();
				while (!stringNode.getString().equals(">")) {
					EleNode eleNode = (EleNode) iterator.next();
					eleNode.typeCheck();
					this.type.addSubType(eleNode.getType());
					stringNode = (StringNode) iterator.next();
				}
				if (iterator.hasNext()) {
					throw SemanticsException.mismatchException(getRootNode()
							.getFileName(), line,
							"after '<>' node can not have others!");
				}
			} else if (stringNode.getString().equals("(")) {
				bNode = iterator.next();
				if (bNode instanceof TypeNode) {
					TypeNode typeNode = (TypeNode) bNode;
					typeNode.typeCheck();
					stringNode = (StringNode) iterator.next();
					if (stringNode.getString().equals("*")) {
						this.type = Type.newPointType();
						this.type.addPointType(typeNode.getType());
						iterator.next();
					} else {
						this.type = typeNode.getType();
					}
					//iterator.next();
					EleNode eleNode = (EleNode) iterator.next();
					eleNode.typeCheck();
					if (!(eleNode.getType().isNumber() || eleNode.getType()
							.isPoint())) {
						throw SemanticsException.mismatchException(
								getRootNode().getFileName(), line,
								"casts can only be Integer or point");
					}
				} else {
					EleNode eleNode = (EleNode) bNode;
					eleNode.typeCheck();
					this.setOpe = eleNode.isSetOpe();
					this.type = eleNode.getType();
					iterator.next();
				}
			} else if (stringNode.getString().equals("sizeof")) {
				this.type = Type.newIntType();
				iterator.next();
				iterator.next().typeCheck();
				iterator.next();
			}
		} else {
			if (bNode instanceof StringLitNode) {
				this.type = ((StringLitNode) bNode).getType();
			} else if (bNode instanceof UIntLitNode) {
				this.type = ((UIntLitNode) bNode).getType();
			} else if (bNode instanceof UFloatLitNode) {
				this.type = ((StringLitNode) bNode).getType();
			} else if (bNode instanceof CharLitNode) {
				this.type = ((CharLitNode) bNode).getType();
			} else if (bNode instanceof TrueLitNode) {
				this.type = ((TrueLitNode) bNode).getType();
			} else if (bNode instanceof FalseLitNode) {
				this.type = ((FalseLitNode) bNode).getType();
			} else {
				if (bNode instanceof FunctionCallNode) {
					FunctionCallNode functionCallNode = (FunctionCallNode) bNode;
					functionCallNode.typeCheck();
					this.type = functionCallNode.getType();
				} else {
					IdNode idNode = (IdNode) bNode;
					if (containsId(idNode.getId())) {
						this.type = getIdType(idNode.getId());
					} else {
						// if(iterator.hasNext()) {
						throw SemanticsException.undefineException(
								getRootNode().getFileName(), line,
								idNode.getId() + " undefined!");
						// }
					}
				}
			}
		}
		while (iterator.hasNext()) {
			if (!this.type.isStruct()) {
				throw SemanticsException.mismatchException(getRootNode()
						.getFileName(), line,
						"you can't take subName from non-struct!");
			}
			StringNode stringNode = (StringNode) iterator.next();
			IdNode idNode = (IdNode) iterator.next();
			if (this.type.isPoint()) {
				if (stringNode.getString().equals(".")) {
					throw SemanticsException.mismatchException(getRootNode()
							.getFileName(), line,
							"'.' should replaced with '->'");
				}
				if (this.type.getSubType(0).containsSubName(idNode.getId())) {
					this.type = this.type.getSubType(0).getSubType(
							idNode.getId());
				} else {
					throw SemanticsException.undefinedChildException(
							getRootNode().getFileName(), line,
							"struct Without this subtype " + idNode.getId());
				}
			} else {
				if (stringNode.getString().equals("->")) {
					throw SemanticsException.mismatchException(getRootNode()
							.getFileName(), line,
							"'->' should replaced with '.'");
				}
				if (this.type.containsSubName(idNode.getId())) {
					this.type = this.type.getSubType(idNode.getId());
				} else {
					throw SemanticsException.undefinedChildException(
							getRootNode().getFileName(), line,
							"struct Without this subtype " + idNode.getId());
				}
			}
		}
	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		int scope = pSymTab == null ? 0 : 1;
		Iterator<BNode> iterator = getChildNodes().iterator();
		BNode bNode = iterator.next();
		if (bNode instanceof StringNode) {
			StringNode stringNode = (StringNode) bNode;
			if (stringNode.getString().equals("nil")) {
				cStringBuilder.append("NULL");
			} else if (stringNode.getString().equals("{")) {
				// StringBuilder stringBuilder = new StringBuilder();
				String setName = productName();
				if (this.leftType.setIString().equals(
						SetImplement.Array.toString())) {
					this.machineC.addFunctionString(this.leftType.setIString()
							+ " *" + setName + " = "
							+ this.leftType.setIString()
							+ "_new(256, BStar_equal);\n");
				} else {
					this.machineC.addFunctionString(this.leftType.setIString()
							+ " *" + setName + " = "
							+ this.leftType.setIString()
							+ "_new(BStar_equal);\n");
				}
				while (true) {
					EleNode eleNode = (EleNode) iterator.next();
					if (eleNode.isSetOpe()) {
						eleNode.setLeftType(this.leftType.getSubType(0));
					}
					eleNode.toCCode();
					if (!eleNode.isSetOpe() && !eleNode.getType().isPoint()) {
						MachineC.functionStaString.append(this.leftType
								.setIString()
								+ "_add("
								+ setName
								+ ", &("
								+ cStringBuilder.toString() + "));\n");
					} else {
						MachineC.functionStaString.append(this.leftType
								.setIString()
								+ "_add("
								+ setName
								+ ", "
								+ cStringBuilder.toString() + ");\n");
					}
					cleanCStringBuilder();
					if (((StringNode) iterator.next()).getString().equals("}")) {
						break;
					}
				}
				// if (scope == 1) {
				// this.machineC.addFunctionString(stringBuilder.toString());
				// } else {
				// this.machineC.addcFile(stringBuilder.toString());
				// }
				cStringBuilder.append(setName);
			} else if (stringNode.getString().equals("<")) { // <>在{}中必须转化为指针（需要malloc申请空间），如果是直接赋给结构体，则不为指针。
				boolean isInSet = false; // 是否是{}中定义的
				if (getFatherNode() // OneE
						.getFatherNode() // UnaryE
						.getFatherNode() // MSET
						.getFatherNode() // MSE
						.getFatherNode() // ConditionTermE
						.getFatherNode() // AndProE
						.getFatherNode() // Ele
						.getFatherNode() // Term
				instanceof TermNode) {
					isInSet = true;
				}
				StringBuilder stringBuilder = new StringBuilder();
				String tupleName = productName();

				String point;
				Type tmpType;
				if (isInSet) {
					point = "->";
					if (this.leftType.isPoint()) { // 左边的类型有可能是指针（例如集合中的类型就直接是指针类型）
						tmpType = this.leftType.getSubType(0);
						this.machineC.addFunctionString(this.leftType
								.getImage() + " " + tupleName);
						this.machineC
								.addFunctionString(" = ("
										+ this.leftType.getImage()
										+ ")malloc(sizeof("
										+ this.leftType.getSubType(0)
												.getImage() + "))");
					} else {
						tmpType = this.leftType;
						this.machineC.addFunctionString(this.leftType
								.getImage() + " *" + tupleName);
						this.machineC.addFunctionString(" = ("
								+ this.leftType.getImage() + "*)malloc(sizeof("
								+ this.leftType.getImage() + "))");
					}
				} else {
					point = ".";
					tmpType = this.leftType;
					this.machineC.addFunctionString(tmpType.getImage() + " "
							+ tupleName);
				}
				this.machineC.addFunctionString(";\n");
				for (int i = 0; i < tmpType.getSubSize(); i++) {
					String subName = tmpType.getSubName(i);
					iterator.next().toCCode();
					iterator.next();
					// if (tmpType.getSubType(i).isPoint()) {
					// stringBuilder.append(tupleName + point + subName
					// + " = &(" + cStringBuilder.toString() +
					// ");\n");//TODO这个地方也有要不要加取地址符号的问题
					// } else {
					stringBuilder.append(tupleName + point + subName + " = "
							+ cStringBuilder.toString() + ";\n");
					// }
					cleanCStringBuilder();
				}
				if (scope == 1) {
					MachineC.functionStaString.append(stringBuilder.toString());
				} else {
					MachineC.functionStaString.append(stringBuilder.toString());
				}
				cStringBuilder.append(tupleName);
			} else if (stringNode.getString().equals("(")) {
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("(");
				while (iterator.hasNext()) {
					iterator.next().toCCode();
					stringBuilder.append(cStringBuilder);
					cleanCStringBuilder();
				}
				cStringBuilder.append(stringBuilder);
			} else if (stringNode.getString().equals("sizeof")) {
				iterator.next();
				iterator.next().toCCode();
				cStringBuilder.insert(0, "sizeof(");
				cStringBuilder.append(")");
			}
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			bNode.toCCode();
			stringBuilder.append(cStringBuilder);
			cleanCStringBuilder();
			while (iterator.hasNext()) {
				iterator.next().toCCode();
				stringBuilder.append(cStringBuilder);
				cleanCStringBuilder();
			}
			cStringBuilder.append(stringBuilder);
		}
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public boolean isSetOpe() {
		// TODO Auto-generated method stub
		return this.setOpe;
	}

	private String productName() {
		String retain = "bStarTemp";
		String name = retain;
		int scope = pSymTab == null ? 0 : 1;
		int num = 0;
		while (true) {
			if (scope == 1) {
				if (pSymTab.containsId(name)) {
					name = retain + (num++);
				} else {
					pSymTab.addVaraT(name, type);
					return name;
				}
			} else {
				if (gSymTab.containsId(name) || incSymTab.containsId(name)) {
					name = retain + (num++);
				} else {
					gSymTab.addVaraT(name, type);
					return name;
				}
			}
		}
	}

	public Type getLeftType() {
		return leftType;
	}

	public void setLeftType(Type leftType) {
		this.leftType = leftType;
	}

}
