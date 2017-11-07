package logic;

import symbol.*;

import java.util.ArrayList;
import java.util.Stack;

public class Transform {
	
	private static Symbol symbol = new Symbol();
	
	private static ArrayList<String> pathValues = new ArrayList<>();
	
	// change Regular Expression to Non-Deterministic Finite Automata
	public State REToNFA(String REStr){
		String RESuffix = REInfixToSuffix(REStr);
		Stack<State> states = new Stack<>();
		
		for( int i=0; i<RESuffix.length(); i++ ){
			char letter = RESuffix.charAt(i);
			if( symbol.isLetter(letter) || symbol.isOperator(letter) ){
				states.push( newBinaryStar(letter) );
			}
			else if( letter==Symbol.leftSquareBracket ){
				String value = "";
				int iTry = i + calIndex( RESuffix.substring(i), Symbol.rightSquareBracket );
				if( iTry<=RESuffix.length() && RESuffix.charAt(iTry)==Symbol.rightSquareBracket ){
					value = RESuffix.substring(i, iTry+1);
					i = iTry;
				}
				else{
					continue;
				}
				
				states.push( newBinaryStar(value) );
			}
			else if( letter==Symbol.leftBrace ){
				String value = "";
				int iTry = i + calIndex( RESuffix.substring(i), Symbol.rightBrace );
				if( iTry<=RESuffix.length() && RESuffix.charAt(iTry)==Symbol.rightBrace ){
					value = RESuffix.substring(i, iTry+1);
					i = iTry;
				}
				else{
					continue;
				}
				
				String sign = value.substring(1, value.length()-1);
				String RETemp = RE.getRE( REKind.valueOf(sign) );
				
				if( RETemp.length()>0 ){
					State state = REToNFA( RETemp );
					states.push( state );
				}
			}
			else if( !states.empty() ){
				if( letter==Symbol.or ){
					// 取出两个操作数
					State stateDown = new State();
					stateDown = states.pop();
					
					if( states.empty() ){
						states.push( stateDown );
						continue;
					}
					State stateUp = new State();
					stateUp = states.pop();
					
					states.push( stateOrState(stateUp, stateDown) );
				}
				else if( letter==Symbol.star ){
					State state = new State();
					state = states.pop();
					
					states.push( stateStar(state) );
				}
				else if( letter==Symbol.connect ){
					// 取出两个操作数
					State stateAfter = new State();
					stateAfter = states.pop();
					
					if( states.empty() ){
						states.push( stateAfter );
						continue;
					}
					State stateFront = new State();
					stateFront = states.pop();
					
					states.push( stateConnectState(stateFront, stateAfter) );
				}
			}
			else if( states.empty() ){
				continue;
			}
		}
		State result = states.pop();
		
		return result;
	}
	
	// 状态之间连接
	private State stateConnectState(State stateFront, State stateAfter){
		State temp = new State();
		temp = getSecondUpLastState(stateFront);
		temp.setUpState(0, stateAfter);
		
		return stateFront;
	}
	
	// 状态之间或运算
	private State stateOrState(State stateUp, State stateDown){
		
		if( stateUp.justHasUpNullValue() ){
			stateUp.addUpStick( new Stick(new Path(Path.nullValue), stateDown) );
			
			State tempUp = new State();
			tempUp = getUpLastState(stateUp);
			State tempDown = new State();
			tempDown = getUpLastState(stateDown);
			tempDown.addUpStick( new Stick(new Path(Path.nullValue), tempUp) );
			
			return stateUp;
		}
		else{
			// 尾巴合并，连接到 State End
			State end = new State();
			
			State tempUp = new State();
			tempUp = getUpLastState(stateUp);
			tempUp.addUpStick( new Stick(new Path(Path.nullValue), end) );
			
			State tempDown = new State();
			tempDown = getUpLastState(stateDown);
			tempDown.addUpStick( new Stick(new Path(Path.nullValue), end) );
			
			// 开头相接
			State start = new State();
			start.addUpStick( new Stick(new Path(Path.nullValue), stateUp) );
			start.addUpStick( new Stick(new Path(Path.nullValue), stateDown) );
			
			return start;
		}
	}
	
