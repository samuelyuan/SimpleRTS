package graphics;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class ImageUtils {

    public static BufferedImage darken(Image img) {
        BufferedImage bImage = new BufferedImage(
            img.getWidth(null),
            img.getHeight(null),
            BufferedImage.TYPE_INT_RGB
        );

        Graphics g = bImage.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        for (int y = 0; y < bImage.getHeight(); y++) {
            for (int x = 0; x < bImage.getWidth(); x++) {
                bImage.setRGB(x, y, darkenRGB(bImage.getRGB(x, y)));
            }
        }

        return bImage;
    }

    public static int darkenRGB(int rgb) {
        int r = (rgb >> 16) & 255;
        int g = (rgb >> 8) & 255;
        int b = rgb & 255;

        r /= 3;
        g /= 3;
        b /= 3;

        return (r << 16) | (g << 8) | b;
    }
}
