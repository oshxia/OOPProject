package game.ui;

import game.core.*;
import game.system.CombatSystem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Random;

public class TrainingScreen {

    private final Player player;
    private Enemy currentEnemy;
    private int currentCycle;
    private int totalCycles;

    private Label cycleLabel;
    private Label playerStatsLabel;
    private VBox enemyStatsBox;

    private Button trainStrBtn, trainAgiBtn, trainIntBtn, nextBtn;

    public TrainingScreen(Player player) {
        this.player = player;
        currentEnemy = GameSession.nextEnemy();
        randomizeCycles();
    }

    private void randomizeCycles() {
        totalCycles = new Random().nextInt(5) + 3; // 3–7 cycles
        currentCycle = 1;
    }

    public Scene createScene() {
        StackPane root = new StackPane();
        ImageView bg = UIUtils.loadImageView("training_bg.jpg", 800, 600, false);

        VBox main = new VBox(15);
        main.setAlignment(Pos.TOP_CENTER);
        main.setPadding(new Insets(15));

        Label title = new Label("⛏ TRAINING GROUNDS");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#d4af37"));
        title.setEffect(new DropShadow(5, Color.BLACK));

        cycleLabel = new Label();
        cycleLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
        cycleLabel.setTextFill(Color.LIGHTGRAY);

        playerStatsLabel = createStatsLabel("PLAYER STATUS", Color.web("#7CFC98"));

        enemyStatsBox = new VBox(10);
        ScrollPane enemyScroll = new ScrollPane(enemyStatsBox);
        enemyScroll.setFitToWidth(true);
        enemyScroll.setPrefViewportHeight(250);
        enemyScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        updateStatsDisplay();

        trainStrBtn = createTrainingButton("Train Strength");
        trainAgiBtn = createTrainingButton("Train Agility");
        trainIntBtn = createTrainingButton("Train Intelligence");

        trainStrBtn.setOnAction(e -> train("STR"));
        trainAgiBtn.setOnAction(e -> train("AGI"));
        trainIntBtn.setOnAction(e -> train("INT"));

        HBox trainBtns = new HBox(10, trainStrBtn, trainAgiBtn, trainIntBtn);
        trainBtns.setAlignment(Pos.CENTER);

        nextBtn = new Button("Next Cycle");
        nextBtn.setDisable(true);
        nextBtn.setStyle("-fx-background-color:#3b2f2f;-fx-text-fill:#d4af37;-fx-font-weight:bold;");
        nextBtn.setOnAction(e -> handleNext());

        main.getChildren().addAll(title, cycleLabel, playerStatsLabel, enemyScroll, trainBtns, nextBtn);
        root.getChildren().addAll(bg, main);

        return new Scene(root, 800, 600);
    }

    private void train(String stat) {
        switch (stat) {
            case "STR" -> player.getStats().increaseStrength(5);
            case "AGI" -> player.getStats().increaseAgility(5);
            case "INT" -> player.getStats().increaseIntelligence(5);
        }

        // Example enemy response
        switch (stat) {
            case "STR" -> currentEnemy.getStats().increaseStrength(3);
            case "AGI" -> currentEnemy.getStats().increaseAgility(3);
            case "INT" -> currentEnemy.getStats().increaseIntelligence(3);
        }

        updateStatsDisplay();
        disableTraining();
        nextBtn.setDisable(false);
    }

    private void handleNext() {
        if (currentCycle < totalCycles) {
            currentCycle++;
            enableTraining();
        } else {
        	CombatSystem combatSystem = GameSession.getCombatSystem();


            SceneManager.showBattleScreen(player, currentEnemy, combatSystem, won -> {
                if (!won || !GameSession.hasMoreEnemies()) {
                    SceneManager.showEndScreen(won, player);
                } else {
                    currentEnemy = GameSession.nextEnemy();
                    randomizeCycles();
                    SceneManager.showTrainingScreen(player);
                }
            });
        }
    }

    private void enableTraining() {
        trainStrBtn.setDisable(false);
        trainAgiBtn.setDisable(false);
        trainIntBtn.setDisable(false);
        nextBtn.setDisable(true);
    }

    private void disableTraining() {
        trainStrBtn.setDisable(true);
        trainAgiBtn.setDisable(true);
        trainIntBtn.setDisable(true);
    }

    private void updateStatsDisplay() {
        cycleLabel.setText("Training Cycle " + currentCycle + " / " + totalCycles);

        Stat s = player.getStats();
        playerStatsLabel.setText(
                "PLAYER STATUS\n" +
                "STR: " + s.getStrength() + " | AGI: " + s.getAgility() + " | INT: " + s.getIntelligence() +
                "\nHP: " + s.getHp() + " | SPD: " + s.getSpeed() + " | ACC: " + s.getAccuracy() + " | EVA: " + s.getEvasion()
        );

        enemyStatsBox.getChildren().clear();

        VBox card = new VBox(4);
        card.setPadding(new Insets(8));
        card.setBackground(new Background(new BackgroundFill(Color.rgb(30, 30, 30, 0.8), new CornerRadii(6), Insets.EMPTY)));

        ImageView img = UIUtils.loadImageView(
                "enemy_" + currentEnemy.getName().toLowerCase().replace(" ", "") + ".png", 100, 100, true
        );

        Label name = new Label(currentEnemy.getName());
        name.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        name.setTextFill(Color.web("#ff6f61"));

        Stat es = currentEnemy.getStats();
        Label stats = new Label(
                "STR: " + es.getStrength() +
                " | AGI: " + es.getAgility() +
                " | INT: " + es.getIntelligence() +
                " | HP: " + es.getHp()
        );
        stats.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
        stats.setTextFill(Color.LIGHTGRAY);

        card.getChildren().addAll(img, name, stats);
        card.setAlignment(Pos.CENTER);
        enemyStatsBox.getChildren().add(card);
    }

    private Label createStatsLabel(String header, Color color) {
        Label l = new Label(header);
        l.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
        l.setTextFill(color);
        l.setWrapText(true);
        l.setMaxWidth(400);
        l.setPadding(new Insets(8));
        l.setBackground(new Background(new BackgroundFill(Color.rgb(10, 10, 10, 0.7), new CornerRadii(6), Insets.EMPTY)));
        return l;
    }

    private Button createTrainingButton(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color:#1f1f1f;-fx-text-fill:#cfcfcf;-fx-font-weight:bold;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color:#3a3a3a;-fx-text-fill:#ffffff;-fx-font-weight:bold;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color:#1f1f1f;-fx-text-fill:#cfcfcf;-fx-font-weight:bold;"));
        return b;
    }
}
