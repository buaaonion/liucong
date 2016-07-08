/** 
 * @author  LiuCong  
 * @file    NomalTypeNode.java 
 * @date    Date：2015年12月23日 下午9:23:38 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import cn.edu.buaa.bstar.exception.SemanticsException;
import cn.edu.buaa.bstar.symbolt.Type;

public class NomalTypeNode extends BNode {

	private Type type;

	public NomalTypeNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub
		super.typeCheck();
		if (getChildNodes().size() == 1) {
			String normalType = ((StringNode) getChildNodeAt(0)).getString();
			switch (normalType) {
			case "int":
				this.type = Type.newIntType();
				break;
			case "short":
				this.type = Type.newShortType();
				break;
			case "float":
				this.type = Type.newFloatType();
				break;
			case "char":
				this.type = Type.newCharType();
				break;
			case "long":
				this.type = Type.newLongType();
				break;
			case "double":
				this.type = Type.newDoubleType();
				break;
			case "string":
				this.type = Type.newStringType();
				break;
			case "void":
				this.type = Type.newVoidType();
				break;
			case "proposition":
				this.type = Type.newPropositionType();
				break;
			}
		} else if (getChildNodes().size() == 2) {
			String type1 = ((StringNode) getChildNodeAt(0)).getString();
			String type2 = ((StringNode) getChildNodeAt(1)).getString();
			if(type1.equals("long")) {
				this.type = Type.newLLongType();
			} else {
				switch (type2) {
				case "int":
					this.type = Type.newUIntType();
					break;
				case "short":
					this.type = Type.newShortType();
					break;
				case "float":
					this.type = Type.newUFloatType();
					break;
				case "char":
					this.type = Type.newUCharType();
					break;
				case "long":
					this.type = Type.newULongType();
					break;
				case "double":
					this.type = Type.newUDoubleType();
					break;
				}
			}
		} else {
			this.type = Type.newULLongType();
		}
	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		cStringBuilder.append(this.type.type.getString());
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
