package game.ui;

import game.core.Player;
import game.core.Enemy;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

        // Placeholder images
        ImageView playerImg = UIUtils.loadImageView("char_create_bg.png", 150, 150, true);
        ImageView enemyImg = UIUtils.loadImageView("char_create_bg.png", 150, 150, true);

        HBox images = new HBox(50, playerImg, enemyImg);
        images.setAlignment(Pos.CENTER);

        Button backBtn = new Button("Back to Menu");
        backBtn.setOnAction(e -> SceneManager.showStartMenu());

        VBox v = new VBox(30, images, backBtn);
        v.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(bg, v);

        return new Scene(root, 800, 600);
    }
}
