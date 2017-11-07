package logic;

import sun.jvm.hotspot.debugger.cdbg.Sym;
import symbol.*;

import java.util.ArrayList;
import java.util.Stack;

public class Transform {
	
	private static Symbol symbol = new Symbol();
	
	private static ArrayList<String> pathValues = new ArrayList<>();
	
	private static int id = 0;
	
	// ========================================================================================================
	// NFAToDFA
	
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
			if( state.hasUpStick() ){
				if( state.getUpStick().getPath().hasValue(value) ){
					core.addState( state.getUpStick().getState() );
				}
			}
			if( state.hasDownStick() ){
				if( state.getDownStick().getPath().hasValue(value) ){
					core.addState( state.getDownStick().getState() );
				}
			}
		}
		return core;
	}
	
	private Closure calClosure(Closure core){
		Closure closure = new Closure(core);
		for( int i=0; i<closure.getStates().size(); i++ ){
			State state = closure.getStates().get(i);
			if( state.hasUpStick() ){
				if( state.getUpStick().getPath().hasValue(Path.nullValue) ){
					if( !closure.hasThisState(state.getUpStick().getState()) ){
						closure.getStates().add( state.getUpStick().getState() );
					}
				}
			}
			if( state.hasDownStick() ){
				if( state.getDownStick().getPath().hasValue(Path.nullValue) ){
					if( !closure.hasThisState(state.getDownStick().getState()) ){
						closure.getStates().add(state.getDownStick().getState());
					}
				}
			}
		}
		return closure;
	}
	
	// ========================================================================================================
	// ========================================================================================================
	// REToNFA
	
	/**
	 * change Regular Expression to Non-Deterministic Finite Automata
	 * @param RE
	 */
	public State REToNFA(String name, String RE){
		String RESuffix = REInfixToSuffix(RE);
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
					break;
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
					break;
				}
				
//				State state = NFA.getNFA( value.substring(1, value.length()-1) );
//				if( state!=null ){
//					states.push( state );
//				}
				String sign = value.substring(1, value.length()-1);
				String RETemp = RegularExpression.getRE( sign );
				
				if( RETemp.length()>0 ){
					State state = REToNFA( sign, RETemp );
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
						break;
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
						break;
					}
					State stateFront = new State();
					stateFront = states.pop();
					
					states.push( stateConnectState(stateFront, stateAfter) );
				}
			}
			else if( states.empty() ){
				break;
			}
		}
		State result = states.pop();
//		NFA.addNFA( name, new State(result) );
		
		return result;
	}
	
	// 状态之间连接
	private State stateConnectState(State stateFront, State stateAfter){
		State temp = new State();
		temp = getUpLastState(stateFront);
		temp.setUpStick( new Stick(new Path(Path.nullValue), stateAfter) );
		
		return stateFront;
	}
	
	// 状态之间或运算
	private State stateOrState(State stateUp, State stateDown){
		// 尾巴合并，连接到 State End
		State end = new State();
		
		State tempUp = new State();
		tempUp = getUpLastState(stateUp);
		tempUp.setUpStick( new Stick(new Path(Path.nullValue), end) );
		
		State tempDown = new State();
		tempDown = getUpLastState(stateDown);
		tempDown.setUpStick( new Stick(new Path(Path.nullValue), end) );
		
		// 开头相接
		State start = new State();
		start.setUpStick( new Stick(new Path(Path.nullValue), stateUp) );
		start.setDownStick( new Stick(new Path(Path.nullValue), stateDown) );
		
		return start;
	}
	
	private State stateStar(State state){
		// 避免形成一个环，使用 downStick 连接到开始
		State start = new State();
		State end = new State();
		start.setUpStick( new Stick(new Path(Path.nullValue), state) );
		start.setDownStick( new Stick(new Path(Path.nullValue), end) );
		
		State temp = new State();
		temp = getUpLastState(state);
		temp.setUpStick( new Stick(new Path(Path.nullValue), end) );
		temp.setDownStick( new Stick(new Path(Path.nullValue), state) );
		
		return start;
	}
	
	private State getUpLastState(State state){
		State temp = state;
		while( temp.hasUpStick() ){
			temp = temp.getUpStick().getState();
		}
		return temp;
	}
	
	private State getUpSecondLastState(State state){
		State temp = state;
		if( temp.hasUpStick() ){
			while( temp.getUpStick().getState().hasUpStick() ){
				temp = temp.getUpStick().getState();
			}
		}
		return temp;
	}
	
	private State newBinaryStar(String value){
		if( !pathValues.contains(value) ){
			pathValues.add(value);
		}
		
		State start = new State();
		start.setUpStick( new Stick(new Path(value), new State()) );
		return start;
	}
	
	private State newBinaryStar(char letter){
		String value = "" + letter;
		return newBinaryStar(value);
	}
	
	/**
	 * 中缀表达式 转 后缀表达式
	 * @param RE
	 * @return
	 */
	private String REInfixToSuffix(String RE){
		String infix = preprocess(RE);
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
			else if( symbol.isStateOperator(letter) ){
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
		
		System.out.println("Suffix: " + suffix);
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
	 * @param RE
	 * @return
	 */
	private String preprocess(String RE){
		String result = "";
		boolean beforeIsLetter = false;
		for( int i=0; i<RE.length(); i++ ){
			char letter = RE.charAt(i);
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
				int iTry = i + calIndex( RE.substring(i), Symbol.rightBrace );
				if( iTry<=RE.length() && RE.charAt(iTry)==Symbol.rightBrace ){
					value = RE.substring(i, iTry+1);
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
				int iTry = i + calIndex( RE.substring(i), Symbol.rightSquareBracket );
				if( iTry<=RE.length() && RE.charAt(iTry)==Symbol.rightSquareBracket ){
					value = RE.substring(i, iTry+1);
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
		System.out.println("预处理：" + result);
		return result;
	}
	
	// ========================================================================================================
	
	
	public static void main(String[] args){
		String Letter = "[a-zA-Z]";
//		String RE = "((a|b)*(ab))*(a|b)";
		String RE = "((a|b)*(ab))*{Letter}";
//		String RE = "((a|b|c)*(ab))*";
		
		Transform transform = new Transform();
		
		System.out.println("\n" + "REToNFA:");
		System.out.println("RESuffix: " + transform.REInfixToSuffix(RE));
		System.out.println("LetterSuffix: " + transform.REInfixToSuffix(Letter));
		
		System.out.println("\n" + "REToNFA:");
		State stateLetter = transform.REToNFA("Letter", Letter);
		State state = transform.REToNFA("sb", RE);
	}
}
