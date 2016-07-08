/** 
 * @author  LiuCong  
 * @file    TupleTypeNode.java 
 * @date    Date：2015年12月23日 下午9:04:28 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import cn.edu.buaa.bstar.exception.SemanticsException;
import cn.edu.buaa.bstar.symbolt.Type;

public class TupleTypeNode extends BNode {

	private Type type = Type.newStructType();
	
	public TupleTypeNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub
		super.typeCheck();
		Set<String> tag = new HashSet<String>();
		Iterator<BNode> iterator = getChildNodes().iterator();
		iterator.next();
		iterator.next();
		for(int i = 0; i < (getChildNodes().size()-3)/3; i++) {
			TypeNode typeNode = (TypeNode)iterator.next();
			typeNode.typeCheck();
			Type subType;
			PointIdNode pointIdNode = (PointIdNode)iterator.next();
			pointIdNode.typeCheck();
			String id = pointIdNode.getId();
			if(tag.add(id)) {
				if(pointIdNode.isPoint()) {
					subType = pointIdNode.getType();
					subType.addPointType(typeNode.getType());
				} else {
					subType = typeNode.getType();
				}
				type.addSubType(subType, id);
			} else {
				throw SemanticsException.redefineException(getRootNode().getFileName(), i, "Struct redefined subId "+id+"!");
			}
			iterator.next();
		}
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		Iterator<BNode> iterator = getChildNodes().iterator();
		iterator.next();
		iterator.next();
		cStringBuilder.append("{\n");
		for(int i = 0; i < (getChildNodes().size()-3)/3; i++) {
			iterator.next().toCCode();
			cStringBuilder.append(" ");
			iterator.next().toCCode();
			iterator.next();
			cStringBuilder.append(";\n");
		}
		cStringBuilder.append("}");
	}
}