package cn.edu.buaa.bstar.grammar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import cn.edu.buaa.bstar.exception.SemanticsException;
import cn.edu.buaa.bstar.symbolt.SymbolTables;

//<Includes>::=("INCLUDE" (<StringLit> | \<<Id>".bs"\>) ";")* 

public class IncludesNode extends BNode {

	private Map<String, Integer> tag = new HashMap<String, Integer>();
	private String fileName;

	public IncludesNode(int line) {
		// TODO Auto-generated constructor stub
		super(line);
	}

	public void typeCheck() throws SemanticsException {
		super.typeCheck();
		LinkedList<String> includeFile = new LinkedList<String>();
		Iterator<BNode> iterator = getChildNodes().iterator();
		while (iterator.hasNext()) {
			BNode bNode = iterator.next();
			bNode = iterator.next();
			if (bNode instanceof StringLitNode) {
				String fileName = ((StringLitNode) bNode).getStringValue();
				if (fileName.trim().endsWith(".bs")) {
					if (!SymbolTables.compiledSymT.containsKey(fileName)) {
						BStar bStar = new BStar(fileName.trim());
						bStar.compile();
					}
					includeFile.add(fileName);
				} else {
					SemanticsException.mismatchException(getRootNode()
							.getFileName(), line, fileName
							+ " is not a BStar file!");
				}
			} else {
				// bNode为IdNode
				bNode = iterator.next();
				String fileName = ((IdNode) bNode).getId() + ".bs";
				if (!SymbolTables.compiledSymT.containsKey(fileName)) {
					BStar bStar = new BStar(fileName);
					bStar.compile();
				}
				includeFile.add(fileName);
				iterator.next();
				iterator.next();
			}
			iterator.next();
		}
		this.fileName = getRootNode().getFileName();
		SymbolTables.machineIncludes.put(this.fileName, includeFile);
		addInclude(this.fileName);
	}

	private void addInclude(String fileName) throws SemanticsException {
		if (SymbolTables.machineIncludes.get(fileName) == null) {
			return;
		}
		for (String file : SymbolTables.machineIncludes.get(fileName)) {
			if (file.equals(this.fileName)) {
				throw SemanticsException.redefineException(getRootNode()
						.getFileName(), line, "recircle include");   //其实检测不出来，因为编译的过程就会出现死循环
			}
			if (!tag.containsKey(file)) {
				addIncSymTab(SymbolTables.compiledSymT.get(file));
				tag.put(file, 1);
			}
			addInclude(file);
		}
	}

	public void addIncSymTab(SymbolTables other) throws SemanticsException {
		this.incSymTab.addInclude(other, getRootNode().getFileName());
	}

}
