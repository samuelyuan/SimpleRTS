package input;

import utils.Constants;

public class GameMouseEvent {
    public enum Type {
        PRESSED(Constants.MOUSE_PRESSED),
        RELEASED(Constants.MOUSE_RELEASED), 
        MOVED(Constants.MOUSE_MOVED),
        DRAGGED(Constants.MOUSE_DRAGGED);
        
        private final int value;
        
        Type(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    
    public final Type type;
    public final int x, y;
    public final int button;

    public GameMouseEvent(Type type, int x, int y, int button) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.button = button;
    }

    public int getType() {
        return type.getValue();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
} 