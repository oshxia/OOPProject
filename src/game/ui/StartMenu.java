package game.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class StartMenu {

    public StartMenu() { }

    public Scene createScene() {
        // Background
        ImageView bg = UIUtils.loadImageView("start_bg.jpg", 800, 600, false);

        // Title
        Text title = new Text("⚔️ The Threefold Trial ⚔️");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 36));
        title.setStyle(
                "-fx-fill: linear-gradient(to right, #FFD700, #FF4500);" +
                " -fx-effect: dropshadow(gaussian, black, 5, 0.5, 2, 2);"
        );

        // Start button
        Button startBtn = createButton("Start Adventure");
        startBtn.setOnAction(e -> {
            // Simply go to Character Creation
            SceneManager.showCharacterCreation();
        });

        VBox vbox = new VBox(25, title, startBtn);
        vbox.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(bg, vbox);
        return new Scene(root, 800, 600);
    }

    private Button createButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-font-size: 18px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-color: linear-gradient(to bottom, #ffcc00, #ff9900); " +
                "-fx-text-fill: black; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 10 25;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-font-size: 18px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-color: linear-gradient(to bottom, #ffdd33, #ffaa00); " +
                "-fx-text-fill: black; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 10 25;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-font-size: 18px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-color: linear-gradient(to bottom, #ffcc00, #ff9900); " +
                "-fx-text-fill: black; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 10 25;"
        ));
        return btn;
    }
}
