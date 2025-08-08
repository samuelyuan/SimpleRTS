package graphics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ImageUtils {

    public static GameImage darken(GameImage img) {
        java.awt.Image awtImg = (java.awt.Image) img.getBackendImage();
        BufferedImage bImage = new BufferedImage(
                awtImg.getWidth(null),
                awtImg.getHeight(null),
                BufferedImage.TYPE_INT_RGB);

        Graphics g = bImage.createGraphics();
        g.drawImage(awtImg, 0, 0, null);
        g.dispose();

        for (int y = 0; y < bImage.getHeight(); y++) {
            for (int x = 0; x < bImage.getWidth(); x++) {
                bImage.setRGB(x, y, darkenRGB(bImage.getRGB(x, y)));
            }
        }

        return new GameImage(bImage, img.getPath(), img.getFormat());
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

    public static GameImage addTeamColorToUnit(GameImage img, boolean isPlayerUnit) {
        java.awt.Image awtImg = (java.awt.Image) img.getBackendImage();
        BufferedImage bImage = new BufferedImage(
                awtImg.getWidth(null),
                awtImg.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        Graphics g = bImage.createGraphics();
        g.drawImage(awtImg, 0, 0, null);
        g.dispose();

        for (int y = 0; y < bImage.getHeight(); y++) {
            for (int x = 0; x < bImage.getWidth(); x++) {
                if (bImage.getRGB(x, y) != Color.WHITE.toAwtColor().getRGB()) {
                    if (isPlayerUnit) {
                        bImage.setRGB(x, y, Color.BLUE.toAwtColor().getRGB());
                    } else {
                        bImage.setRGB(x, y, Color.RED.toAwtColor().getRGB());
                    }
                }
            }
        }

        return new GameImage(bImage, img.getPath(), img.getFormat());
    }

    /**
     * Creates a copy of the image with a tint applied
     * @param img The source image
     * @param tintColor The color to tint with
     * @param intensity The intensity of the tint (0.0 to 1.0)
     * @return A new tinted GameImage
     */
    public static GameImage tint(GameImage img, Color tintColor, double intensity) {
        java.awt.Image awtImg = (java.awt.Image) img.getBackendImage();
        BufferedImage bImage = new BufferedImage(
                awtImg.getWidth(null),
                awtImg.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        Graphics g = bImage.createGraphics();
        g.drawImage(awtImg, 0, 0, null);
        g.dispose();

        for (int y = 0; y < bImage.getHeight(); y++) {
            for (int x = 0; x < bImage.getWidth(); x++) {
                int rgb = bImage.getRGB(x, y);
                int r = (rgb >> 16) & 255;
                int g_val = (rgb >> 8) & 255;
                int b = rgb & 255;
                int a = (rgb >> 24) & 255;

                // Apply tint
                r = (int) (r * (1 - intensity) + tintColor.r * intensity);
                g_val = (int) (g_val * (1 - intensity) + tintColor.g * intensity);
                b = (int) (b * (1 - intensity) + tintColor.b * intensity);

                bImage.setRGB(x, y, (a << 24) | (r << 16) | (g_val << 8) | b);
            }
        }

        return new GameImage(bImage, img.getPath(), img.getFormat());
    }

    /**
     * Creates a grayscale version of the image
     * @param img The source image
     * @return A new grayscale GameImage
     */
    public static GameImage grayscale(GameImage img) {
        java.awt.Image awtImg = (java.awt.Image) img.getBackendImage();
        BufferedImage bImage = new BufferedImage(
                awtImg.getWidth(null),
                awtImg.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        Graphics g = bImage.createGraphics();
        g.drawImage(awtImg, 0, 0, null);
        g.dispose();

        for (int y = 0; y < bImage.getHeight(); y++) {
            for (int x = 0; x < bImage.getWidth(); x++) {
                int rgb = bImage.getRGB(x, y);
                int r = (rgb >> 16) & 255;
                int g_val = (rgb >> 8) & 255;
                int b = rgb & 255;
                int a = (rgb >> 24) & 255;

                // Convert to grayscale using luminance formula
                int gray = (int) (0.299 * r + 0.587 * g_val + 0.114 * b);

                bImage.setRGB(x, y, (a << 24) | (gray << 16) | (gray << 8) | gray);
            }
        }

        return new GameImage(bImage, img.getPath(), img.getFormat());
    }

    /**
     * Creates a flipped version of the image
     * @param img The source image
     * @param horizontal Whether to flip horizontally
     * @param vertical Whether to flip vertically
     * @return A new flipped GameImage
     */
    public static GameImage flip(GameImage img, boolean horizontal, boolean vertical) {
        java.awt.Image awtImg = (java.awt.Image) img.getBackendImage();
        BufferedImage bImage = new BufferedImage(
                awtImg.getWidth(null),
                awtImg.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = bImage.createGraphics();
        
        // Apply transformations
        if (horizontal) {
            g.scale(-1, 1);
            g.translate(-awtImg.getWidth(null), 0);
        }
        if (vertical) {
            g.scale(1, -1);
            g.translate(0, -awtImg.getHeight(null));
        }
        
        g.drawImage(awtImg, 0, 0, null);
        g.dispose();

        return new GameImage(bImage, img.getPath(), img.getFormat());
    }
}
