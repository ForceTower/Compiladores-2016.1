package model;

import symbols.VariableSymbol;

public class Pair<F, S> {
	public F f;
	public S s;
	public int t;
	public int d;
	public VariableSymbol v;
	
	public Pair(F e, S t) {
		this.f = e;
		this.s = t;
	}
	
	public F getFirst() {
		return f;
	}
	
	public S getSecond() {
		return s;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Pair) {
			Pair<?, ?> p = (Pair<?, ?>)o;
			return p.getFirst().equals(f) && p.getSecond().equals(s);
		}
		return false;
	}

}
