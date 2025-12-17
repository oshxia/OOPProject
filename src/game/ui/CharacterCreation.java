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
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class CharacterCreation extends StackPane {

    public CharacterCreation(int width, int height) {
        // Background
        ImageView bg = UIUtils.loadImageView("char_create_bg.jpg", width, height, false);

        VBox center = new VBox(20);
        center.setAlignment(Pos.TOP_CENTER);

        // Title
        Text title = new Text("🛡️ Character Creation 🛡️");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, 32));
        title.setFill(Color.web("#FFD700")); // gold-ish
        title.setEffect(new DropShadow(5, Color.BLACK));

        // Character display
        ImageView charDisplay = UIUtils.loadImageView("player_warrior_battle.png", 150, 150, true);

        // Profession buttons
        HBox profButtons = new HBox(20);
        profButtons.setAlignment(Pos.CENTER);
        Button bWar = createButton("Warrior");
        Button bMag = createButton("Mage");
        Button bRog = createButton("Rogue");
        profButtons.getChildren().addAll(bWar, bMag, bRog);

        // Description label
        Label profDesc = new Label("Balanced fighter with consistent damage");
        profDesc.setFont(Font.font("Consolas", FontWeight.NORMAL, 14));
        profDesc.setTextFill(Color.LIGHTGRAY);
        profDesc.setWrapText(true);
        profDesc.setMaxWidth(400);
        profDesc.setAlignment(Pos.CENTER);

        final Profession[] selected = {Profession.WARRIOR};
        bWar.setOnAction(e -> {
            selected[0] = Profession.WARRIOR;
            charDisplay.setImage(UIUtils.loadImageView("player_warrior_battle.png", 150, 150, true).getImage());
            profDesc.setText("Balanced fighter with consistent damage");
        });
        bMag.setOnAction(e -> {
            selected[0] = Profession.MAGE;
            charDisplay.setImage(UIUtils.loadImageView("player_mage_battle.png", 150, 150, true).getImage());
            profDesc.setText("Powerful spellcaster with high burst damage");
        });
        bRog.setOnAction(e -> {
            selected[0] = Profession.ROGUE;
            charDisplay.setImage(UIUtils.loadImageView("player_rogue_battle.png", 150, 150, true).getImage());
            profDesc.setText("Swift assassin with fast attacks");
        });

        // Name input
        HBox nameBox = new HBox(10);
        nameBox.setAlignment(Pos.CENTER);
        Label nameLabel = new Label("Name:");
        nameLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 16));
        nameLabel.setTextFill(Color.LIGHTGRAY);
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
            GameSession.init(player, enemies);

            // Show training screen
            SceneManager.showTrainingScreen(player);
        });

        center.getChildren().addAll(title, charDisplay, profButtons, profDesc, nameBox, startBtn);
        getChildren().addAll(bg, center);
    }

    private Button createButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: #3b2f2f; " + // dark panel
                "-fx-text-fill: #d4af37; " +         // gold text
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 8 20;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #5a4a4a; " +
                "-fx-text-fill: #ffd700; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 8 20;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: #3b2f2f; " +
                "-fx-text-fill: #d4af37; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 8 20;"
        ));
        return btn;
    }
}
