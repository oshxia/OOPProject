package game.ui;

import game.core.Player;
import game.core.Enemy;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private static Stage stage;
    private static int width;
    private static int height;

    public static void init(Stage primaryStage, int w, int h) {
        stage = primaryStage;
        width = w;
        height = h;
    }

    // START MENU
    public static void showStartMenu() {
        StartMenu menu = new StartMenu();
        stage.setScene(menu.createScene());
        stage.show();
    }

    // CHARACTER CREATION
    public static void showCharacterCreation() {
        CharacterCreation cc = new CharacterCreation(width, height);
        stage.setScene(new Scene(cc, width, height));
        stage.show();
    }

    // TRAINING SCREEN
    public static void showTrainingScreen(Player player) {
        TrainingScreen ts = new TrainingScreen(player);
        stage.setScene(ts.createScene());
        stage.show();
    }

    // BATTLE SCREEN
    public static void showBattleScreen(Player player, Enemy enemy) {
        BattleScreen bs = new BattleScreen(player, enemy);
        stage.setScene(bs.createScene());
        stage.show();
    }

    // END SCREEN
    public static void showEndScreen(boolean playerWon, Player player) {
        EndScreen es = new EndScreen(width, height, playerWon, player);
        stage.setScene(new Scene(es, width, height));
        stage.show();
    }
}
