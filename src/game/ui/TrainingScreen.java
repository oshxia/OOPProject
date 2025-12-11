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

import java.util.ArrayList;
import java.util.List;

public class TrainingScreen {

    private final Player player;
    private final List<Enemy> enemies;
    private int currentCycle = 0;
    private final int totalCycles = 7;

    private Label playerStatsLabel;
    private Label enemyStatsLabel;
    private Button trainStrBtn, trainAgiBtn, trainIntBtn, nextBtn;

    public TrainingScreen(Player player) {
        this.player = player;

        // Initialize enemies
        enemies = new ArrayList<>();
        enemies.add(new Enemy("Goblin", new Stat(20, 20, 30)));
        enemies.add(new Enemy("Orc", new Stat(25, 25, 20)));
        enemies.add(new Enemy("Skeleton", new Stat(15, 30, 20)));
    }

    public Scene createScene() {
        StackPane root = new StackPane();

        // Background
        ImageView bg = UIUtils.loadImageView("battle_bg.png", 800, 600, false);

        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setPadding(new Insets(20));

        // Title
        Label title = new Label("🏋️‍♂️ Training Phase");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 32));
        title.setTextFill(Color.LIGHTYELLOW);
        title.setEffect(new DropShadow(3, Color.BLACK));

        // Player & Enemy images
        ImageView playerImg = UIUtils.loadImageView(
                "player_" + player.getProfession().name().toLowerCase() + "_battle.png",
                150, 150, true
        );

        HBox enemyImagesBox = new HBox(20);
        enemyImagesBox.setAlignment(Pos.CENTER);
        for (Enemy e : enemies) {
            ImageView img = UIUtils.loadImageView(
                    "enemy_" + e.getName().toLowerCase() + ".png",
                    120, 120, true
            );
            enemyImagesBox.getChildren().add(img);
        }

        HBox imagesBox = new HBox(50, playerImg, enemyImagesBox);
        imagesBox.setAlignment(Pos.CENTER);

        // Stats labels
        playerStatsLabel = new Label();
        playerStatsLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
        playerStatsLabel.setTextFill(Color.LIGHTGREEN);
        playerStatsLabel.setBackground(new Background(new BackgroundFill(Color.rgb(0,0,0,0.5), new CornerRadii(5), Insets.EMPTY)));
        playerStatsLabel.setPadding(new Insets(8));

        enemyStatsLabel = new Label();
        enemyStatsLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
        enemyStatsLabel.setTextFill(Color.LIGHTCORAL);
        enemyStatsLabel.setBackground(new Background(new BackgroundFill(Color.rgb(0,0,0,0.5), new CornerRadii(5), Insets.EMPTY)));
        enemyStatsLabel.setPadding(new Insets(8));

        updateStatsDisplay();

        // Training buttons
        trainStrBtn = new Button("Train Strength (STR)");
        trainAgiBtn = new Button("Train Agility (AGI)");
        trainIntBtn = new Button("Train Intelligence (INT)");

        for (Button btn : new Button[]{trainStrBtn, trainAgiBtn, trainIntBtn}) {
            btn.setStyle("-fx-font-weight: bold; -fx-background-color: #333; -fx-text-fill: white;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #555; -fx-text-fill: white; -fx-font-weight: bold;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-font-weight: bold;"));
        }

        trainStrBtn.setOnAction(e -> trainStat("STR"));
        trainAgiBtn.setOnAction(e -> trainStat("AGI"));
        trainIntBtn.setOnAction(e -> trainStat("INT"));

        HBox trainButtons = new HBox(12, trainStrBtn, trainAgiBtn, trainIntBtn);
        trainButtons.setAlignment(Pos.CENTER);

        // Next cycle button
        nextBtn = new Button("Next Cycle");
        nextBtn.setDisable(true);
        nextBtn.setStyle("-fx-font-weight: bold; -fx-background-color: gold; -fx-text-fill: black;");
        nextBtn.setOnAction(e -> {
            currentCycle++;
            if (currentCycle < totalCycles) {
                trainStrBtn.setDisable(false);
                trainAgiBtn.setDisable(false);
                trainIntBtn.setDisable(false);
                nextBtn.setDisable(true);
            } else {
                // Go to first enemy battle
                SceneManager.showBattleScreen(player, enemies.get(0));
            }
        });

        mainLayout.getChildren().addAll(title, imagesBox, playerStatsLabel, enemyStatsLabel, trainButtons, nextBtn);
        root.getChildren().addAll(bg, mainLayout);

        return new Scene(root, 800, 600);
    }

    // Train player stat and enemies random stats
    private void trainStat(String stat) {
        switch (stat) {
            case "STR" -> player.getStats().increaseStrength(5);
            case "AGI" -> player.getStats().increaseAgility(5);
            case "INT" -> player.getStats().increaseIntelligence(5);
        }

        // Enemy gains random stat (simulate increaseRandomStat)
        for (Enemy enemy : enemies) {
            int rand = (int)(Math.random() * 3);
            switch (rand) {
                case 0 -> enemy.getStats().increaseStrength(5);
                case 1 -> enemy.getStats().increaseAgility(5);
                case 2 -> enemy.getStats().increaseIntelligence(5);
            }
        }

        updateStatsDisplay();

        trainStrBtn.setDisable(true);
        trainAgiBtn.setDisable(true);
        trainIntBtn.setDisable(true);
        nextBtn.setDisable(false);
    }

    private void updateStatsDisplay() {
        Stat s = player.getStats();
        playerStatsLabel.setText(String.format(
                "PLAYER STATS:\nSTR: %d | AGI: %d | INT: %d\nHP: %d | SPD: %d | ACC: %d | EVA: %d",
                s.getStrength(), s.getAgility(), s.getIntelligence(),
                s.getHp(), s.getSpeed(), s.getAccuracy(), s.getEvasion()
        ));

        StringBuilder sb = new StringBuilder("ENEMY STATS:\n");
        for (Enemy e : enemies) {
            Stat es = e.getStats();
            sb.append(String.format("%s: STR:%d AGI:%d INT:%d HP:%d\n",
                    e.getName(), es.getStrength(), es.getAgility(), es.getIntelligence(), es.getHp()));
        }
        enemyStatsLabel.setText(sb.toString());
    }
}
