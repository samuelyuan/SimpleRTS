import java.awt.Color;
import java.awt.Rectangle;

public class DrawingInstruction {
    public final Rectangle rect;
    public final Color color;
    public final boolean fill;

    public DrawingInstruction(Rectangle rect, Color color, boolean fill) {
        this.rect = rect;
        this.color = color;
        this.fill = fill;
    }
}