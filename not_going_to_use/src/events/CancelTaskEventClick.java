package events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JOptionPane;

import model.CThread;
import starters.MainInterface;

public class CancelTaskEventClick implements ActionListener {
	private MainInterface master;
	private File file;
	
	public CancelTaskEventClick(MainInterface master, File file) {
		this.master = master;
		this.file = file;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		CThread ct = master.getRunningTasks().get(file);
		master.taskComplete(file);
		ct.interrupt();
		JOptionPane.showMessageDialog(master, "Task Interrupted", "Success", JOptionPane.INFORMATION_MESSAGE);
	}

}
