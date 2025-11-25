package game.ui;

import game.core.Player;
import game.core.Profession;
import game.core.Stat;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CharacterCreation extends BorderPane {

    public CharacterCreation(int width, int height) {
        build(width, height);
    }

    private void build(int width, int height) {
        VBox center = new VBox(12);
        center.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Character Creation");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Icons (placeholders if images are missing)
        HBox icons = new HBox(16);
        icons.setAlignment(Pos.CENTER);

        ImageView war = UIUtils.loadImageView("icon_warrior.png", 128, 128, true);
        ImageView mag = UIUtils.loadImageView("icon_mage.png", 128, 128, true);
        ImageView rog = UIUtils.loadImageView("icon_rogue.png", 128, 128, true);

        if (war == null) war = new ImageView();
        if (mag == null) mag = new ImageView();
        if (rog == null) rog = new ImageView();

        Button bWar = new Button("Warrior", war);
        Button bMag = new Button("Mage", mag);
        Button bRog = new Button("Rogue", rog);

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
            System.out.println("Start Adventure clicked!"); // debug
            String chosenName = nameField.getText().trim();
            if (chosenName.isEmpty()) chosenName = "Hero";

            Stat s = new Stat(5,5,5);
            Player player = new Player(chosenName, selected[0], s);

            // Demo enemy
            Stat eStat = new Stat(4,3,2);
            game.core.Enemy enemy = new game.core.Enemy("Goblin", eStat);

            SceneManager.showBattleScreen(player, enemy);
        });

        center.getChildren().addAll(title, icons, nameBox, start);
        setCenter(center);

        // Temporary background to make it visible
        setStyle("-fx-background-color: lightgray;");
    }
}
