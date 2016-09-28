package controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import exception.IOErrorException;
import exception.LexicalErrorException;
import lexical.LexicalAnalyzer;
import model.Token;
import syntax.SyntaxAnalizer;

public class Compiller {
	private File directory;
	private LexicalAnalyzer lexicalAnalyzer;
	private SyntaxAnalizer syntacticAnalyzer;
	
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
			if (file.isFile() && (!file.getName().startsWith("rLex_") && !file.getName().startsWith("rSin_")) && !file.getName().endsWith(".jar"))
				completeAnalysis(file);
			
	}
	
	public void completeAnalysis(File arq) {
		System.out.println("Currently Analyzing: " + arq.getName());
		File folderLex = new File(arq.getParentFile() + "\\Saida_Lexico");
		File folderSyn = new File(arq.getParentFile() + "\\Saida_Sintatico");
		folderLex.mkdirs();
		folderSyn.mkdirs();
		
		File resultLex = new File(folderLex.getPath() + "\\rLex_" + arq.getName());
		File resultSyn = new File(folderSyn.getPath() + "\\rSin_" + arq.getName());
		
		//List<Token> allTokens;
		try {
			resultLex.createNewFile();
			resultSyn.createNewFile();
			startLexical(arq, resultLex);
			startSyntactic(lexicalAnalyzer.allValidTokens, resultSyn);
		} catch (IOErrorException | IOException | LexicalErrorException e) {
			e.printStackTrace();
		}
	}

	public List<Token> startLexical(File arq, File result) throws IOErrorException, IOException, LexicalErrorException {
		lexicalAnalyzer = new LexicalAnalyzer(arq, result);
		lexicalAnalyzer.startAnalysis();
		return lexicalAnalyzer.getAllTokens();
	}
	
	public void startSyntactic(List<Token> allValidTokens, File result) throws IOException {
		if (lexicalAnalyzer.lexicalErrors == 0) {
			syntacticAnalyzer = new SyntaxAnalizer(allValidTokens, result);
			syntacticAnalyzer.startAnalysis();
		} else {
			System.out.println("Has lex erros, fix lex errors first!");
		}
	}

}
