package game.ui;

import game.core.Player;
import game.core.Enemy;
import game.system.CombatSystem;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class SceneManager {

    private static Stage stage;
    private static int width;
    private static int height;

    public static void init(Stage primaryStage, int w, int h) {
        stage = primaryStage;
        width = w;
        height = h;
    }

    public static void showStartMenu() {
        StartMenu menu = new StartMenu();
        stage.setScene(menu.createScene());
        stage.show();
    }

    public static void showCharacterCreation() {
        CharacterCreation cc = new CharacterCreation(width, height);
        stage.setScene(new Scene(cc, width, height));
        stage.show();
    }

    public static void showTrainingScreen(Player player) {
        TrainingScreen ts = new TrainingScreen(player);
        stage.setScene(ts.createScene());
        stage.show();
    }

    public static void showBattleScreen(Player player, Enemy enemy, CombatSystem combatSystem, Consumer<Boolean> afterBattle) {
        BattleScreen battleScreen = new BattleScreen(player, enemy, combatSystem, afterBattle);
        stage.setScene(battleScreen.createScene());
        stage.show();
    }

    public static void showEndScreen(boolean playerWon, Player player) {
        EndScreen es = new EndScreen(width, height, playerWon, player);
        stage.setScene(new Scene(es, width, height));
        stage.show();
    }
}
