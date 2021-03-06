/** 
 * @author  LiuCong  
 * @file    StringNode.java 
 * @date    Date：2015年12月28日 下午3:27:09 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import cn.edu.buaa.bstar.exception.SemanticsException;

public class StringNode extends BNode {
	private String str;
	
	public StringNode(String str, int line) {
		super(line);
		this.str = str;
	}

	public String getString() {
		return str;
	}

	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub
		super.typeCheck();
	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		cStringBuilder.append(str);
	}
	
}