	private State stateStar(State state){
		// 避免形成一个环，使用 downStick 连接到开始
		State start = new State();
		State end = new State();
		start.addUpStick( new Stick(new Path(Path.nullValue), state) );
		start.addUpStick( new Stick(new Path(Path.nullValue), end) );
		
		State temp = new State();
		temp = getUpLastState(state);
		temp.addUpStick( new Stick(new Path(Path.nullValue), end) );
		temp.addDownStick( new Stick(new Path(Path.nullValue), state) );
		
		return start;
	}
	
	private State getUpLastState(State state){
		State temp = state;
		while( temp.hasUpStick() ){
			temp = temp.getUpStick(0).getState();
		}
		return temp;
	}
	
	private State getSecondUpLastState(State state){
		State temp = state;
		while( temp.hasUpStick() && temp.getUpStick(0).getState().hasUpStick() ){
			temp = temp.getUpStick(0).getState();
		}
		return temp;
	}
	
	private State newBinaryStar(String value){
		if( !pathValues.contains(value) ){
			pathValues.add(value);
		}
		
		State start = new State();
		start.addUpStick( new Stick(new Path(value), new State()) );
		return start;
	}
	
	private State newBinaryStar(char letter){
		String value = "" + letter;
		return newBinaryStar(value);
	}
	
	/**
	 * 中缀表达式 转 后缀表达式
	 * @param REStr
	 * @return
	 */
	private String REInfixToSuffix(String REStr){
		String infix = preprocess(REStr);
		String suffix = "";
		Stack<Character> words = new Stack<>();
		
		for( int i=0; i<infix.length(); i++ ){
			char letter = infix.charAt(i);
			
			if( letter==Symbol.leftBracket ){
				words.push(letter);
			}
			else if( letter==Symbol.leftSquareBracket ){
				int iTry = i + calIndex( infix.substring(i), Symbol.rightSquareBracket );
				if( iTry<=infix.length() && infix.charAt(iTry)==Symbol.rightSquareBracket ){
					suffix += infix.substring(i, iTry+1);
					i = iTry;
				}
			}
			else if( letter==Symbol.leftBrace ){
				int iTry = i + calIndex( infix.substring(i), Symbol.rightBrace );
				if( iTry<=infix.length() && infix.charAt(iTry)==Symbol.rightBrace ){
					suffix += infix.substring(i, iTry+1);
					i = iTry;
				}
			}
			else if( symbol.isLetter(letter) || letter==Symbol.star || symbol.isOperator(letter)){
				suffix += letter;
			}
			else if( letter==Symbol.connect ){
				words.push(letter);
			}
			else if( letter==Symbol.or ){
				char before = ' ';
				if( words.empty() ){
					words.push(letter);
					continue;
				}
				while( !words.empty() && (before=words.pop())==Symbol.connect ){
					suffix += before;
				}
				if( before!=Symbol.connect ){
					words.push(before);
				}
				words.push(letter);
			}
			else if( letter==Symbol.rightBracket ){
				if( !words.empty() ){
					char temp = words.pop();
					while( temp!=Symbol.leftBracket && !words.empty() ){
						suffix += temp;
						temp = words.pop();
					}
				}
			}
		}
		
		while( !words.empty() ){
			char letter = words.pop();
			if( symbol.isStateOperator(letter) ){
				suffix += letter;
			}
		}
		
//		System.out.println("Suffix: " + suffix);
		return suffix;
	}
	
	private int calIndex(String str, char letter){
		int index = str.indexOf(letter);
		if( index>=0 ){
			return index;
		}
		else{
			return 10000;
		}
	}
	
