package game.ui;

import game.core.Player;
import game.core.Enemy;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Label;

import java.util.function.Consumer;

public class BattleScreen {

    private final Player player;
    private final Enemy enemy;
    private final Consumer<Boolean> afterBattleCallback;

    public BattleScreen(Player player, Enemy enemy, Consumer<Boolean> callback) {
        this.player = player;
        this.enemy = enemy;
        this.afterBattleCallback = callback;
    }

    public Scene createScene() {
        StackPane root = new StackPane();
        ImageView bg = UIUtils.loadImageView("battle_bg.jpg", 800, 600, false);

        HBox characters = new HBox(50);
        characters.setAlignment(Pos.CENTER);

        ImageView playerImg = UIUtils.loadImageView("player_" + player.getProfession().name().toLowerCase() + "_battle.png", 150, 150, true);
        ImageView enemyImg = UIUtils.loadImageView("enemy_" + enemy.getName().toLowerCase().replace(" ", "") + ".png", 150, 150, true);

        characters.getChildren().addAll(playerImg, enemyImg);

        Label playerStats = createStatsLabel(
                "PLAYER\nSTR: " + player.getStats().getStrength() +
                        " | AGI: " + player.getStats().getAgility() +
                        " | INT: " + player.getStats().getIntelligence() +
                        "\nHP: " + player.getStats().getHp(),
                Color.LIGHTGREEN
        );

        Label enemyStats = createStatsLabel(
                "ENEMY\nSTR: " + enemy.getStats().getStrength() +
                        " | AGI: " + enemy.getStats().getAgility() +
                        " | INT: " + enemy.getStats().getIntelligence() +
                        "\nHP: " + enemy.getStats().getHp(),
                Color.LIGHTCORAL
        );

        HBox statsBox = new HBox(40, playerStats, enemyStats);
        statsBox.setAlignment(Pos.CENTER);

        boolean playerWon = player.getStats().getHp() > enemy.getStats().getHp(); // simple example

        Label result = new Label(playerWon ? "🎉 You Won!" : "💀 You Lost!");
        result.setFont(Font.font("Verdana", FontWeight.BOLD, 26));
        result.setTextFill(Color.GOLD);
        result.setStyle("-fx-effect: dropshadow(gaussian, black, 5, 0.5, 2, 2);");

        Button continueBtn = new Button("Continue");
        continueBtn.setStyle("-fx-font-weight:bold; -fx-background-color:#3b2f2f; -fx-text-fill:#d4af37;");
        continueBtn.setOnAction(e -> {
            if (afterBattleCallback != null) afterBattleCallback.accept(playerWon);
        });

        VBox layout = new VBox(20, characters, statsBox, result, continueBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(15));

        root.getChildren().addAll(bg, layout);
        return new Scene(root, 800, 600);
    }

    private Label createStatsLabel(String text, Color color) {
        Label l = new Label(text);
        l.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
        l.setTextFill(color);
        l.setBackground(new Background(new BackgroundFill(Color.rgb(0,0,0,0.55), new CornerRadii(5), Insets.EMPTY)));
        l.setPadding(new Insets(10));
        return l;
    }
}
