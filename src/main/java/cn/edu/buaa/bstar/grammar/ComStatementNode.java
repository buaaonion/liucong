/** 
 * @author  LiuCong  
 * @file    ComStatementNode.java 
 * @date    Date：2015年12月23日 下午9:16:19 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import java.util.Iterator;

import cn.edu.buaa.bstar.exception.SemanticsException;

public class ComStatementNode extends BNode {

	public ComStatementNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub
		super.typeCheck();
		Iterator<BNode> iterator = getChildNodes().iterator();
		while(iterator.hasNext()) {
			BNode bNode = iterator.next();
			bNode.typeCheck();
		}
	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		Iterator<BNode> iterator = getChildNodes().iterator();
		while(iterator.hasNext()) {
			iterator.next().toCCode();
		}
	}

}
