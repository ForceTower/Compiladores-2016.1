package controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import exception.IOErrorException;
import exception.LexicalErrorException;
import lexical.LexicalAnalyzer;
import model.Token;
import semanthic.SemanthicAnalyzer;
import syntax.SyntaxAnalizer;
import syntax_tree.AbstractSyntaxTree;

public class Compiller {
	private File directory;
	private LexicalAnalyzer lexicalAnalyzer;
	private SyntaxAnalizer syntacticAnalyzer;
	private SemanthicAnalyzer semanthicAnalyzer;
	
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
		File folderSem = new File(arq.getParentFile() + "\\Saida_Semantico");
		
		folderLex.mkdirs();
		folderSyn.mkdirs();
		folderSem.mkdirs();
		
		File resultLex = new File(folderLex.getPath() + "\\rLex_" + arq.getName());
		File resultSyn = new File(folderSyn.getPath() + "\\rSin_" + arq.getName());
		File resultSem = new File(folderSem.getPath() + "\\rSem_" + arq.getName());
		
		try {
			resultLex.createNewFile();
			resultSyn.createNewFile();
			resultSem.createNewFile();
			
			startLexical(arq, resultLex);
			startSyntactic(lexicalAnalyzer.allValidTokens, resultSyn);
			if (syntacticAnalyzer != null) startSemanthic(syntacticAnalyzer.getAST(), resultSem);
		} catch (IOErrorException | IOException | LexicalErrorException e) {
			e.printStackTrace();
		}
	}

	public List<Token> startLexical(File arq, File result) throws IOErrorException, IOException, LexicalErrorException {
		lexicalAnalyzer = new LexicalAnalyzer(arq, result);
		lexicalAnalyzer.startAnalysis();
		return lexicalAnalyzer.getAllTokens();
	}
	
	public AbstractSyntaxTree startSyntactic(List<Token> allValidTokens, File result) throws IOException {
		if (lexicalAnalyzer.lexicalErrors == 0) {
			syntacticAnalyzer = new SyntaxAnalizer(allValidTokens, result);
			return syntacticAnalyzer.startAnalysis();
		} else {
			System.out.println("Has lex errors, fix lex errors first!");
			return null;
		}
	}
	
	public void startSemanthic(AbstractSyntaxTree ast, File result) {
		if (syntacticAnalyzer.syntaxErrors == 0) {
			semanthicAnalyzer = new SemanthicAnalyzer(ast, result);
			semanthicAnalyzer.startAnalysis();
		} else {
			System.out.println("Has syntax errors, fix syntax errors first");
		}
		
	}

}
