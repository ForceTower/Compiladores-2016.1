package semanthic;

public class SemanthicError implements Comparable<SemanthicError>{
	private String error;
	private int factor;
	
	public SemanthicError(int line, String error) {
		this.error = error;
		this.factor = line;
		//calcFactor();
	}
	
	public String getError() {
		return error;
	}
	
	public String toString() {
		return getError();
	}
	
	public int getFactor() {
		return factor;
	}
	
	private void calcFactor() {
		int beginIndex = 9;
		int endIndex = beginIndex;
		
		while (error.charAt(endIndex) != '.') endIndex++;
		
		String l = error.substring(beginIndex, endIndex);
		factor = Integer.parseInt(l);
	}

	@Override
	public int compareTo(SemanthicError o) {
		if (o.getFactor() == factor)
			return 0;
		if (o.getFactor() > factor)
			return -1;
		
		return 1;
	}

}
