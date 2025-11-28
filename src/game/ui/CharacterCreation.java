package game.ui;

import game.core.Player;
import game.core.Enemy;
import game.core.Profession;
import game.core.Stat;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CharacterCreation extends StackPane {

    public CharacterCreation(int width, int height) {

        // Background
        ImageView bg = UIUtils.loadImageView("char_create_bg.png", width, height, false);

        BorderPane layout = new BorderPane();

        VBox center = new VBox(12);
        center.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Character Creation");

        // Icons placeholders
        HBox icons = new HBox(16);
        icons.setAlignment(Pos.CENTER);

        Button bWar = new Button("Warrior");
        Button bMag = new Button("Mage");
        Button bRog = new Button("Rogue");

        icons.getChildren().addAll(bWar, bMag, bRog);

        // Name input
        HBox nameBox = new HBox(8);
        nameBox.setAlignment(Pos.CENTER);
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField("Hero");
        nameBox.getChildren().addAll(nameLabel, nameField);

        // Selected profession
        final Profession[] selected = {Profession.WARRIOR};

        bWar.setOnAction(e -> selected[0] = Profession.WARRIOR);
        bMag.setOnAction(e -> selected[0] = Profession.MAGE);
        bRog.setOnAction(e -> selected[0] = Profession.ROGUE);

        // Start button
        Button start = new Button("Start Adventure");
        start.setOnAction(e -> {
            String chosenName = nameField.getText().trim();
            if (chosenName.isEmpty()) chosenName = "Hero";

            Stat s = new Stat(5, 5, 5);
            Player player = new Player(chosenName, selected[0], s);

            Stat eStat = new Stat(4, 3, 2);
            Enemy enemy = new Enemy("Goblin", eStat);

            SceneManager.showBattleScreen(player, enemy);
        });

        center.getChildren().addAll(title, icons, nameBox, start);
        layout.setCenter(center);

        getChildren().addAll(bg, layout);
    }
}
