/** 
 * @author  LiuCong  
 * @file    TypeNode.java 
 * @date    Date：2015年12月23日 下午9:02:24 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import cn.edu.buaa.bstar.exception.SemanticsException;
import cn.edu.buaa.bstar.symbolt.Type;

public class TypeNode extends BNode {

	private Type type;

	public TypeNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub
		super.typeCheck();
		BNode bNode = getChildNodeAt(0);
		bNode.typeCheck();
		if (bNode instanceof NomalTypeNode) {
			this.type = ((NomalTypeNode) bNode).getType();
		} else if (bNode instanceof SetTypeNode) {
			this.type = ((SetTypeNode) bNode).getType();
		} else {
			String id = ((IdNode) bNode).getId();
			if (pSymTab != null && pSymTab.containsType(id)) {
				this.type = pSymTab.getTypeT(id);
			} else if (gSymTab.containsType(id)) {
				this.type = gSymTab.getTypeT(id);
			} else if (incSymTab.containsType(id)) {
				this.type = incSymTab.getTypeT(id);
			} else {
				throw SemanticsException.undefineException(getRootNode().getFileName(), line,
						"undefined type " + id);
			}
		}
	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		getChildNodeAt(0).toCCode();
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public String toString() {
		BNode bNode = getChildNodeAt(0);
		if(bNode instanceof NomalTypeNode) {
			return this.type.type.getString();
		} else if (bNode instanceof SetTypeNode) {
			return this.type.setIString();
		} else {
			return ((IdNode)bNode).getId();
		}
	}
}
