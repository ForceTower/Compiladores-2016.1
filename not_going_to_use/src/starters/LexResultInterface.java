package starters;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;

public class LexResultInterface extends JDialog{
	private static final long serialVersionUID = 266948859869516467L;
	private File resultFile;
	
	public LexResultInterface(File resultFile) throws IOException {
		this.resultFile = resultFile;
		create();
	}

	private void create() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(resultFile));
		StringBuilder sb = new StringBuilder();
		
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
			sb.append("\n");
		}
		br.close();
		
		setLayout(new BorderLayout());
		setTitle("Lexical Result: " + resultFile.getName().substring(5));
		setSize(600, 500);
		
		StyledDocument doc = new DefaultStyledDocument();
		JTextPane text = new JTextPane(doc);
		//((AbstractDocument) text.getDocument()).setDocumentFilter(new DocFilterLexResults(text));
		
		text.setEditable(false);
		text.setText(sb.toString());
		
		add(new JScrollPane(text), BorderLayout.CENTER);
	}

}
