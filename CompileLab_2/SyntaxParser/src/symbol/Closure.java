package symbol;

import java.util.ArrayList;

public class Closure {
	private ArrayList<Expression> expressions = new ArrayList<>();
	
	public void addExpression(Expression expression){
		for( Expression exp : this.expressions ){
			if( exp.isSameWith(expression) ){
				return;
			}
		}
		this.expressions.add(expression);
	}
	
	public ArrayList getExpressions(){
		return this.expressions;
	}
	
	public Expression getExpression(int index){
		return this.expressions.get(index);
	}
	
	public void show(){
		for( Expression expression : this.expressions ){
			expression.show();
		}
		System.out.println();
	}
}
