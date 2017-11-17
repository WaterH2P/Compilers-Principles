package logic;

import symbol.Expression;
import symbol.Symbol;

import java.io.*;
import java.util.ArrayList;
import java.util.Stack;

public class Judge {
	private static String InputToken = "";
	
	// 初始化需要判断的 Tokens
	private void initializeInputToken(){
		String filePath = "symbol/InputToken.txt";
		try{
			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(filePath));
			BufferedReader buff = new BufferedReader( inputStreamReader );
			String line = buff.readLine();
			if( line==null ){
				System.out.println("Please add tokens into " + filePath);
			}
			else{
				InputToken = line;
				InputToken.replace(" ", "");
				InputToken += Symbol.dollar;
			}
		}catch( FileNotFoundException e ){
			System.out.println( filePath  + " is not found!");
		}catch( IOException e ){
			e.printStackTrace();
		}
	}
	
	// 开始判断，会预先调用 Transform 和 Judge 的初始化
	public void judge(){
		if( !Transform.hasInitialize() ){
			Transform transform = new Transform();
			transform.initialize();
		}
		initializeInputToken();
		
		// 初始化两个栈和输入队列
		Stack<Integer> state = new Stack<>();
		Stack<Character> chars = new Stack<>();
		ArrayList<Character> input = new ArrayList<>();
		state.push(0);
		chars.push(Symbol.dollar);
		for( int i=0; i<InputToken.length(); i++ ){
			if( InputToken.charAt(i)!=Symbol.whiteSpace ){
				input.add(InputToken.charAt(i));
			}
		}
		
//		System.out.println("Step " + 0 + " : ");
//		System.out.print("Stack State: " + state);
//		for( int i=0; i<30-state.toString().length(); i++ ){
//			System.out.print(" ");
//		}
//		System.out.println( input );
//		System.out.println("Stack Chars: " + chars);
		
		try{
			FileWriter writer = new FileWriter(Transform.outputFilePath, true);
			writer.write("Step " + 0 + " : " + "\n");
			writer.write("Stack State: " + state);
			for( int i=0; i<30-state.toString().length(); i++ ){
				writer.write(" ");
			}
			writer.write( input + "\n" );
			writer.write("Stack Chars: " + chars + "\n");
			writer.close();
		}catch(FileNotFoundException e) {
			System.out.println(Transform.outputFilePath + "is not found");
		}catch(IOException e) {
			System.out.println(Transform.outputFilePath + "has IOException");
		}
		
		boolean isFinished = false;
		boolean isWrong = false;
		int step = 1;
		while( !isFinished && !isWrong ){
			char action = Symbol.whiteSpace;
			int stateID = Transform.notExist;

			// Action
			if( state.size()==chars.size() ){
				int row = state.pop();
				int col = Transform.getVtIndex( input.get(0) );
				action = Transform.getAction(row, col);
				stateID = Transform.getState(row, col);
				
				if( action==Transform.Shift ){
					state.push(row);
					state.push(stateID);
					chars.push( input.remove(0) );
				}
				else if( action==Transform.Reduce ){
					// R0 表示成功
					if( stateID==0 ){
						state.push(row);
						isFinished = true;
					}
					else{
						Expression expression = Transform.getExpressionByIndex(stateID);
						String rightValue = expression.getRightValue();
						if( chars.size()<=rightValue.length() ){
							isWrong = true;
						}
						else {
							String rightValueTemp = "";
							int[] stateSave = new int[rightValue.length()];
							state.push( row );
							for( int i=0; i<rightValue.length(); i++ ){
								rightValueTemp = chars.pop() + rightValueTemp;
								stateSave[rightValue.length()-1-i] = state.pop();
							}
							
							// 用表达式左部替代右部
							if( rightValueTemp.equals(rightValue) ){
								chars.push( expression.getLeftValue() );
							}
							else {
								for( int i=0; i<stateSave.length; i++ ){
									state.push( stateSave[stateSave.length-1-i] );
									chars.push( rightValueTemp.charAt(stateSave.length-1-i) );
								}
								isWrong = true;
							}
						}
					}
				}
			}
			// Goto
			else {
				int row = state.pop();
				char letter = chars.pop();
				int col = Transform.getVnIndex( letter );
				action = Transform.getAction(row, col);
				stateID = Transform.getState(row, col);
				if( action==Transform.Goto ){
					state.push(row);
					state.push(stateID);
					chars.push(letter);
				}
				else {
					state.push(row);
					chars.push(letter);
					isWrong = true;
				}
			}
			
//			System.out.println("Step " + step + " : " + action + stateID);
//			System.out.print("Stack State: " + state);
//			for( int i=0; i<30-state.toString().length(); i++ ){
//				System.out.print(" ");
//			}
//			System.out.println( input );
//			System.out.println("Stack Chars: " + chars);
			
			try{
				FileWriter writer = new FileWriter(Transform.outputFilePath, true);
				writer.write("Step " + step + " : " + action + stateID + "\n");
				writer.write("Stack State: " + state);
				for( int i=0; i<30-state.toString().length(); i++ ){
					writer.write(" ");
				}
				writer.write( input + "\n" );
				writer.write("Stack Chars: " + chars + "\n");
				writer.close();
			}catch(FileNotFoundException e) {
				System.out.println(Transform.outputFilePath + "is not found");
			}catch(IOException e) {
				System.out.println(Transform.outputFilePath + "has IOException");
			}
			
			step++;
		}
		
//		System.out.println();
//		if( isFinished ){
//			System.out.println( "Accept" );
//		}
//		else if( isWrong ){
//			System.out.println( "Wrong" );
//		}
		
		try{
			FileWriter writer = new FileWriter(Transform.outputFilePath, true);
			writer.write("\n");
			if( isFinished ){
				writer.write( "Accept" + "\n" );
			}
			else if( isWrong ){
				writer.write( "Wrong" + "\n" );
			}
			writer.close();
		}catch(FileNotFoundException e) {
			System.out.println(Transform.outputFilePath + "is not found");
		}catch(IOException e) {
			System.out.println(Transform.outputFilePath + "has IOException");
		}
	}
}
