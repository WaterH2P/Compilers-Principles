package symbol;

public class Expression {
	private char leftValue = ' ';
	private String rightValue = "";
	
	private int rightLen = 0;
	private int dotIndex = 0;
	
	public Expression(Expression expression){
		this.leftValue = expression.leftValue;
		this.rightValue = expression.rightValue;
		this.rightLen = expression.rightLen;
		this.dotIndex = expression.dotIndex;
	}
	
	public Expression(char leftValue, String rightValue){
		this.leftValue = leftValue;
		this.rightValue = rightValue;
		this.rightLen = this.rightValue.length();
	}
	
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
		if( this.dotIndex!=expression.dotIndex )
			return false;
		
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
		System.out.println();
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
}
