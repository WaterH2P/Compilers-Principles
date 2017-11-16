package symbol;

import logic.Transform;

import java.util.ArrayList;
import java.util.HashMap;

public class State {
	private int stateID = Transform.notExist;
	private ArrayList<Expression> expressions = new ArrayList<>();
	private ArrayList<Character> throughValues = new ArrayList<>();
	private HashMap<Character, Integer> Action = new HashMap<>();
	private HashMap<Character, Integer> Goto = new HashMap<>();
	
	private ArrayList<Character> Vns = new ArrayList<>();
	
	public State(){}
	
	public State(char Vn){
		this.expressions = Transform.getClosure( Vn ).getExpressions();
	}
	public void addExpression(Expression expression){
		for( Expression exp : this.expressions ){
			if( exp.isSameWith(expression) ){
				return;
			}
		}
		this.expressions.add(expression);
	}
	
	public State through(char letter){
		Vns = new ArrayList<>();
		State state = new State();
		ArrayList<Expression> exps = this.getExpressions();
		for( Expression exp : exps ){
			if( exp.canMove() && exp.getRightValue().charAt(exp.getDotIndex())==letter ){
				exp = new Expression(exp);
				exp.moveDot();
				state.addExpression(exp);
				if( exp.canMove() && Nonterminal.isVn(exp.getRightValue().charAt(exp.getDotIndex())) ){
					char Vn = exp.getRightValue().charAt(exp.getDotIndex());
					if( !Vns.contains(Vn) ){
						Vns.add( Vn );
						ArrayList<Expression> VnExps = Transform.getClosure( Vn ).getExpressions();
						for( Expression VnExp : VnExps ){
							state.addExpression( VnExp );
						}
					}
				}
			}
		}
		Vns = new ArrayList<>();
		return state;
	}
	
	public void addStateChange(char throughValue, int stateID){
		if( Nonterminal.isVn(throughValue) ){
			if( !Goto.keySet().contains(throughValue) ){
				Goto.put( throughValue, stateID );
			}
		}
		else {
			if( !Action.keySet().contains(throughValue) ){
				Action.put( throughValue, stateID );
			}
		}
	}
	
	public ArrayList<Expression> getExpressions(){
		return this.expressions;
	}
	
	public ArrayList<Character> getThroughValues(){
		if( this.throughValues.size()==0 ){
			for( Expression expression : this.expressions ){
				if( expression.canMove() ){
					char throughValue = expression.getRightValue().charAt(expression.getDotIndex());
					if( !throughValues.contains(throughValue) ){
						throughValues.add(throughValue);
					}
				}
			}
		}
		return throughValues;
	}
	
	public boolean isSameWith(State state){
		ArrayList<Expression> exps = state.getExpressions();
		if( this.expressions.size()!=exps.size() ){
			return false;
		}
		for( int i=0; i<exps.size(); i++ ){
			if( !this.expressions.get(i).isSameWith(exps.get(i)) ){
				return false;
			}
		}
		return true;
	}
	
	public int getStateID(){
		return this.stateID;
	}
	
	public void setStateID(int stateID){
		this.stateID = stateID;
	}
	
	public void show(){
		System.out.println("State " + this.stateID);
		System.out.println("Expression: ");
		for( Expression expression : this.expressions ){
			expression.show();
		}
		
		ArrayList<Character> throughValues = new ArrayList<>();
		System.out.println("Action: ");
		throughValues = new ArrayList<>(this.Action.keySet());
		for( char throughValue : throughValues ){
			System.out.println("through " + throughValue + " to " + this.Action.get(throughValue));
		}
		System.out.println("Goto: ");
		throughValues = new ArrayList<>(this.Goto.keySet());
		for( char throughValue : throughValues ){
			System.out.println("through " + throughValue + " to " + this.Goto.get(throughValue));
		}
		System.out.println();
	}
}
