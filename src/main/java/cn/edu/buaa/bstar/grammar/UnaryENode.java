/** 
 * @author  LiuCong  
 * @file    UnaryENode.java 
 * @date    Date：2015年12月23日 下午9:29:23 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import java.util.Iterator;

import cn.edu.buaa.bstar.exception.SemanticsException;
import cn.edu.buaa.bstar.symbolt.Type;

public class UnaryENode extends BNode {

	private Type type;
	private boolean setOpe = false;
	private Type leftType;
	
	public UnaryENode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub
		super.typeCheck();
		Iterator<BNode> iterator = getChildNodes().iterator();
		BNode bNode = iterator.next();
		if(bNode instanceof StringNode) {
			TermNode termNode = (TermNode)iterator.next();
			termNode.typeCheck();
			if(!termNode.getType().isNumber()) {
				throw SemanticsException.mismatchException(getRootNode().getFileName(), line, ((StringNode)bNode).getString()+" both side type should be number!");
			}
			if(termNode.getType().isEnum()) {
				this.type = Type.newIntType();
			} else {
				this.type = termNode.getType();
			}
		} else {
			bNode.typeCheck();
			this.type = ((OneENode)bNode).getType();
			this.setOpe = ((OneENode)bNode).isSetOpe();
		}
	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		BNode bNode = getChildNodeAt(0);
		if(bNode instanceof StringNode) {
			String string = ((StringNode)bNode).getString();
			getChildNodeAt(1).toCCode();
			cStringBuilder.insert(0, string);
		} else {
			if(((OneENode)bNode).isSetOpe()) {
				((OneENode)bNode).setLeftType(this.leftType);
			}
			bNode.toCCode();
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

	public Type getLeftType() {
		return leftType;
	}

	public void setLeftType(Type leftType) {
		this.leftType = leftType;
	}
	
}
