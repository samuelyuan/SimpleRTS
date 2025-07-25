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

        return new GameImage(bImage);
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

        return new GameImage(bImage);
    }

    
	/**
	 * scale image
	 * 
	 * @param sbi       image to scale
	 * @param imageType type of image
	 * @param dWidth    width of destination image
	 * @param dHeight   height of destination image
	 * @param fWidth    x-factor for transformation / scaling
	 * @param fHeight   y-factor for transformation / scaling
	 * @return scaled image
	 */
	public static BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth,
			double fHeight) {
		BufferedImage dbi = null;
		if (sbi != null) {
			dbi = new BufferedImage(dWidth, dHeight, imageType);
			Graphics2D g = dbi.createGraphics();
			AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
			g.drawRenderedImage(sbi, at);
		}
		return dbi;
	}
}
