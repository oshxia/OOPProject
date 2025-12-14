package game.ui;

import game.core.Player;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class EndScreen extends StackPane {

    public EndScreen(int width, int height, boolean playerWon, Player player) {
        setPrefSize(width, height);

        // Background image
        ImageView bg = UIUtils.loadImageView("end_bg.jpg", width, height, false);

        // Result text
        Text result = new Text(playerWon ? "🎉 Victory!" : "💀 Defeat!");
        result.setFont(Font.font("Consolas", FontWeight.BOLD, 36));
        result.setFill(Color.GOLD);
        result.setEffect(new DropShadow(5, Color.BLACK));

        // Player stats summary
        Text stats = new Text(
                "Player Stats:\n" +
                "STR: " + player.getStats().getStrength() +
                " | AGI: " + player.getStats().getAgility() +
                " | INT: " + player.getStats().getIntelligence() +
                " | HP: " + player.getStats().getHp()
        );
        stats.setFont(Font.font("Consolas", FontWeight.BOLD, 18));
        stats.setFill(Color.LIGHTGRAY);

        // Back to menu button
        Button menuBtn = new Button("Back to Menu");
        menuBtn.setStyle(
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-color: linear-gradient(to bottom, #FFA500, #FF4500); " +
                "-fx-text-fill: black; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 8 20;"
        );
        menuBtn.setOnAction(e -> SceneManager.showStartMenu());

        VBox vbox = new VBox(20, result, stats, menuBtn);
        vbox.setAlignment(Pos.CENTER);

        getChildren().addAll(bg, vbox);
    }
}
