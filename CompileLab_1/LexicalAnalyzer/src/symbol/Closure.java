package symbol;

import java.util.ArrayList;

public class Closure {
	private static int id = 0;
	private String name = "";
	private boolean hasArrived = false;
	
	private ArrayList<State> states = new ArrayList<>();
	private ArrayList<Denoter> denoters = new ArrayList<>();
	
	public Closure(){}
	public Closure(Closure closure){
		this.name = closure.getName();
		this.states = closure.getStates();
		this.denoters = closure.getDenoters();
	}
	
	public void addState(State state){
		this.states.add(state);
	}
	public void addDenoter(Denoter denoter){
		this.denoters.add(denoter);
	}
	
	public void setName(String name){
		this.name = name;
	}
	public void setStates(ArrayList<State> states){
		this.states = states;
	}
	public void setDenoters(ArrayList<Denoter> denoters){
		this.denoters = denoters;
	}
	public void setArrived(){
		this.hasArrived = true;
	}
	
	public String getName(){
		return name;
	}
	public ArrayList<State> getStates(){
		return states;
	}
	public ArrayList<Denoter> getDenoters(){
		return denoters;
	}
	public boolean hasArrived(){
		return hasArrived;
	}
	
	public boolean inTheGather(ArrayList<Closure> closures){
		for( Closure temp : closures ){
			if( twoCLosuresAreSame(this, temp) ){
				return true;
			}
		}
		return false;
	}
	
	public int getIndexInGather(ArrayList<Closure> closures){
		for( Closure temp : closures ){
			if( twoCLosuresAreSame(this, temp) ){
				return closures.indexOf( temp );
			}
		}
		return -1;
	}
	
	private boolean twoCLosuresAreSame(Closure clOne, Closure clTwo){
		ArrayList<String> namesOne = new ArrayList<>();
		for( State state : clOne.getStates() ){
			if( !namesOne.contains(state.getName()) ){
				namesOne.add(state.getName());
			}
		}
		
		ArrayList<String> namesTwo = new ArrayList<>();
		for( State state : clTwo.getStates() ){
			if( !namesTwo.contains(state.getName()) ){
				namesTwo.add(state.getName());
			}
		}
		
		if( namesOne.size()!=namesTwo.size() ){
			return false;
		}
		for( String nameOne : namesOne){
			if( !namesTwo.contains(nameOne) ){
				return false;
			}
		}
		
		return true;
	}
	
	public boolean hasThisState(State state){
		ArrayList<String> names = new ArrayList<>();
		for( State stateTemp : this.getStates() ){
			if( !names.contains(stateTemp.getName()) ){
				names.add(stateTemp.getName());
			}
		}
		if( names.contains(state.getName()) ){
			return true;
		}
		return false;
	}
	
	public void nameEveryClosure(){
		if( this.getName().length()==0 ){
			this.setName("I" + id);
			id++;
		}
		
		if( this.getDenoters().size()>0 ){
			for( Denoter denoter : this.getDenoters() ){
				if( denoter.getClosure().getName().length()==0 ){
					denoter.getClosure().nameEveryClosure();
				}
			}
		}
	}
	
	public void showEveryCLosure(){
		this.setArrived();
		System.out.println( "Closure: " + this.getName() );
		if( this.getStates().size()>0 ){
			System.out.println( "里面的状态: " );
			for( State state : this.getStates() ){
				System.out.print( state.getName() + " " );
			}
			System.out.println("\n");
		}
		
		if( this.getDenoters().size()>0 ){
			for( Denoter denoter : this.getDenoters() ){
				System.out.println( this.getName() + " through " + denoter.getPath().getValue()
						+ " to " + denoter.getClosure().getName() + "\n");
				if( !denoter.getClosure().hasArrived() ){
					denoter.getClosure().showEveryCLosure();
				}
			}
		}
	}
}
