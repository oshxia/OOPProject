package game.ui;


import javafx.scene.Scene;
import javafx.stage.Stage;


public class SceneManager {
private static Stage primaryStage;
private static int width;
private static int height;


public static void init(Stage stage, int w, int h) {
primaryStage = stage;
width = w;
height = h;
}


public static void showStartMenu() {
StartMenu start = new StartMenu(width, height);
Scene scene = new Scene(start, width, height);
primaryStage.setScene(scene);
}


public static void showCharacterCreation() {
CharacterCreation cc = new CharacterCreation(width, height);
Scene scene = new Scene(cc, width, height);
primaryStage.setScene(scene);
}


public static void showBattleScreen(game.core.Player player, game.core.Enemy enemy) {
BattleScreen bs = new BattleScreen(width, height, player, enemy);
Scene scene = new Scene(bs, width, height);
primaryStage.setScene(scene);
}


public static void showEndScreen(boolean playerWon) {
EndScreen es = new EndScreen(width, height, playerWon);
Scene scene = new Scene(es, width, height);
primaryStage.setScene(scene);
}
}