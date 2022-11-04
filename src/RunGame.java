
public class RunGame {
	public static void main(String[] args) {
		Run(); 
		
	}
	
	public static void Run() {
		Game game = new Game();
		while(true) {
			game.gameLoop();
		}
		
		
	}
}
