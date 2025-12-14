package game.ui;

import game.core.Player;
import game.core.Enemy;
import game.core.Stat;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.Consumer;

public class BattleScreen {

    private final Player player;
    private final Enemy enemy;
    private final Consumer<Boolean> afterBattleCallback;

    public BattleScreen(Player player, Enemy enemy, Consumer<Boolean> afterBattle) {
        this.player = player;
        this.enemy = enemy;
        this.afterBattleCallback = afterBattle;
    }

    public Scene createScene() {
        StackPane root = new StackPane();

        // Background
        ImageView bg = UIUtils.loadImageView("battle_bg.jpg", 800, 600, false);

        VBox main = new VBox(20);
        main.setAlignment(Pos.TOP_CENTER);
        main.setPadding(new Insets(15));

        // Title
        Label title = new Label("⚔️ Battle!");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 28));
        title.setTextFill(Color.GOLD);
        title.setEffect(new DropShadow(5, Color.BLACK));

        // Characters display
        HBox characters = new HBox(50);
        characters.setAlignment(Pos.CENTER);

        ImageView playerImg = UIUtils.loadImageView(
                "player_" + player.getProfession().name().toLowerCase() + "_battle.png",
                150, 150, true
        );
        ImageView enemyImg = UIUtils.loadImageView(
                "enemy_" + enemy.getName().toLowerCase().replace(" ", "") + ".png",
                150, 150, true
        );

        characters.getChildren().addAll(playerImg, enemyImg);

        // Stats display
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

        // Determine outcome (for demo, simple HP comparison)
        boolean playerWon = player.getStats().getHp() >= enemy.getStats().getHp();

        Label result = new Label(playerWon ? "🎉 Victory!" : "💀 Defeat!");
        result.setFont(Font.font("Verdana", FontWeight.BOLD, 26));
        result.setTextFill(Color.GOLD);
        result.setEffect(new DropShadow(5, Color.BLACK));

        // Continue button
        Button continueBtn = new Button("Continue");
        continueBtn.setStyle("-fx-background-color:#3b2f2f;-fx-text-fill:#d4af37;-fx-font-weight:bold;");
        continueBtn.setOnAction(e -> {
            if (afterBattleCallback != null) afterBattleCallback.accept(playerWon);
        });

        main.getChildren().addAll(title, characters, statsBox, result, continueBtn);
        root.getChildren().addAll(bg, main);

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
