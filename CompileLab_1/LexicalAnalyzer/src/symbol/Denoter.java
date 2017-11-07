package symbol;

public class Denoter {
	private Path path = null;
	private Closure closure = null;
	
	public Denoter(Path path, Closure closure){
		this.path = path;
		this.closure = closure;
	}
	
	public void setPath(Path path){
		this.path = path;
	}
	public void setClosure(Closure closure){
		this.closure = closure;
	}
	
	public Path getPath(){
		return path;
	}
	
	public Closure getClosure(){
		return closure;
	}
}
