package graphics;

public interface IGraphics {
    void setColor(Color color);
    void setFont(GameFont font);
    void drawString(String str, int x, int y);
    void drawImage(GameImage img, int x, int y, int width, int height);
    void fillRect(int x, int y, int width, int height);
    void drawRect(int x, int y, int width, int height);
    void fillPolygon(int[] xPoints, int[] yPoints, int nPoints);
    void drawPolygon(int[] xPoints, int[] yPoints, int nPoints);
    void drawLine(int x1, int y1, int x2, int y2);
    IFontMetrics getFontMetrics(GameFont font);
} 