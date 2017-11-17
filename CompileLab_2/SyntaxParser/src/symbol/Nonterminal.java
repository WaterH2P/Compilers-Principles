package symbol;

import logic.Transform;

import java.util.ArrayList;
import java.util.HashMap;

public class Nonterminal {
	private char Vn = Symbol.whiteSpace;
	private ArrayList<Character> First = new ArrayList<>();
	private ArrayList<Character> Follow = new ArrayList<>();
	private ArrayList<Character> VnHasCalFirst = new ArrayList<>();
	private ArrayList<Character> VnHasCalFollow = new ArrayList<>();
	
	private Nonterminal(char Vn, HashMap<Character, Closure> VnExpressions){
		calFirst( Vn, VnExpressions );
		VnHasCalFirst = new ArrayList<>();
	}
	public Nonterminal(char Vn, HashMap<Character, Closure> VnExpressions, ArrayList<Expression> expressions){
		this.Vn = Vn;
		
		calFirst( this.Vn, VnExpressions );
		VnHasCalFirst = new ArrayList<>();
		
		calFollow( this.Vn, expressions, VnExpressions );
		VnHasCalFollow = new ArrayList<>();
	}
	
	private void calFirst(char Vn, HashMap<Character, Closure> VnExpressions){
		if( VnHasCalFirst.contains(Vn) ){
			return;
		}
		VnHasCalFirst.add( Vn );
		Closure closure = VnExpressions.get( Vn );
		ArrayList<Expression> expressions = closure.getExpressions();
		for( Expression exp : expressions ){
			char letter = exp.getRightValue().charAt(0);
			if( isVn(letter) ){
				calFirst( letter, VnExpressions );
			}
			else {
				if( !this.First.contains(letter) ){
					this.First.add(letter);
				}
			}
		}
	
	}
	
	private void calFollow(char Vn, ArrayList<Expression> expressions, HashMap<Character, Closure> VnExpressions){
		if( VnHasCalFollow.contains(Vn) ){
			return;
		}
		VnHasCalFollow.add( Vn );
		
		if( Vn==Transform.getStartChar() ){
			if( !this.Follow.contains(Symbol.dollar) ){
				this.Follow.add(Symbol.dollar);
			}
		}
		else{
			for( Expression exp : expressions ){
				String rightValue = exp.getRightValue();
				if( rightValue.indexOf(Vn) >= 0 ){
					for( int i = 0; i < rightValue.length(); i++ ){
						if( rightValue.charAt(i)==Vn ){
							if( (i + 1) < rightValue.length() ){
								char letter = rightValue.charAt(i + 1);
								if( isVn(letter) ){
									ArrayList<Character> first = new Nonterminal(letter, VnExpressions).getFirst();
									for( char Vt : first ){
										if( !this.Follow.contains(Vt) ){
											this.Follow.add(Vt);
										}
									}
								} else{
									if( !this.Follow.contains(letter) ){
										this.Follow.add(letter);
									}
								}
							} else{
								calFollow(exp.getLeftValue(), expressions, VnExpressions);
							}
						}
					}
				}
			}
		}
	}
	
	public static boolean isVn(char letter){
		if( letter>='A' && letter<='Z' ){
			return true;
		}
		else {
			return false;
		}
	}
	
	public ArrayList<Character> getFirst(){
		return First;
	}
	
	public ArrayList<Character> getFollow(){
		return Follow;
	}
	
	public void show(){
		System.out.println( this.Vn );
		System.out.println( "First: " + this.First );
		System.out.println( "Follow: " + this.Follow );
		System.out.println();
	}
	
	public String output(){
		String result = this.Vn + "\n";
		result += "First: " + this.First + "\n";
		result += "Follow: " + this.Follow + "\n";
		result += "\n";
		return result;
	}
}
