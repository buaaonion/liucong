/** 
 * @author  LiuCong  
 * @file    DefinesNode.java 
 * @date    Date：2015年12月23日 下午8:59:28 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import java.util.Iterator;

import cn.edu.buaa.bstar.exception.SemanticsException;

public class DefinesNode extends BNode {

	public DefinesNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	@Override
	public void typeCheck() throws SemanticsException {
		super.typeCheck();
		//TODO 不能有集合的操作
		Iterator<BNode> iterator = getChildNodes().iterator();
		while (iterator.hasNext()) {
			iterator.next();
			IdNode idNode = (IdNode) iterator.next();
			String id = idNode.getId();
			if (gSymTab.containsId(id) || incSymTab.containsId(id)) {
				throw SemanticsException.redefineException(getRootNode().getFileName(), line, id
						+ " redefine");
			} else {
				EleNode eleNode = (EleNode) iterator.next();
				eleNode.typeCheck();
				if(eleNode.isSetOpe()) {
					throw SemanticsException.mismatchException(getRootNode().getFileName(), line, "defines value can not be set operation!");
				}
				gSymTab.addDefT(id, eleNode.getType());
			}
		}
	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		Iterator<BNode> iterator = getChildNodes().iterator();
		while(iterator.hasNext()) {
			iterator.next();
			iterator.next().toCCode();
			String id = cStringBuilder.toString();
			cleanCStringBuilder();
			this.machineC.addhFile("#define "+id+" ");
			EleNode eleNode = (EleNode) iterator.next();
			eleNode.toCCode();
			this.machineC.addhFile(cStringBuilder.toString());
			this.machineC.addhFile("\n");
			cleanCStringBuilder();
		}
	}

}
