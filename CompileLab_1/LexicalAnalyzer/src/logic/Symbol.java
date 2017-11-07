package logic;

public class Symbol {
	
	public static final char leftBracket = '(';
	public static final char rightBracket = ')';
	public static final char leftSquareBracket = '[';
	public static final char rightSquareBracket = ']';
	public static final char leftBrace = '{';
	public static final char rightBrace = '}';
	public static final char connectSymbol = '-';
	public static final char whiteSpace = ' ';
	public static final char backslash = '\\';
	public static final char doubleQuotationMark = '\"';
	public static final char singleQuotationMark = '\'';
	
	// 循环操作
	public static final char star = '*';
	// 或操作
	public static final char or = '|';
	// 连接操作
	public static final char connect = '·';
	
	public static final char GT = '>';
	public static final char LT = '<';
	public static final char EQ = '=';
	public static final char PLUS = '+';
	public static final char MINUS = '-';
	public static final char DIV = '/';
	
	public boolean isLetter(char letter){
		if( letter>='a' && letter<='z' ){
			return true;
		}
		else if( letter>='A' && letter<='Z' ){
			return true;
		}
		return false;
	}
	
	public boolean isNumber(char letter){
		if( letter>='0' && letter<='9' ){
			return true;
		}
		return false;
	}
	
	public boolean isOperator(char letter){
		if( letter==GT || letter==LT || letter==EQ || letter==PLUS
				|| letter==MINUS || letter==DIV ){
			return true;
		}
		return false;
	}
	
	public boolean isStateOperator(char letter){
		if( letter==or || letter==connect ){
			return true;
		}
		return false;
	}
	
	public boolean isSeparator(char letter){
		if( letter==',' || letter==';' || letter==':'
				|| letter=='(' || letter==')' || letter=='{'
				|| letter=='}' || letter=='[' || letter==']' ){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断是否是反斜杠
	 * @param letter：传入的字符
	 * @return
	 */
	public boolean isBackslash(char letter){
		if( letter=='\\' ){
			return true;
		}
		return false;
	}
	
	/**
	 * 是否为 单引号或双引号
	 * @param letter
	 * @return
	 */
	public boolean isQuotationMark(char letter){
		if( letter=='\"' || letter=='\'' ){
			return true;
		}
		return false;
	}

}
