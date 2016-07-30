package view_gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import view_event.CreateNewFileEventClick;
import view_event.SelectFileEventClick;
import view_event.SelectFolderEventClick;

public class MainWindowGUI extends JFrame{
	private static final long serialVersionUID = -3739008754324139579L;
	public static MainWindowGUI INSTANCE;
	
	public MainWindowGUI() {
		super();
		INSTANCE = this;
		lookAndFeel("Windows");
		setTitle("C-Arah");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
		setSize(500, 300);
		create();
		
		setVisible(true);
	}
	
	private void create() {
		setLayout(new BorderLayout());
		
		JPanel west = new JPanel();
		JPanel east = new JPanel();
		
		east.setPreferredSize(new Dimension(300, 300));
		west.setPreferredSize(new Dimension(200, 300));
		
		Dimension btn_dimension = new Dimension(140, 30);
		
		JButton select_file = new JButton("Select a File");
		JButton select_dirc = new JButton("Select a Directory");
		JButton create_file = new JButton("Create a New File");
		
		select_file.setPreferredSize(btn_dimension);
		select_dirc.setPreferredSize(btn_dimension);
		create_file.setPreferredSize(btn_dimension);
		
		select_file.addActionListener(new SelectFileEventClick());
		select_dirc.addActionListener(new SelectFolderEventClick());
		create_file.addActionListener(new CreateNewFileEventClick());
		
		JPanel a = new JPanel(new FlowLayout());
		a.add(select_file);
		
		JPanel b = new JPanel(new FlowLayout());
		b.add(select_dirc);
		
		JPanel c = new JPanel(new FlowLayout());
		c.add(create_file);
		
		west.setLayout(new GridLayout(7, 1)); //Todo dia um 7 a 1 diferente
		west.add(new JLabel());
		west.add(a);
		west.add(new JLabel());
		west.add(b);
		west.add(new JLabel());
		west.add(c);
		west.add(new JLabel());
		
		add(east, BorderLayout.EAST);
		add(west, BorderLayout.WEST);
	}
	
	private void lookAndFeel(String look) {
		try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (look.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainWindowGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MainWindowGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MainWindowGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MainWindowGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
	}

	public static void main(String[] args) {
		new MainWindowGUI();
	}

}
