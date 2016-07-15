package starters;

import java.awt.BorderLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import controller.Compiller;

@SuppressWarnings("unused")
public class CodeEditInterface extends JPanel {
	private static final long serialVersionUID = -7544699336817273461L;
	private MainInterface master;
	private JTextPane text;
	private Compiller compiller;
	private String code_text;
	private File saving_file;
	
	public CodeEditInterface(MainInterface master, Compiller compiller, String code_text, File saving_file) {
		this.master = master;
		this.compiller = compiller;
		this.code_text = code_text;
		this.saving_file = saving_file;
		this.text = new JTextPane();
	}

	public void create() {
		setLayout(new BorderLayout());
		
		master.centerInterface.setTabComponentAt(master.centerInterface.indexOfComponent(this), new ButtonTabComponent(master.centerInterface));
		
		text.setText(code_text);
		
		add(new JScrollPane(text), BorderLayout.CENTER);
		
		master.centerInterface.setSelectedComponent(this);
	}

	public void saveFile(boolean force_new_save) throws IOException {
		if (saving_file == null || force_new_save) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int sel = chooser.showSaveDialog(master);
			if (sel == JFileChooser.APPROVE_OPTION) {
				saving_file = chooser.getSelectedFile();
				this.setName(saving_file.getName());
			} else {
				return;
			}
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(saving_file));
		bw.write(text.getText());
		bw.flush();
		bw.close();
	}

	public File getSourceFile() {
		return saving_file;
	}

}
