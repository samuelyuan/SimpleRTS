import java.awt.Color;
import java.awt.Font;

public class GameFont {
	public static void setFont(Font newFont) {
		SimpleRTS.offscr.setFont(newFont);
	}

	public static void setColor(Color color) {
		SimpleRTS.offscr.setColor(color);
	}

	public static void changeFont(int style, float size) {
		Font currentFont = SimpleRTS.offscr.getFont();
		Font modifiedFont = currentFont.deriveFont(style).deriveFont(size);
		SimpleRTS.offscr.setFont(modifiedFont);
	}

	public static void printString(String text, int x, int y) {
		SimpleRTS.offscr.drawString(text, x, y);
	}
}