	/**
	 * 对 RE 进行预处理，加上乘号
	 * @param REStr
	 * @return
	 */
	private String preprocess(String REStr){
		String result = "";
		boolean beforeIsLetter = false;
		for( int i=0; i<REStr.length(); i++ ){
			char letter = REStr.charAt(i);
			if( symbol.isLetter(letter) ){
				if( beforeIsLetter ){
					result += Symbol.connect;
					beforeIsLetter = true;
				}
				else{
					beforeIsLetter = true;
				}
				result += letter;
			}
			else if( letter==Symbol.leftBrace ){
				// 大括号
				String value = "";
				int iTry = i + calIndex( REStr.substring(i), Symbol.rightBrace );
				if( iTry<=REStr.length() && REStr.charAt(iTry)==Symbol.rightBrace ){
					value = REStr.substring(i, iTry+1);
					if( beforeIsLetter ){
						result += Symbol.connect;
					}
					i = iTry;
					beforeIsLetter = true;
					result += value;
				}
			}
			else if( letter==Symbol.leftSquareBracket ){
				// 中括号
				String value = "";
				int iTry = i + calIndex( REStr.substring(i), Symbol.rightSquareBracket );
				if( iTry<=REStr.length() && REStr.charAt(iTry)==Symbol.rightSquareBracket ){
					value = REStr.substring(i, iTry+1);
					if( beforeIsLetter ){
						result += Symbol.connect;
					}
					i = iTry;
					beforeIsLetter = true;
					result += value;
				}
			}
			else if( letter==Symbol.leftBracket ){
				// 括号
				if( beforeIsLetter ){
					result += Symbol.connect;
				}
				beforeIsLetter = false;
				result += letter;
			}
			else if( letter==Symbol.rightBracket ){
				beforeIsLetter = true;
				result += letter;
			}
			else if( letter==Symbol.star ){
				result += letter;
			}
			else if( symbol.isStateOperator(letter) ){
				result += letter;
				beforeIsLetter = false;
			}
			else if( symbol.isOperator(letter) ){
				if( beforeIsLetter ){
					result += Symbol.connect;
					beforeIsLetter = true;
				}
				else{
					beforeIsLetter = true;
				}
				result += letter;
			}
			else {
				beforeIsLetter = false;
			}
		}
		return result;
	}
	
	
	
	
	
	
	
	
	public Closure NFAToDFA(State state){
		ArrayList<Closure> closures = new ArrayList<>();
		
		// I0 状态集
		Closure start = new Closure();
		start.addState(state);
		start = calClosure(start);
		
		closures.add(start);
		
		for( int i=0; i<closures.size(); i++ ){
			for( String value : pathValues ){
				System.out.print( i + " : " + value + " ... " );
				Closure frontCl = closures.get(i);
				Closure laterCl = calCore( frontCl, value );
				laterCl = calClosure( laterCl );
				if( !laterCl.inTheGather(closures) ){
					System.out.print( " first is right   ....   ");
					frontCl.addDenoter( new Denoter(new Path(value), laterCl) );
					closures.add( laterCl );
				}
				else{
					System.out.print( " second is right   ....   ");
					laterCl = closures.get( laterCl.getIndexInGather(closures) );
					frontCl.addDenoter( new Denoter(new Path(value), laterCl) );
				}
			}
			System.out.println();
		}
		
		Closure result = closures.get(0);
		result.nameEveryClosure();
		return result;
	}
	
	private Closure calCore(Closure closure, String value){
		Closure core = new Closure();
		for( State state : closure.getStates() ){
			for( int i=0; i<state.getUpStickNum(); i++ ){
				if( state.getUpStick(i).getPath().hasValue(value) ){
					core.addState( state.getUpStick(i).getState() );
				}
			}
			for( int i=0; i<state.getDownStickNum(); i++ ){
				if( state.getDownStick(i).getPath().hasValue(value) ){
					core.addState( state.getDownStick(i).getState() );
				}
			}
		}
		return core;
	}
	
	private Closure calClosure(Closure core){
		Closure closure = new Closure(core);
		for( int i=0; i<closure.getStates().size(); i++ ){
			State state = closure.getStates().get(i);
			for( int j=0; j<state.getUpStickNum(); j++ ){
				if( state.getUpStick(j).getPath().hasValue(Path.nullValue) ){
					if( !closure.hasThisState(state.getUpStick(j).getState()) ){
						closure.getStates().add( state.getUpStick(j).getState() );
					}
				}
			}
			for( int j=0; j<state.getDownStickNum(); j++ ){
				if( state.getDownStick(j).getPath().hasValue(Path.nullValue) ){
					if( !closure.hasThisState(state.getDownStick(j).getState()) ){
						closure.getStates().add(state.getDownStick(j).getState());
					}
				}
			}
		}
		return closure;
	}
}
