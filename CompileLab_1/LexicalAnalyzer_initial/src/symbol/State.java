package symbol;

public class State {
	private static int id = 0;
	private String name = "";
	private boolean hasArrived = false;
	
	private Stick upStick = null;
	private Stick downStick = null;
	
	public State(){}
	public State(State state){
		this.name = state.getName();
		this.upStick = state.getUpStick();
		this.downStick = state.getDownStick();
	}
	
	public void setName(String name){
		this.name = name;
	}
	public void setUpStick(Stick stick){
		this.upStick = stick;
	}
	public void setDownStick(Stick stick){
		this.downStick = stick;
	}
	
	public void HasArrived(){
		this.hasArrived = true;
	}
	
	public String getName(){
		return name;
	}
	public Stick getUpStick(){
		return this.upStick;
	}
	public Stick getDownStick(){
		return this.downStick;
	}
	public boolean isHasArrived(){
		return hasArrived;
	}
	
	public boolean hasUpStick(){
		if( this.upStick==null ){
			return false;
		}
		else{
			return true;
		}
	}
	public boolean hasDownStick(){
		if( this.downStick==null ){
			return false;
		}
		else{
			return true;
		}
	}
	
	private void nameEveryStateFromZero(String sign){
		id = 0;
		this.nameEveryState( sign );
	}

	private void nameEveryState(String sign){
		if( !nameIsRight(this.getName(), sign) ){
			this.setName(sign + id);
			id++;
		}

		if( this.hasUpStick() && !nameIsRight(this.getUpStick().getState().getName(), sign) ){
			this.getUpStick().getState().nameEveryState(sign);
		}
		if( this.hasDownStick() && !nameIsRight(this.getDownStick().getState().getName(), sign) ){
			this.getDownStick().getState().nameEveryState(sign);
		}
	}

	private boolean nameIsRight(String name, String sign){
		if( name.length()>=sign.length() ){
			if( name.substring(0, sign.length()).equals(sign) ){
				String temp = name.substring( sign.length() );
				for( int i=0; i<temp.length(); i++ ){
					if( (temp.charAt(i)>='a' && temp.charAt(i)<='z')
							|| (temp.charAt(i)>='A' && temp.charAt(i)<='Z') ){
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	public void showEveryStateFromZero(String sign){
		this.nameEveryStateFromZero(sign);
		this.showEveryState(sign);
	}

	private void showEveryState(String sign){
		this.HasArrived();
		System.out.println(this.getName());
		if( this.hasUpStick() ){
			System.out.println("U_Path: through " + this.getUpStick().getPath().getValue()
					+ " to " + this.getUpStick().getState().getName() + " "
					+ " UpStick: " + this.getUpStick().getState().isHasArrived() );
		}
		if( this.hasDownStick() ){
			System.out.println("D_Path: through " + this.getDownStick().getPath().getValue()
					+ " to " + this.getDownStick().getState().getName() + " "
					+ " DownStick: " + this.getDownStick().getState().isHasArrived() );
		}

		System.out.println();

		if( this.hasUpStick() && !this.getUpStick().getState().isHasArrived() ){
			this.getUpStick().getState().showEveryState(sign);
		}
		if( this.hasDownStick() && !this.getDownStick().getState().isHasArrived() ){
			this.getDownStick().getState().showEveryState(sign);
		}
	}
}
