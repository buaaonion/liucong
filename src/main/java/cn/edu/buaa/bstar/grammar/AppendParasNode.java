/** 
 * @author  LiuCong  
 * @file    AppendParasNode.java 
 * @date    Date：2015年12月23日 下午9:23:12 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import java.util.Iterator;
import java.util.Stack;

import cn.edu.buaa.bstar.symbolt.SetImplement;
import cn.edu.buaa.bstar.exception.SemanticsException;

public class AppendParasNode extends BNode {

	private Stack<SetImplement> appendParas = new Stack<SetImplement>();
	
	public AppendParasNode(int line) {
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
		while(true) {
			//idNode.typeCheck();
			SetImplement setImplement = SetImplement.fromString(idNode.getId());
			if(setImplement == null) {
				throw SemanticsException.parameterErorException(getRootNode().getFileName(), line, "setImplement Error,"+idNode.getId()+"is not fit");
			}
			appendParas.add(setImplement);
			StringNode node = (StringNode)(iterator.next());
			if(node.getString().equals(",")) {
				idNode = (IdNode)iterator.next();
			} else {
				break;
			}
		}
	}
	
	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
	}

	public Stack<SetImplement> getAppendParas() {
		return appendParas;
	}

	public void setAppendParas(Stack<SetImplement> appendParas) {
		this.appendParas = appendParas;
	}

}
