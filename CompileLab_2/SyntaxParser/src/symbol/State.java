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
		ArrayList<Expression> exps = Transform.getClosure( Vn ).getExpressions();
		for( Expression exp : exps ){
			this.expressions.add( new Expression(exp) );
		}
		this.calPredictChar();
	}
	public void addExpression(Expression expression){
		for( Expression exp : this.expressions ){
			if( exp.isSameWith(expression) ){
				return;
			}
		}
		this.expressions.add( new Expression(expression) );
	}
	
	// 根据目前状态和转换路径的值，计算出下一个状态
	public State through(char letter){
		Vns = new ArrayList<>();
		State state = new State();
		ArrayList<Expression> exps = this.getExpressions();
		for( Expression expression : exps ){
			if( expression.canMove() && expression.getRightValue().charAt(expression.getDotIndex())==letter ){
				Expression exp = new Expression(expression);
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
		state.calPredictChar();
		return state;
	}
	
	// 保存状态转换，根据转换值是否为非终结符添加 Action or Goto
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
	
	public HashMap<Character, Integer> getAction(){
		return Action;
	}
	
	public HashMap<Character, Integer> getGoto(){
		return Goto;
	}
	
	// 计算预测符
	private final void calPredictChar(){
		ArrayList<Character> VnHasCal = new ArrayList<>();
		ArrayList<Expression> exps = this.getExpressions();
		Expression exp = exps.get(0);
		if( exp.getLeftValue()==Transform.getStartChar() && exp.getDotIndex()==0
				&& exp.getRightValue().length()==1
				&& Nonterminal.isVn(exp.getRightValue().charAt(exp.getDotIndex())) ){
			exp.addPredictChar(Symbol.dollar);
			VnHasCal.add( Transform.getStartChar() );
		}
		boolean isChanged = true;
		while( isChanged ){
			isChanged = false;
			Expression expTemp = null;
			
			// 找到一个还未移进的表达式
			for( int i=0; i<exps.size(); i++ ){
				expTemp = exps.get(i);
				if( expTemp!=null && !VnHasCal.contains(expTemp.getLeftValue()) && expTemp.getDotIndex()==0 ){
					isChanged = true;
					break;
				}
			}
			
			if( isChanged ){
				char leftValue = expTemp.getLeftValue();
				VnHasCal.add( leftValue );
				
				// 找到所有 leftValue 相同还未移进的表达式
				ArrayList<Expression> expTemps = new ArrayList<>();
				expTemps.add(expTemp);
				for( Expression expTemp2 : exps ){
					if( expTemp2.getLeftValue()==leftValue && expTemp2.getDotIndex()==0 ){
						boolean inArray = false;
						for( Expression expTemp3 : expTemps ){
							if( expTemp3.isSameWith(expTemp2) ){
								inArray = true;
								break;
							}
						}
						if( !inArray ){
							expTemps.add(expTemp2);
						}
					}
				}
				
				// 如果存在直接左递归，计算由自身产生的预测符
				ArrayList<Character> predictChars = new ArrayList<>();
				for( Expression expTemp2 : expTemps ){
					if( expTemp2.canMove() && expTemp2.getLeftValue()==expTemp2.getRightValue().charAt(expTemp2.getDotIndex()) ){
						if( expTemp2.getRightValue().length()-1>expTemp2.getDotIndex() ){
							char next = expTemp2.getRightValue().charAt( expTemp2.getDotIndex()+1 );
							if( Nonterminal.isVn(next) ){
								ArrayList<Character> FirstValue = Transform.getNonterminal(next).getFirst();
								for( char first : FirstValue ){
									if( !predictChars.contains(first) ){
										predictChars.add( first );
									}
								}
							}
							else {
								if( !predictChars.contains(next) ){
									predictChars.add( next );
								}
							}
						}
					}
				}
				
				for( Expression expTemp2 : exps ){
					// 找到产生这组表达式的表达式
					if( expTemp2.getLeftValue()!=leftValue && expTemp2.canMove()
							&& expTemp2.getRightValue().charAt(expTemp2.getDotIndex())==leftValue ){
						
						// 如果表达式可以再移进两次或两次以上
						if( expTemp2.getRightValue().length()-1>expTemp2.getDotIndex() ){
							char next = expTemp2.getRightValue().charAt( expTemp2.getDotIndex()+1 );
							
							// 如果下下个是非终结符，那就获得其 First
							if( Nonterminal.isVn(next) ){
								for( char predictValue : Transform.getNonterminal(next).getFirst() ){
									if( !predictChars.contains(predictValue) ){
										predictChars.add(predictValue);
									}
								}
								for( Expression expTemp3 : expTemps ){
									for( char predictChar : predictChars ){
										expTemp3.addPredictChar( predictChar );
									}
								}
							}
							// 如果是终结符，则直接加入预测符队列
							else {
								if( !predictChars.contains(next) ){
									predictChars.add(next);
								}
								for( Expression expTemp3 : expTemps ){
									for( char predictChar : predictChars ){
										expTemp3.addPredictChar( predictChar );
									}
								}
							}
						}
						// 如果不可以移进两次以上，则将其预测符传递给其产生的表达式
						else {
							for( char predictValue : expTemp2.getPrdictChars() ){
								if( !predictChars.contains(predictValue) ){
									predictChars.add(predictValue);
								}
							}
							for( Expression expTemp3 : expTemps ){
								for( char predictChar : predictChars ){
									expTemp3.addPredictChar( predictChar );
								}
							}
							
						}
						break;
					}
				}
			}
		}
		
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
	
	public String output(){
		String result = "State " + this.stateID + "\n";
		result += "Expression: " + "\n";
		for( Expression expression : this.expressions ){
			result += expression.output();
		}
		
		ArrayList<Character> throughValues = new ArrayList<>();
		result += "Action: " + "\n";
		throughValues = new ArrayList<>(this.Action.keySet());
		for( char throughValue : throughValues ){
			result += "through " + throughValue + " to " + this.Action.get(throughValue) + "\n";
		}
		result += "Goto: " + "\n";
		throughValues = new ArrayList<>(this.Goto.keySet());
		for( char throughValue : throughValues ){
			result += "through " + throughValue + " to " + this.Goto.get(throughValue) + "\n";
		}
		result += "\n";
		
		return result;
	}
}
