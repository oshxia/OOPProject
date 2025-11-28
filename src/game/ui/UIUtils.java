package game.ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Utility class for loading images safely.
 * Uses placeholders if images are missing.
 */
public class UIUtils {

    /**
     * Load an image from resources/images folder.
     * If not found, returns a placeholder ImageView.
     *
     * @param fileName image file name (e.g., start_bg.png)
     * @param width    desired width
     * @param height   desired height
     * @param preserveRatio whether to preserve aspect ratio
     * @return ImageView
     */
    public static ImageView loadImageView(String fileName, double width, double height, boolean preserveRatio) {
        try {
            var stream = UIUtils.class.getResourceAsStream("/images/" + fileName);
            if (stream == null) {
                System.out.println("Resource not found: " + fileName + " — Using placeholder.");
                return createPlaceholder(width, height);
            }

            Image img = new Image(stream);
            ImageView iv = new ImageView(img);
            iv.setFitWidth(width);
            iv.setFitHeight(height);
            iv.setPreserveRatio(preserveRatio);
            return iv;
        } catch (Exception e) {
            System.out.println("Failed to load image: " + fileName + " — Using placeholder.");
            return createPlaceholder(width, height);
        }
    }

    /**
     * Overload without preserveRatio (default true)
     */
    public static ImageView loadImageView(String fileName, double width, double height) {
        return loadImageView(fileName, width, height, true);
    }

    /**
     * Creates a simple colored rectangle as placeholder
     */
    private static ImageView createPlaceholder(double width, double height) {
        Rectangle rect = new Rectangle(width, height, Color.LIGHTGRAY);
        ImageView placeholder = new ImageView(rect.snapshot(null, null));
        return placeholder;
    }
}
