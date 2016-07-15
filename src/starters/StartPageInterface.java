package starters;

import javax.swing.JPanel;

import controller.Compiller;

@SuppressWarnings("unused")
public class StartPageInterface extends JPanel{
	private static final long serialVersionUID = 8600747573973005909L;
	private MainInterface master;
	private Compiller compiller;

	public StartPageInterface(MainInterface master, Compiller compiller) {
		this.master = master;
		this.compiller = compiller;
		
		create();
	}

	private void create() {
		
	}

}
