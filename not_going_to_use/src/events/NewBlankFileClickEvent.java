package events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import starters.MainInterface;

public class NewBlankFileClickEvent implements ActionListener{
	private MainInterface master;
	
	public NewBlankFileClickEvent(MainInterface master) {
		this.master = master;
	}
		

	@Override
	public void actionPerformed(ActionEvent e) {
		master.newCode("Untitled", "", null);
	}

}
