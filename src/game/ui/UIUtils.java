package game.ui;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.io.InputStream;


public class UIUtils {
private static final String ASSET_PATH = "/game/ui/assets/"; // resources path


public static ImageView loadImageView(String filename, double width, double height, boolean preserveRatio) {
try {
InputStream is = UIUtils.class.getResourceAsStream(ASSET_PATH + filename);
if (is == null) return null;
Image img = new Image(is);
ImageView iv = new ImageView(img);
iv.setFitWidth(width);
iv.setFitHeight(height);
iv.setPreserveRatio(preserveRatio);
return iv;
} catch (Exception e) {
System.err.println("Failed to load image: " + filename + " -> " + e.getMessage());
return null;
}
}
}