/** 
 * @author  LiuCong  
 * @file    CVDefineNode.java 
 * @date    Date：2015年12月23日 下午9:00:23 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import java.util.HashMap;
import java.util.Iterator;

import cn.edu.buaa.bstar.exception.SemanticsException;
import cn.edu.buaa.bstar.symbolt.Type;

public class CVDefineNode extends BNode {

	// key->pointid, value->id
	private HashMap<String, String> nullStruct = new HashMap<String, String>();// 记录下所有自我调用的名称（typedef
																				// struct
																				// id
																				// pointId）

	public CVDefineNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	@Override
	public void typeCheck() throws SemanticsException {
		// TODO Auto-generated method stub
		super.typeCheck();
		int scope = pSymTab == null ? 0 : 1;// 作用域，全局(0)or局部(1)
		Iterator<BNode> iterator = getChildNodes().iterator();
		while (iterator.hasNext()) {
			BNode bNode = iterator.next();
			bNode.typeCheck();
			iterator.next();
		}
		//将结构体自我调用的昵称附上type
		if (!this.nullStruct.isEmpty()) {
			Type type, tmpType;
			if (scope == 0) {
				for (String key : this.nullStruct.keySet()) {
					String value = this.nullStruct.get(key);
					tmpType = getTypeType(value);
					type = getTypeType(key);
					type.addPointType(tmpType);
					this.gSymTab.addTypeT(key, type);
				}
			} else if (scope == 1) {
				for (String key : this.nullStruct.keySet()) {
					String value = this.nullStruct.get(key);
					tmpType = getTypeType(value);
					type = getTypeType(key);
					type.addPointType(tmpType);
					this.pSymTab.addTypeT(key, type);
				}
			}
		}
	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		super.toCCode();
		Iterator<BNode> iterator = getChildNodes().iterator();
		while (iterator.hasNext()) {
			BNode bNode = iterator.next();
			bNode.toCCode();
			iterator.next();
		}
	}

	public void addNullStruct(String key, String value) {
		this.nullStruct.put(key, value);
	}
	
	//获取typedef类型
	private Type getTypeType(String type) throws SemanticsException {
		if(pSymTab != null&&pSymTab.containsType(type)) {
			return pSymTab.getTypeT(type);
		} else if (gSymTab.containsType(type)) {
			return gSymTab.getTypeT(type);
		} else if (incSymTab.containsType(type)) {
			return incSymTab.getTypeT(type);
		} else {
			throw SemanticsException.undefineException(getRootNode().getFileName(), line,
					"undefined type " + type);
		}
	}
}
