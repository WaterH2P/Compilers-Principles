package symbol;

import logic.Handle;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

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
		try{
			FileWriter writer = new FileWriter(Handle.outputFilePath, true);
			writer.write("< " + this.kind + " , " + this.value + " >\n");
			writer.close();
		}catch(FileNotFoundException e) {
			System.out.print(Handle.outputFilePath + "is not found");
		}catch(IOException e) {
			System.out.print(Handle.outputFilePath + "has IOException");
		}
//		System.out.println("< " + this.kind + " , " + this.value + " >");
	}
}
