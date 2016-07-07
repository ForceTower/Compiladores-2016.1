package lexical;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import debug.Debug;
import exception.IOErrorException;
import exception.LexicalErrorException;
import model.Token;
import model.TokenFactory;

public class LexicalAnalyzer {
	private File file;
	private BufferedReader reader;
	private List<Token> allTokens;

	public LexicalAnalyzer(File file) throws IOErrorException, FileNotFoundException {
		if (file.canRead())
			this.file = file;
		else
			throw new IOErrorException("The file is unreadble");
		
		createStreams();
		allTokens = new ArrayList<>();
	}

	private void createStreams() throws FileNotFoundException {
		reader = new BufferedReader(new FileReader(file));
	}

	public void startAnalysis() throws IOException, LexicalErrorException {
		int currentLine = 0;
		String line;
		Token hold = null;
		
		while ((line = reader.readLine()) != null) { //Reads an entire line
			currentLine++;
			Character c;
			int position = 0;
			int lineLen = line.length();
			boolean readingString = false;
			Token temp;
			String str = "";
			boolean validToken = false;
			
			while (position < lineLen) { //Prepare to read every single char of the line and generate tokens
				c = line.charAt(position);
				str = str + c;
				position++;
				//Handles the line comments
				if ( (c == '/') && ((position) < lineLen) && (line.charAt(position) == '/') && !readingString) {
					if (validToken) {
						allTokens.add(hold);
						Debug.println("Created token with Lexem: " + hold.getLexem());
						validToken = false;
					}
					position = lineLen; //This will break the line loop;
					str = "";
					Debug.println("Line Comment at line: " + currentLine + " starting at: " + position);
					continue;
				} 
				//Handles the block comments
				else if ( (c == '/') && ((position) < lineLen) && (line.charAt(position) == '*') && !readingString) {
					if (validToken) {
						allTokens.add(hold);
						Debug.println("Created token with Lexem: " + hold.getLexem()); //This way, your'll make the dream
						validToken = false;
					}
					
					Debug.println("Block Comment at line: " + currentLine + " starting at: " + position);
					String substr = str.substring(position);
					if (substr.contains("*/")) {
						position = substr.indexOf("*/") + 2;
						Debug.println("-> Block Comment finished at line: " + currentLine + " at position: " + position);
						continue;
					} else {
						while (true) {
							line = reader.readLine();
							currentLine++;
							
							if (line == null) { //If we reached the EOF but the block comment didn't had an end we throw a Lexical Error
								Debug.println("-> Block Commentary doesn't have an end");
								throw new LexicalErrorException("Block Commentary doesn't have an end\nAt line: " + currentLine);
							} else if (!line.contains("*/")) { //If the line doesn't have an Block Comment End indentifier we continue on the loop
								Debug.println("Skipping line...");
								continue;
							} else { //If the line does have an Block Comment End, we finish our search and place the position at the right place;
								position = line.indexOf("*/") + 2;
								lineLen = line.length();
								str = str.substring(0, str.length() - 1); //Delete the "/" that remains in the Analyzed String
								str = ""; //Destroy the dream
								Debug.println("-> Block Comment finished at line: " + currentLine + " at position: " + position);
								break; //Finishes the search
							}
						} //End of search end block comment loop
							
					} //End of not in the same line block comment
				}
				
				//We don't have a comment ahead, let's begin the next phase
				//Debug.println("Analyzing: " + str);
				
				if (c == '"')
					readingString = !readingString; //We switch between reading or not a string in the line
				
				else if (c == ' ' && !readingString) {//If we're not reading a string constant and we get an white space on input
					if (validToken) {
						if (validToken) { //we check if the previous lexem was accepted, and if it was
							allTokens.add(hold); //We add the new token to the list
							Debug.println("Created token with Lexem: " + hold.getLexem());
							validToken = false;
						}
					} else {
						if (str.trim().length() > 0)
							throw new LexicalErrorException(str + " is not a valid lexem.\n At line: " + currentLine + " position: " + position);
					}
					str = ""; //We set the analyzed string to empty
						
					continue; //we should continue till we get to the next word
				}
				
				temp = TokenFactory.findToken(str);
				if (temp != null) { //If we find a valid token
					validToken = true; //Change the flag
					hold = temp; //We hold the accepted token
				} else { // if we don't
					if (validToken) { //we check if the previous lexem was accepted, and if it was
						allTokens.add(hold); //We add the new token to the list
						Debug.println("Created token with Lexem: " + hold.getLexem());
						str = c.toString(); //We reset the analyzed lexem
						
						temp = TokenFactory.findToken(str);  //We double check if now it's an accepted lexem
						if (temp != null) { //If so, we let the process happen once again
							validToken = true; //Change the flag
							hold = temp; //We hold the accepted token
						} else { //If not, we mark the flag as false;
							validToken = false;
							hold = null;
						}
					}
				}
				
			} //End of char-by-char loop
			if (readingString)
				throw new LexicalErrorException("String constant doesn't have an end\nAt line: " + currentLine);
			
			if (validToken) { //When we finish reading a line and there were a valid token in the end of it
				allTokens.add(hold);
				Debug.println("Created token with Lexem: " + hold.getLexem());
				validToken = false;
			} else if (!str.trim().isEmpty()) { //If in the end it's not empty and it's invalid
				throw new LexicalErrorException("Expected knowledge. You have none\n" + str + " is invalid\nAt line: " + currentLine + ", position: " + position);
			}
				
		} //End of Lines
		
		allTokens.add(TokenFactory.createToken(0, "EOF"));
		Debug.println("Finished Reading all lines");
	}

}
