package view_event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import controller.Compiller;
import exception.IOErrorException;
import exception.LexicalErrorException;
import view_gui.LexResultInterface;
import view_gui.MainWindowGUI;

public class SelectFileEventClick implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser f_chooser = new JFileChooser();
		f_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		f_chooser.setDialogTitle("Select a File");
		int ret_val = f_chooser.showOpenDialog(MainWindowGUI.INSTANCE);
		
		if (ret_val == JFileChooser.APPROVE_OPTION) {
			File arq = f_chooser.getSelectedFile();
			File result = new File(arq.getParentFile() + "\\rLex_" + arq.getName());
			if (!result.exists()) {
				try {
					result.createNewFile();
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(MainWindowGUI.INSTANCE, "Can't create the result file: " + result.getName() + "\nMessage: " + ex.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			lex(arq, result);
		}
	}
	
	public static void lex(File arq, File result) {
		new Thread() {	
			public void run() {
				Compiller compiler = new Compiller();
				try {
					compiler.startLexical(arq, result);
					new LexResultInterface(result);
				} catch (IOErrorException | IOException | LexicalErrorException e) {
					JOptionPane.showMessageDialog(MainWindowGUI.INSTANCE, "Error in file: " + arq.getName() + "\nMessage: " + e.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		}.start();
	}

}
