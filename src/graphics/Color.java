package graphics;

public class Color {
    public final int r;
    public final int g;
    public final int b;

    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color RED = new Color(255, 0, 0);
    public static final Color GREEN = new Color(0, 255, 0);
    public static final Color BLUE = new Color(0, 0, 255);
    public static final Color GRAY = new Color(128, 128, 128);
    public static final Color YELLOW = new Color(255, 255, 0);
    public static final Color ORANGE = new Color(255, 165, 0);

    public Color(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Color(Color color) {
        this.r = color.r;
        this.g = color.g;
        this.b = color.b;
    }

    public java.awt.Color toAwtColor() {
        return new java.awt.Color(r, g, b);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Color color = (Color) o;
        return r == color.r && g == color.g && b == color.b;
    }

    @Override
    public int hashCode() {
        int result = r;
        result = 31 * result + g;
        result = 31 * result + b;
        return result;
    }

    @Override
    public String toString() {
        return "Color[r=" + r + ",g=" + g + ",b=" + b + "]";
    }
} 