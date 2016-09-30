package view_event_dont_use;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import view_gui_dont_use.CodeEditInterface;

public class CreateNewFileEventClick implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		new CodeEditInterface("", null);
	}

}