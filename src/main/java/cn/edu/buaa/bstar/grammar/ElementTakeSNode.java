/** 
 * @author  LiuCong  
 * @file    ElementTakeSNode.java 
 * @date    Date：2015年12月23日 下午9:17:23 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cn.edu.buaa.bstar.c.MachineC;
import cn.edu.buaa.bstar.exception.SemanticsException;
import cn.edu.buaa.bstar.symbolt.Type;

public class ElementTakeSNode extends BNode {

	private Type setType;

	public ElementTakeSNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	// TODO 取运算的右边不能是集合的并、差等操作
	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub
		super.typeCheck();
		int scope = pSymTab == null ? 0 : 1;
		Iterator<BNode> iterator = getChildNodes().iterator();
		IdNode idNode = (IdNode) iterator.next();
		String var = idNode.getId();
		if (scope == 1) {
			if (this.pSymTab.containsId(var) || this.gSymTab.containsType(var)) {
				throw SemanticsException.redefineException(getRootNode()
						.getFileName(), line, var + "redefine!");
			}
		} else {
			if (this.gSymTab.containsId(var) || this.incSymTab.containsId(var)) {
				throw SemanticsException.redefineException(getRootNode()
						.getFileName(), line, var + "redefine!");
			}
		}
		MSENode mseNode = (MSENode) getLastChildNode();
		mseNode.typeCheck();
		this.setType = mseNode.getType();
		Type type;
		if (mseNode.isSetOpe()) {
			throw SemanticsException.mismatchException(getRootNode()
					.getFileName(), line, "ElementTakes cannot have <> or {}");
		}
		if (!setType.isSet()) {
			throw SemanticsException.mismatchException(getRootNode()
					.getFileName(), line,
					"can not take element from non-collection!");
		}
		if (setType.setIString() == null) {
			throw SemanticsException.parameterErorException(getRootNode()
					.getFileName(), line, "miss the set achieve type!");
		}
		if (!setType.getSubType(0).isPoint()) {
			type = Type.newPointType();
			type.addPointType(setType.getSubType(0));
		} else {
			type = setType.getSubType(0);
		}
		if (getChildNodes().size() != 3) {
			iterator.next();
			iterator.next();
			while (true) {
				idNode = (IdNode) iterator.next();
				String id = idNode.getId();
				if (!type.isStruct()) {
					throw SemanticsException.mismatchException(getRootNode()
							.getFileName(), line,
							"can not take subType from unStruct!");
				} else {
					if (!type.getSubType(0).containsSubName(id)) {
						throw SemanticsException.undefinedChildException(
								getRootNode().getFileName(), line,
								"struct don't have such child name " + id);
					}
					iterator.next();
					EleNode eleNode = (EleNode) iterator.next();
					eleNode.typeCheck();
					if (eleNode.isSetOpe()) {
						throw SemanticsException.mismatchException(
								getRootNode().getFileName(), line,
								"ElementTakes cannot have <> or {}");
					}

					if (!type.getSubType(0).getSubType(id).equals(
							eleNode.getType())) {
						throw SemanticsException.mismatchException(
								getRootNode().getFileName(), line, id
										+ "'s value type can not fit!");
					}
					StringNode stringNode = (StringNode) iterator.next();
					if (stringNode.getString().equals(">")) {
						break;
					}
				}
			}
		}

		if (scope == 1) {
			this.pSymTab.addVaraT(var, type);
		} else {
			this.gSymTab.addVaraT(var, type);
		}
	}

	@Override
	/*
	 * 如果是出现在while中需要特殊处理，主要是a:b
	 * 
	 * @see cn.edu.buaa.bstar.grammar.BNode#toCCode()
	 */
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		int scope = pSymTab == null ? 0 : 1;
		List<String> key = new LinkedList<String>();
		List<String> value = new LinkedList<String>();
		String image = this.setType.getImage(); // 集合存储的类型名
		Iterator<BNode> iterator = getChildNodes().iterator();
		iterator.next().toCCode();
		String id = cStringBuilder.toString();
		cleanCStringBuilder();
		MSENode mseNode = (MSENode) getChildNodeAt(getChildNodes().size() - 1);
		mseNode.toCCode();
		String setName = cStringBuilder.toString(); // 获取集合的名称
		cleanCStringBuilder();
		if (getChildNodes().size() == 3) {
			// if (scope == 0) {
			// this.machineC.addcFile(image + " *" + id + ";\n");
			// } else {
			// this.machineC.addFunctionString(image + " *" + id + ";\n");
			// }
			// 判断是不是从while中出来的,只能是在函数中
			String flag = " ";
			if (!this.setType.getSubType(0).isPoint()) {
				flag = " *";
			}
			if (this.getFatherNode() instanceof ConditionTermENode
					&& this.getFatherNode().getFatherNode().getFatherNode()
							.getFatherNode() instanceof WhileSNode) {
				String tmpName = productName(); // 下标，临时变量
				this.machineC.addFunctionString("int " + tmpName + " = 0;\n");
				this.machineC.addFunctionString(image + flag + id + ";\n");//TODO 这句需要加到函数的最开头
				cStringBuilder.append(id + " = (" + image + flag + ")" + this.setType.setIString()
						+ "_get_poi(" + setName + ", " + tmpName + "++)");
			} else {
				this.machineC.addFunctionString(image + flag + id + ";\n");
				cStringBuilder.append(id + " = (" + image + flag + ")"
						+ this.setType.setIString() + "_get_poi(" + setName
						+ ", 0)");
			}
		} else { // 从集合中取特定元素
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("_" + this.setType.getImage());
			iterator.next();
			iterator.next();
			while (true) {
				iterator.next().toCCode();
				key.add(cStringBuilder.toString());
				cleanCStringBuilder();
				iterator.next();
				iterator.next().toCCode();
				value.add(cStringBuilder.toString());
				cleanCStringBuilder();
				StringNode stringNode = (StringNode) iterator.next();
				if (stringNode.getString().equals(">")) {
					break;
				}
			}
			for (String string : key) {
				stringBuilder.append("_" + string);
			}
			stringBuilder.append("_equal");
			if (this.machineC.addStaticFunctionName(stringBuilder.toString())) {
				this.machineC.addcFile("static proposition "
						+ stringBuilder.toString());
				this.machineC.addcFile("(void *o1, void *o2) {\n");
				if(this.setType.getSubType(0).isPoint()) {
					this.machineC.addcFile(image + " k1 = (" + image + ")o1;\n");
					this.machineC.addcFile(image + " k2 = (" + image + ")o2;\n");
				} else {
					this.machineC.addcFile(image + " *k1 = (" + image + "*)o1;\n");
					this.machineC.addcFile(image + " *k2 = (" + image + "*)o2;\n");
				}
				this.machineC.addcFile("return k1==k2||");
				for (int i = 0; i < key.size(); i++) {
					String string = key.get(i);
					this.machineC.addcFile("k1->" + string + " == k2->"
							+ string);
					if (i != key.size() - 1) {
						this.machineC.addcFile("&&");
					}
				}
				this.machineC.addcFile(";\n}\n");
			}
			// 中间变量的名称需要检验
			String tmpName = "_" + id;
			while (true) {
				if (scope == 1) {
					if (this.pSymTab.containsId(tmpName)) {
						tmpName = "_" + tmpName;
					} else {
						this.pSymTab.addVaraT(tmpName, setType.getSubType(0));
						break;
					}
				} else {
					if (this.gSymTab.containsId(tmpName)
							|| this.incSymTab.containsFun(tmpName)) {
						tmpName = "_" + tmpName;
					} else {
						this.gSymTab.addVaraT(tmpName, setType.getSubType(0));
						break;
					}
				}
			}
			if (this.setType.getSubType(0).isPoint()) {
				this.machineC.addFunctionString(image + " " + tmpName + " = (" + image
						+ ")malloc(sizeof(" + this.setType.getSubType(0).getSubType(0).getImage() + "))" + ";\n");  //sizeof不能使指针，否则而只分配八位的空间
				this.machineC.addFunctionString(image + " " + id + ";\n");
			} else {
				this.machineC.addFunctionString(image + " " + tmpName + ";\n");
				this.machineC.addFunctionString(image + " *" + id + ";\n");
			}

			for (int i = 0; i < key.size(); i++) {
				String _key = key.get(i);
				String _value = value.get(i);
				if (this.setType.getSubType(0).isPoint()) {
					cStringBuilder.append(tmpName + "->" + _key + " = "
							+ _value + ";\n");
				} else {
					cStringBuilder.append(tmpName + "." + _key + " = " + _value
							+ ";\n");
				}
			}
			if (scope == 0) {
				this.machineC.addcFile(cStringBuilder.toString());
			} else {
				MachineC.functionStaString.append(cStringBuilder.toString());
			}
			cleanCStringBuilder();
			cStringBuilder.append(id + " = (" + image + (this.setType.getSubType(0).isPoint()?"":"*") + ")" + setType.setIString() + "_get2("
					+ setName + ", "
					+ (this.setType.getSubType(0).isPoint() ? "" : "&")
					+ tmpName + ", " + stringBuilder.toString() + ")");
		}
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
					pSymTab.addVaraT(name, Type.newIntType());
					return name;
				}
			} else {
				if (gSymTab.containsId(name) || incSymTab.containsId(name)) {
					name = retain + (num++);
				} else {
					gSymTab.addVaraT(name, Type.newIntType());
					return name;
				}
			}
		}
	}

}
