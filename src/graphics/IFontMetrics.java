package graphics;

public interface IFontMetrics {
    int stringWidth(String text);
    int getAscent();
    int getDescent();
    int getHeight();
}