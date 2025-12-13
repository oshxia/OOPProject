package game.ui;

import game.core.Player;
import game.core.Profession;
import game.core.Stat;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.util.List;
import game.core.Enemy;


public class CharacterCreation extends StackPane {

    public CharacterCreation(int width, int height) {
        // Background
        ImageView bg = UIUtils.loadImageView("char_create_bg.jpg", width, height, false);

        VBox center = new VBox(20);
        center.setAlignment(Pos.TOP_CENTER);

        // Title
        Text title = new Text("🛡️ Character Creation 🛡️");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 32));
        title.setStyle("-fx-fill: linear-gradient(to right, #00FFFF, #1E90FF); -fx-effect: dropshadow(gaussian, black, 5, 0.5, 2, 2);");

        // Character display
        ImageView charDisplay = UIUtils.loadImageView("player_warrior_battle.png", 150, 150, true);

        // Profession buttons
        HBox profButtons = new HBox(20);
        profButtons.setAlignment(Pos.CENTER);
        Button bWar = createButton("Warrior");
        Button bMag = createButton("Mage");
        Button bRog = createButton("Rogue");
        profButtons.getChildren().addAll(bWar, bMag, bRog);

        final Profession[] selected = {Profession.WARRIOR};
        bWar.setOnAction(e -> {
            selected[0] = Profession.WARRIOR;
            charDisplay.setImage(UIUtils.loadImageView("player_warrior_battle.png", 150, 150, true).getImage());
        });
        bMag.setOnAction(e -> {
            selected[0] = Profession.MAGE;
            charDisplay.setImage(UIUtils.loadImageView("player_mage_battle.png", 150, 150, true).getImage());
        });
        bRog.setOnAction(e -> {
            selected[0] = Profession.ROGUE;
            charDisplay.setImage(UIUtils.loadImageView("player_rogue_battle.png", 150, 150, true).getImage());
        });

        // Name input
        HBox nameBox = new HBox(10);
        nameBox.setAlignment(Pos.CENTER);
        Label nameLabel = new Label("Name:");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        TextField nameField = new TextField("Hero");
        nameBox.getChildren().addAll(nameLabel, nameField);

        // Start button
        Button startBtn = createButton("Start Adventure");
        startBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) name = "Hero";
            Player player = new Player(name, selected[0], new Stat(5, 5, 5));

            // Initialize enemies for the training session
            List<Enemy> enemies = List.of(
                new Enemy("Minotaur", new Stat(20, 20, 30)),
                new Enemy("Killer Rabbit", new Stat(25, 25, 20)),
                new Enemy("Mindflayer", new Stat(15, 30, 20))
            );
            GameSession.init(enemies);

            // Show training screen
            SceneManager.showTrainingScreen(player);
        });


        center.getChildren().addAll(title, charDisplay, profButtons, nameBox, startBtn);
        getChildren().addAll(bg, center);
    }

    private Button createButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-color: linear-gradient(to bottom, #00FF7F, #32CD32); " +
                "-fx-text-fill: black; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 8 20;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-color: linear-gradient(to bottom, #00FF99, #3CB371); " +
                "-fx-text-fill: black; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 8 20;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-color: linear-gradient(to bottom, #00FF7F, #32CD32); " +
                "-fx-text-fill: black; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 8 20;"
        ));
        return btn;
    }
}
