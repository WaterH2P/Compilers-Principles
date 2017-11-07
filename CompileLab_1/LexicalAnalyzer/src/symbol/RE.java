package symbol;

import logic.Symbol;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 初始化正则表达式
 */
public class RE {
	private static String Letter = "[a-zA-Z]";
	private static String Digit = "[0-9]";
	private static String KeyWord = "";
	private static String Identifier = "{Letter}({Letter}|{Digit})*";
	private static String Number = "[1-9]{Digit}*";
	private static String Operator = "";
	private static String Decision = "";
	
	public static int maxKeyWordLength = 15;
	
	private static HashMap<REKind, String> REs = new HashMap<>();
	
	static {
		KeyWord = initializeDefaultValue(KeyWord, "symbol/KeyWord.txt");
		Operator = initializeDefaultValue(Operator, "symbol/Operator.txt");
		Decision = initializeDefaultValue(Decision, "symbol/Decision.txt");
		REs.put( REKind.Letter, Letter );
		REs.put( REKind.Digit, Digit );
		REs.put( REKind.KeyWord, KeyWord );
		REs.put( REKind.Identifier, Identifier );
		REs.put( REKind.Number, Number );
		REs.put( REKind.Operator, Operator );
		if( Decision.length()>0 ){
			REs.put(REKind.Decision, Decision);
		}
	}
	
	private static String initializeDefaultValue(String RE, String filePath){
		try {
			ArrayList<String> values = new ArrayList<>();
			
			FileInputStream fileName = new FileInputStream(filePath);
			BufferedReader br = new BufferedReader( new InputStreamReader(fileName) );
			String data = null;
			while( (data = br.readLine())!=null ){
				String subDate = "";
				for(int i=0; i<data.length(); i++){
					char letter = data.charAt(i);
					if( letter!=Symbol.whiteSpace ){
						subDate += letter;
					}
					if( letter==Symbol.whiteSpace || i==data.length()-1 ){
						if( !values.contains(subDate) ){
							if( RE.length()==0 ){
								RE = subDate;
							}
							else{
								RE = RE + "|" + subDate;
							}
							values.add(subDate);
							subDate = "";
						}
					}
				}
			}
		}catch(FileNotFoundException e) {
			System.out.print(filePath + "is not found");
		}catch(IOException e) {
			System.out.print(filePath + "has IOException");
		}
		return RE;
	}
	
	public static String getRE(REKind kind){
		if( REs.containsKey(kind) ){
			return REs.get(kind);
		}
		return "";
	}
}
