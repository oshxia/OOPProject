package game.ui;

import game.core.Player;
import game.core.Enemy;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class BattleScreen {

    private final Player player;
    private final Enemy enemy;

    public BattleScreen(Player player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
    }

    public Scene createScene() {
        // Background
        ImageView bg = UIUtils.loadImageView("battle_bg.png", 800, 600, false);

        // Player image
        ImageView playerImg = UIUtils.loadImageView(
                "player_" + player.getProfession().name().toLowerCase() + "_battle.png",
                150, 150, true
        );

        // Enemy image
        ImageView enemyImg = UIUtils.loadImageView(
                "enemy_" + enemy.getName().toLowerCase() + ".png",
                150, 150, true
        );

        HBox characters = new HBox(50, playerImg, enemyImg);
        characters.setAlignment(Pos.CENTER);

        // Stats display
        Label playerStats = new Label(
                "Player: " + player.getName() +
                        " | STR:" + player.getStats().getStrength() +
                        " | AGI:" + player.getStats().getAgility() +
                        " | INT:" + player.getStats().getIntelligence() +
                        " | HP:" + player.getStats().getHp()
        );

        Label enemyStats = new Label(
                "Enemy: " + enemy.getName() +
                        " | STR:" + enemy.getStats().getStrength() +
                        " | AGI:" + enemy.getStats().getAgility() +
                        " | INT:" + enemy.getStats().getIntelligence() +
                        " | HP:" + enemy.getStats().getHp()
        );

        VBox statsBox = new VBox(10, playerStats, enemyStats);
        statsBox.setAlignment(Pos.CENTER);

        // Back button
        Button backBtn = new Button("Back to Menu");
        backBtn.setOnAction(e -> SceneManager.showStartMenu());

        VBox layout = new VBox(30, characters, statsBox, backBtn);
        layout.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(bg, layout);
        return new Scene(root, 800, 600);
    }
}
