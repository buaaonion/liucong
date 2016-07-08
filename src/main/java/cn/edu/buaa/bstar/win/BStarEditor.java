package cn.edu.buaa.bstar.win;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;

import org.fife.rsta.ui.CollapsibleSectionPanel;
//import org.fife.rsta.ui.DocumentMap;
import org.fife.rsta.ui.GoToDialog;
import org.fife.rsta.ui.SizeGripIcon;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.ReplaceToolBar;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.rsta.ui.search.FindToolBar;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import cn.edu.buaa.bstar.grammar.BStar;

/**
 * An application that demonstrates use of the RSTAUI project. Please don't take
 * this as good application design; it's just a simple example.
 * <p>
 *
 * Unlike the library itself, this class is public domain.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class BStarEditor extends JFrame implements SearchListener {

	private CollapsibleSectionPanel cspl;  //左边的文件列表
	private CollapsibleSectionPanel cspru; //编辑区域
	private CollapsibleSectionPanel csprd; //结果显示区域
	private RSyntaxTextArea textAreal;
	private RSyntaxTextArea textArearu;
	private RSyntaxTextArea textAreard;
	private FindDialog findDialog;
	private ReplaceDialog replaceDialog;
	private FindToolBar findToolBar;
	private ReplaceToolBar replaceToolBar;
	private StatusBar statusBar;
	private File file;
	private JFileChooser chooser;
//	private FileListBar fileListBar;
	private List<String> fileRoots; // 文件的根路径

	public BStarEditor() {

		initSearchDialogs();

		fileRoots = new ArrayList<String>();

		chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setMultiSelectionEnabled(true);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("B*", "bs");
		chooser.setFileFilter(filter);// 设置文件后缀过滤器

		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory
				.getDefaultInstance();
		atmf.putMapping("text/BStar", "cn.edu.buaa.bstar.win.BStarTokenMaker");

		JPanel contentPane = new JPanel(new BorderLayout());
		JPanel rightPane = new JPanel(new BorderLayout());
		setContentPane(contentPane);
		this.setResizable(false);
		cspl = new CollapsibleSectionPanel();
		cspru = new CollapsibleSectionPanel();
		csprd = new CollapsibleSectionPanel();
		
		contentPane.add(cspl, BorderLayout.WEST);
		contentPane.add(rightPane, BorderLayout.EAST);
		rightPane.add(cspru, BorderLayout.NORTH);
		rightPane.add(csprd, BorderLayout.SOUTH);

		setJMenuBar(createMenuBar());

		textArearu = new RSyntaxTextArea(30, 100);
		textArearu.setSyntaxEditingStyle("text/BStar");
		textArearu.setCodeFoldingEnabled(true);
		textArearu.setMarkOccurrences(true);
		textArearu.setTabSize(4);
		RTextScrollPane spru = new RTextScrollPane(textArearu);
		cspru.add(spru,BorderLayout.WEST);
		
		textAreal = new RSyntaxTextArea(40, 20);
		RTextScrollPane spl = new RTextScrollPane(textAreal);
		textAreal.setEditable(false);
		textAreal.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				int index = textAreal.getCaretLineNumber();
				if(index == fileRoots.size()) {
					return;
				}
				readFile(new File(fileRoots.get(index)));
				textArearu.setCaretPosition(0);
			}
		});
		textAreal.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					int index = textAreal.getCaretLineNumber();
					try {
						fileRoots.remove(index);
						textAreal.replaceRange(null, textAreal.getLineStartOffset(index), textAreal.getLineStartOffset(index+1));
						textArearu.setText("");
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
					
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		spl.setLineNumbersEnabled(false);
		cspl.add(spl);
		
		textAreard = new RSyntaxTextArea(10, 100);
		RTextScrollPane sprd = new RTextScrollPane(textAreard);
		textAreard.setEditable(false);
		csprd.add(sprd);
		textAreard.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);

//		ErrorStrip errorStrip = new ErrorStrip(textArearu);
//		contentPane.add(errorStrip, BorderLayout.LINE_END);
		// org.fife.rsta.ui.DocumentMap docMap = new
		// org.fife.rsta.ui.DocumentMap(textArea);
		// contentPane.add(docMap, BorderLayout.LINE_END);

		statusBar = new StatusBar();

		contentPane.add(statusBar, BorderLayout.SOUTH);
//		contentPane.add(fileListBar, BorderLayout.WEST);

		setTitle("B*编辑器");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		System.setErr(new MyPrintStream(System.err, textAreard));
		System.setOut(new MyPrintStream(System.out, textAreard));
	}

	// private void addItem(Action a, ButtonGroup bg, JMenu menu) {
	// JRadioButtonMenuItem item = new JRadioButtonMenuItem(a);
	// bg.add(item);
	// menu.add(item);
	// }

	private JMenuBar createMenuBar() {

		JMenuBar mb = new JMenuBar();

		JMenu saveMenu = new JMenu("文件");
		saveMenu.add(new JMenuItem(new SaveAction()));
		saveMenu.add(new JMenuItem(new SaveAsAction()));
		saveMenu.add(new JMenuItem(new OpenAction()));
		saveMenu.add(new JMenuItem(new CreateAction()));

		JMenu compileMenu = new JMenu("编译");
		compileMenu.add(new JMenuItem(new CompileFileAction()));
		compileMenu.add(new JMenuItem(new CompileAllAction()));

		JMenu menu = new JMenu("查找");
		menu.add(new JMenuItem(new ShowFindDialogAction()));
		menu.add(new JMenuItem(new ShowReplaceDialogAction()));
		menu.add(new JMenuItem(new GoToLineAction()));
//		menu.addSeparator();

//		int ctrl = getToolkit().getMenuShortcutKeyMask();
//		int shift = InputEvent.SHIFT_MASK;
//		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F, ctrl | shift);
//		Action a = cspru.addBottomComponent(ks, findToolBar);
//		a.putValue(Action.NAME, "显示查找框");
//		menu.add(new JMenuItem(a));
//		ks = KeyStroke.getKeyStroke(KeyEvent.VK_H, ctrl | shift);
//		a = cspru.addBottomComponent(ks, replaceToolBar);
//		a.putValue(Action.NAME, "显示替换框");
//		menu.add(new JMenuItem(a));
		mb.add(saveMenu);
		mb.add(menu);
		mb.add(compileMenu);

		// menu = new JMenu("LookAndFeel");
		// ButtonGroup bg = new ButtonGroup();
		// LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
		// for (int i=0; i<infos.length; i++) {
		// addItem(new LookAndFeelAction(infos[i]), bg, menu);
		// }
		// mb.add(menu);

		return mb;

	}

	public String getSelectedText() {
		return textArearu.getSelectedText();
	}

	/**
	 * Creates our Find and Replace dialogs.
	 */
	public void initSearchDialogs() {

		findDialog = new FindDialog(this, this);
		replaceDialog = new ReplaceDialog(this, this);

		// This ties the properties of the two dialogs together (match case,
		// regex, etc.).
		SearchContext context = findDialog.getSearchContext();
		replaceDialog.setSearchContext(context);

		// Create tool bars and tie their search contexts together also.
		findToolBar = new FindToolBar(this);
		findToolBar.setSearchContext(context);
		replaceToolBar = new ReplaceToolBar(this);
		replaceToolBar.setSearchContext(context);

	}

	/**
	 * Listens for events from our search dialogs and actually does the dirty
	 * work.
	 */
	public void searchEvent(SearchEvent e) {

		SearchEvent.Type type = e.getType();
		SearchContext context = e.getSearchContext();
		SearchResult result = null;

		switch (type) {
		default: // Prevent FindBugs warning later
		case MARK_ALL:
			result = SearchEngine.markAll(textArearu, context);
			break;
		case FIND:
			result = SearchEngine.find(textArearu, context);
			if (!result.wasFound()) {
				UIManager.getLookAndFeel().provideErrorFeedback(textArearu);
			}
			break;
		case REPLACE:
			result = SearchEngine.replace(textArearu, context);
			if (!result.wasFound()) {
				UIManager.getLookAndFeel().provideErrorFeedback(textArearu);
			}
			break;
		case REPLACE_ALL:
			result = SearchEngine.replaceAll(textArearu, context);
			JOptionPane.showMessageDialog(null, result.getCount()
					+ " occurrences replaced.");
			break;
		}

		String text = null;
		if (result.wasFound()) {
			text = "Text found; occurrences marked: " + result.getMarkedCount();
		} else if (type == SearchEvent.Type.MARK_ALL) {
			if (result.getMarkedCount() > 0) {
				text = "Occurrences marked: " + result.getMarkedCount();
			} else {
				text = "";
			}
		} else {
			text = "Text not found";
		}
		statusBar.setLabel(text);

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
					// UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel");
				} catch (Exception e) {
					e.printStackTrace();
				}
				new BStarEditor().setVisible(true);
			}
		});
	}

	private class SaveAction extends AbstractAction {

		public SaveAction() {
			super("保存");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, c));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (file == null) {
				int retval = chooser.showSaveDialog(textArearu);// 显示“保存文件”对话框
				if (retval == JFileChooser.APPROVE_OPTION) {
					file = chooser.getSelectedFile();
					try {
						if (!file.exists()) {
							file.createNewFile();
						} else {
							statusBar.setLabel("保存失败！文件已经存在！");
							return;
						}
						FileWriter fileWriter = new FileWriter(file);
						fileWriter.write(textArearu.getText());
						fileWriter.close();
						fileRoots.add(file.getPath());
						textAreal.append(file.getName()+"\n");
					} catch (IOException e1) {
						statusBar.setLabel("保存失败");
						e1.printStackTrace();
					}
				}
			} else {
				try {
					if (!file.exists()) {
						file.createNewFile();
					}
					FileWriter fileWriter = new FileWriter(file);
					fileWriter.write(textArearu.getText());
					fileWriter.close();
				} catch (IOException e1) {
					statusBar.setLabel("保存失败");
					e1.printStackTrace();
				}

			}
		}

	}

	private class SaveAsAction extends AbstractAction {

		public SaveAsAction() {
			super("另存为...");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int retval = chooser.showSaveDialog(textArearu);// 显示“保存文件”对话框
			if (retval == JFileChooser.APPROVE_OPTION) {
				file = chooser.getSelectedFile();
				try {
					if (!file.exists()) {
						file.createNewFile();
					} else {
						statusBar.setLabel("保存失败！文件已经存在！");
						return;
					}
					FileWriter fileWriter = new FileWriter(file);
					fileWriter.write(textArearu.getText());
					fileWriter.close();
					fileRoots.add(file.getPath());
					textAreal.append(file.getName()+"\n");
				} catch (IOException e1) {
					statusBar.setLabel("保存失败");
					e1.printStackTrace();
				}
			}
		}

	}

	private class CreateAction extends AbstractAction {

		public CreateAction() {
			super("新建B*文件");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int retval = chooser.showSaveDialog(textArearu);
			if (retval == JFileChooser.APPROVE_OPTION) {
				file = chooser.getSelectedFile();
				try {
					if (!file.exists()) {
						file.createNewFile();
					} else {
						statusBar.setLabel("新建失败！文件已经存在");
						return;
					}
					FileWriter fileWriter = new FileWriter(file);
					fileWriter.write(textArearu.getText());
					fileWriter.close();
					fileRoots.add(file.getPath());
					textAreal.append(file.getName()+"\n");
				} catch (IOException e1) {
					statusBar.setLabel("新建失败");
					e1.printStackTrace();
				}
			}
		}

	}

	private class OpenAction extends AbstractAction {

		public OpenAction() {
			super("从外部导入...");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int retval = chooser.showOpenDialog(textArearu);
			if (retval == JFileChooser.APPROVE_OPTION) {
				file = chooser.getSelectedFile();
				if (file.isDirectory()) {
					for (String simpleFile : file.list()) {
						if (simpleFile.endsWith(".bs")) {
							fileRoots.add(file.getAbsolutePath() + "/"
									+ simpleFile);
							textAreal.append(simpleFile+"\n");
						}
					}
				} else {
					fileRoots.add(file.getAbsolutePath());
					textAreal.append(file.getName()+"\n");
				}
			}
		}

	}

	private class GoToLineAction extends AbstractAction {

		public GoToLineAction() {
			super("跳转到行...");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, c));
		}

		public void actionPerformed(ActionEvent e) {
			if (findDialog.isVisible()) {
				findDialog.setVisible(false);
			}
			if (replaceDialog.isVisible()) {
				replaceDialog.setVisible(false);
			}
			GoToDialog dialog = new GoToDialog(BStarEditor.this);
			dialog.setMaxLineNumberAllowed(textArearu.getLineCount());
			dialog.setVisible(true);
			int line = dialog.getLineNumber();
			if (line > 0) {
				try {
					textArearu.setCaretPosition(textArearu
							.getLineStartOffset(line - 1));
				} catch (BadLocationException ble) { // Never happens
					UIManager.getLookAndFeel().provideErrorFeedback(textArearu);
					ble.printStackTrace();
				}
			}
		}

	}

	//
	// private class LookAndFeelAction extends AbstractAction {
	//
	// private LookAndFeelInfo info;
	//
	// public LookAndFeelAction(LookAndFeelInfo info) {
	// putValue(NAME, info.getName());
	// this.info = info;
	// }
	//
	// public void actionPerformed(ActionEvent e) {
	// try {
	// UIManager.setLookAndFeel(info.getClassName());
	// SwingUtilities.updateComponentTreeUI(BStarEditor2.this);
	// if (findDialog!=null) {
	// findDialog.updateUI();
	// replaceDialog.updateUI();
	// }
	// pack();
	// } catch (RuntimeException re) {
	// throw re; // FindBugs
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// }
	// }
	
	private class CompileFileAction extends AbstractAction {

		public CompileFileAction() {
			super("编译当前文件");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, c));
		}

		public void actionPerformed(ActionEvent e) {
			if (replaceDialog.isVisible()) {
				replaceDialog.setVisible(false);
			}
			findDialog.setVisible(true);
		}

	}
	
	private class CompileAllAction extends AbstractAction {

		public CompileAllAction() {
			super("编译所有文件");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, c));
		}

		public void actionPerformed(ActionEvent e) {
			if(fileRoots.isEmpty()) {
				return;
			}
			int index = textAreal.getCaretLineNumber();
			BStar.baseRoot = fileRoots.get(index).substring(0, fileRoots.get(index).lastIndexOf('/')+1); 
			for(String root : fileRoots) {
				if(root.startsWith(BStar.baseRoot)) {
					BStar bStar = new BStar(root.substring(root.lastIndexOf('/')+1, root.length()));
					// Date dateStart = new Date();
					bStar.compile();
					// Date dateEnd = new Date();
					System.out.println(root.substring(root.lastIndexOf('/')+1, root.length()));
				}
			}
		}

	}

	private class ShowFindDialogAction extends AbstractAction {

		public ShowFindDialogAction() {
			super("查找...");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, c));
		}

		public void actionPerformed(ActionEvent e) {
			if (replaceDialog.isVisible()) {
				replaceDialog.setVisible(false);
			}
			findDialog.setVisible(true);
		}

	}

	private class ShowReplaceDialogAction extends AbstractAction {

		public ShowReplaceDialogAction() {
			super("替换...");
			int c = getToolkit().getMenuShortcutKeyMask();
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, c));
		}

		public void actionPerformed(ActionEvent e) {
			if (findDialog.isVisible()) {
				findDialog.setVisible(false);
			}
			replaceDialog.setVisible(true);
		}

	}

	private static class StatusBar extends JPanel {

		private JLabel label;

		public StatusBar() {
			label = new JLabel("Ready");
			setLayout(new BorderLayout());
			add(label, BorderLayout.LINE_START);
			add(new JLabel(new SizeGripIcon()), BorderLayout.LINE_END);
		}

		public void setLabel(String label) {
			this.label.setText(label);
		}

	}

