package game.ui;


import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {
public static final int WIDTH = 800;
public static final int HEIGHT = 600;


@Override
public void start(Stage stage) {
SceneManager.init(stage, WIDTH, HEIGHT);
SceneManager.showStartMenu();
stage.setTitle("Turn-Based RPG - Demo");
stage.show();
}


public static void main(String[] args) {
launch(args);
}
}