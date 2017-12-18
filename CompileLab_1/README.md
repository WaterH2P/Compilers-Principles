### 1. Motivation / Aim

```
构建词法分析器
```

### 2. Content description

```
词法分析器
```

### 3. Ideas / Methods

```

```

### 4. Assumptions

```
1. 输入 Java 代码，解析其中的 Token 根据 src/symbol/RE 中定义的正则表达式
2. 在 symbol/Decision 输入自定义正则表达式，在 symbol/Input 输入解析的字符串

如果 symbol/Decision 中输入自定义正则表达式，则用其解析 Input 中字符串，否则使用 Java 代码分析器。

可修改：
	symbol/Decision.txt
	symbol/Input.txt
Run：
	scr/logic/Handle.main()
输出：
	symbol/Output.txt
```

### 5. Related FA description

```Java
public static String Letter = "[a-zA-Z]";
public static String Digit = "[0-9]";
public static String KeyWord = "package|import|...|boolean|int|double";
public static String Identifier = "{Letter}({Letter}|{Digit})*";
public static String Number = "[1-9]{Digit}*";
public static String Operator = "+|-|/|+=|-=|/=|++|--";
```

### 6. Description of important Data Structures

```
State:
	表示一个状态
	存有向后的 Stick 的 list: upSticks
	存有向前的 Stick 的 list: downSticks
		解决循环向前状态的转换
	存有向后所有路径的值: upValues
		保存的值都是唯一的
	存有向前所有路径的值: downValues
Stick:
	一条路径
	一个通过该路径到达的下一个状态
Path:
	保存路径的值，如果是类似[0-9]，则使用 List 将值存起来
Closure:
	状态集合，NFA 转 DFA 中的状态集
Denoter:
	一条路径
	一个通过该路径到达的下一个状态集合
```

### 7.  Description of core Algorithms

```
1. 将正则表达式先进行'·'的添加
	src/logic/Transform: private
		String preprocess(String RE)
	
2. 将中缀表达式转换为后缀表达式
	src/logic/Transform: private
		String REInfixToSuffix(String RE)
	'*'优先级最高，'·'次之，'|'最后
	
3. 将后缀表达式转换为 NFA
	src/logic/Transform: 
		public State REToNFA(String name, String RE)
		private State stateConnectState(State stateFront, State stateAfter)
			状态连接运算
		private State stateOrState(State stateUp, State stateDown)
			状态或运算
		private State stateStar(State state)
			状态 * 运算
		...
4. NFA 转 DFA
	src/logic/Transform: 
		public Closure NFAToDFA(State state)
		private Closure calCore(Closure closure, String value)
		private Closure calClosure(Closure core)
```

### 8 .Use cases on running

### 9. Problems occurred and related solutions 

```
题目理解困难，对输入和输出不是很理解
	听其他同学对问题的理解
二叉树方式构建 NFA 导致 KeyWord 的 NFA 状态数量过多
	使用二叉树，但每个方向的分支可以多于一个。状态少了 100 多个。
NFA 过于庞大，导致检测字符串很长时，耗时巨长。
```

### 10. Your feelings and comments

```
tired
```

