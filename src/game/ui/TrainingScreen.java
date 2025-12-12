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
    private VBox enemyStatsBox;
    private Button trainStrBtn, trainAgiBtn, trainIntBtn, nextBtn;

    public TrainingScreen(Player player) {
        this.player = player;

        enemies = new ArrayList<>();
        enemies.add(new Enemy("Minotaur", new Stat(20, 20, 30)));
        enemies.add(new Enemy("Killer Rabbit", new Stat(25, 25, 20)));
        enemies.add(new Enemy("Mindflayer", new Stat(15, 30, 20)));
    }

    public Scene createScene() {
        StackPane root = new StackPane();

        // Background
        ImageView bg = UIUtils.loadImageView("battle_bg.jpg", 800, 600, false);

        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setPadding(new Insets(20));

        // Title
        Label title = new Label("🏋️ Training Phase");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 34));
        title.setTextFill(Color.GOLD);
        title.setEffect(new DropShadow(4, Color.BLACK));

        // Player image
        ImageView playerImg = UIUtils.loadImageView(
                "player_" + player.getProfession().name().toLowerCase() + "_battle.png",
                160, 160, true
        );

        // Enemy images
        HBox enemyImagesBox = new HBox(25);
        enemyImagesBox.setAlignment(Pos.CENTER);
        for (Enemy e : enemies) {
            String fileName = e.getName().equalsIgnoreCase("Killer Rabbit") 
                    ? "enemy_killer rabbit.png"
                    : "enemy_" + e.getName().toLowerCase() + ".png";

            ImageView img = UIUtils.loadImageView(fileName, 130, 130, true);
            VBox container = new VBox(8, img, createStyledLabel(e.getName(), 16, Color.WHITE));
            container.setAlignment(Pos.CENTER);
            enemyImagesBox.getChildren().add(container);
        }

        HBox imagesBox = new HBox(60, playerImg, enemyImagesBox);
        imagesBox.setAlignment(Pos.CENTER);

        // Stats Displays
        playerStatsLabel = createStatsLabel(Color.LIGHTGREEN, "PLAYER STATS");
        enemyStatsBox = new VBox(8);
        enemyStatsBox.setPadding(new Insets(12));

        updateStatsDisplay();

        // Training Buttons
        trainStrBtn = createTrainingButton("Train STR");
        trainAgiBtn = createTrainingButton("Train AGI");
        trainIntBtn = createTrainingButton("Train INT");

        trainStrBtn.setOnAction(e -> trainStat("STR"));
        trainAgiBtn.setOnAction(e -> trainStat("AGI"));
        trainIntBtn.setOnAction(e -> trainStat("INT"));

        HBox trainButtons = new HBox(15, trainStrBtn, trainAgiBtn, trainIntBtn);
        trainButtons.setAlignment(Pos.CENTER);

        // Next cycle button below training buttons
        nextBtn = new Button("Next Cycle");
        nextBtn.setDisable(true);
        nextBtn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: gold; -fx-text-fill: black;");

        nextBtn.setOnAction(e -> {
            currentCycle++;
            if (currentCycle < totalCycles) {
                trainStrBtn.setDisable(false);
                trainAgiBtn.setDisable(false);
                trainIntBtn.setDisable(false);
                nextBtn.setDisable(true);
            } else {
                SceneManager.showBattleScreen(player, enemies.get(0));
            }
        });

        mainLayout.getChildren().addAll(
                title,
                imagesBox,
                playerStatsLabel,
                enemyStatsBox,
                trainButtons,
                nextBtn
        );

        root.getChildren().addAll(bg, mainLayout);
        return new Scene(root, 800, 600);
    }

    private Button createTrainingButton(String name) {
        Button b = new Button(name);
        b.setStyle("-fx-font-size: 14px; -fx-background-color: #2d2d2d; -fx-text-fill: white; -fx-font-weight: bold;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-font-size: 14px; -fx-background-color: #555; -fx-text-fill: white; -fx-font-weight: bold;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-font-size: 14px; -fx-background-color: #2d2d2d; -fx-text-fill: white; -fx-font-weight: bold;"));
        return b;
    }

    private Label createStatsLabel(Color color, String header) {
        Label l = new Label();
        l.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
        l.setTextFill(color);
        l.setText(header);
        l.setBackground(new Background(new BackgroundFill(Color.rgb(0,0,0,0.55), new CornerRadii(6), Insets.EMPTY)));
        l.setPadding(new Insets(10));
        l.setWrapText(true);   // ✅ Enable multi-line display
        l.setMaxWidth(400);    // ✅ Limit width to fit layout
        return l;
    }

    private Label createStyledLabel(String text, int size, Color c) {
        Label l = new Label(text);
        l.setFont(Font.font("Verdana", FontWeight.BOLD, size));
        l.setTextFill(c);
        l.setEffect(new DropShadow(3, Color.BLACK));
        return l;
    }

    private void trainStat(String stat) {
        switch (stat) {
            case "STR" -> player.getStats().increaseStrength(5);
            case "AGI" -> player.getStats().increaseAgility(5);
            case "INT" -> player.getStats().increaseIntelligence(5);
        }

        // Enemy training
        for (Enemy enemy : enemies) {
            Stat es = enemy.getStats();
            switch (enemy.getName()) {
                case "Minotaur" -> es.increaseStrength(5);
                case "Killer Rabbit" -> es.increaseAgility(5);
                case "Mindflayer" -> es.increaseIntelligence(5);
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
        playerStatsLabel.setText(
                "PLAYER STATS\n" +
                "STR: " + s.getStrength() +
                " | AGI: " + s.getAgility() +
                " | INT: " + s.getIntelligence() + "\n" +
                "HP: " + s.getHp() +
                " | SPD: " + s.getSpeed() +
                " | ACC: " + s.getAccuracy() +
                " | EVA: " + s.getEvasion()
        );

        enemyStatsBox.getChildren().clear();

        for (Enemy e : enemies) {
            Stat es = e.getStats();

            VBox card = new VBox(4);
            card.setPadding(new Insets(8));
            card.setBackground(new Background(new BackgroundFill(Color.rgb(0,0,0,0.6), new CornerRadii(5), Insets.EMPTY)));

            Label name = createStyledLabel(e.getName(), 16, Color.LIGHTCORAL);
            Label stats = new Label(
                    "STR: " + es.getStrength() +
                    " | AGI: " + es.getAgility() +
                    " | INT: " + es.getIntelligence() + "\n" +
                    "HP: " + es.getHp()
            );
            stats.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
            stats.setTextFill(Color.WHITE);
            stats.setWrapText(true);
            stats.setMaxWidth(300);

            card.getChildren().addAll(name, stats);
            enemyStatsBox.getChildren().add(card);
        }
    }
}
