import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import graphics.*;

/**
 * Test class for the simplified graphics abstraction layer
 */
public class GraphicsAbstractionTest {
    private BufferedImage testImage;
    private Graphics2D testGraphics;
    private AwtGraphicsAdapter graphicsAdapter;
    private GameImage gameImage;
    
    @BeforeEach
    void setUp() {
        // Create a test image
        testImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        testGraphics = testImage.createGraphics();
        graphicsAdapter = new AwtGraphicsAdapter(testGraphics);
        
        // Create a test GameImage
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(java.awt.Color.RED);
        g2d.fillRect(0, 0, 64, 64);
        g2d.dispose();
        
        gameImage = new GameImage(img, "test.png", GameImage.ImageFormat.PNG);
    }
    
    @Test
    void testBasicDrawingMethods() {
        // Test basic drawing methods still work
        graphicsAdapter.setColor(Color.RED);
        graphicsAdapter.fillRect(10, 10, 50, 50);
        
        graphicsAdapter.setColor(Color.BLUE);
        graphicsAdapter.drawRect(100, 100, 50, 50);
        
        graphicsAdapter.setColor(Color.GREEN);
        graphicsAdapter.drawLine(0, 0, 100, 100);
        
        // These should not throw exceptions
        assertDoesNotThrow(() -> {
            graphicsAdapter.setColor(Color.WHITE);
            graphicsAdapter.setFont(new GameFont("Arial", GameFont.PLAIN, 12));
            graphicsAdapter.drawString("Test", 200, 200);
        });
    }
    
    @Test
    void testTransformMethods() {
        // Test transformation methods
        graphicsAdapter.save();
        graphicsAdapter.translate(100, 100);
        graphicsAdapter.rotate(45);
        graphicsAdapter.scale(2.0, 2.0);
        
        // Draw something with transformation
        graphicsAdapter.setColor(Color.RED);
        graphicsAdapter.fillRect(0, 0, 25, 25);
        
        graphicsAdapter.restore();
        
        // Should not throw exceptions
        assertDoesNotThrow(() -> {
            graphicsAdapter.translate(50, 50);
            graphicsAdapter.rotate(30);
            graphicsAdapter.scale(1.5, 1.5);
        });
    }
    
    @Test
    void testImageDrawingWithRotation() {
        // Test drawing image with rotation
        assertDoesNotThrow(() -> {
            graphicsAdapter.drawImage(gameImage, 100, 100, 64, 64, 45);
        });
    }
    
    @Test
    void testAntiAliasing() {
        // Test anti-aliasing
        assertDoesNotThrow(() -> {
            graphicsAdapter.setAntiAliasing(true);
            graphicsAdapter.setAntiAliasing(false);
        });
    }
    
    @Test
    void testGameImageEnhancements() {
        // Test GameImage enhancements
        assertNotNull(gameImage);
        assertEquals(64, gameImage.getWidth());
        assertEquals(64, gameImage.getHeight());
        assertEquals(GameImage.ImageFormat.PNG, gameImage.getFormat());
        assertEquals("test.png", gameImage.getPath());
        assertFalse(gameImage.isDisposed());
        assertTrue(gameImage.isValid());
        
        // Test scaling
        GameImage scaled = gameImage.getScaled(32, 32, true);
        assertNotNull(scaled);
        assertEquals(32, scaled.getWidth());
        assertEquals(32, scaled.getHeight());
        
        // Test rotation
        GameImage rotated = gameImage.getRotated(45);
        assertNotNull(rotated);
        
        // Test disposal
        gameImage.dispose();
        assertTrue(gameImage.isDisposed());
        assertFalse(gameImage.isValid());
    }
    
    @Test
    void testColorEnhancements() {
        // Test Color class enhancements
        Color color = new Color(255, 128, 64, 200);
        assertEquals(255, color.r);
        assertEquals(128, color.g);
        assertEquals(64, color.b);
        assertEquals(200, color.a);
        
        // Test withAlpha
        Color transparent = color.withAlpha(100);
        assertEquals(255, transparent.r);
        assertEquals(128, transparent.g);
        assertEquals(64, transparent.b);
        assertEquals(100, transparent.a);
        
        // Test darker/lighter
        Color darker = color.darker();
        assertTrue(darker.r < color.r);
        assertTrue(darker.g < color.g);
        assertTrue(darker.b < color.b);
        
        Color lighter = color.lighter();
        assertTrue(lighter.r >= color.r);
        assertTrue(lighter.g >= color.g);
        assertTrue(lighter.b >= color.b);
    }
} 