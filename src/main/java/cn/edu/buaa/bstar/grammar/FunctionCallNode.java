/** 
 * @author  LiuCong  
 * @file    FunctionCallNode.java 
 * @date    Date：2015年12月23日 下午9:17:45 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import java.util.Iterator;

import cn.edu.buaa.bstar.exception.SemanticsException;
import cn.edu.buaa.bstar.symbolt.FunctionType;
import cn.edu.buaa.bstar.symbolt.Type;

public class FunctionCallNode extends BNode {

	private Type type;
	
	public FunctionCallNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub
		super.typeCheck();
		Iterator<BNode> iterator = getChildNodes().iterator();
		IdNode idNode = (IdNode)iterator.next();
		FunctionType functionType;
		if(gSymTab.containsFun(idNode.getId())) {
			functionType = gSymTab.getFuncT(idNode.getId());
		} else {
			functionType = incSymTab.getFuncT(idNode.getId());
		}
		if(functionType == null) {
			throw SemanticsException.undefineException(getRootNode().getFileName(), line, "undefine the function "+idNode.getId());
		}
		iterator.next();
		BNode bNode = iterator.next();
		if(bNode instanceof StringNode) {
			//函数的atlit先不管
			if(functionType.getSubSize()!=0) {
				throw SemanticsException.mismatchException(getRootNode().getFileName(), line, "function "+idNode.getId()+" para mismatch!");
			}
			
		} else {
			ParaValueListNode paraValueListNode = (ParaValueListNode)bNode;
			paraValueListNode.setFunctionType(functionType);
			paraValueListNode.typeCheck();
		}
		this.type = functionType.getrType();
	}
	
	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		if(getChildNodes().size() == 4) {
			getChildNodeAt(2).toCCode();
			String paraValueList = cStringBuilder.toString();
			cleanCStringBuilder();
			getChildNodeAt(0).toCCode();
			cStringBuilder.append("("+paraValueList+")");
		} else {
			getChildNodeAt(0).toCCode();
			cStringBuilder.append("()");
		}
	}

	public Type getType() {
		return this.type;
	}
}
