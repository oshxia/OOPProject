package game.ui;

import game.core.Player;
import game.core.Enemy;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class BattleScreen {

    private final Player player;
    private final Enemy enemy;

    public BattleScreen(Player player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
    }

    public Scene createScene() {
        // Background
        ImageView bg = UIUtils.loadImageView("battle_bg.jpg", 800, 600, false);

        // Player image
        ImageView playerImg = UIUtils.loadImageView(
                "player_" + player.getProfession().name().toLowerCase() + "_battle.png",
                150, 150, true
        );

        // Enemy image (Killer Rabbit, Mindflayer, Minotaur still load properly)
        ImageView enemyImg = UIUtils.loadImageView(
                "enemy_" + enemy.getName().toLowerCase().replace(" ", "") + ".png",
                150, 150, true
        );

        HBox characters = new HBox(50, playerImg, enemyImg);
        characters.setAlignment(Pos.CENTER);

        // Stats labels with styled backgrounds
        Label playerStats = createStatsLabel(
                "PLAYER: " + player.getName() +
                        " | STR:" + player.getStats().getStrength() +
                        " | AGI:" + player.getStats().getAgility() +
                        " | INT:" + player.getStats().getIntelligence() +
                        " | HP:" + player.getStats().getHp(),
                Color.LIGHTGREEN
        );

        Label enemyStats = createStatsLabel(
                "ENEMY: " + enemy.getName() +
                        " | STR:" + enemy.getStats().getStrength() +
                        " | AGI:" + enemy.getStats().getAgility() +
                        " | INT:" + enemy.getStats().getIntelligence() +
                        " | HP:" + enemy.getStats().getHp(),
                Color.LIGHTCORAL
        );

        VBox statsBox = new VBox(12, playerStats, enemyStats);
        statsBox.setAlignment(Pos.CENTER);

        // Back button
        Button backBtn = new Button("Back to Menu");
        backBtn.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: gold; -fx-text-fill: black;");
        backBtn.setOnAction(e -> SceneManager.showStartMenu());

        VBox layout = new VBox(30, characters, statsBox, backBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        StackPane root = new StackPane(bg, layout);
        return new Scene(root, 800, 600);
    }

    private Label createStatsLabel(String text, Color color) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
        lbl.setTextFill(color);
        lbl.setBackground(new Background(new BackgroundFill(Color.rgb(0,0,0,0.55), new CornerRadii(6), Insets.EMPTY)));
        lbl.setPadding(new Insets(10));
        return lbl;
    }
}
