package ui;

import graphics.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import graphics.AwtGraphicsAdapter;
import graphics.IGraphics;

public class UIButton extends UIComponent {
    private String text;
    private Runnable onClick;
    private Font font = new Font("Comic Sans", Font.BOLD, 20); // default
    private Color color = Color.BLACK; // default

    public UIButton(int x, int y, int width, int height, String text, Runnable onClick) {
        super(x, y, width, height);
        this.text = text;
        this.onClick = onClick;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    protected void draw(IGraphics g) {
        // For advanced drawing, we need the underlying Graphics object
        Graphics g2 = null;
        if (g instanceof AwtGraphicsAdapter) {
            g2 = ((AwtGraphicsAdapter) g).getGraphics();
        }
        if (g2 != null) {
            // Colors for the button
            Color mainBlue = new Color(60, 90, 200);
            Color endBlue = new Color(40, 60, 150);
            Color textColor = Color.WHITE;

            // Draw main rectangle (center part)
            g2.setColor(mainBlue.toAwtColor());
            g2.fillRect(x + 20, y, width - 40, height);

            // Draw left trapezoid
            int[] xLeft = {x, x + 20, x + 20, x};
            int[] yLeft = {y + 10, y, y + height, y + height - 10};
            g2.setColor(endBlue.toAwtColor());
            g2.fillPolygon(xLeft, yLeft, 4);

            // Draw right trapezoid
            int[] xRight = {x + width, x + width - 20, x + width - 20, x + width};
            int[] yRight = {y + 10, y, y + height, y + height - 10};
            g2.fillPolygon(xRight, yRight, 4);

            // Draw centered text
            g2.setFont(font);
            g2.setColor(textColor.toAwtColor());
            java.awt.FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            int textX = x + (width - textWidth) / 2;
            int textY = y + (height + textHeight) / 2 - 4;
            g2.drawString(text, textX, textY);
        } else {
            // Fallback: use IGraphics interface for basic text
            g.setFont(font);
            g.setColor(color);
            g.drawString(text, x + 10, y + height / 2);
        }
    }

    @Override
    protected boolean onMouse(MouseEvent e) {
        if (contains(e.getX(), e.getY())
            && (e.getID() == MouseEvent.MOUSE_PRESSED || e.getID() == MouseEvent.MOUSE_RELEASED)
            && e.getButton() == MouseEvent.BUTTON1) {
            if (onClick != null) onClick.run();
            return true;
        }
        return false;
    }
} 