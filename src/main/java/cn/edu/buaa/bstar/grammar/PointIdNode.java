/** 
 * @author  LiuCong  
 * @file    PointIdNode.java 
 * @date    Date：2015年12月23日 下午9:03:30 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import cn.edu.buaa.bstar.exception.SemanticsException;
import cn.edu.buaa.bstar.symbolt.Type;

public class PointIdNode extends BNode {

	private String id;
	private Type type;
	private boolean isPoint;

	public PointIdNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	public boolean isPoint() {
		return this.isPoint;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub
		super.typeCheck();
		BNode bNode = getChildNodeAt(0);
		if (bNode instanceof StringNode) {
			if (((StringNode) bNode).getString().equals("*")) {
				this.isPoint = true;
				type = Type.newPointType();
				PointIdNode pointIdNode = (PointIdNode) getChildNodeAt(1);
				pointIdNode.typeCheck();
				if(pointIdNode.isPoint()) {
					type.addPointType(pointIdNode.getType());
				}
				this.id = pointIdNode.getId();
			} else {
				PointIdNode pointIdNode = (PointIdNode) getChildNodeAt(1);
				pointIdNode.typeCheck();
				this.type = pointIdNode.getType();
				this.isPoint = pointIdNode.isPoint();
				this.id = pointIdNode.getId();
			}
		} else {
			this.id = ((IdNode) bNode).getId();
			this.isPoint = false;
		}

	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		if (getChildNodes().size() == 1) {
			getChildNodeAt(0).toCCode();
		} else if (getChildNodes().size() == 2) {
			cStringBuilder.append("*");
			getChildNodeAt(1).toCCode();
		} else {
			cStringBuilder.append("(");
			getChildNodeAt(1).toCCode();
			cStringBuilder.append(")");
		}
	}
}
