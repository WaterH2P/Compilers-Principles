package logic;

import symbol.Path;
import symbol.REKind;
import symbol.State;
import symbol.Token;

import java.util.HashMap;

public class NFA {
	private static HashMap<REKind, State> NFAs = new HashMap<>();
	
	private static String maxMatch = "";
	
	public static void addNFA(REKind kind, State NFA){
		if( !NFAs.containsKey(kind) ){
			NFAs.put(kind, NFA);
		}
	}
	
	public static State getNFA(REKind kind){
		if( NFAs.containsKey(kind) ){
			return NFAs.get(kind);
		}
		else{
			return null;
		}
	}
	
	public void findMatch(String match, String str, State state){
		maxMatch = "";
		match(match, str, state);
	}
	
	private void match(String matchStr, String str, State state){
		for( int i=0; i<str.length(); i++ ){
			char letter = str.charAt(i);
			if( state.hasUpStick() ){
				if( state.getUpStick().getPath().getValue()==Path.nullValue ){
					match( matchStr, str.substring(i), state.getUpStick().getState() );
				}
				else if( state.getUpStick().getPath().hasValue(letter) ){
					matchStr += letter;
					match( matchStr, str.substring(i+1), state.getUpStick().getState() );
				}
			}
			if( matchStr.length()>maxMatch.length() ){
				maxMatch = matchStr;
			}
			if( state.hasDownStick() ){
				if( state.getDownStick().getPath().getValue()==Path.nullValue ){
					match( matchStr, str.substring(i), state.getDownStick().getState() );
				}
				else if( state.getDownStick().getPath().hasValue(letter) ){
					matchStr += letter;
					match( matchStr, str.substring(i+1), state.getDownStick().getState() );
				}
			}
			if( matchStr.length()>maxMatch.length() ){
				maxMatch = matchStr;
			}
		}
	}
	
	public static String getMaxMatch(){
		return maxMatch;
	}
}
