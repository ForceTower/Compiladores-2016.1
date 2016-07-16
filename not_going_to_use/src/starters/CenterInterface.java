package starters;

import javax.swing.JTabbedPane;

import controller.Compiller;

@SuppressWarnings("unused")
public class CenterInterface extends JTabbedPane{
	private static final long serialVersionUID = -3890904839758834253L;
	private MainInterface master;
	private Compiller control;
	
	public CenterInterface(MainInterface master, Compiller control) {
		this.master = master;
		this.control = control;
		
		create();
	}

	private void create() {
		
	}

}
