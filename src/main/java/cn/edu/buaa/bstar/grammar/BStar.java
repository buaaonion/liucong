/** 
 * @author  LiuCong  
 * @file    test.java
 * @date    Date：2015年12月20日 下午2:55:03 
 * @version 1.0 
 */

package cn.edu.buaa.bstar.grammar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.buaa.bstar.automata.Automata;
import cn.edu.buaa.bstar.exception.BstarException;
import cn.edu.buaa.bstar.exception.GrammarException;
import cn.edu.buaa.bstar.exception.NoteException;
import cn.edu.buaa.bstar.exception.SemanticsException;

public class BStar {

//	public static String baseRoot = "/Users/liuc/Documents/2015/study/lab/test/";//测试专用
	public static String baseRoot = "/Users/liuc/Documents/2015/study/lab/631/acoreos_bstar_20160608/";

	private FileReader bstarFile;
	private int row = 0; // 行号
	private int noteFlag = 0; // 是否是多段注释
	private String fileName; // 文件名称
	private Stack<Automata> analysisStack = new Stack<Automata>(); // analysis
																	// automata
	private Stack<Automata> rollBackStack = new Stack<Automata>(); // rollback
																	// automatas
	private MachineNode machineNode = new MachineNode(0);
	private BNode nowNode = machineNode;
	private ArrayList<String> allWords = new ArrayList<String>(); // 存储所有拆分好的字符串
	private ArrayList<Integer> wordsPosition = new ArrayList<Integer>();// 拆分好的字符串所处的行
	private int wordNum = 0;
	private int rollBackRow = 0; // 标记不能匹配的位置，方便标记错误出现的地方
	private Stack<Integer> nowStatueRollNums = new Stack<Integer>(); // 当前确定状态回滚栈中自动机的数目，方便不匹配时的回滚
	private Stack<Integer> nowNewStatueNums = new Stack<Integer>(); // 当前新自动机在分析栈中自动机的数目
	private int nextIsPlain = 1; // 拆分后接下来的字符是否是空白字符，主要是问了判断'.'两边是否是空白

	public BStar() {
		bstarFile = null;
	}

	/**
	 * 识别算法
	 * 
	 * @param fileName
	 * @throws SemanticsException
	 * @throws BstarException
	 */
	public BStar(String fileName) {
		// 生成自动机
		this.fileName = fileName;
		machineNode.setFileName(this.fileName);
	}

