package logic;

import symbol.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Transform {
	public static final String outputFilePath = "symbol/Output.txt";
	
	// save the expression's entrance
	private static char StartChar = Symbol.whiteSpace;
	private static boolean isInitialize = false;
	
	// 按顺序保存每一个表达式
	private static ArrayList<Expression> expressions = new ArrayList<>();
	private static ArrayList<Character> Vns = new ArrayList<>();
	private static ArrayList<Character> Vts = new ArrayList<>();
	// 保存每一个非终结符的闭包
	private static HashMap<Character, Closure> closures = new HashMap<>();
	private static ArrayList<Character> VnHasCal = new ArrayList<>();
	
	// 保存每一个非终结符 First 和 Follow 的结果
	private static HashMap<Character, Nonterminal> nonterminals = new HashMap<>();
	
	private static int ID = 0;
	private static HashMap<Integer, State> states = new HashMap<>();
	public static final int notExist = -250;
	
	private static int[][] PPTState = null;
	private static char[][] PPTAction = null;
	public static final char Shift = 'S';
	public static final char Goto = 'G';
	public static final char Reduce = 'R';
	
	// 初始化每一个表达式
	private void initializeExpression(){
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
		
		// 得到所有终结符
		for( Expression expression : expressions ){
			String rightValue = expression.getRightValue();
			for( int i=0; i<rightValue.length(); i++ ){
				char letter = rightValue.charAt(i);
				if( !Nonterminal.isVn(letter) ){
					if( !Vts.contains(letter) ){
						Vts.add(letter);
					}
				}
			}
		}
		Vts.add(Symbol.dollar);
		
		// 计算每一个非终结符闭包
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
		
//		System.out.println("Vn: " + Vns + "\n");
//		System.out.println("Vt: " + Vts + "\n");
//
//		System.out.println("Vn Expression ");
//		ArrayList<Character> keys = new ArrayList<>(closures.keySet());
//		for( char key : keys ){
//			closures.get(key).show();
//		}
		try{
			FileWriter writer = new FileWriter(Transform.outputFilePath, true);
			writer.write("Vn: " + Vns + "\n");
			writer.write("Vt: " + Vts + "\n");
			writer.write("\n");
			writer.write("Vn Expression " + "\n");
			ArrayList<Character> keys = new ArrayList<>(closures.keySet());
			for( char key : keys ){
				writer.write( closures.get(key).output() );
			}
			writer.close();
		}catch(FileNotFoundException e) {
			System.out.println(Transform.outputFilePath + "is not found");
		}catch(IOException e) {
			System.out.println(Transform.outputFilePath + "has IOException");
		}
	}
	
	// 计算每一个非终结符 First 和 Follow
	private void initializeVn(){
		for( char Vn : Vns ){
			Nonterminal nonterminal = new Nonterminal(Vn, closures, expressions);
			if( !nonterminals.keySet().contains(Vn) ){
				nonterminals.put( Vn, nonterminal );
//				nonterminal.show();
				try{
					FileWriter writer = new FileWriter(Transform.outputFilePath, true);
					writer.write(nonterminal.output());
					writer.close();
				}catch(FileNotFoundException e) {
					System.out.println(Transform.outputFilePath + "is not found");
				}catch(IOException e) {
					System.out.println(Transform.outputFilePath + "has IOException");
				}
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
//			state.show();
			try{
				FileWriter writer = new FileWriter(Transform.outputFilePath, true);
				writer.write(state.output());
				writer.close();
			}catch(FileNotFoundException e) {
				System.out.println(Transform.outputFilePath + "is not found");
			}catch(IOException e) {
				System.out.println(Transform.outputFilePath + "has IOException");
			}
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
	
	private void initializePPT(){
		PPTState = new int[ID][Vts.size()+Vns.size()];
		PPTAction = new char[ID][Vts.size()+Vns.size()];
		for( int i=0; i<PPTState.length; i++ ){
			for( int j=0; j<PPTState[0].length; j++ ){
				PPTState[i][j] = notExist;
			}
		}
		for( int i=0; i<PPTAction.length; i++ ){
			for( int j=0; j<PPTAction[0].length; j++ ){
				PPTAction[i][j] = Symbol.whiteSpace;
			}
		}
		
		ArrayList<Integer> keys = new ArrayList<>(states.keySet());
		for( int key : keys ){
			State state = states.get(key);
			int row = state.getStateID();
			
			ArrayList<Character> ActionKeys = new ArrayList<>(state.getAction().keySet());
			for( char ActionKey : ActionKeys ){
				int col = Vts.indexOf(ActionKey);
				if( PPTState[row][col]==notExist ){
					PPTState[row][col] = state.getAction().get(ActionKey);
					PPTAction[row][col] = Transform.Shift;
				}
				else {
					System.out.print("Action[" + row + ", " + ActionKey + "] = "
							+ PPTAction[row][col] + PPTState[row][col] + " / "
							+ Transform.Shift + state.getAction().get(ActionKey) );
				}
			}
			
			ArrayList<Character> GotoKeys = new ArrayList<>(state.getGoto().keySet());
			for( char GotoKey : GotoKeys ){
				int col = Vts.size() + Vns.indexOf(GotoKey);
				if( PPTState[row][col]==notExist ){
					PPTState[row][col] = state.getGoto().get(GotoKey);
					PPTAction[row][col] = Transform.Goto;
				}
				else {
					System.out.print("Action[" + row + ", " + GotoKey + "] = "
							+ PPTAction[row][col] + PPTState[row][col] + " / "
							+ Transform.Goto + state.getAction().get(GotoKey) );
				}
			}
			
			ArrayList<Expression> exps = state.getExpressions();
			for( Expression exp : exps ){
				if( exp.canBeReduced() ){
					Expression expTemp = new Expression(exp);
					expTemp.returnIntial();
					int index = Transform.notExist;
					for( Expression expression : expressions ){
						if( expTemp.isSameWith(expression) ){
							index = expressions.indexOf(expression);
						}
					}
					ArrayList<Character> predictChars = exp.getPrdictChars();
					for( char predictChar : predictChars ){
						int col = Vts.indexOf( predictChar );
						if( PPTState[row][col]==notExist ){
							PPTState[row][col] = index;
							PPTAction[row][col] = Transform.Reduce;
						}
						else {
							System.out.print("Action[" + row + ", " + predictChar + "] = "
									+ PPTAction[row][col] + PPTState[row][col] + " / "
									+ Transform.Reduce + index );
						}
					}
				}
			}
		}
		
//		int len = ( Vts.size()+Vns.size()+1 )*5 + 4;
//		for( int i=0; i<(len-3)/2; i++ ){
//			System.out.print(" ");
//		}
//		System.out.print("PPT");
//		for( int i=0; i<len-(len-3)/2; i++ ){
//			System.out.print(" ");
//		}
//		System.out.println();
//
//		System.out.print("State |");
//		for( char Vt : Vts ){
//			System.out.print( "  " + Vt + "  ");
//		}
//		System.out.print(" |");
//		for( char Vn : Vns ){
//			System.out.print( "  " + Vn + "  ");
//		}
//		System.out.println();
//
//		ArrayList<Integer> stateIDs = new ArrayList<>( states.keySet() );
//		for( int i=0; i<stateIDs.size(); i++ ){
//			State state = states.get( i );
//			if( state.getStateID()<10 ){
//				System.out.print("  " + state.getStateID() + "   | ");
//			}
//			else {
//				System.out.print("  " + state.getStateID() + "  | ");
//			}
//			for( int j=0; j<PPTState[i].length; j++ ){
//				if( PPTState[i][j]==Transform.notExist ){
//					System.out.print( "     " );
//				}
//				else {
//					if( PPTState[i][j]<10 ){
//						System.out.print(" " + PPTAction[i][j] + PPTState[i][j] + "  ");
//					}
//					else {
//						System.out.print(" " + PPTAction[i][j] + PPTState[i][j] + " ");
//					}
//				}
//				if( j==Vts.size()-1 ){
//					System.out.print("|");
//				}
//			}
//			System.out.println();
//		}
//		System.out.println();
		
		try{
			FileWriter writer = new FileWriter(Transform.outputFilePath, true);
			int len = ( Vts.size()+Vns.size()+1 )*5 + 4;
			for( int i=0; i<(len-3)/2; i++ ){
				writer.write(" ");
			}
			writer.write("PPT");
			for( int i=0; i<len-(len-3)/2; i++ ){
				writer.write(" ");
			}
			writer.write("\n");
			
			writer.write("State |");
			for( char Vt : Vts ){
				writer.write( "  " + Vt + "  ");
			}
			writer.write(" |");
			for( char Vn : Vns ){
				writer.write( "  " + Vn + "  ");
			}
			writer.write("\n");
			
			ArrayList<Integer> stateIDs = new ArrayList<>( states.keySet() );
			for( int i=0; i<stateIDs.size(); i++ ){
				State state = states.get( i );
				if( state.getStateID()<10 ){
					writer.write("  " + state.getStateID() + "   | ");
				}
				else {
					writer.write("  " + state.getStateID() + "  | ");
				}
				for( int j=0; j<PPTState[i].length; j++ ){
					if( PPTState[i][j]==Transform.notExist ){
						writer.write( "     " );
					}
					else {
						if( PPTState[i][j]<10 ){
							writer.write(" " + PPTAction[i][j] + PPTState[i][j] + "  ");
						}
						else {
							writer.write(" " + PPTAction[i][j] + PPTState[i][j] + " ");
						}
					}
					if( j==Vts.size()-1 ){
						writer.write("|");
					}
				}
				writer.write("\n");
			}
			writer.write("\n");
			writer.close();
		}catch(FileNotFoundException e) {
			System.out.println(Transform.outputFilePath + "is not found");
		}catch(IOException e) {
			System.out.println(Transform.outputFilePath + "has IOException");
		}
	}
	
	private int getExpressionIndex(Expression expression){
		for( int i=0; i<expressions.size(); i++ ){
			if( expression.isSameWith(expressions.get(i)) ){
				return i;
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
		try{
			FileWriter writer = new FileWriter(Transform.outputFilePath);
			writer.write("");
			writer.close();
		}catch(FileNotFoundException e) {
			System.out.println(Transform.outputFilePath + "is not found");
		}catch(IOException e) {
			System.out.println(Transform.outputFilePath + "has IOException");
		}
		
		initializeExpression();
		initializeVn();
		initializeState();
		initializePPT();
		isInitialize = true;
	}
	
	public static boolean hasInitialize(){
		return isInitialize;
	}
	
	public static char getStartChar(){
		return StartChar;
	}
	
	public static Nonterminal getNonterminal(char Vn){
		return nonterminals.get(Vn);
	}
	
	public static int getVtIndex(char Vt){
		return Vts.indexOf(Vt);
	}
	public static int getVnIndex(char Vn){
		return Vts.size() + Vns.indexOf(Vn);
	}
	
	public static char getAction(int row, int col){
		return PPTAction[row][col];
	}
	public static int getState(int row, int col){
		return PPTState[row][col];
	}
	
	public static Expression getExpressionByIndex(int index){
		if( index<expressions.size() ){
			return new Expression(expressions.get(index));
		}
		else return new Expression();
	}
}
