package view_event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import view_gui.CodeEditInterface;
import view_gui.MainWindowGUI;

public class CompilleEventClick implements ActionListener{
	private CodeEditInterface codeedit;
	
	public CompilleEventClick(CodeEditInterface codeedit) {
		this.codeedit = codeedit;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		codeedit.saveFile(false);
		File arq = codeedit.getSourceFile();
		if (arq != null) {
			File result = new File(arq.getParentFile() + "\\rLex_" + arq.getName());
			if (!result.exists()) {
				try {
					result.createNewFile();
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(MainWindowGUI.INSTANCE, "Can't create the result file: " + result.getName() + "\nMessage: " + ex.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}	
			SelectFileEventClick.lex(arq, result);
		}
	}

}