/** 
 * @author  LiuCong  
 * @file    test.java
 * @date    Date：2015年12月20日 下午2:55:03 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import java.util.LinkedList;

import cn.edu.buaa.bstar.c.MachineC;
import cn.edu.buaa.bstar.exception.SemanticsException;
import cn.edu.buaa.bstar.symbolt.SymbolTables;

//<Machine>::="MACHINE" <Id> <Includes> <Attributes> <Operations> "END"

public class MachineNode extends BNode {

	private String machineName;
	private String fileName;

	// private Map<String, SymbolTables>

	public String getMachineName() {
		return machineName;
	}

	public void typeCheck() throws SemanticsException {
		this.gSymTab = new SymbolTables();
		this.incSymTab = new SymbolTables();
		setRootNode(this);
		machineName = ((IdNode) getChildNodeAt(1)).getId();
		for (int i = 2; i < getChildNodes().size() - 1; i++) {
			getChildNodeAt(i).typeCheck();
		}
		//检查所有的函数头是否在下面有具体的实现
		this.gSymTab.checkFuncAllDefined(fileName);  
		SymbolTables.compiledSymT.put(fileName, gSymTab);
	}

	@Override
	public void toCCode() {
		// TODO Auto-generated method stub
		this.machineC = new MachineC(fileName.substring(0,
				fileName.lastIndexOf('.')));
		LinkedList<String> includeFiles = SymbolTables.machineIncludes
				.get(fileName);
		if (includeFiles != null) {
			for (String name : includeFiles) {
				this.machineC.addhFile("#include \""
						+ name.substring(0, name.lastIndexOf('.'))
						+ ".h\"\n");
			}
		}
		for (int i = 2; i < getChildNodes().size() - 1; i++) {
			getChildNodeAt(i).toCCode();
		}
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	public MachineNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
		this.nodeKeyWord = "MACHINE";
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return this.fileName;
	}
}
