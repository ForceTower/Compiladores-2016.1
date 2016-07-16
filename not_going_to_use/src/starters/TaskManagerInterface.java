package starters;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import events.CancelTaskEventClick;

public class TaskManagerInterface extends JPanel implements ActionListener{
	private static final long serialVersionUID = -2585325919740432428L;
	private MainInterface master;
	private Timer timer;
	private JPanel generalPart;

	public TaskManagerInterface(MainInterface master) {
		this.master = master;
		timer = new Timer(1000, this);
		
		create();
		timer.start();
	}

	public void create() {
		int lines = master.getRunningTasks().size();
		setVisible(false);
		removeAll();
		setLayout(new BorderLayout());
		
		generalPart = new JPanel(new GridLayout(lines, 1));	
		JScrollPane panelScroll = new JScrollPane();
		JScrollBar scrollBar = new JScrollBar();
		
		panelScroll.setVerticalScrollBar(scrollBar);
		//panelScroll.setSize(sizeX, sizeY);
		panelScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		panelScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		panelScroll.getViewport().add(generalPart);
		Set<File> files = master.getRunningTasks().keySet();
		
		synchronized (files) {
			for (File file : files) {
				JPanel btnPanel = new JPanel(new FlowLayout());
				btnPanel.setName(file.getName());
				
				JButton button = new JButton(file.getName());
				button.setName(file.getName());
				button.setToolTipText("Progress: " + Math.ceil(master.getRunningTasks().get(file).getProgress()) + "% complete");
				button.addActionListener(new CancelTaskEventClick(master, file));
				//btnPanel.add(button);
				generalPart.add(button);
			}
		}
		
		/*
		for(int i = 0; i < lines; i++){
			Set actual = master.getRunningTasks()..get(i);
			
			JPanel btnPanel = new JPanel(new FlowLayout());
			
			
			JButton button = new JButton(actual.toString());
			
			button.addActionListener(new ContactClickEvent(actual, father, control));
			
			btnPanel.add(button);
			generalPart.add(btnPanel);
		}*/
		
		add(panelScroll, BorderLayout.CENTER);
		add(scrollBar, BorderLayout.EAST);
		
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (Component t : generalPart.getComponents()) {
			JButton b = (JButton)t;
			Set<File> files = master.getRunningTasks().keySet();
			for (File k : files) {
				if (k.getName().equals(b.getName())) {
					b.setVisible(false);
					b.setToolTipText("Progress: " + Math.ceil(master.getRunningTasks().get(k).getProgress()) + "% complete");
					b.setVisible(true);
					break;
				}
			}
		}
		//create();
	}
}
