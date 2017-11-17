package symbol;

import java.util.ArrayList;

public class Expression {
	private char leftValue = ' ';
	private String rightValue = "";
	
	private int rightLen = 0;
	private int dotIndex = 0;
	
	private ArrayList<Character> prdictChars = new ArrayList<>();
	
	public Expression(){}
	
	public Expression(Expression expression){
		this.leftValue = expression.leftValue;
		this.rightValue = expression.rightValue;
		this.rightLen = expression.rightLen;
		this.dotIndex = expression.dotIndex;
		for( char predictChar : expression.getPrdictChars() ){
			this.prdictChars.add( predictChar );
		}
	}
	
	public Expression(char leftValue, String rightValue){
		this.leftValue = leftValue;
		this.rightValue = rightValue;
		this.rightLen = this.rightValue.length();
	}
	
	// 表达式是否合法
	public boolean isRightful(){
		if( !(this.leftValue>='A' && this.leftValue<='Z') ){
			return false;
		}
		if( this.rightLen<=0 ){
			return false;
		}
		if( this.rightValue.contains("$") ){
			return false;
		}
		return true;
	}
	
	public boolean isSameWith(Expression expression){
		if( this.leftValue!=expression.leftValue ){
			return false;
		}
		if( this.rightLen!=expression.rightLen ){
			return false;
		}
		if( !this.rightValue.equals(expression.rightValue) ){
			return false;
		}
		if( this.dotIndex!=expression.dotIndex ){
			return false;
		}
		if( this.prdictChars.size()!=expression.getPrdictChars().size() ){
			return false;
		}
		for( char predictChar : this.prdictChars ){
			if( !expression.getPrdictChars().contains(predictChar) ){
				return false;
			}
		}
		
		return true;
	}
	
	public void show(){
		System.out.print( this.leftValue + " -> " );
		for( int i=0; i<rightLen; i++ ){
			if( dotIndex==i ){
				System.out.print(".");
			}
			System.out.print(rightValue.charAt(i));
		}
		if( dotIndex==rightLen ){
			System.out.print(".");
		}
		System.out.print( " , " + prdictChars );
		System.out.println();
	}
	
	public String output(){
		String result = this.leftValue + " -> ";
		for( int i=0; i<rightLen; i++ ){
			if( dotIndex==i ){
				result += ".";
			}
			result += rightValue.charAt(i);
		}
		if( dotIndex==rightLen ){
			result += ".";
		}
		result += " , " + prdictChars + "\n";
		return result;
	}
	
	public char getLeftValue(){
		return this.leftValue;
	}
	
	public String getRightValue(){
		return this.rightValue;
	}
	
	public int getDotIndex(){
		return this.dotIndex;
	}
	
	public ArrayList<Character> getPrdictChars(){
		return prdictChars;
	}
	
	public void addPredictChar(char predictChar){
		if( !this.prdictChars.contains(predictChar) ){
			this.prdictChars.add(predictChar);
		}
	}
	
	// 移进一位
	public void moveDot(){
		if( this.canMove() ){
			this.dotIndex++;
		}
	}
	
	public boolean canMove(){
		if( this.dotIndex<this.rightLen ){
			return true;
		}
		return false;
	}
	
	public boolean canBeReduced(){
		return !this.canMove();
	}
	
	public void returnIntial(){
		this.dotIndex = 0;
		this.prdictChars = new ArrayList<>();
	}
}
