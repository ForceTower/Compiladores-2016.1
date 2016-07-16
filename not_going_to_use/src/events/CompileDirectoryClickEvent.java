package events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

import starters.MainInterface;

public class CompileDirectoryClickEvent implements ActionListener{
	private MainInterface master;
	
	public CompileDirectoryClickEvent(MainInterface master) {
		this.master = master;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int val = chooser.showOpenDialog(master);
		if (val == JFileChooser.APPROVE_OPTION) {
			master.compileFolder(chooser.getSelectedFile());
		}
	}
}
