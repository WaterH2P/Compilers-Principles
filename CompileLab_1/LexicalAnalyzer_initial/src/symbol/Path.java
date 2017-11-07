package symbol;

import logic.Symbol;

import java.util.ArrayList;

public class Path {
	public static final String nullValue = "nothing";
	
	private static Symbol symbol = new Symbol();
	
	private ArrayList<String> values = new ArrayList<>();
	private String value = "";
	
	public Path(String value){
		this.value = value;
		
		if( value.equals(nullValue) ){
			if( !this.values.contains(nullValue) ){
				this.values.add(nullValue);
			}
		}
		else if( value.length()==1 && symbol.isLetter(value.charAt(0)) ){
			if( !this.values.contains(value) ){
				this.values.add(value);
			}
		}
		else if( value.charAt(0)==Symbol.leftSquareBracket ){
			boolean beforeIsLetter = false;
			boolean beforeIsNumber = false;
			boolean beforeIsConnect = false;
			char beforeChar = Symbol.whiteSpace;
			
			for( int i=0; i<value.length(); i++ ){
				char letter = value.charAt(i);
				if( symbol.isLetter(letter) ){
					if( beforeIsLetter && beforeIsConnect ){
						while( beforeChar<=letter ){
							if( !this.values.contains(""+beforeChar) ){
								this.values.add("" + beforeChar);
							}
							beforeChar++;
						}
						beforeIsLetter = false;
						beforeChar = Symbol.whiteSpace;
					}
					else{
						if( beforeIsLetter || beforeIsNumber ){
							if( !this.values.contains(""+beforeChar) ){
								this.values.add("" + beforeChar);
							}
						}
						beforeIsLetter = true;
						beforeChar = letter;
					}
					beforeIsConnect = false;
					beforeIsNumber = false;
				}
				else if( symbol.isNumber(letter) ){
					if( beforeIsNumber && beforeIsConnect ){
						while( beforeChar <= letter ){
							if( !this.values.contains(""+beforeChar) ){
								values.add("" + beforeChar);
							}
							beforeChar++;
						}
						beforeIsNumber = false;
						beforeChar = Symbol.whiteSpace;
					}
					else{
						if( beforeIsLetter || beforeIsNumber ){
							if( !this.values.contains(""+beforeChar) ){
								this.values.add("" + beforeChar);
							}
						}
						beforeIsNumber = true;
						beforeChar = letter;
					}
					beforeIsLetter = false;
					beforeIsConnect = false;
				}
				else if( letter==Symbol.connectSymbol ){
					beforeIsConnect = true;
				}
			}
		}
		else if( value.length()==1 && symbol.isOperator(value.charAt(0)) ){
			if( !this.values.contains(value) ){
				this.values.add(value);
			}
		}
		else if( symbol.isWord(value) ){
			if( !this.values.contains(value) ){
				this.values.add(value);
			}
		}
		else { }
	}
	
	public boolean hasValue(String value){
		if( this.values.contains(value) ){
			return true;
		}
		return false;
	}
	public boolean hasValue(char value){
		return hasValue(""+value);
	}
	
	public void showValues(){
		for( int i=0; i<values.size(); i++ ){
			System.out.print( values.get(i) + " " );
		}
	}
	
	public String getValue(){
		return value;
	}
	public String getValues(){
		return values.toString();
	}
}
