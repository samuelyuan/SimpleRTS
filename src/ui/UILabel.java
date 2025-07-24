package ui;

import graphics.Color;
import java.awt.Font;
import graphics.IGraphics;

public class UILabel extends UIComponent {
    private String text;
    private Font font = new Font("Comic Sans", Font.BOLD, 20); // default
    private Color color = Color.BLACK; // default

    public UILabel(int x, int y, String text) {
        super(x, y, 0, 0); // width/height not used for label
        this.text = text;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    protected void draw(IGraphics g) {
        g.setFont(font);
        g.setColor(color);
        g.drawString(text, x, y);
    }
} 