package model;

import java.util.ArrayList;
import java.util.List;

public abstract class ConfirmedToken {
	public static List<Token> instances = new ArrayList<>();
	
	public ConfirmedToken() {
		
	}
	
	public abstract void confirm();

}
