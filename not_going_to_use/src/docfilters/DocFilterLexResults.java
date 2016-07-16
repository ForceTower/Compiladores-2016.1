package docfilters;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class DocFilterLexResults extends DocumentFilter{
	private final StyledDocument styledDocument;
	public JTextPane yourTextPane;
	private boolean once;
	
	private final StyleContext styleContext = StyleContext.getDefaultStyleContext();
	private final AttributeSet normal = styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, Color.BLACK);
	private final AttributeSet invalid_token = styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, Color.YELLOW);
	private final Pattern invalid_t = Pattern.compile("<- TOKEN_ERR .* ->");
	
	public DocFilterLexResults(JTextPane yourTextPane) {
		this.yourTextPane = yourTextPane;
		styledDocument = yourTextPane.getStyledDocument();
	}
	
	@Override
	public void insertString(FilterBypass fb, int offset, String text, AttributeSet attributeSet) throws BadLocationException {
		super.insertString(fb, offset, text, attributeSet);

		handleTextChanged();
	}
	
	@Override
	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
		super.remove(fb, offset, length);

		handleTextChanged();
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attributeSet) throws BadLocationException {
		super.replace(fb, offset, length, text, attributeSet);

		handleTextChanged();
	}

	
	private void handleTextChanged() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateTextStyles();
			}
		});
	}
	
	private void updateTextStyles() {
		styledDocument.setCharacterAttributes(0, yourTextPane.getText().length(), normal, true);
		
		if (!once) {
			Matcher matcher;
			
			matcher = invalid_t.matcher(yourTextPane.getText());
			while (matcher.find()) {
				System.out.println("D: " + matcher.group());
				styledDocument.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), invalid_token, false);
			}
			once = true;
		}
	}
}
