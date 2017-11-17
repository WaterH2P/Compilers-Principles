package logic;

public class Main {
	
	// 程序的入口
	public static void main(String[] args){
		Transform transform = new Transform();
		transform.initialize();
		
		Judge judge = new Judge();
		judge.judge();
	}
}
