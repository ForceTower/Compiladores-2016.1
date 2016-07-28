package view_event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import view.Window;

public class SelectFolderEventClick implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser f_chooser = new JFileChooser();
		f_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		f_chooser.setDialogTitle("Select a Folder");
		int ret_val = f_chooser.showOpenDialog(Window.INSTANCE);
		
		if (ret_val == JFileChooser.APPROVE_OPTION) {
			for (File file : f_chooser.getSelectedFile().listFiles())
				if (file.isFile() && !file.getName().startsWith("rLex_"))
					file_analysis(file);
			
		}
	}

	private void file_analysis(File arq) {
		File result = new File(arq.getParentFile() + "\\rLex_" + arq.getName());
		if (!result.exists()) {
			try {
				result.createNewFile();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(Window.INSTANCE, "Can't create the result file: " + result.getName() + "\nMessage: " + ex.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		SelectFileEventClick.lex(arq, result);
	}

}