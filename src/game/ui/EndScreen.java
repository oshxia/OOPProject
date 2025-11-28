package game.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class EndScreen extends StackPane {

    private final int width;
    private final int height;
    private final boolean playerWon;

    public EndScreen(int width, int height, boolean playerWon) {
        this.width = width;
        this.height = height;
        this.playerWon = playerWon;
        build();
    }

    private void build() {
        setPrefSize(width, height);

        // Temporary placeholder background
        StackPane bg = new StackPane();
        bg.setStyle("-fx-background-color: gray;");

        VBox v = new VBox(12);
        v.setAlignment(Pos.CENTER);

        Text txt = new Text(playerWon ? "You Win!" : "You Lose");
        txt.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-fill: white;");

        Button menu = new Button("Back to Menu");
        menu.setOnAction(e -> SceneManager.showStartMenu());

        v.getChildren().addAll(txt, menu);

        getChildren().addAll(bg, v);
    }
}
