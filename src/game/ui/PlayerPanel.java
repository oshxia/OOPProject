package game.ui;

import game.core.*;
import game.system.*;
import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

public class PlayerPanel extends HBox {

    private Player player;
    private SkillSystem skillSystem;
    private CooldownSystem cooldownSystem;

    private ImageView portraitView;
    private Label nameLabel;
    private Label statsLabel;
    private ProgressBar hpBar;
    private ProgressBar avBar;

    public PlayerPanel(Player player, SkillSystem skillSystem, CooldownSystem cooldownSystem) {
        this.player = player;
        this.skillSystem = skillSystem;
        this.cooldownSystem = cooldownSystem;

        setSpacing(15);
        setPadding(new javafx.geometry.Insets(10));
        setAlignment(Pos.CENTER_LEFT);

        portraitView = new ImageView(new Image("file:src/game/ui/Assets/player.png"));
        portraitView.setFitWidth(80);
        portraitView.setFitHeight(80);

        VBox infoBox = new VBox(5);

        nameLabel = new Label(player.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        statsLabel = new Label();
        statsLabel.setFont(Font.font("Arial", 14));

        hpBar = new ProgressBar();
        hpBar.setPrefWidth(200);

        avBar = new ProgressBar();
        avBar.setPrefWidth(200);

        infoBox.getChildren().addAll(nameLabel, statsLabel, new Label("HP:"), hpBar, new Label("Action Value:"), avBar);

        getChildren().addAll(portraitView, infoBox);

        update();
    }

    public void update() {
        int maxHP = player.getStats().getMaxHP();
        int curHP = player.getStats().getCurrentHP();
        hpBar.setProgress((double) curHP / maxHP);

        avBar.setProgress(player.getActionValue());

        statsLabel.setText("STR: " + player.getStats().getStrength() +
                           " | AGI: " + player.getStats().getAgility() +
                           " | INT: " + player.getStats().getIntelligence());
    }
}
