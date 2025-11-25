package game.ui;


import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class StartMenu extends StackPane {
private final int width;
private final int height;


public StartMenu(int width, int height) {
this.width = width;
this.height = height;
build();
}


private void build() {
setPrefSize(width, height);


// Background
ImageView bg = UIUtils.loadImageView("start_bg.png", width, height, true);
if (bg != null) getChildren().add(bg);


VBox menu = new VBox(20);
menu.setAlignment(Pos.CENTER);


ImageView logo = UIUtils.loadImageView("logo.png", 500, 120, true);
if (logo != null) menu.getChildren().add(logo);


Button play = new Button("Play");
play.setPrefWidth(240);
play.setOnAction(e -> SceneManager.showCharacterCreation());


Button exit = new Button("Exit");
exit.setPrefWidth(240);
exit.setOnAction(e -> System.exit(0));


menu.getChildren().addAll(play, exit);
getChildren().add(menu);
}
}