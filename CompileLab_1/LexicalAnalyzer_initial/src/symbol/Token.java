package symbol;

public class Token {

	private REKind kind = null;
	
	private String value = "";
	
	public Token(REKind kind, String value){
		this.kind = kind;
		this.value = value;
	}
	
	public Token(REKind kind, char value){
		this.kind = kind;
		this.value = ""+value;
	}
	
	public void show(){
		System.out.println("< " + this.kind + " , " + this.value + " >");
	}
}
