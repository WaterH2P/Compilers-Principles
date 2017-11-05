package logic;

import symbol.REKind;
import symbol.RE;
import symbol.State;

public class test {
	public static void main(String[] args){
//		String REStr = "((a|b)*(ab))*(a|b)";
//		String REStr = "((a|b)*ab)*";
		String REStr = "(ab|cd)*ab";
//		String REStr = "a*";
//		String RREStrE = "((a|b|c)*(ab))*";
		
		Transform transform = new Transform();
		
		System.out.println("\n" + "REToNFA:");
		State state = transform.REToNFA(REStr);
		System.out.println();
		
		state.nameEveryStateFromZero("RE");
		state.showEveryState();
		
		NFA.addNFA(REKind.Letter, transform.REToNFA(RE.getRE(REKind.Letter)));
		NFA.addNFA(REKind.Digit, transform.REToNFA(RE.getRE(REKind.Digit)));
		NFA.addNFA(REKind.KeyWord, transform.REToNFA(RE.getRE(REKind.KeyWord)));
		NFA.addNFA(REKind.Operator, transform.REToNFA(RE.getRE(REKind.Operator)));
		NFA.addNFA(REKind.Identifier, transform.REToNFA(RE.getRE(REKind.Identifier)));
		NFA.addNFA(REKind.Number, transform.REToNFA(RE.getRE(REKind.Number)));
		
		System.out.println(RE.getRE(REKind.Operator));
		state = NFA.getNFA(REKind.Operator);
		state.nameEveryStateFromZero(REKind.Operator.toString());
		state.showEveryState();
	}
}
