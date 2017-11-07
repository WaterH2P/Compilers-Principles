package symbol;

public class Stick {
	private Path path = null;
	private State state = null;
	
	public Stick(Path path, State state){
		this.path = path;
		this.state = state;
	}
	
	public void setPath(Path path){
		this.path = path;
	}
	public void setState(State state){
		this.state = state;
	}
	
	public Path getPath(){
		return path;
	}
	
	public State getState(){
		return state;
	}
}
