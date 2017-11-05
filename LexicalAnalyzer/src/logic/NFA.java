package logic;

import symbol.*;

import java.util.HashMap;

/**
 * 所有生成的 NFA 都保存在 Map 中
 */
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
//			System.out.println(str);
			char letter = str.charAt(i);
			for( int j=0; j<state.getUpStickNum(); j++ ){
				if( state.getUpStick(j).getPath().getValue().equals(Path.nullValue) ){
					match( matchStr, str.substring(i), state.getUpStick(j).getState() );
				}
				else if( state.getUpStick(j).getPath().hasValue(letter) ){
					matchStr += letter;
					match( matchStr, str.substring(i+1), state.getUpStick(j).getState() );
				}
			}
			if( matchStr.length()>maxMatch.length() ){
				maxMatch = matchStr;
			}
			for( int j=0; j<state.getDownStickNum(); j++ ){
				if( state.getDownStick(j).getPath().getValue().equals(Path.nullValue) ){
					match( matchStr, str.substring(i), state.getDownStick(j).getState() );
				}
				else if( state.getDownStick(j).getPath().hasValue(letter) ){
					matchStr += letter;
					match( matchStr, str.substring(i+1), state.getDownStick(j).getState() );
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
