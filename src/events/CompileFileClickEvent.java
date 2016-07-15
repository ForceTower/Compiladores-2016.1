package events;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import starters.CodeEditInterface;
import starters.MainInterface;

public class CompileFileClickEvent implements ActionListener{
	private MainInterface master;
	
	public CompileFileClickEvent(MainInterface master) {
		this.master = master;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Component c = master.centerInterface.getSelectedComponent();
		
		if (c instanceof CodeEditInterface) {
			CodeEditInterface code = (CodeEditInterface)c;
			try {
				code.saveFile(false);
				File source_file = code.getSourceFile();
				master.compile(source_file);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(master, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}
}
