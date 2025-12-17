package game.ui;

import game.core.*;
import game.system.*;
import game.data.*;
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

public class BattleScreen {

    private final Player player;
    private final Enemy enemy;
    private final CombatSystem combatSystem;
    private final java.util.function.Consumer<Boolean> afterBattleCallback;

    private VBox playerStatsBox;
    private VBox enemyStatsBox;
    private VBox logBox;
    private HBox skillButtonsBox;

    private boolean playerTurn = true;

    public BattleScreen(Player player, Enemy enemy, CombatSystem combatSystem,
                        java.util.function.Consumer<Boolean> afterBattle) {
        this.player = player;
        this.enemy = enemy;
        this.combatSystem = combatSystem;
        this.afterBattleCallback = afterBattle;
    }

    public Scene createScene() {
        StackPane root = new StackPane();

        ImageView bg = UIUtils.loadImageView("battle_bg.jpg", 800, 600, false);

        VBox main = new VBox(15);
        main.setAlignment(Pos.TOP_CENTER);
        main.setPadding(new Insets(15));

        Label title = new Label("⚔️ Battle!");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 28));
        title.setTextFill(Color.GOLD);
        title.setEffect(new DropShadow(5, Color.BLACK));

        // Characters
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

        // Stats
        playerStatsBox = createStatsBox(player, true);
        enemyStatsBox = createStatsBox(enemy, false);
        HBox statsBox = new HBox(40, playerStatsBox, enemyStatsBox);
        statsBox.setAlignment(Pos.CENTER);

        // Combat log
        logBox = new VBox(5);
        logBox.setPadding(new Insets(5));
        logBox.setBackground(new Background(
                new BackgroundFill(Color.rgb(0,0,0,0.5), new CornerRadii(5), Insets.EMPTY)
        ));
        logBox.setPrefHeight(150);

        // Skill buttons
        skillButtonsBox = new HBox(10);
        skillButtonsBox.setAlignment(Pos.CENTER);
        updateSkillButtons(); // initial creation

        main.getChildren().addAll(title, characters, statsBox, skillButtonsBox, logBox);
        root.getChildren().addAll(bg, main);

        return new Scene(root, 800, 600);
    }

    private VBox createStatsBox(Object entity, boolean isPlayer) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(5));
        box.setAlignment(Pos.CENTER);
        box.setBackground(new Background(
                new BackgroundFill(Color.rgb(30,30,30,0.7), new CornerRadii(5), Insets.EMPTY)
        ));

        Label nameLabel = new Label(isPlayer ? player.getName() : enemy.getName());
        nameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
        nameLabel.setTextFill(isPlayer ? Color.LIGHTGREEN : Color.LIGHTCORAL);

        Label statsLabel = new Label();
        statsLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
        statsLabel.setTextFill(Color.LIGHTGRAY);

        box.getChildren().addAll(nameLabel, statsLabel);
        updateStatsBox(entity, box);

        return box;
    }

    private void updateStatsBox(Object entity, VBox box) {
        var s = (entity instanceof Player) ? ((Player)entity).getStats() : ((Enemy)entity).getStats();
        Label statsLabel = (Label) box.getChildren().get(1);
        statsLabel.setText(
                "STR: " + s.getStrength() +
                " | AGI: " + s.getAgility() +
                " | INT: " + s.getIntelligence() +
                " | HP: " + s.getHp()
        );
    }

    private void updateSkillButtons() {
        skillButtonsBox.getChildren().clear();
        Skill[] skills = SkillsData.getSkillsForProfession(player.getProfession());

        for (int i = 0; i < skills.length; i++) {
            Skill skill = skills[i];
            Button btn = createBattleButton(skill.getName());

            int cd = GameSession.getCooldownSystem().getRemainingCooldown(player, skill);
            if (cd > 0) btn.setText(skill.getName() + " (CD " + cd + ")");

            final int index = i;
            btn.setOnAction(e -> usePlayerSkill(index));

            skillButtonsBox.getChildren().add(btn);
        }
    }

    private void usePlayerSkill(int skillIndex) {
        if (!playerTurn) return;

        Skill[] skills = SkillsData.getSkillsForProfession(player.getProfession());
        Skill skill = skills[skillIndex];

        if (GameSession.getCooldownSystem().getRemainingCooldown(player, skill) > 0) return;

        // Player action
        CombatSystem.CombatResult result = combatSystem.playerAttack(player, enemy, skill);
        appendLog(result.getMessage());
        updateStatsBox(player, playerStatsBox);
        updateStatsBox(enemy, enemyStatsBox);

        // Check combat over
        if (combatSystem.isCombatOver(player, enemy)) {
            endCombat();
            return;
        }

        // Tick cooldowns
        GameSession.getCooldownSystem().tickPlayerCooldowns(player);
        GameSession.getCooldownSystem().tickEnemyCooldowns(enemy);

        // Enemy turn
        playerTurn = false;
        handleEnemyTurn();
    }

    private void handleEnemyTurn() {
        CombatSystem.CombatResult result;
        Skill skill = enemy.hasSkills() ? enemy.getSkills()[0] : null;

        if (skill != null && GameSession.getCooldownSystem().getRemainingCooldown(enemy, skill) == 0) {
            result = combatSystem.enemyAttack(enemy, player, skill);
        } else {
            result = combatSystem.enemyBasicAttack(enemy, player);
        }

        appendLog(result.getMessage());
        updateStatsBox(player, playerStatsBox);
        updateStatsBox(enemy, enemyStatsBox);

        if (combatSystem.isCombatOver(player, enemy)) {
            endCombat();
            return;
        }

        // Restore player's turn
        playerTurn = true;
        updateSkillButtons();
    }

    private void endCombat() {
        boolean playerWon = combatSystem.didPlayerWin(player, enemy);

        appendLog(playerWon ? "🎉 You won!" : "💀 You were defeated!");
        skillButtonsBox.setDisable(true);

        // Use callback to SceneManager for EndScreen transition
        if (afterBattleCallback != null) {
            new Thread(() -> {
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                javafx.application.Platform.runLater(() -> afterBattleCallback.accept(playerWon));
            }).start();
        }
    }

    private void appendLog(String message) {
        Label lbl = new Label(message);
        lbl.setFont(Font.font("Consolas", 12));
        lbl.setTextFill(Color.WHITE);
        logBox.getChildren().add(0, lbl); // newest on top
    }

    private Button createBattleButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color:#1f1f1f;-fx-text-fill:#cfcfcf;-fx-font-weight:bold;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:#3a3a3a;-fx-text-fill:#ffffff;-fx-font-weight:bold;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:#1f1f1f;-fx-text-fill:#cfcfcf;-fx-font-weight:bold;"));
        return btn;
    }
}
