package symbol;

import com.sun.xml.internal.bind.v2.model.core.ID;
import logic.Symbol;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class RegularExpression {
	public static String Letter = "[a-zA-Z]";
	public static String Digit = "[0-9]";
	public static String KeyWord = "";
	public static String Identifier = "{Letter}({Letter}|{Digit})*";
	public static String Number = "[1-9]{Digit}*";
	public static String Operator = "";
	
	private static HashMap<String, String> info = new HashMap<>();
	
	static {
		KeyWord = initializeDefaultValue(KeyWord, "symbol/KeyWord.txt");
		Operator = initializeDefaultValue(Operator, "symbol/Operator.txt");
		info.put(REKind.Letter.toString(), Letter);
		info.put(REKind.Digit.toString(), Digit);
		info.put(REKind.KeyWord.toString(), KeyWord);
		info.put(REKind.Identifier.toString(), Identifier);
		info.put(REKind.Number.toString(), Number);
		info.put(REKind.Operator.toString(), Operator);
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
	
	public static String getRE(String sign){
		if( info.containsKey(sign) ){
			return info.get(sign);
		}
		return "";
	}
}
