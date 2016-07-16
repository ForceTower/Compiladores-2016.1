package starters;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controller.Compiller;
import events.CompileDirectoryClickEvent;
import events.NewBlankFileClickEvent;

@SuppressWarnings("unused")
public class StartPageInterface extends JPanel{
	private static final long serialVersionUID = 8600747573973005909L;
	private MainInterface master;
	private Compiller compiller;
	private TaskManagerInterface taskmanager;

	public StartPageInterface(MainInterface master, Compiller compiller, TaskManagerInterface taskmanager) {
		this.master = master;
		this.compiller = compiller;
		this.taskmanager = taskmanager;
		
		create();
	}

	private void create() {
		setLayout(new BorderLayout());
		
		ImageIcon icon = new ImageIcon("res/img/logo.png");
		icon = new ImageIcon(icon.getImage().getScaledInstance(600, 230, 100));
		
		JPanel top_logo = new JPanel();
		top_logo.setPreferredSize(new Dimension(600, 230));
		JLabel logo_label = new JLabel(icon);
		top_logo.add(logo_label);
		
		JPanel botton_buttons = new JPanel(new GridLayout(1, 2));
		botton_buttons.setPreferredSize(new Dimension(220, 115));
		
		JPanel first = new JPanel(new GridLayout(3, 1));
		JPanel f1 = new JPanel();
		JPanel createNewFile = new JPanel();
		JButton createFile_btn = new JButton("New Empty File");
		createFile_btn.setToolTipText("Creates a new empty file to edit a text");
		createNewFile.add(createFile_btn);
		createFile_btn.addActionListener(new NewBlankFileClickEvent(master));
		JPanel f2 = new JPanel();
		
		first.add(f1);
		first.add(createNewFile);
		first.add(f2);
		botton_buttons.add(first);
		
		JPanel second = new JPanel(new GridLayout(3, 1));
		JPanel f3 = new JPanel();
		JPanel openfolder = new JPanel();
		JButton openfolder_btn = new JButton("Open Directory And Compile");
		openfolder_btn.setToolTipText("Compiles every single file within the selected directory. Subfolders are not included");
		openfolder_btn.addActionListener(new CompileDirectoryClickEvent(master));
		openfolder.add(openfolder_btn);
		JPanel f4 = new JPanel();
		second.add(f3);
		second.add(openfolder);
		second.add(f4);
		botton_buttons.add(second);
		
		JPanel west = new JPanel(new BorderLayout());
		west.add(top_logo, BorderLayout.NORTH);
		west.add(botton_buttons, BorderLayout.SOUTH);
		add(west, BorderLayout.WEST);
		
		taskmanager.setPreferredSize(new Dimension(300, 600));
		add(taskmanager, BorderLayout.EAST);
		
	}

}
