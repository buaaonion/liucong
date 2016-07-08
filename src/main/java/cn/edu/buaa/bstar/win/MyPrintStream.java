package cn.edu.buaa.bstar.win;

import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.SwingUtilities;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public class MyPrintStream extends PrintStream {

	private RSyntaxTextArea textArea;
	private StringBuffer sb = new StringBuffer();

	public MyPrintStream(OutputStream out, RSyntaxTextArea textArea) {
		super(out);
		this.textArea = textArea;
	}

	/**
	 * 在这里重截,所有的打印方法都要调用的方法
	 */
	public void write(byte[] buf, int off, int len) {
		final String message = new String(buf, off, len);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				sb.append(message + "\n");
				textArea.setText(sb.toString());
			}
		});
	}
}