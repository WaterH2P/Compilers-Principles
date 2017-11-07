package logic;

import java.io.*;
import java.util.ArrayList;

import symbol.REKind;
import symbol.RE;
import symbol.Token;

public class Handle {
	
	private static String input = "";
	
	private Symbol symbol = new Symbol();
	private static NFA nfa = new NFA();
	
	private boolean isSentence = false;
	private ArrayList<String> wordsList = new ArrayList<>();
	
	public static final String inputFilePath = "symbol/Input.txt";
	public static final String outputFilePath = "symbol/Output.txt";
	
	public static void main(String args[]){
		try{
			FileWriter writer = new FileWriter(Handle.outputFilePath);
			writer.write("");
			writer.close();
		}catch(FileNotFoundException e) {
			System.out.println(Handle.outputFilePath + "is not found");
		}catch(IOException e) {
			System.out.println(Handle.outputFilePath + "has IOException");
		}
		
		try{
			FileInputStream fileName = new FileInputStream(inputFilePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(fileName));
			String data = null;
			while( (data = br.readLine())!=null ){
				input += data;
			}
		}catch(FileNotFoundException e) {
			System.out.print(inputFilePath + "is not found");
		}catch(IOException e) {
			System.out.print(inputFilePath + "has IOException");
		}
		
		Transform transform = new Transform();
		NFA.addNFA( REKind.Letter,      transform.REToNFA( RE.getRE(REKind.Letter)) );
		NFA.addNFA( REKind.Digit,       transform.REToNFA( RE.getRE(REKind.Digit)) );
		NFA.addNFA( REKind.KeyWord,     transform.REToNFA( RE.getRE(REKind.KeyWord)) );
		NFA.addNFA( REKind.Operator,    transform.REToNFA( RE.getRE(REKind.Operator)) );
		NFA.addNFA( REKind.Identifier,  transform.REToNFA( RE.getRE(REKind.Identifier)) );
		NFA.addNFA( REKind.Number,      transform.REToNFA( RE.getRE(REKind.Number)) );
		
		Handle handle = new Handle();
		NFA nfa = new NFA();
		Symbol symbol = new Symbol();
		
		String words[] = handle.divideString( input );
		for( int i=0; i<words.length; i++ ){
			String word =words[i];
			String sub = "";
			while( word.length()>0 ){
				char firstLetter = word.charAt(0);
				if( RE.getRE(REKind.Decision).length()>0 ){
					System.out.println("BBBBB");
					nfa.findMatch("", word, NFA.getNFA(REKind.Decision));
					if( NFA.getMaxMatch().length() > 0 ){
						new Token(REKind.Decision, NFA.getMaxMatch()).show();
						word = word.substring(NFA.getMaxMatch().length());
					}
				}
				else{
					if( symbol.isSeparator(firstLetter) ){
//					System.out.println("AAAAA" + i + firstLetter);
						new Token(REKind.Separator, firstLetter).show();
						word = word.substring(1);
					} else if( symbol.isQuotationMark(firstLetter) ){
						int iTry = 1;
						while( iTry < word.length() && !symbol.isQuotationMark(word.charAt(iTry)) ){
							iTry++;
						}
						if( symbol.isQuotationMark(word.charAt(iTry)) ){
							new Token(REKind.Sentence, word.substring(0, iTry + 1)).show();
							word = word.substring(iTry + 1);
						} else{
							word = word.substring(1);
						}
					} else{
//					System.out.println("BBBBB" + i);
						if( symbol.isOperator(firstLetter) ){
//						nfa.findMatch("", word, NFA.getNFA(REKind.Operator));
//						if( NFA.getMaxMatch().length() > 0 ){
//							new Token(REKind.Operator, NFA.getMaxMatch()).show();
//							word = word.substring(NFA.getMaxMatch().length());
//						}
							if( word.length()==1 ){
								new Token(REKind.Operator, word).show();
								word = "";
							} else{
								if( symbol.isOperator(word.charAt(1)) ){
									new Token(REKind.Operator, word.substring(0, 2)).show();
									word = word.substring(2);
								} else{
									new Token(REKind.Operator, firstLetter).show();
									word = word.substring(1);
								}
							}
						} else{
//						System.out.println("CCCCC" + i);
							int iTry = 1;
							while( iTry < word.length() && symbol.isLetter(word.charAt(iTry)) ){
								iTry++;
							}
							String subSave = word.substring(iTry);
							word = word.substring(0, iTry);
							nfa.findMatch("", word, NFA.getNFA(REKind.KeyWord));
							if( NFA.getMaxMatch().length() > 0 && NFA.getMaxMatch().length()==word.length() ){
								new Token(REKind.KeyWord, NFA.getMaxMatch()).show();
								word = word.substring(NFA.getMaxMatch().length());
								word += subSave;
							} else{
								word += subSave;
//							System.out.println("DDDDD" + i);
								if( symbol.isNumber(firstLetter) ){
									nfa.findMatch("", word, NFA.getNFA(REKind.Number));
									if( NFA.getMaxMatch().length() > 0 ){
										new Token(REKind.Number, NFA.getMaxMatch()).show();
										word = word.substring(NFA.getMaxMatch().length());
									}
								} else{
//								System.out.println("EEEEE" + i);
//								nfa.findMatch("", word, NFA.getNFA(REKind.Identifier));
//								if( NFA.getMaxMatch().length() > 0 ){
//									new Token(REKind.Identifier, NFA.getMaxMatch()).show();
//									word = word.substring(NFA.getMaxMatch().length());
//								}
									if( symbol.isLetter(firstLetter) ){
										iTry = 1;
										while( iTry < word.length()
												&& (symbol.isLetter(word.charAt(iTry)) || symbol.isNumber(word.charAt(iTry))) ){
											iTry++;
										}
										if( iTry > RE.maxKeyWordLength ){
											iTry = RE.maxKeyWordLength;
										}
										new Token(REKind.Identifier, word.substring(0, iTry)).show();
										word = word.substring(iTry);
									}
									else{
											new Token(REKind.Wrong, word);
											word = "";
									}
								}
							}
						}
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

