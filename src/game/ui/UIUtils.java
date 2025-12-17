package game.ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class UIUtils {

    public static ImageView loadImageView(String fileName, double width, double height, boolean preserveRatio) {
        try {
            var stream = UIUtils.class.getResourceAsStream("/images/" + fileName);
            if (stream == null) return createPlaceholder(width,height);

            Image img = new Image(stream);
            ImageView iv = new ImageView(img);
            iv.setFitWidth(width);
            iv.setFitHeight(height);
            iv.setPreserveRatio(preserveRatio);
            return iv;
        } catch (Exception e) {
            return createPlaceholder(width, height);
        }
    }

    public static ImageView loadImageView(String fileName, double width, double height) {
        return loadImageView(fileName, width, height, true);
    }

    private static ImageView createPlaceholder(double width, double height) {
        Rectangle r = new Rectangle(width, height, Color.LIGHTGRAY);
        return new ImageView(r.snapshot(null, null));
    }
}
