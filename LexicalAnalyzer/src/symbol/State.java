package symbol;

import java.util.ArrayList;

public class State {
	private String name = "";
	private boolean hasArrived = false;
	
	private static int id = 0;
	
	private ArrayList<Stick> upSticks = new ArrayList<>();
	private ArrayList<String> upValues = new ArrayList<>();
	private ArrayList<Stick> downSticks = new ArrayList<>();
	private ArrayList<String> downValues = new ArrayList<>();
	
	public State(){}
	
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
	
	public void addUpStick(Stick stick){
		if( !upValues.contains(stick.getPath().getValue()) ){
			upValues.add( stick.getPath().getValue() );
			this.upSticks.add(stick);
		}
		else if( stick.getPath().getValue().equals(Path.nullValue) ){
			this.upSticks.add(stick);
		}
	}
	public void addDownStick(Stick stick){
		if( !downValues.contains(stick.getPath().getValue()) ){
			downValues.add( stick.getPath().getValue() );
			this.downSticks.add(stick);
		}
		else if( stick.getPath().getValue().equals(Path.nullValue) ){
			this.downSticks.add(stick);
		}
	}
	
	public void hasArrived(){
		this.hasArrived = true;
	}
	public boolean isHasArrived(){
		return this.hasArrived;
	}
	
	public int getUpStickNum(){
		return this.upSticks.size();
	}
	public int getDownStickNum(){
		return this.downSticks.size();
	}
	public Stick getUpStick( int index ){
		return this.upSticks.get(index);
	}
	public Stick getDownStick( int index ){
		return this.downSticks.get(index);
	}
	
	public boolean hasUpStick(){
		if( this.upSticks.size()>0 ){
			return true;
		}
		return false;
	}
	public boolean hasDownStick(){
		if( this.downSticks.size()>0 ){
			return false;
		}
		return true;
	}
	public boolean justHasUpNullValue(){
		if( upValues.size()==1 && upValues.contains(Path.nullValue) ){
			return true;
		}
		return false;
	}
	
	public void setUpState(int index, State state){
		this.upSticks.get(index).setState(state);
	}
	
	public void nameEveryStateFromZero(String sign){
		id = 0;
		this.nameEveryState(sign);
	}
	private void nameEveryState(String sign){
		this.setName(sign+" "+id);
		id++;
		
		for( Stick stick : this.upSticks ){
			if( stick.getState().getName().length()==0 ){
				stick.getState().nameEveryState(sign);
			}
		}
	}

	public void showEveryState(){
		System.out.println(this.getName());
		this.hasArrived();
		
		System.out.println("UP");
		for( Stick stick : this.upSticks ){
			System.out.println("Up through " + stick.getPath().getValue() + " to " + stick.getState().getName());
		}
		System.out.println("Down");
		for( Stick stick : this.downSticks ){
			System.out.println("Down through " + stick.getPath().getValue() + " to " + stick.getState().getName());
		}
		System.out.println("");
		
		for( Stick stick : this.upSticks ){
			if( !stick.getState().isHasArrived() ){
				stick.getState().showEveryState();
			}
		}
	}
}
