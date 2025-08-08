import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import graphics.*;

/**
 * Test class for the enhanced graphics abstraction layer
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
        // Test transform methods
        assertDoesNotThrow(() -> {
            graphicsAdapter.save();
            graphicsAdapter.translate(10, 20);
            graphicsAdapter.scale(2.0, 2.0);
            graphicsAdapter.rotate(45);
            graphicsAdapter.restore();
        });
    }
    
    @Test
    void testEnhancedDrawingMethods() {
        // Test enhanced drawing methods
        assertDoesNotThrow(() -> {
            graphicsAdapter.setColor(Color.RED);
            graphicsAdapter.drawCircle(100, 100, 50, true);
            graphicsAdapter.drawEllipse(200, 200, 100, 50, false);
            graphicsAdapter.drawRoundRect(300, 300, 80, 40, 10, 10, true);
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
        
        // Test scaling with factors
        GameImage scaledByFactor = gameImage.getScaled(0.5, 0.5, true);
        assertNotNull(scaledByFactor);
        assertEquals(32, scaledByFactor.getWidth());
        assertEquals(32, scaledByFactor.getHeight());
        
        // Test rotation
        GameImage rotated = gameImage.getRotated(45);
        assertNotNull(rotated);
        
        // Test cropping
        GameImage cropped = gameImage.getCropped(16, 16, 32, 32);
        assertNotNull(cropped);
        assertEquals(32, cropped.getWidth());
        assertEquals(32, cropped.getHeight());
        
        // Test copying
        GameImage copied = gameImage.copy();
        assertNotNull(copied);
        assertEquals(gameImage.getWidth(), copied.getWidth());
        assertEquals(gameImage.getHeight(), copied.getHeight());
        
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
    
    @Test
    void testImageUtilsEnhancements() {
        // Test ImageUtils enhancements
        assertDoesNotThrow(() -> {
            // Test tinting
            GameImage tinted = ImageUtils.tint(gameImage, Color.BLUE, 0.5);
            assertNotNull(tinted);
            
            // Test grayscale
            GameImage grayscale = ImageUtils.grayscale(gameImage);
            assertNotNull(grayscale);
            
            // Test flipping
            GameImage flippedH = ImageUtils.flip(gameImage, true, false);
            assertNotNull(flippedH);
            
            GameImage flippedV = ImageUtils.flip(gameImage, false, true);
            assertNotNull(flippedV);
            
            // Test team coloring
            GameImage playerUnit = ImageUtils.addTeamColorToUnit(gameImage, true);
            assertNotNull(playerUnit);
            
            GameImage enemyUnit = ImageUtils.addTeamColorToUnit(gameImage, false);
            assertNotNull(enemyUnit);
        });
    }
    
    @Test
    void testGraphicsAdapterState() {
        // Test state management
        Color testColor = new Color(255, 0, 0);
        GameFont testFont = new GameFont("Times", GameFont.BOLD, 16);
        
        graphicsAdapter.setColor(testColor);
        graphicsAdapter.setFont(testFont);
        
        assertEquals(testColor, graphicsAdapter.getColor());
        assertEquals(testFont, graphicsAdapter.getFont());
    }
    
    @Test
    void testClearMethod() {
        // Test clear method
        assertDoesNotThrow(() -> {
            graphicsAdapter.clear(Color.WHITE);
            // Should not throw exception
        });
    }
    
    @Test
    void testStrokeWidth() {
        // Test stroke width setting
        assertDoesNotThrow(() -> {
            graphicsAdapter.setStrokeWidth(3.0f);
            graphicsAdapter.drawRect(10, 10, 50, 50);
        });
    }
} 