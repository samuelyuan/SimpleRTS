package ui;

import graphics.Color;
import graphics.IGraphics;
import graphics.GameFont;
import graphics.IFontMetrics;
import input.GameMouseEvent;
import input.GameMouseListener;

public class UIButton extends UIComponent implements GameMouseListener {
    private String text;
    private Runnable onClick;
    private GameFont font = new GameFont("Comic Sans", GameFont.BOLD, 20); // default
    private Color color = Color.BLACK; // default

    public UIButton(int x, int y, int width, int height, String text, Runnable onClick) {
        super(x, y, width, height);
        this.text = text;
        this.onClick = onClick;
    }

    public void setFont(GameFont font) {
        this.font = font;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    protected void draw(IGraphics g) {
        // Colors for the button
        Color mainBlue = new Color(60, 90, 200);
        Color endBlue = new Color(40, 60, 150);
        Color textColor = Color.WHITE;

        // Draw main rectangle (center part)
        g.setColor(mainBlue);
        g.fillRect(x + 20, y, width - 40, height);

        // Draw left trapezoid
        int[] xLeft = {x, x + 20, x + 20, x};
        int[] yLeft = {y + 10, y, y + height, y + height - 10};
        g.setColor(endBlue);
        g.fillPolygon(xLeft, yLeft, 4);

        // Draw right trapezoid
        int[] xRight = {x + width, x + width - 20, x + width - 20, x + width};
        int[] yRight = {y + 10, y, y + height, y + height - 10};
        g.fillPolygon(xRight, yRight, 4);

        // Draw centered text (approximate centering)
        g.setFont(font);
        g.setColor(textColor);
        
        // Get text dimensions
        IFontMetrics fm = g.getFontMetrics(font);
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent(); // use ascent for baseline positioning

        // Calculate coordinates to center the text
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height + textHeight) / 2 - 2; // small tweak for visual centering

        g.drawString(text, textX, textY);
    }

    // Use the new input abstraction
    @Override
    public boolean onGameMouseEvent(GameMouseEvent e) {
        if (contains(e.x, e.y)
            && (e.type == GameMouseEvent.Type.PRESSED || e.type == GameMouseEvent.Type.RELEASED)
            && e.button == 1) {
            if (onClick != null) onClick.run();
            return true;
        }
        return false;
    }
} 