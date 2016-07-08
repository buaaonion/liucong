package cn.edu.buaa.bstar.win;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

public class BStarEditor3 extends JFrame {

	private static final long serialVersionUID = 1L;

	private RSyntaxTextArea textArea;

	public BStarEditor3() {

		JPanel cp = new JPanel(new BorderLayout());

		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory
				.getDefaultInstance();
		atmf.putMapping("text/BStar",
				"cn.edu.buaa.bstar.win.BStarTokenMaker");

		textArea = new RSyntaxTextArea(50, 100);
//		textArea.setSyntaxEditingStyle("text/BStar");
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
		textArea.setCodeFoldingEnabled(true);
		textArea.setTabSize(4);
		RTextScrollPane sp = new RTextScrollPane(textArea);
		cp.add(sp);
		
		CompletionProvider provider = createCompletionProvider();
		
		AutoCompletion ac = new AutoCompletion(provider);
		ac.setTriggerKey(KeyStroke.getKeyStroke(KeyEvent.VK_M , InputEvent.CTRL_MASK, true));
		ac.install(textArea);
		
		setContentPane(cp);
		setTitle("B*编辑器");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		// Start all Swing applications on the EDT.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new BStarEditor3().setVisible(true);
			}
		});
	}

	/**
	    * Create a simple provider that adds some Java-related completions.
	    */
	   private CompletionProvider createCompletionProvider() {

	      // A DefaultCompletionProvider is the simplest concrete implementation
	      // of CompletionProvider. This provider has no understanding of
	      // language semantics. It simply checks the text entered up to the
	      // caret position for a match against known completions. This is all
	      // that is needed in the majority of cases.
	      DefaultCompletionProvider provider = new DefaultCompletionProvider();

	      // Add completions for all Java keywords. A BasicCompletion is just
	      // a straightforward word completion.
	      provider.addCompletion(new BasicCompletion(provider, "MACHINE"));
	      provider.addCompletion(new BasicCompletion(provider, "OPERATIONS"));
	      provider.addCompletion(new BasicCompletion(provider, "ATTRIBUTES"));
	      provider.addCompletion(new BasicCompletion(provider, "END"));
	      provider.addCompletion(new BasicCompletion(provider, "INCLUDE"));
	      provider.addCompletion(new BasicCompletion(provider, "#define"));
	      provider.addCompletion(new BasicCompletion(provider, "typedef"));
	      provider.addCompletion(new BasicCompletion(provider, "struct"));
	      provider.addCompletion(new BasicCompletion(provider, "const"));
	      provider.addCompletion(new BasicCompletion(provider, "enum"));
	      provider.addCompletion(new BasicCompletion(provider, "return"));
	      provider.addCompletion(new BasicCompletion(provider, "while"));
	      provider.addCompletion(new BasicCompletion(provider, "if"));
	      provider.addCompletion(new BasicCompletion(provider, "sizeof"));
	      provider.addCompletion(new BasicCompletion(provider, "else"));
	      provider.addCompletion(new BasicCompletion(provider, "nil"));
	      provider.addCompletion(new BasicCompletion(provider, "true"));
	      provider.addCompletion(new BasicCompletion(provider, "false"));
	      
	      provider.addCompletion(new BasicCompletion(provider, "int"));
	      provider.addCompletion(new BasicCompletion(provider, "double"));
	      provider.addCompletion(new BasicCompletion(provider, "float"));
	      provider.addCompletion(new BasicCompletion(provider, "unsigned"));
	      provider.addCompletion(new BasicCompletion(provider, "long"));
	      provider.addCompletion(new BasicCompletion(provider, "void"));
	      provider.addCompletion(new BasicCompletion(provider, "short"));
	      provider.addCompletion(new BasicCompletion(provider, "char"));
	      provider.addCompletion(new BasicCompletion(provider, "proposition"));
	      provider.addCompletion(new BasicCompletion(provider, "set"));
	      
	      provider.addCompletion(new BasicCompletion(provider, "/-\\"));
	      provider.addCompletion(new BasicCompletion(provider, "\\-/"));

	      // Add a couple of "shorthand" completions. These completions don't
	      // require the input text to be the same thing as the replacement text.
//	      provider.addCompletion(new ShorthandCompletion(provider, "sysout",
//	            "System.out.println(", "System.out.println("));
//	      provider.addCompletion(new ShorthandCompletion(provider, "syserr",
//	            "System.err.println(", "System.err.println("));

	      return provider;

	   }

}