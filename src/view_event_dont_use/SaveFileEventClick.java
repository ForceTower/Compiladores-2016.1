package view_event_dont_use;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import view_gui_dont_use.CodeEditInterface;

public class SaveFileEventClick implements ActionListener{
	private CodeEditInterface codeedit;
	private boolean force_new_save;
	
	public SaveFileEventClick(CodeEditInterface codeedit, boolean force_new_save) {
		this.codeedit = codeedit;
		this.force_new_save = force_new_save;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		codeedit.saveFile(force_new_save);
	}

}