	/**
	 * 编译入口
	 */
	public void compile() {
		try {
			if (Automata.automatas.isEmpty()) {
				Automata.generateAutomata();
			}
			analysisStack.push(Automata.automatas.get("Mac").get(0));
			nowNewStatueNums.push(1);
			bstarFile = new FileReader(baseRoot + "/" + fileName);
			indentification();
			deleteNullNode(this.machineNode); // 删除节点中的空节点
			machineNode.typeCheck();
			// printNode(this.machineNode, 0);
			// System.out.println(fileName + " success!");
			machineNode.toCCode();
			machineNode.machineC.toFile();
//			System.out.println(fileName + " success!");
		} catch (BstarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public BNode getMachineNode() {
		return machineNode;
	}

	/**
	 * 词法分析以及语法分析(下推自动机解析)
	 */
	private void indentification() throws BstarException {
		BufferedReader bufferedReader = new BufferedReader(bstarFile);
		// 相当于词法分析
		try {
			do {
				++this.row;
				String line = bufferedReader.readLine();
				if (line == null) {
					break;
				}
				line = line.replace('\t', ' ');// tab缩进
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				if (this.judgeNote(line)) {
					continue;
				} else {
					line = removeNote(line);
				}
				if (line.length() == 0) {
					continue;
				} else {
					splitLine(line);
				}
			} while (true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (wordNum < allWords.size()) {
			Automata automata = analysisStack.peek();
			if (automata.getEnterWord().equals("ε")) {
				if (automata.getReturnStatus() != null) {
					jumpNewStatue();
				} else if (automata.getJumpStatus().equals("RET")) {
					retStack();
				} else {
					int num = nowNewStatueNums.pop();
					nowNewStatueNums.push(num + 1);
					addStack(automata.getJumpStatus());
				}
			} else {
				if (allWords.get(wordNum).equals(automata.getEnterWord())) {
					BNode bNode = new StringNode(allWords.get(wordNum),
							wordsPosition.get(wordNum++));
					nowNode.addChildNode(bNode);
					bNode.setFatherNode(nowNode);
					if (automata.getReturnStatus() != null) {
						jumpNewStatue();
					} else {
						if (automata.getJumpStatus().equals("FIN")) {
							if (wordNum < allWords.size()) {
								throw new GrammarException(fileName,
										wordsPosition.get(wordNum)
												+ ":MACHINE is over!");
							}
						} else if (automata.getJumpStatus().equals("RET")) {
							retStack();
						} else {
							int num = nowNewStatueNums.pop();
							nowNewStatueNums.push(num + 1);
							addStack(automata.getJumpStatus());
						}
					}
				} else {
					if (rollBackRow < wordsPosition.get(wordNum)) {
						rollBackRow = wordsPosition.get(wordNum);
					}
					rollBack(0);
				}
			}
		}
		if (!analysisStack.peek().getJumpStatus().equals("FIN")) {
			throw new GrammarException(fileName, "END is lost");
		}
	}

	/**
	 * 跳转到一个新的状态
	 * 
	 * @throws GrammarException
	 */
	private void jumpNewStatue() throws GrammarException {
		Automata automata = analysisStack.peek();
		if (automata.getJumpStatus().equals("Id")) {
			// Id匹配
			if (Pattern.matches("[a-zA-Z_](\\w)*", allWords.get(wordNum))
					&& !isKeyWord(allWords.get(wordNum))) {
				BNode idNode = new IdNode(allWords.get(wordNum),
						wordsPosition.get(wordNum++));
				idNode.setFatherNode(nowNode);
				nowNode.addChildNode(idNode);
				if (automata.getReturnStatus().equals("RET")) {
					retStack();
				} else {
					int num = nowNewStatueNums.pop();
					nowNewStatueNums.push(num + 1);
					addStack(automata.getReturnStatus());
				}
			} else {
				// 回滚
				if (rollBackRow < wordsPosition.get(wordNum)) {
					rollBackRow = wordsPosition.get(wordNum);
				}
				if (automata.getEnterWord().equals("ε")) {
					rollBack(0);
				} else {
					rollBack(1);
				}
			}
		} else if (automata.getJumpStatus().equals("ChaL")) {
			// 字符匹配
			if (allWords.get(wordNum).startsWith("'")
					&& allWords.get(wordNum).endsWith("'")) {
				BNode charNode = new CharLitNode(allWords.get(wordNum)
						.charAt(1), wordsPosition.get(wordNum++));
				charNode.setFatherNode(nowNode);
				nowNode.addChildNode(charNode);
				if (automata.getReturnStatus().equals("RET")) {
					retStack();
				} else {
					int num = nowNewStatueNums.pop();
					nowNewStatueNums.push(num + 1);
					addStack(automata.getReturnStatus());
				}
			} else {
				// 回滚
				if (rollBackRow < wordsPosition.get(wordNum)) {
					rollBackRow = wordsPosition.get(wordNum);
				}
				if (automata.getEnterWord().equals("ε")) {
					rollBack(0);
				} else {
					rollBack(1);
				}
			}
		} else if (automata.getJumpStatus().equals("StrL")) {
			// 字符串匹配
			if (allWords.get(wordNum).startsWith("\"")
					&& allWords.get(wordNum).endsWith("\"")) {
				BNode stringLitNode = new StringLitNode(allWords.get(wordNum)
						.substring(1, allWords.get(wordNum).length() - 1),
						wordsPosition.get(wordNum++));
				nowNode.addChildNode(stringLitNode);
				stringLitNode.setFatherNode(nowNode);
				if (automata.getReturnStatus().equals("RET")) {
					retStack();
				} else {
					int num = nowNewStatueNums.pop();
					nowNewStatueNums.push(num + 1);
					addStack(automata.getReturnStatus());
				}
			} else {
				// 回滚
				if (rollBackRow < wordsPosition.get(wordNum)) {
					rollBackRow = wordsPosition.get(wordNum);
				}
				if (automata.getEnterWord().equals("ε")) {
					rollBack(0);
				} else {
					rollBack(1);
				}
			}
		} else if (automata.getJumpStatus().equals("UinL")) {
			// 无符号整形匹配
			if (allWords.get(wordNum).equals("0")
					|| Pattern.matches("[1-9]([0-9])*", allWords.get(wordNum))) {
				BNode intNode;
				try {
					intNode = new UIntLitNode(Integer.parseInt(allWords
							.get(wordNum)), wordsPosition.get(wordNum++));
				} catch (Exception e) {
					intNode = new UIntLitNode(Long.parseLong(allWords
							.get(wordNum)), wordsPosition.get(wordNum++));
				}
				intNode.setFatherNode(nowNode);
				nowNode.addChildNode(intNode);
				if (automata.getReturnStatus().equals("RET")) {
					retStack();
				} else {
					int num = nowNewStatueNums.pop();
					nowNewStatueNums.push(num + 1);
					addStack(automata.getReturnStatus());
				}
			} else if (Pattern.matches("0x([0-9a-f])*", allWords.get(wordNum))) {
				BNode intNode = new UIntLitNode(allWords.get(wordNum),
						wordsPosition.get(wordNum++));
				intNode.setFatherNode(nowNode);
				nowNode.addChildNode(intNode);
				if (automata.getReturnStatus().equals("RET")) {
					retStack();
				} else {
					int num = nowNewStatueNums.pop();
					nowNewStatueNums.push(num + 1);
					addStack(automata.getReturnStatus());
				}
			} else {
				// 回滚
				if (rollBackRow < wordsPosition.get(wordNum)) {
					rollBackRow = wordsPosition.get(wordNum);
				}
				if (automata.getEnterWord().equals("ε")) {
					rollBack(0);
				} else {
					rollBack(1);
				}
			}
		} else if (automata.getJumpStatus().equals("UflL")) {
			// 无符号浮点型匹配
			if ((allWords.get(wordNum).equals("0") || Pattern.matches(
					"[1-9]([0-9])*", allWords.get(wordNum)))
					&& allWords.get(wordNum + 1).equals(".")
					&& Pattern.matches("([0-9])+", allWords.get(wordNum + 2))) {
				BNode floatNode = new UFloatLitNode(Float.valueOf(allWords
						.get(wordNum) + '.' + allWords.get(wordNum + 2)),
						wordsPosition.get(wordNum));
				floatNode.setFatherNode(nowNode);
				nowNode.addChildNode(floatNode);
				wordNum += 3;
				if (automata.getReturnStatus().equals("RET")) {
					retStack();
				} else {
					int num = nowNewStatueNums.pop();
					nowNewStatueNums.push(num + 1);
					addStack(automata.getReturnStatus());
				}
			} else {
				// 回滚
				if (rollBackRow < wordsPosition.get(wordNum)) {
					rollBackRow = wordsPosition.get(wordNum);
				}
				if (automata.getEnterWord().equals("ε")) {
					rollBack(0);
				} else {
					rollBack(1);
				}
			}
		} else if (automata.getJumpStatus().equals("TruL")) {
			// 命题true
			if (allWords.get(wordNum).equals("true")) {
				BNode trueNode = new TrueLitNode(wordsPosition.get(wordNum++));
				trueNode.setFatherNode(nowNode);
				nowNode.addChildNode(trueNode);
				if (automata.getReturnStatus().equals("RET")) {
					retStack();
				} else {
					int num = nowNewStatueNums.pop();
					nowNewStatueNums.push(num + 1);
					addStack(automata.getReturnStatus());
				}
			} else {
				// 回滚
				if (rollBackRow < wordsPosition.get(wordNum)) {
					rollBackRow = wordsPosition.get(wordNum);
				}
				if (automata.getEnterWord().equals("ε")) {
					rollBack(0);
				} else {
					rollBack(1);
				}
			}
		} else if (automata.getJumpStatus().equals("FalL")) {
			// 命题false
			if (allWords.get(wordNum).equals("false")) {
				BNode falseNode = new FalseLitNode(wordsPosition.get(wordNum++));
				falseNode.setFatherNode(nowNode);
				nowNode.addChildNode(falseNode);
				if (automata.getReturnStatus().equals("RET")) {
					retStack();
				} else {
					int num = nowNewStatueNums.pop();
					nowNewStatueNums.push(num + 1);
					addStack(automata.getReturnStatus());
				}
			} else {
				// 回滚
				if (rollBackRow < wordsPosition.get(wordNum)) {
					rollBackRow = wordsPosition.get(wordNum);
				}
				if (automata.getEnterWord().equals("ε")) {
					rollBack(0);
				} else {
					rollBack(1);
				}
			}
		} else {
			nowNewStatueNums.push(1);
			BNode bNode = BNode.getNode(automata.getJumpStatus(),
					wordsPosition.get(wordNum));
			bNode.setFatherNode(nowNode);
			nowNode.addChildNode(bNode);
			nowNode = bNode;
			addStack(automata.getJumpStatus());
		}
	}

	/**
	 * 当不能匹配自动机的时候需要回滚
	 * 
	 * @param flag
	 *            标记是初始的回滚自动机否匹配了输入字符，只有在回滚刚开始发生的自动机才会出现，因为当前自动机有可能匹配了输入字符，
	 *            但是没有匹配跳转状态中的Id等,1表示匹配了，0表示为”ε“,2表示不是起始的回滚自动机
	 * @throws GrammarException
	 */
	private void rollBack(int flag) throws GrammarException {
		Automata automata = analysisStack.peek();
		if (flag == 1) {
			// 初始的回滚自动机匹配了输入字符
			nowNode.removeLastChildNode();
			wordNum--;
		} else if (flag == 0) {
			// 初始的回滚自动机没有匹配输入字符
			;
		} else {
			// flag = 2表示自动机不是初始的回滚自动机
			if (automata.getReturnStatus() != null) {
				nowNode.removeLastChildNode();
				if (automata.getJumpStatus().equals("Id")
						|| automata.getJumpStatus().equals("UinL")
						|| automata.getJumpStatus().equals("ChaL")
						|| automata.getJumpStatus().equals("StrL")) {
					wordNum--;
				} else if (automata.getJumpStatus().equals("UflL")) {
					wordNum -= 3;
				}
			}
			if (!automata.getEnterWord().equals("ε")) {
				nowNode.removeLastChildNode();
				wordNum--;
			}
		}
		if (rollBackStack.isEmpty()) {
			throw new GrammarException(fileName, rollBackRow + "");
		}
		if (nowStatueRollNums.peek() > 0) {
			analysisStack.pop();
			analysisStack.push(rollBackStack.pop());
			int num = nowStatueRollNums.pop();
			nowStatueRollNums.push(num - 1);
		} else {
			nowStatueRollNums.pop();
			analysisStack.pop();
			int num = nowNewStatueNums.pop();
			if (num != 1) {
				nowNewStatueNums.push(num - 1);
				if (analysisStack.peek().getReturnStatus() != null) {
					// 新状态已经匹配完毕
					if (!analysisStack.peek().getJumpStatus().equals("Id")
							&& !analysisStack.peek().getJumpStatus()
									.equals("UinL")
							&& !analysisStack.peek().getJumpStatus()
									.equals("UflL")
							&& !analysisStack.peek().getJumpStatus()
									.equals("ChaL")
							&& !analysisStack.peek().getJumpStatus()
									.equals("StrL")) {
						rollBackNum(nowNode.getLastChildNode());
					}
				}
			} else {
				nowNode = nowNode.fatherNode;
			}
			rollBack(2);
		}
	}

	/**
	 * 回滚时，若已经匹配完一个完整地之状态，则需要将子状态中所有的word退栈
	 * 
	 * @param bNode
	 */
	private void rollBackNum(BNode bNode) {
		for (BNode node : bNode.getChildNodes()) {
			if ((node instanceof IdNode) || (node instanceof UIntLitNode)
					|| (node instanceof CharLitNode)
					|| (node instanceof StringLitNode)
					|| (node instanceof StringNode)) {
				wordNum--;
			} else if ((node instanceof UFloatLitNode)) {
				wordNum -= 3;
			} else {
				rollBackNum(node);
			}
		}
	}

	/**
	 * 当跳转状态为"RET",分析栈和回滚栈将相应的自动机出栈
	 */
	private void retStack() {
		Automata automata = analysisStack.peek();
		while (true) {
			nowNode = nowNode.fatherNode;
			for (int i = 0, num = nowNewStatueNums.pop(); i < num; i++) {
				analysisStack.pop();
				for (int j = 0, numRoll = nowStatueRollNums.pop(); j < numRoll; j++) {
					rollBackStack.pop();
				}
			}
			automata = analysisStack.peek();
			if (!automata.getReturnStatus().equals("RET")) {
				int num = nowNewStatueNums.pop();
				nowNewStatueNums.push(num + 1);
				addStack(automata.getReturnStatus());
				break;
			}
		}
	}

	/**
	 * 判断是否为关键字
	 * 
	 * @param word
	 * @return
	 */
	private boolean isKeyWord(String word) {
		switch (word) {
		case "int":
		case "char":
		case "long":
		case "short":
		case "float":
		case "double":
		case "string":
		case "void":
		case "unsigned":
		case "MACHINE":
		case "END":
		case "ATTRIBUTES":
		case "INCLUDE":
		case "define":
		case "typedef":
		case "const":
		case "enum":
		case "struct":
		case "OPERATIONS":
		case "return":
		case "while":
		case "if":
		case "set":
		case "nil":
		case "main":
		case "true":
		case "false":
		case "proposition":
		case "sizeof":
			return true;
		default:
			return false;
		}

	}

	/**
	 * 跳转当当前类型的下一个状态
	 * 
	 * @param statue
	 */
	private void addStack(String statue) {
		List<Automata> statueAutomatas = Automata.automatas.get(statue);
		analysisStack.push(statueAutomatas.get(0));
		for (int j = statueAutomatas.size() - 1; j > 0; j--) {
			rollBackStack.push(statueAutomatas.get(j));
		}
		nowStatueRollNums.push(statueAutomatas.size() - 1);
	}

	/**
	 * 获取状态的前缀,例如Mac1获取Mac
	 */
	public String getStatuePre() {
		Automata automata = analysisStack.peek();
		String startStatue = automata.getStartStatue();
		Pattern pattern = Pattern.compile("([a-zA-Z])+");
		Matcher matcher = pattern.matcher(startStatue);
		matcher.find();
		startStatue = matcher.group();
		return startStatue;
	}

	/**
	 * 拆分一行，解析出字符以及字符串
	 * 
	 * @param line
	 * @return
	 * @throws GrammarException
	 */
	private void splitLine(String line) throws GrammarException {
		nextIsPlain = 1;
		line = line.trim();
		int i = 0;
		while (i < line.length()) {
			int length = isKeyChar(line, i);
			if (length == 0) {
				if (line.charAt(i) == ' ') {
					allWords.add(line.substring(0, i));
					wordsPosition.add(row);
					line = line.substring(i + 1).trim();
					i = 0;
					nextIsPlain = 1;
				} else {
					i++;
					nextIsPlain = 0;
				}

			} else {
				if (i != 0) {
					allWords.add(line.substring(0, i));
					wordsPosition.add(row);
				}
				allWords.add(line.substring(i, i + length));
				wordsPosition.add(row);
				if (line.length() > i + length
						&& line.charAt(i + length) == ' ') {
					nextIsPlain = 1;
				} else {
					nextIsPlain = 0;
				}
				line = line.substring(i + length).trim();
				i = 0;
			}
			// if(isKeyChar(line.charAt(i), line.charAt(i+1))) {
			// if(i>0) {
			// ret.add(line.substring(0, i));
			// }
			// ret.add(line.substring(i, i+1));
			// line = line.substring(i+1).trim();
			// i = -1;
			// } else if (line.charAt(i) ==' ') {
			// line = line.substring(i+1).trim();
			// ret.add(line.substring(0, i));
			// i = -1;
			// }
		}
		if (i != 0) {
			allWords.add(line);
			wordsPosition.add(row);
		}
	}

	private int deleteNullNode(BNode bNode) {
		int ret = 0;
		for (int i = 0; i < bNode.getChildNodes().size(); i++) {
			BNode node = bNode.getChildNodeAt(i);
			if ((node instanceof IdNode) || (node instanceof UIntLitNode)
					|| (node instanceof CharLitNode)
					|| (node instanceof StringLitNode)
					|| (node instanceof StringNode)
					|| (node instanceof UFloatLitNode)
					|| (node instanceof TrueLitNode)
					|| (node instanceof FalseLitNode)) {
				ret++;
			} else {
				int num = deleteNullNode(node);
				if (num == 0) {
					bNode.removeChildNodeAt(i--);
				} else {
					ret += num;
				}
			}
		}
		return ret;
	}

	/**
	 * 判断是否整行都是注释
	 * 
	 * @param line
	 */
	private boolean judgeNote(String line) {
		line = line.trim();
		if (noteFlag == 0 && line.startsWith("//")) {
			return true;
		} else if (noteFlag == 1) {
			if (line.endsWith("*/")) {
				noteFlag = 0;
				return true;
			} else if (!Pattern.matches("\\*/", line)) {
				return true;
			}
		} else if (line.startsWith("/*")) {
			line = line.substring(2);
			if (line.endsWith("*/")) {
				return true;
			} else {
				Pattern pattern = Pattern.compile("\\*/");
				Matcher matcher = pattern.matcher(line);
				if (!matcher.find()) {
					noteFlag = 1;
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 需要比较两个甚至三个字符，因为/-\等等之间不允许出现空格
	 * 
	 * @param line
	 * @param start
	 * @throws GrammarException
	 * @return：匹配的关键字符的个数，失败返回0，错误则跑出错误
	 */
	private int isKeyChar(String line, int start) throws GrammarException {
		Set<Character> keyHelp = new HashSet<Character>(Arrays.asList('{', '}',
				'(', ')', '@', ':', '*', '+', ';', ',', '~'));
		if (keyHelp.contains(line.charAt(start))) {
			return 1;
		} else if (line.charAt(start) == '#') {
			if (line.length() < start + 7) {
				throw new GrammarException(fileName, row
						+ "; After '#' need 'define'");
			} else if (line.substring(start + 1, start + 7).equals("define")) {
				return 7;
			} else {
				throw new GrammarException(fileName, row
						+ "; After '#' need 'define'");
			}
		} else if (line.charAt(start) == '=' || line.charAt(start) == '>'
				|| line.charAt(start) == '<') {
			if (line.length() > start + 1) {
				if (line.charAt(start + 1) == '=') {
					return 2;
				} else if (line.charAt(start) == '>'
						&& line.charAt(start + 1) == '>') {
					return 2;
				} else if (line.charAt(start) == '<'
						&& line.charAt(start + 1) == '<') {
					return 2;
				} else {
					return 1;
				}
			} else {
				return 1;
			}
		} else if (line.charAt(start) == '!') {
			if (line.length() > start + 1 && line.charAt(start + 1) == '=') {
				return 2;
			} else {
				throw new GrammarException(fileName, row
						+ "; After '!' need '='");
			}
		} else if (line.charAt(start) == '.') {
			if (nextIsPlain == 1
					|| (line.length() > start + 1 && line.charAt(start + 1) == ' ')
					|| line.length() == start + 1) {
				throw new GrammarException(fileName, row
						+ ";exception occurred in '.'");
			} else {
				return 1;
			}
		} else if (line.charAt(start) == '|') {
			if (line.length() > start + 1 && line.charAt(start + 1) == '|') {
				return 2;
			} else {
				return 1;
			}
		} else if (line.charAt(start) == '&') {
			if (line.length() > start + 1 && line.charAt(start + 1) == '&') {
				return 2;
			} else {
				return 1;
			}
		} else if (line.charAt(start) == '/') {
			if (line.length() > start + 2
					&& line.substring(start, start + 3).equals("/-\\")) {
				return 3;
			} else {
				return 1;
			}
		} else if (line.charAt(start) == '-') {
			if (line.length() > start + 1
					&& (line.charAt(start + 1) == '-' || line.charAt(start + 1) == '>')) {
				return 2;
			} else {
				return 1;
			}
		} else if (line.charAt(start) == '\\') {
			if (line.length() > start + 2
					&& line.substring(start, start + 3).equals("\\-/")) {
				return 3;
			} else {
				throw new GrammarException(fileName, row
						+ "; After '\\' need '-/'");
			}
		} else if (line.charAt(start) == '"') {
			for (int i = start + 1; i < line.length(); i++) {
				if (line.charAt(i) == '"' && line.charAt(i - 1) != '\\') {
					return i - start + 1;
				}
			}
			throw new GrammarException(fileName, row + ";String is not fit");
		} else if (line.charAt(start) == '\'') {
			if (line.length() < start + 3) {
				throw new GrammarException(fileName, row + "; char is not fit");
			}
			if (line.charAt(start + 1) == '\\') {
				if (line.charAt(start + 2) == 'b'
						|| line.charAt(start + 2) == 't'
						|| line.charAt(start + 2) == 'n'
						|| line.charAt(start + 2) == 'f'
						|| line.charAt(start + 2) == 'r'
						|| line.charAt(start + 2) == '"'
						|| line.charAt(start + 2) == '\''
						|| line.charAt(start + 2) == '\\') {
					if (line.length() == start + 3) {
						throw new GrammarException(fileName, row
								+ "; char is not fit");
					} else if (line.charAt(start + 3) != '\'') {
						throw new GrammarException(fileName, row
								+ "; char is not fit");
					} else {
						return 4;
					}
				} else {
					throw new GrammarException(fileName, row
							+ "; char is not fit");
				}
			} else if (line.charAt(start + 2) == '\'') {
				return 3;
			} else {
				throw new GrammarException(fileName, row + "; char is not fit");
			}
		}
		return 0;
	}

	/**
	 * 删除一行中夹杂的注释
	 */
	private String removeNote(String line) throws BstarException {
		if (noteFlag == 1) {
			Pattern pattern = Pattern.compile("\\*/");
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				noteFlag = 0;
				line = line.substring(matcher.end());
				if (!judgeNote(line)) {
					line = removeNote(line);
				} else {
					line = null;
				}
			}
		} else {
			Pattern patternSlashStar = Pattern.compile("/\\*");
			Pattern patternStarSlash = Pattern.compile("\\*/");
			Pattern patternDoubleSlash = Pattern.compile("//");
			Matcher matcherSlashStar = patternSlashStar.matcher(line);
			Matcher matcherStarSlash = patternStarSlash.matcher(line);
			Matcher matcherDoubleSlash = patternDoubleSlash.matcher(line);
			int startSlashStar = matcherSlashStar.find() ? matcherSlashStar
					.start() : Integer.MAX_VALUE;
			int startStarSlash = matcherStarSlash.find() ? matcherStarSlash
					.start() : Integer.MAX_VALUE;
			int startDoubleSlash = matcherDoubleSlash.find() ? matcherDoubleSlash
					.start() : Integer.MAX_VALUE;
			if (startDoubleSlash < startSlashStar) {
				if (startStarSlash < startDoubleSlash) { // */在//之前,这里就不考虑字符串中包含了
					throw new NoteException(fileName, row);
				} else {
					line = line.substring(0, startDoubleSlash);
				}
			} else if (startSlashStar < startDoubleSlash) {
				if (startStarSlash < startSlashStar) { // */在//之前,这里就不考虑字符串中包含了
					throw new BstarException(
							"NoteException:exception occured in line" + row);
				} else if (startStarSlash == Integer.MAX_VALUE) {
					noteFlag = 1;
					line = line.substring(0, startSlashStar);
				} else if (startStarSlash == startSlashStar + 1) { // /*/情况
					if (matcherStarSlash.find()) {
						if (matcherStarSlash.end() != line.length()) {
							line = line.substring(0, startSlashStar)
									+ line.substring(matcherStarSlash.end());
							line = removeNote(line);
						} else {
							return line.substring(0, startSlashStar);
						}
					} else {
						noteFlag = 1;
						line = line.substring(0, startSlashStar);
					}
				} else {
					if (matcherStarSlash.end() != line.length()) {
						line = line.substring(0, startSlashStar)
								+ line.substring(matcherStarSlash.end());
						line = removeNote(line);
					} else {
						return line.substring(0, startSlashStar);
					}
				}
			}

		}
		return line;
	}

	/*
	 * private void printNode(BNode bNode, int n) { String plain = ""; for (int
	 * i = 0; i < n; i++) { plain += " "; } System.out.println(plain +
	 * bNode.getClass().getSimpleName() + "  " + bNode.getLine()); for (BNode
	 * node : bNode.getChildNodes()) { printNode(node, n + 4); } }
	 */

	public static void main(String args[]) {
		BStar bStar;
		File file = new File(baseRoot);
		File fileC = new File(baseRoot+"c");
		if(!fileC.exists()) {
			fileC.mkdir();
		}
		String[] fileNames = file.list();
		for (String fileName : fileNames) {
			if (fileName.endsWith(".bs")) {
				bStar = new BStar(fileName);
				// Date dateStart = new Date();
				bStar.compile();
				// Date dateEnd = new Date();
				System.out.println(fileName);
				// System.out.println(dateEnd.getTime() - dateStart.getTime());
				// SymbolTables.compiledSymT.clear();
			}
		}
//		bStar = new BStar("test.bs");
//		bStar.compile();
//		System.out.println("Mutex.bs");
	}
}
//
// /**
// * 单行解析，返回为真时表明已经读取到了END
// * @param line
// * @return bool
// * @throws BstarException
// */
// private boolean lineExplain(String line) throws BstarException {
// for (int i = 0; i < line.length();) {
// Automata automata = analysisStack.peek();
// if (!automata.getEnterWord().equals("ε")) {
// String word = automata.getEnterWord();
// if(line.substring(i, word.length()).equals(word)) {
// line = line.substring(i+word.length()).trim();
// if (automata.getJumpStatus().equals("FIN")) {
// return true;//结束抽象机
// } else if (automata.getJumpStatus().equals("RET")) {
// this.stackPushOut();//回滚
// } else {
// this.nowNode.addChildNode(new StringNode(word));
// if (automata.getReturnStatus() != null) {
// line = this.jumpNewStatue(line);
// } else {
// this.addStack(automata.getJumpStatus());
// }
// }
// } else {
// //stack Out
// String startStatue = automata.getStartStatue();
// if(!rollBackStack.isEmpty()&&rollBackStack.peek().getStartStatue().equals(startStatue))
// {
// analysisStack.pop();
// analysisStack.push(rollBackStack.pop());
// } else if (!rollBackStack.isEmpty()) {
//
// analysisStack.pop();
// this.rollBack();
// } else {
// throw new GrammarException(this.row+":About at "+word);
// }
// }
// } else {
// if (automata.getJumpStatus().equals("RET")) {
// this.stackPushOut();
// } else {
// if (automata.getReturnStatus() != null) {
// line = this.jumpNewStatue(line);
// } else {
// this.addStack(automata.getJumpStatus());
// }
// }
// }
// }
//
// return false;//并不是抽象机的结束
// }
//
// ///**
// // * 栈顶元素处理,每个自动机栈顶元素的出入
// // * @throws GrammarException
// // */
// //private void stackTopEle() throws GrammarException {
// // Automata automata = analysisStack.peek();
// // if((enterStack.isEmpty()&&!automata.getStackTopEleIn().equals("@"))||
// //
// (!enterStack.isEmpty()&&!automata.getStackTopEleIn().equals(enterStack.peek())))
// {
// // throw new GrammarException(this.row+enterStack.peek()+" was expected!");
// // }
// // if (!automata.getStackTopEleIn().equals(automata.getStackTopEleOut())) {
// // if (automata.getStackTopEleOut().equals("@")) {
// // enterStack.pop();
// // } else if (automata.getStackTopEleIn().equals("@")) {
// // enterStack.push(automata.getEnterWord());
// // } else {
// // String temp = enterStack.pop();
// // temp = temp.concat(automata.getEnterWord());
// // enterStack.push(temp);
// // }
// // }
// //}
//
// /**
// * 当跳转状态为RET时，分析栈与回滚栈退栈
// */
// private void stackPushOut() {
// Automata automata = analysisStack.pop();
// while (true) {
// nowNode = nowNode.fatherNode;
// String startStatue = this.getStatuePre();
// while (!analysisStack.isEmpty()
// && analysisStack.peek().getStartStatue()
// .startsWith(startStatue)) {
// analysisStack.pop();
// }
// while (!rollBackStack.isEmpty()
// && rollBackStack.peek().getStartStatue()
// .startsWith(startStatue)) {
// rollBackStack.pop();
// }
// automata = analysisStack.peek();
// if (!automata.getReturnStatus().equals("RET")) {
// this.addStack(automata.getJumpStatus());
// break;
// } else {
// automata = analysisStack.peek();
// }
// }
// }
//
// /**
// * 不匹配后回滚
// */
// public void rollBack() throws GrammarException {
// Automata automata = analysisStack.peek();
// if(automata.getEnterWord().equals("ε")) {
// if(automata.getReturnStatus()!=null) {
// nowNode = nowNode.fatherNode;
// nowNode.removeLastChildNode();
// }
// if(rollBackStack.pop().getStartStatue().equals(automata.getStartStatue())) {
// analysisStack.pop();
// analysisStack.push(rollBackStack.pop());
// } else {
// this.analysisStack.pop();
// this.rollBack();
// }
// } else {
// throw new GrammarException(this.row+":About at "+automata.getEnterWord());
// }
// }
//
// /**
// * 获取状态的前缀,例如Mac1获取Mac
// */
// public String getStatuePre() {
// Automata automata = analysisStack.pop();
// String startStatue = automata.getStartStatue();
// Pattern pattern = Pattern.compile("([a-zA-Z])+");
// Matcher matcher = pattern.matcher(startStatue);
// matcher.find();
// startStatue = matcher.group();
// return startStatue;
// }
//
// /**
// * 跳转到下一状态时，把所有符合的自动机入相应的栈
// * @param statue
// */
// private void addStack(String statue) {
// List<Automata> statueAutomatas = Automata.automatas.get(statue);
// analysisStack.push(statueAutomatas.get(0));
// for (int j = statueAutomatas.size() - 1; j > 0; j--) {
// rollBackStack.push(statueAutomatas.get(j));
// }
// }
//
// /**
// * 跳转到新的状态
// */
// private String jumpNewStatue(String line) {
// Automata automata = analysisStack.peek();
// // TODO 跳转新状态，需要跟新相关的节点
// // BNode newNode = BNode.getNode(automata.getJumpStatus());
// // newNode.setFatherNode(this.nowNode);
// // this.nowNode.addChildNode(newNode);
// if(automata.getJumpStatus().equals("Id")) {
// int i;
// for(i = 0; i < line.length(); i++) {
// if(line.charAt(i) == ' ') {
// break;
// }
// }
// String temp = line.substring(0, i);
// BNode newNode = new IdNode("temp");
// this.nowNode.addChildNode(newNode);
// if(Pattern.matches("[a-zA-Z_](\\w)*", temp)) {
// if(automata.getReturnStatus().equals("RET")) {
// this.stackPushOut();
// } else {
// this.addStack(automata.getReturnStatus());
// }
// return line.substring(i).trim();
// } else {
// this.nowNode = newNode;//方便回滚
// this.rollBack();
// }
// } else if (automata.getJumpStatus().equals("ChaL")) {
//
// } else if (automata.getJumpStatus().equals("StrL")) {
//
// } else {
// this.nowNode = newNode;
// }
// return line;
// }
