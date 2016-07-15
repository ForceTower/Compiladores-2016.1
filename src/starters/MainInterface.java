package starters;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import controller.Compiller;
import debug.Debug;
import events.CompileFileClickEvent;
import events.NewBlankFileClickEvent;
import events.OpenFileClickEvent;
import events.SaveFileClickEvent;
import exception.IOErrorException;

public class MainInterface extends JFrame {
	private static final long serialVersionUID = -1131091556933796536L;
	private HashMap<File, Thread> runningTasks;
	protected JPanel center;
	public CenterInterface centerInterface;
	protected StartPageInterface startPage;
	protected CardLayout cardLayout;
	private Compiller compiller;
	
	public MainInterface() throws IOErrorException {
		compiller = new Compiller();
		runningTasks = new HashMap<>();
		compiller.setDirectory(".\\");
		setTitle("C-Arah");
		setSize(1000, 660);
		setResizable(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		lookAndFeel("Windows");
		
		configureJMenu();
		mountInterface();
		
		setVisible(true);
	}

	private void configureJMenu() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu file = new JMenu("File");
		
		JMenuItem newf = new JMenuItem("New Blank File");
		JMenuItem open = new JMenuItem("Open");
		JMenuItem save = new JMenuItem("Save");
		JMenuItem saAs = new JMenuItem("Save As");
		JMenuItem comp = new JMenuItem("Compile");
		
		newf.addActionListener(new NewBlankFileClickEvent(this));
		open.addActionListener(new OpenFileClickEvent(this));
		save.addActionListener(new SaveFileClickEvent(this, false));
		saAs.addActionListener(new SaveFileClickEvent(this, true));
		comp.addActionListener(new CompileFileClickEvent(this));
		
		file.add(newf);
		file.add(open);
		file.add(save);
		file.add(saAs);
		file.add(comp);
		
		menuBar.add(file);
		
		setJMenuBar(menuBar);
	}
	
	private void mountInterface() {
		setLayout(new BorderLayout());
		
		cardLayout = new CardLayout();
		
		centerInterface = new CenterInterface(this, compiller);
		startPage = new StartPageInterface(this, compiller);
		centerInterface.add("Start Page", startPage);
		
		center = new JPanel(cardLayout);
		center.add(centerInterface, "Online");
		
		add(center, BorderLayout.CENTER);
	}
	
	public CodeEditInterface newCode(String title, String code_text, File arq) {
		String name = title;
		CodeEditInterface code = new CodeEditInterface(this, compiller, code_text, arq);
		code.setName(name);
		centerInterface.add(name, code);
		code.create();
		return code;
	}
	
	public void compile(File source_file) {
		Debug.println("Prepare to Compile: " + source_file.getName());
		
		Thread t = new Thread() {
			public void run() {
				Compiller task = new Compiller();
				task.completeAnalysis(source_file);
				try {
					JDialog k = new LexResultInterface(new File(source_file.getParentFile() + "\\rLex_" + source_file.getName()));
					k.setVisible(true);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(MainInterface.this, "File Compilled, but can't open lex results file");
					e.printStackTrace();
				}
			}
		};
		runningTasks.put(source_file, t);
		t.start();
		
	}
	
	public void lookAndFeel(String look) {
		try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (look.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainInterface.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MainInterface.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
        	Logger.getLogger(MainInterface.class.getName()).log(Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MainInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
	}

	public static void main(String[] args) {
		try {
			new MainInterface();
		} catch (IOErrorException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

}
