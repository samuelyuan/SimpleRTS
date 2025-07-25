package input;

public class GameMouseEvent {
    public enum Type { PRESSED, RELEASED, MOVED, DRAGGED }
    public final Type type;
    public final int x, y;
    public final int button;

    public GameMouseEvent(Type type, int x, int y, int button) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.button = button;
    }
} 