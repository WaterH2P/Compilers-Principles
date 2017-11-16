package logic;

import symbol.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Transform {
	private static char StartChar = Symbol.whiteSpace;
	
	private static ArrayList<Expression> expressions = new ArrayList<>();
	private static ArrayList<Character> Vns = new ArrayList<>();
	private static ArrayList<Character> Vts = new ArrayList<>();
	private static HashMap<Character, Closure> closures = new HashMap<>();
	private static ArrayList<Character> VnHasCal = new ArrayList<>();
	
	private static HashMap<Character, Nonterminal> nonterminals = new HashMap<>();
	
	private static int ID = 0;
	private static HashMap<Integer, State> states = new HashMap<>();
	public static final int notExist = -250;
	
	private void initializeExpression(){
		Vts.add(Symbol.dollar);
		String filePath = "symbol/CFGInput.txt";
		try{
			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(filePath));
			BufferedReader buff = new BufferedReader(inputStreamReader);
			String line = null;
			while( (line=buff.readLine())!=null ){
				line = line.replace(" ", "");
				if( line.length()<3 ){
					continue;
				}
				Expression expression = new Expression( line.charAt(0), line.substring(3) );
				if( expression.isRightful() ){
					boolean inArray = false;
					for( Expression exp : expressions ){
						if( exp.isSameWith(expression) ){
							inArray = true;
							break;
						}
					}
					if( !inArray && StartChar==Symbol.whiteSpace ){
						String rightValue = expression.getRightValue();
						if( rightValue.length()==1 ){
							if( Nonterminal.isVn(rightValue.charAt(0)) ){
								StartChar = expression.getLeftValue();
							}
							else {
								// 还未找到开始项
								inArray = true;
							}
						}
						else {
							// 还未找到开始项
							inArray = true;
						}
					}
					if( !inArray ){
						expressions.add(expression);
						if( !Vns.contains(expression.getLeftValue()) ){
							Vns.add(expression.getLeftValue());
						}
						Closure closure = new Closure();
						if( closures.keySet().contains( expression.getLeftValue() ) ){
							closure = closures.get( expression.getLeftValue() );
						}
						closure.addExpression( expression );
						closures.put( expression.getLeftValue(), closure );
					}
				}
			}
		}catch( FileNotFoundException e ){
			System.out.println( filePath + " is not found");
		}catch( IOException e ){
			e.printStackTrace();
		}
		
		
		
		VnHasCal = new ArrayList<>();
		for( char key : Vns ){
			Closure closure = closures.get(key);
			for( int i=0; i<closure.getExpressions().size(); i++ ){
				Expression exp = (Expression)closure.getExpression(i);
				char letter = exp.getRightValue().charAt(0);
				if( Nonterminal.isVn(letter) && !VnHasCal.contains(letter) ){
					ArrayList<Expression> exps = closures.get(exp.getRightValue().charAt(0)).getExpressions();
					for( Expression expression : exps ){
						closure.addExpression(expression);
					}
					VnHasCal.add(letter);
				}
			}
			VnHasCal = new ArrayList<>();
		}
		
		System.out.println(" Vn Expression ");
		ArrayList<Character> keys = new ArrayList<>(closures.keySet());
		for( char key : keys ){
			closures.get(key).show();
		}
		
	}
	
	private void initializeVn(){
		for( char Vn : Vns ){
			Nonterminal nonterminal = new Nonterminal(Vn, closures, expressions);
			if( !nonterminals.keySet().contains(Vn) ){
				nonterminals.put( Vn, nonterminal );
				nonterminal.show();
			}
		}
	}
	
	private void initializeState(){
		State startState = new State(StartChar);
		int stateID = Transform.getID();
		startState.setStateID( stateID );
		states.put( stateID, startState );
		for( int i=0; i<states.values().size(); i++ ){
			State state = states.get(i);
			ArrayList<Character> throughValues = state.getThroughValues();
			for( char throughValue : throughValues ){
				State stateTemp = state.through( throughValue );
				int index = getStateIndex(stateTemp);
				if( index==notExist ){
					stateID = Transform.getID();
					stateTemp.setStateID( stateID );
					states.put( stateID, stateTemp );
					state.addStateChange( throughValue, stateID );
				}
				else {
					state.addStateChange( throughValue, index );
				}
			}
		}
		ArrayList<Integer> keys = new ArrayList<>(states.keySet());
		for( int key : keys ){
			State state = states.get(key);
			state.show();
		}
	}
	
	private int getStateIndex(State state){
		ArrayList<Integer> keys = new ArrayList<>(states.keySet());
		for( int key : keys ){
			if( states.get(key).isSameWith(state) ){
				return key;
			}
		}
		return notExist;
	}
	
	public static int getID(){
		return ID++;
	}
	
	public static Closure getClosure(char key){
		return closures.get(key);
	}
	
	public void initialize(){
		initializeExpression();
		initializeVn();
		initializeState();
//		System.out.println("StartChar: " +StartChar);
	}
	
	public static char getStartChar(){
		return StartChar;
	}
}
