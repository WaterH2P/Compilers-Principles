package logic;

import java.util.ArrayList;
import java.util.Stack;

import symbol.REKind;
import symbol.RegularExpression;
import symbol.State;
import symbol.Token;

public class Handle {
	
	private static String input = "private boolean isKeyWord(String word){\n" +
			"        String sb = \\\"hello sb!\"" +
			"        if( defaultValue_KeyWord.contains(word) )\n" +
			"            return true;\n" +
			"        return false;\n" +
			"    }";
//	private static String input = "private";
	
	private Symbol symbol = new Symbol();
	private static NFA nfa = new NFA();
	
	private boolean isSentence = false;
	private ArrayList<String> wordsList = new ArrayList<>();
	
	public static void main(String args[]){
		Transform transform = new Transform();
		NFA.addNFA( REKind.Letter, transform.REToNFA(REKind.Letter.toString(), RegularExpression.Letter) );
		NFA.addNFA( REKind.Digit, transform.REToNFA(REKind.Digit.toString(), RegularExpression.Digit) );
		NFA.addNFA( REKind.KeyWord, transform.REToNFA(REKind.KeyWord.toString(), RegularExpression.KeyWord) );
		NFA.addNFA( REKind.Operator, transform.REToNFA(REKind.Operator.toString(), RegularExpression.Operator) );
		NFA.addNFA( REKind.Identifier, transform.REToNFA(REKind.Identifier.toString(), RegularExpression.Identifier) );
		NFA.addNFA( REKind.Number, transform.REToNFA(REKind.Number.toString(), RegularExpression.Number) );
		
		Handle handle = new Handle();
		NFA nfa = new NFA();
		Symbol symbol = new Symbol();
		
		String words[] = handle.divideString( input );
		for( int i=0; i<words.length; i++ ){
			String word =words[i];
			String sub = "";
			while( word.length()>0 ){
				char firstLetter = word.charAt(0);
				if( symbol.isSeparator(firstLetter) ){
					System.out.println("AAAAA" + i + firstLetter);
					new Token( REKind.Separator, firstLetter ).show();
					word = word.substring(1);
				}
				else{
					System.out.println("BBBBB" + i);
					nfa.findMatch("", word, NFA.getNFA(REKind.Operator));
					if( NFA.getMaxMatch().length()>0 ){
						new Token( REKind.Operator, NFA.getMaxMatch() ).show();
						word = word.substring( NFA.getMaxMatch().length() );
					}
					else{
						System.out.println("CCCCC" + i);
						nfa.findMatch("", word, NFA.getNFA(REKind.KeyWord));
						if( NFA.getMaxMatch().length()>0 && NFA.getMaxMatch().length()==word.length() ){
							new Token( REKind.KeyWord, NFA.getMaxMatch() ).show();
							word = word.substring( NFA.getMaxMatch().length() );
						}
						else{
							System.out.println("DDDDD" + i);
							nfa.findMatch("", word, NFA.getNFA(REKind.Number));
							if( NFA.getMaxMatch().length()>0 ){
								new Token( REKind.Number, NFA.getMaxMatch() ).show();
								word = word.substring( NFA.getMaxMatch().length() );
							}
							else{
								System.out.println("EEEEE" + i);
								nfa.findMatch("", word, NFA.getNFA(REKind.Identifier));
								if( NFA.getMaxMatch().length()>0 ){
									new Token( REKind.Identifier, NFA.getMaxMatch() ).show();
									word = word.substring( NFA.getMaxMatch().length() );
								}
								else{
									new Token( REKind.Wrong, word );
									word = "";
								}
							}
						}
						System.out.println("maxMatch:  " + NFA.getMaxMatch());
					}
				}
			}
		}
	}

		
		
	/**
	 * 将传入句子按照 空格 切割为一个个 String
	 * @param sentence
	 * @return
	 */
	public String[] divideString(String sentence){
		// 前一个是反斜杠
		boolean beforeIsBackslash = false;
		
		String wordSave = "";
		
		for( int i=0; i<sentence.length(); i++ ){
			char letter = sentence.charAt(i);
			if( symbol.isLetter(letter) ){
				wordSave += letter;
			}
			else if( symbol.isBackslash(letter) ){
				if( beforeIsBackslash ){
					beforeIsBackslash = false;
					wordSave += "\\";
				}
				else{
					beforeIsBackslash = true;
				}
			}
			else if( beforeIsBackslash ){
				if( symbol.isQuotationMark(letter) ){
					wordSave = meetQuotationMark(wordSave);
				}
				else{
					wordSave = wordSave + "\\" + letter;
				}
				beforeIsBackslash = false;
			}
			else if( symbol.isQuotationMark(letter) ){
				wordSave = meetQuotationMark(wordSave);
			}
			else if( isSentence ){
				wordSave += letter;
			}
			else if( letter==Symbol.whiteSpace ){
				wordSave = meetWhiteSpace(wordSave, letter);
			}
			else{
				wordSave += letter;
			}
		}
		if( !wordsList.contains(wordSave) ){
			wordsList.add( wordSave );
		}
		
		int i = 0;
		String[] words = new String[wordsList.size()];
		for( String word: wordsList ){
			words[i++] = word;
		}
		return words;
	}
	
	private String meetQuotationMark(String wordSave){
		if( isSentence ){
			isSentence = false;
			wordSave = "\"" + wordSave + "\"";
			if( wordSave.length()>2 ){
				// 不是空字符串
				wordsList.add(wordSave);
			}
			wordSave = "";
		}
		else{
			isSentence = true;
		}
		return wordSave;
	}
	
	private String meetWhiteSpace(String wordSave, char letter){
		if( isSentence ){
			wordSave += letter;
		}
		else{
			if( wordSave.length()>0 ){
				wordsList.add(wordSave);
			}
			wordSave = "";
		}
		return wordSave;
	}
}

