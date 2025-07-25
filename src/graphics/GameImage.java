package graphics;

public class GameImage {
    private final Object backendImage;

    public GameImage(Object backendImage) {
        this.backendImage = backendImage;
    }

    public Object getBackendImage() {
        return backendImage;
    }
} 