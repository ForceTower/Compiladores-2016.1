package starters;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import model.CThread;

public class MainInterface extends JFrame {
	private static final long serialVersionUID = -1131091556933796536L;
	private HashMap<File, CThread> runningTasks;
	protected JPanel center;
	public CenterInterface centerInterface;
	protected StartPageInterface startPage;
	protected CardLayout cardLayout;
	protected TaskManagerInterface taskmanager;
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
		taskmanager = new TaskManagerInterface(this);
		startPage = new StartPageInterface(this, compiller, taskmanager);
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
	
	public synchronized void compile(File source_file) {
		Debug.println("Prepare to Compile: " + source_file.getName());
		
		CThread t = new CThread() {
			Compiller task;
			private int numlines;
			
			public void run() {
				try {
					BufferedReader br = new BufferedReader(new FileReader(source_file));
					while (br.readLine() != null)
						numlines++;
					br.close();
					
					task = new Compiller();
					task.completeAnalysis(source_file);
				
					JDialog k = new LexResultInterface(new File(source_file.getParentFile() + "\\rLex_" + source_file.getName()));
					k.setLocationRelativeTo(null);
					k.setVisible(true);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(MainInterface.this, "File Compilled, but can't open lex results file");
					e.printStackTrace();
				} finally {
					MainInterface.this.taskComplete(source_file);
				}
				
			}

			@Override
			public double getProgress() {
				if (task == null)
					return 0;
				return Double.parseDouble(task.getCurrentlyAnalyzedLine() + "")/numlines*100;
			}
		};
		MainInterface.this.taskStarted(source_file, t);
		t.start();
		
	}

	public void compileFolder(File folder) {
		CThread t = new CThread() {
			private int numfiles;
			private int allfiles;
			
			public void run() {
				//Compiller task = new Compiller();
				try {
					for (File f : folder.listFiles())
						if (f.isFile())
							allfiles++;
					//task.setDirectory(folder.getAbsolutePath());
					//task.analysisAll();
					
					for (File f : folder.listFiles())
						if (f.isFile() && !f.getName().startsWith("rLex_")) {
							numfiles++;
							compile(f);
							//task.completeAnalysis(f);
							//JDialog k = new LexResultInterface(new File(f.getParentFile() + "\\rLex_" + f.getName()));
							//k.setLocationRelativeTo(null);
							//k.setVisible(true);
						}
					
					
				} /*catch (IOErrorException e) {
					JOptionPane.showMessageDialog(MainInterface.this, e.getMessage(), "Invalid Directory", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(MainInterface.this, "Files Compilled, but can't open lex results file");
					e.printStackTrace();
				}*/ finally {
					taskComplete(folder);
				}
			}

			@Override
			public double getProgress() {
				if (allfiles == 0)
					return 0;
				return Double.parseDouble(numfiles + "")/allfiles*100;
			}
		};
		
		taskStarted(folder, t);
		t.start();
	}
	
	protected void taskStarted(File source_file, CThread t) {
		runningTasks.put(source_file, t);
		taskmanager.create();
	}

	public synchronized void taskComplete(File source_file) {
		runningTasks.remove(source_file);
		taskmanager.create();
	}
	
	public HashMap<File, CThread> getRunningTasks() {
		return runningTasks;
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
