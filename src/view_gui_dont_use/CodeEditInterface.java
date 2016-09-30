package view_gui_dont_use;

import java.awt.BorderLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import view_event_dont_use.CompilleEventClick;
import view_event_dont_use.SaveFileEventClick;

public class CodeEditInterface extends JDialog {
	private static final long serialVersionUID = -7544699336817273461L;
	private JTextPane text;
	private String code_text;
	private File saving_file;

	public CodeEditInterface(String code_text, File saving_file) {
		this.code_text = code_text;
		this.saving_file = saving_file;
		this.text = new JTextPane();
		create();
		setVisible(true);
	}

	public void create() {
		setLayout(new BorderLayout());
		setSize(800, 550);
		setLocationRelativeTo(null);
		setTitle("Untitled.fpp");
		text.setText(code_text);
		add(new JScrollPane(text), BorderLayout.CENTER);
		
		JMenuBar jmenubar = new JMenuBar();
		
		JMenu jmenu = new JMenu("File");
		JMenuItem jmenuitem_save = new JMenuItem("Save");
		JMenuItem jmenuitem_saveas = new JMenuItem("Save As");;
		JMenuItem jmenuitem_compille = new JMenuItem("Compille");
		
		jmenuitem_save.addActionListener(new SaveFileEventClick(this, false));
		jmenuitem_saveas.addActionListener(new SaveFileEventClick(this, true));
		jmenuitem_compille.addActionListener(new CompilleEventClick(this));
		
		jmenu.add(jmenuitem_compille);
		jmenu.add(jmenuitem_save);
		jmenu.add(jmenuitem_saveas);
		
		jmenubar.add(jmenu);
		
		setJMenuBar(jmenubar);
	}

	public void saveFile(boolean force_new_save) {
		if (saving_file == null || force_new_save) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setDialogTitle("Save File");
			int sel = chooser.showSaveDialog(MainWindowGUI.INSTANCE);
			if (sel == JFileChooser.APPROVE_OPTION)
				saving_file = chooser.getSelectedFile();
			else
				return;
		}

		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(saving_file));
			bw.write(text.getText());
			bw.flush();
			bw.close();
			this.setName(saving_file.getName());
			this.setTitle(saving_file.getName());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Can't save the file: " + e.getMessage(), "Save error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			saving_file = null;
		}
	}

	public File getSourceFile() {
		return saving_file;
	}
}