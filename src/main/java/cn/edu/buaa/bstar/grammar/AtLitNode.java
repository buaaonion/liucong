/** 
 * @author  LiuCong  
 * @file    AtLitNode.java 
 * @date    Date：2015年12月23日 下午9:31:10 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import java.util.Iterator;

import cn.edu.buaa.bstar.exception.SemanticsException;
import cn.edu.buaa.bstar.symbolt.SetImplement;

public class AtLitNode extends BNode {

	private SetImplement setImplement;
	
	public AtLitNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub
		super.typeCheck();
		Iterator<BNode> iterator = getChildNodes().iterator();
		iterator.next();
		IdNode idNode = (IdNode)iterator.next();
		//idNode.typeCheck();
		setImplement = SetImplement.fromString(idNode.getId());
		if(setImplement == null) {
			throw SemanticsException.parameterErorException(getRootNode().getFileName(), line, "setImplement Error,"+idNode.getId()+"is not fit");
		}
	}

	public SetImplement getSetImplement() {
		return setImplement;
	}

	public void setSetImplement(SetImplement setImplement) {
		this.setImplement = setImplement;
	}

}
