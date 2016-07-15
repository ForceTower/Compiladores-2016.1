package events;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;

import starters.CodeEditInterface;
import starters.MainInterface;

public class SaveFileClickEvent implements ActionListener{
	private MainInterface master;
	private boolean force_new_save;
	
	public SaveFileClickEvent(MainInterface master, boolean b) {
		this.master = master;
		force_new_save = b;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Component c = master.centerInterface.getSelectedComponent();
		
		if (c instanceof CodeEditInterface) {
			CodeEditInterface code = (CodeEditInterface)c;
			try {
				code.saveFile(force_new_save);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(master, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
	}
}
