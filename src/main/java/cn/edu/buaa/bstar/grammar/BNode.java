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
import cn.edu.buaa.bstar.symbolt.Type;


/*
 * 所有节点的基类
 */

public class BNode {
	
	public static StringBuilder cStringBuilder = new StringBuilder(); //记录下当前转为c的代码
	public static StringBuilder cSetBuilder = new StringBuilder();    //记录下转化为集合需要的其他代码
	protected String nodeKeyWord;
	protected LinkedList<BNode>  childNodes   = new LinkedList<BNode>();
	protected BNode      fatherNode;
	protected SymbolTables pSymTab;             //局部符号表
	protected SymbolTables gSymTab;             //全局符号表
	protected SymbolTables incSymTab;           //include进来的符号表
	protected int line;
	protected Type retType;
	protected MachineNode rootNode;
	protected MachineC machineC;
	
	public BNode(int line){
		this.line = line;
	}
	
	/**
	 * 返回对应状态的新的节点
	 * @param statue
	 * @return
	 */
	public static BNode getNode(String statue, int line) {
		switch(statue) {
			case "Mac"   : return new MachineNode(line);
			case "Inc"   : return new IncludesNode(line);
			case "Att"   : return new AttributesNode(line);
			case "Def"   : return new DefinesNode(line);
			case "Cvd"   : return new CVDefineNode(line);
			case "VarD"  : return new VarDefineNode(line);
			case "CstD"  : return new CstDefineNode(line);
			case "Typ"   : return new TypeNode(line);
			case "PoiI"  : return new PointIdNode(line);
			case "EnuT"  : return new EnumTypeNode(line);
			case "TupT"  : return new TupleTypeNode(line);
			case "Ope"   : return new OperationsNode(line);
			case "FunD"  : return new FunctionDefineNode(line);
			case "ParDL" : return new ParaDefineListNode(line);
			case "ComS"  : return new ComStatementNode(line);
			case "Sta"   : return new StatementNode(line);
			case "EleTS" : return new ElementTakeSNode(line);
			case "FunC"  : return new FunctionCallNode(line);
			case "AssS"  : return new AssignmentSNode(line);
			case "RetS"  : return new ReturnSNode(line);
			case "WhiS"  : return new WhileSNode(line);
			case "Ifs"   : return new IfSNode(line);
			case "ParVL" : return new ParaValueListNode(line);
			case "AppP"  : return new AppendParasNode(line);
			case "NorT"  : return new NomalTypeNode(line);
			case "SetT"  : return new SetTypeNode(line);
			case "Ele"   : return new EleNode(line);
			case "AndPE" : return new AndProENode(line);
			case "ConTE" : return new ConditionTermENode(line);
			case "Mse"   : return new MSENode(line);
			case "UnaE"  : return new UnaryENode(line);
			case "OneE"  : return new OneENode(line);
			case "Ter"   : return new TermNode(line);
			case "Atl"   : return new AtLitNode(line);
			case "MseT"  : return new MSETNode(line);
			case "FunDH"  : return new FunctionDefineHeadNode(line);
			default      : return null;
			
		}
	}
	
	/**
	 * 设定当前节点的父节点
	 * @param fatherNode
	 */
	public void setFatherNode(BNode fatherNode) {
		this.fatherNode = fatherNode;
	}
	
	/**
	 * 获取父节点
	 * @return
	 */
	public BNode getFatherNode() {
		return this.fatherNode;
	}
	
	/**
	 * 加入子节点
	 * @param childNode
	 */
	public void addChildNode(BNode childNode) {
		this.childNodes.add(childNode);
	}
	
	/**
	 * 获取所有的子节点
	 * @return
	 */
	public LinkedList<BNode> getChildNodes() {
		return this.childNodes;
	}
	
	public void removeLastChildNode() {
		if(!this.childNodes.isEmpty()) {
			this.childNodes.removeLast();
		}
	}
	
	public void removeFirstChildNode() {
		if(!this.childNodes.isEmpty()) {
			this.childNodes.removeFirst();
		}
	}
	
	public BNode getLastChildNode() {
		return this.childNodes.getLast();
	}
	
	public BNode getChildNodeAt(int index) {
		if(this.childNodes.size() > index){
			return this.childNodes.get(index);
		} else {
			return null;
		}
	}
	
	public boolean removeChildNodeAt(int index) {
		if(this.childNodes.size() > index){
			this.childNodes.remove(index);
			return true;
		} else {
			return false;
		}
	}

	public void typeCheck() throws SemanticsException {
		//updateSymTab;
		this.gSymTab   = getFatherNode().gSymTab;
		this.pSymTab   = getFatherNode().pSymTab;
		this.incSymTab = getFatherNode().incSymTab;
		this.retType   = getFatherNode().retType;
		this.rootNode  = getFatherNode().getRootNode();
	}
	
	public void toCCode() {
		this.machineC  = getFatherNode().machineC;
	}

	/**
	 * 查找变量id的类型
	 * @param id
	 * @return
	 * @throws SemanticsException 
	 */
	public Type getIdType(String id) throws SemanticsException {
		if(pSymTab != null) {
			if(pSymTab.containsVara(id)) {
				return pSymTab.getVaraT(id);
			} 
		}
		if (gSymTab.containsVara(id)){
			return gSymTab.getVaraT(id);
		}  else if (incSymTab.containsVara(id)) {
			return incSymTab.getVaraT(id);
		} else {
			throw SemanticsException.undefineException(getRootNode().getFileName(), line, id + " undefined!");
		}
	}
	
	/**
	 * 查找id是否已经定义
	 * @param id
	 * @return
	 */
	public boolean containsId(String id) {
		if(pSymTab != null) {
			if(pSymTab.containsId(id)) {
				return true;
			}
		}
		if (gSymTab.containsId(id)){
			return true;
		}  else if (incSymTab.containsId(id)) {
			return true;
		} else {
			return false;
		}
	}
	
	public MachineNode getRootNode() {
		return rootNode;
	}
	
	public void setRootNode(MachineNode machineNode) {
		this.rootNode = machineNode;
	}
	
	public int getLine(){
		return this.line;
	}
	
	public static void cleanCStringBuilder() {
		cStringBuilder.delete(0, cStringBuilder.length());
	}
	
	public static void cleanCSetBuilder() {
		cSetBuilder.delete(0, cSetBuilder.length());
	}
}
