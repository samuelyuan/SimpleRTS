import graphics.Color;
import graphics.Rect;

public class DrawingInstruction {
    public final Rect rect;
    public final Color color;
    public final boolean fill;

    public DrawingInstruction(Rect rect, Color color, boolean fill) {
        this.rect = rect;
        this.color = color;
        this.fill = fill;
    }
}