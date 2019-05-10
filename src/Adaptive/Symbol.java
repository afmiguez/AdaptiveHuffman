package Adaptive;


public class Symbol implements Comparable<Symbol>{
	
	String letter;//Inner node's letter is null.
	int frequency;
	double low;
	double high;
	double probability;
	
	Symbol(String letter, int frequency){
		this.letter = letter;
		this.frequency = frequency;
	}

	
	@Override
	public int compareTo(Symbol other) {
		return letter.charAt(0) - other.letter.charAt(0);
	}
	
	public String toString(){
		return letter + " " + frequency;
	}
}