package view_event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import view_gui.CodeEditInterface;

public class CreateNewFileEventClick implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		new CodeEditInterface("", null);
	}

}