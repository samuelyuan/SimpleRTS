package graphics;

public class Color {
    public final int r;
    public final int g;
    public final int b;
    public final int a; // Alpha transparency (0-255, 0 = transparent, 255 = opaque)

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
        this.a = 255; // Fully opaque by default
    }
    
    public Color(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(Color color) {
        this.r = color.r;
        this.g = color.g;
        this.b = color.b;
        this.a = color.a;
    }
    
    /**
     * Creates a new color with the same RGB values but different alpha
     */
    public Color withAlpha(int alpha) {
        return new Color(this.r, this.g, this.b, alpha);
    }
    
    /**
     * Creates a darker version of this color
     */
    public Color darker() {
        return new Color(r / 2, g / 2, b / 2, a);
    }
    
    /**
     * Creates a lighter version of this color
     */
    public Color lighter() {
        return new Color(Math.min(255, r + 50), Math.min(255, g + 50), Math.min(255, b + 50), a);
    }

    public java.awt.Color toAwtColor() {
        return new java.awt.Color(r, g, b, a);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Color color = (Color) o;
        return r == color.r && g == color.g && b == color.b && a == color.a;
    }

    @Override
    public int hashCode() {
        int result = r;
        result = 31 * result + g;
        result = 31 * result + b;
        result = 31 * result + a;
        return result;
    }

    @Override
    public String toString() {
        return "Color[r=" + r + ",g=" + g + ",b=" + b + ",a=" + a + "]";
    }
} 