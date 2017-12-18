### 1. Motivation / Aim

```
构建语法分析器
```

### 2. Content description

```
语法分析器
```

### 3. Ideas / Methods

```
 LR(1)
```

### 4. Assumptions

```
1. 输入文法，根据 LR(1) 文法规则构建分析表
2. 输入 Tokens，根据 LR(1) 分析表判断 Tokens 是否满足该文法

可修改：
	symbol/CFGInput.txt
		不支持空值
		推导符号使用: ->
		[A-Z]: 默认非终结符
	symbol/InputToken.txt
Run：
	scr/logic/Main.main()
输出：
	symbol/Output.txt
```

### 5. Related FA description

```java

```

### 6. Description of important Data Structures

```
Closure:
	用于存储每一个非终结符的表达式闭包
Expression:
	存储每一个表达式
	保存左部的值和右部的值，以及 dot 位置
	实现移进将 dot 位置后移
Nonterminal:
	用于计算每一个非终结符 First 和 Follow
State:
	保存一组表达式
	保存所有转换路径和结果
	实现状态通过某一个符号进行转换
	实现计算每一个状态里面表达式的预测符
```

### 7.  Description of core Algorithms

```java
1.	先清空 Output.txt 文件

2.	初始化表达式
		保存在: Transform: private static ArrayList<Expression> expressions;
		将表达式输出到 Output.txt 文件中
	得到所有终结符和非终结符
		保存在: Transform: private static ArrayList<Character> Vns;
			   Transform: private static ArrayList<Character> Vts;
		将 Vns 和 Vts 输出到 Output.txt 文件中
	计算每一个非终结符的闭包
		保存在: Transform: private static HashMap<Character, Closure> closures;

3.	计算每一个非终结符的 First 和 Follow
		保存在: Transform: private static HashMap<Character, Nonterminal> nonterminals;
		将每一个非终结符的 First 和 Follow 输出到 Output.txt 文件中

4.	根据开始表达式构建初始状态
	调用 State.through(char letter) 方法计算出下一个状态
		使用 State.calPredictChar() 计算出每一个状态的预测符
		保存在: Transform: private static HashMap<Integer, State> states;
		将状态转换过程输出到 Output.txt 文件中

5.	根据 states 中 Action 和 Goto 以及每一个表达式的预测符构建 PPT
		保存在: Transform: private static int[][] PPTState;
			   Transform: private static char[][] PPTAction;
		将 PPT 输出到 Output.txt 文件中

6.	初始化输入的 Tokens，就是读取文件第一行并删去空格
		保存在: Judge: private static String InputToken
		
7.	使用状态栈、字符栈和输入队列来判断输入是否符合之前的 LR(1) 文法
	将分析过程输出到 Output.txt 文件中
```

### 8 .Use cases on running

```
见 Example.txt
```

### 9. Problems occurred and related solutions

### 10. Your feelings and comments

```
happy
```

