package events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import starters.MainInterface;

public class OpenFileClickEvent implements ActionListener{
	
	private MainInterface master;
	
	public OpenFileClickEvent(MainInterface master) {
		this.master = master;
	}
		

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		int returnVal = chooser.showOpenDialog(master);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			StringBuilder sb = new StringBuilder();
			try {
				BufferedReader br = new BufferedReader(new FileReader(chooser.getSelectedFile()));
				
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
					sb.append("\n");
				}
				br.close();
				
				master.newCode(chooser.getSelectedFile().getName(), sb.toString(), chooser.getSelectedFile());
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(master, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
			
		}
		
	}

}
