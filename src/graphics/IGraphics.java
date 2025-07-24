package graphics;

import graphics.Color;
import java.awt.Font;
import java.awt.Image;

public interface IGraphics {
    void setColor(Color color);
    void setFont(Font font);
    void drawString(String str, int x, int y);
    void drawImage(Image img, int x, int y, int width, int height);
} 