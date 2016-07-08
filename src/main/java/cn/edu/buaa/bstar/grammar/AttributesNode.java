/** 
 * @author  LiuCong  
 * @file    test.java
 * @date    Date：2015年12月20日 下午2:55:03 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import java.util.Iterator;

import cn.edu.buaa.bstar.exception.SemanticsException;

public class AttributesNode extends BNode {
	
	public AttributesNode(int line) {
		super(line);
	}
	
	@Override
	public void typeCheck() throws SemanticsException {
		super.typeCheck();
		Iterator<BNode> iterator = getChildNodes().iterator();
		iterator.next();
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
		iterator.next();
		while(iterator.hasNext()) {
			BNode bNode = iterator.next();
			bNode.toCCode();
		}
	}
	
}
