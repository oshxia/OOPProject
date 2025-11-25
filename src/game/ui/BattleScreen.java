package game.ui;

import game.core.Player;
import game.core.Enemy;
import game.core.Skill;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class BattleScreen extends BorderPane {
    private final int width;
    private final int height;
    private final Player player;
    private final Enemy enemy;

    private Label lblPlayerHP;
    private Label lblEnemyHP;
    private TextArea battleLog;
    private ImageView playerView;
    private ImageView enemyView;

    private Skill skill1;
    private Skill skill2;

    public BattleScreen(int width, int height, Player player, Enemy enemy) {
        this.width = width;
        this.height = height;
        this.player = player;
        this.enemy = enemy;
        build();
    }

    private void build() {
        setPrefSize(width, height);

        // Temporary background
        setStyle("-fx-background-color: lightblue;");

        // --- Top: Enemy info ---
        HBox top = new HBox(12);
        top.setPadding(new Insets(10));
        top.setAlignment(Pos.TOP_CENTER);

        enemyView = UIUtils.loadImageView("enemy.png", 200, 200, true);
        if (enemyView == null) enemyView = new ImageView();
        lblEnemyHP = new Label("Enemy HP: " + getEnemyHP());
        top.getChildren().addAll(enemyView, lblEnemyHP);

        // --- Bottom: Player info + actions ---
        HBox bottom = new HBox(20);
        bottom.setPadding(new Insets(10));
        bottom.setAlignment(Pos.CENTER_LEFT);

        playerView = UIUtils.loadImageView("player.png", 200, 200, true);
        if (playerView == null) playerView = new ImageView();
        lblPlayerHP = new Label("Player HP: " + getPlayerHP());

        VBox playerBox = new VBox(8, playerView, lblPlayerHP);
        playerBox.setAlignment(Pos.CENTER);

        VBox actions = new VBox(8);
        Button attackBtn = new Button("Attack");
        attackBtn.setOnAction(e -> playerAttack());

        Button skillBtn1 = new Button("Skill 1");
        Button skillBtn2 = new Button("Skill 2");

        // Demo skills
        skill1 = new Skill("Power Strike", player.getProfession(), 5, 3);
        skill2 = new Skill("Special Move", player.getProfession(), 8, 5);

        skillBtn1.setOnAction(e -> useSkill(skill1));
        skillBtn2.setOnAction(e -> useSkill(skill2));

        actions.getChildren().addAll(attackBtn, skillBtn1, skillBtn2);
        bottom.getChildren().addAll(playerBox, actions);

        // --- Center: Battle log ---
        battleLog = new TextArea();
        battleLog.setEditable(false);
        battleLog.setPrefRowCount(10);

        setTop(top);
        setCenter(battleLog);
        setBottom(bottom);

        log("Battle start! " + player.getName() + " vs " + enemy.getName());
    }

    private int getPlayerHP() {
        return player.getStats().getStrength() * 5 + 50;
    }

    private int getEnemyHP() {
        return enemy.getStats().getStrength() * 5 + 50;
    }

    private void playerAttack() {
        int dmg = player.getStats().getStrength();
        enemy.takeDamage(dmg);
        animateAttack(playerView, enemyView);
        log(player.getName() + " attacks for " + dmg + " damage.");
        updateHPLabels();
        checkBattleEnd();
        enemyTurn();
    }

    private void useSkill(Skill skill) {
        if (!skill.canUse(player)) {
            log("Cannot use " + skill.getName() + " (wrong profession or on cooldown).");
            return;
        }
        skill.use(player, enemy);
        log(player.getName() + " used " + skill.getName() + ".");
        updateHPLabels();
        checkBattleEnd();
        enemyTurn();
    }

    private void enemyTurn() {
        if (enemy.isDead()) return;
        int dmg = Math.max(1, enemy.getStats().getStrength());
        player.getStats().takeDamage(dmg);
        animateAttack(enemyView, playerView);
        log(enemy.getName() + " attacks for " + dmg + " damage.");
        updateHPLabels();
        checkBattleEnd();
    }

    private void checkBattleEnd() {
        boolean playerDead = player.getStats().isDead(0);
        boolean enemyDead = enemy.isDead();
        if (playerDead || enemyDead) {
            log("Battle ended.");
            SceneManager.showEndScreen(!playerDead);
        }
    }

    private void updateHPLabels() {
        lblPlayerHP.setText("Player HP: " + getPlayerHP());
        lblEnemyHP.setText("Enemy HP: " + getEnemyHP());
    }

    private void log(String text) {
        battleLog.appendText(text + "\n");
    }

    private void animateAttack(ImageView from, ImageView to) {
        if (from == null || to == null) return;
        TranslateTransition tt = new TranslateTransition(Duration.millis(200), from);
        tt.setByX(40);
        tt.setAutoReverse(true);
        tt.setCycleCount(2);
        tt.play();
    }
}