//	private class FileListBar extends JPanel {
//
//		private JList<String> jList;
//		private DefaultComboBoxModel<String> defaultComboBoxModel = new DefaultComboBoxModel<String>();
//
//		public FileListBar() {
//			jList = new JList<String>();
//			jList.setModel(defaultComboBoxModel);
//			setLayout(new BorderLayout());
//			jList.setLayoutOrientation(JList.VERTICAL);
//			jList.addKeyListener(new KeyListener() {
//
//				@Override
//				public void keyTyped(KeyEvent e) {
//
//				}
//
//				@Override
//				public void keyReleased(KeyEvent e) {
//					if (e.getKeyCode() == KeyEvent.VK_DELETE) {
//						int index = jList.getSelectedIndex();
//						fileRoots.remove(index);
//						if (index == -1) {
//							textArearu.setText("");
//						} else {
//							defaultComboBoxModel.removeElementAt(index);
//							index = jList.getSelectedIndex();
//							readFile(file);
//						}
//					}
//				}
//
//				@Override
//				public void keyPressed(KeyEvent e) {
//
//				}
//			});
//			jList.addListSelectionListener(new ListSelectionListener() {
//
//				@Override
//				public void valueChanged(ListSelectionEvent e) {
//					int index = jList.getSelectedIndex();
//					File file = new File(fileRoots.get(index));
//					if (!file.exists()) {
//						fileRoots.remove(index);
//						defaultComboBoxModel.removeElementAt(index);
//					} else {
//						readFile(file);
//					}
//				}
//			});
//			add(jList, BorderLayout.WEST);
//		}
//
//		public void addFile(String fileName) {
//			defaultComboBoxModel.addElement(fileName);
//		}
//
//		public void setSelectedIndex() {
//			jList.setSelectedIndex(defaultComboBoxModel.getSize() - 1);
//		}
//
//	}

	public void readFile(File file) {
		try {
			if(!file.exists()) {
				int index = textAreal.getCaretLineNumber();
				try {
					fileRoots.remove(index);
					textAreal.replaceRange(null, textAreal.getLineStartOffset(index), textAreal.getLineStartOffset(index+1));
					textArearu.setText("");
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				return;
			}

			String aline;
			FileReader fileReader = new FileReader(file);
			BufferedReader br = new BufferedReader(fileReader);
			StringBuffer sb = new StringBuffer();
			while ((aline = br.readLine()) != null)
				sb.append(aline +"\n");
			fileReader.close();
			br.close();
			textArearu.setText(sb.toString());
		} catch (FileNotFoundException e1) {
			statusBar.setLabel("打开文件失败");
			e1.printStackTrace();
		} catch (IOException e2) {
			statusBar.setLabel("读取文件失败");
			e2.printStackTrace();
		}
	}

}