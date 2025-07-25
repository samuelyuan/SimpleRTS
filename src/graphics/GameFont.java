package graphics;

public class GameFont {
    public static final int PLAIN = 0;
    public static final int BOLD = 1;
    public static final int ITALIC = 2;

    public final String family;
    public final int style;
    public final int size;

    public GameFont(String family, int style, int size) {
        this.family = family;
        this.style = style;
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameFont gameFont = (GameFont) o;
        return style == gameFont.style && size == gameFont.size && family.equals(gameFont.family);
    }

    @Override
    public int hashCode() {
        int result = family.hashCode();
        result = 31 * result + style;
        result = 31 * result + size;
        return result;
    }
} 