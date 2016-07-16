package controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import debug.Debug;
import exception.IOErrorException;
import exception.LexicalErrorException;
import lexical.LexicalAnalyzer;
import model.Token;

public class Compiller {
	private File directory;
	private LexicalAnalyzer la;
	
	public Compiller () {
		
	}
	
	public Compiller (String dir) throws IOErrorException {
		setDirectory(dir);
	}

	public void setDirectory(String dir) throws IOErrorException {
		File file = new File(dir);
		if (file.exists() && file.isDirectory())
			this.directory = file;
		else
			throw new IOErrorException("Path is not a directory or directory doesn't exists");
		
		File test = new File(directory.getAbsolutePath() + "\\test.k");
		try {
			test.createNewFile();
			test.delete();
		} catch (IOException e) {
			directory = null;
			throw new IOErrorException("Can't create files in this directory");
		}
		
	}
	
	public void analysisAll() throws IOErrorException {
		if (directory == null)
			throw new IOErrorException("You must set a valid directory before starting");
		
		for (File file : directory.listFiles())
			if (file.isFile() && !file.getName().startsWith("rLex_"))
				completeAnalysis(file);
			
	}
	
	public void completeAnalysis(File arq) {
		Debug.println("Currently Analyzing: " + arq.getName());
		File result = new File(arq.getParentFile() + "\\rLex_" + arq.getName());
		
		//List<Token> allTokens;
		try {
			result.createNewFile();
			startLexical(arq, result);
		} catch (IOErrorException | IOException | LexicalErrorException e) {
			e.printStackTrace();
		}
	}

	public List<Token> startLexical(File arq, File result) throws IOErrorException, IOException, LexicalErrorException {
		la = new LexicalAnalyzer(arq, result);
		la.startAnalysis();
		return la.getAllTokens();
	}
	
	public int getCurrentlyAnalyzedLine() {
		if (la == null)
			return 0;
		return la.current_line;
	}

}
