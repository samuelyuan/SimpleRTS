import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import graphics.GameImage;
import utils.PathResolver;

/**
 * Test class for ImageService error handling
 */
public class ImageServiceTest {
    private ImageService imageService;
    
    @BeforeEach
    void setUp() {
        PathResolver pathResolver = new PathResolver();
        imageService = new ImageService(pathResolver);
        imageService.loadImages();
    }
    
    @Test
    void testMissingImageFallback() {
        // Test that requesting a missing image returns a fallback instead of throwing
        assertDoesNotThrow(() -> {
            GameImage fallbackImage = imageService.getGameImage(999); // Non-existent ID
            assertNotNull(fallbackImage);
            assertTrue(fallbackImage.isValid());
        });
    }
    
    @Test
    void testMissingTileFallback() {
        // Test that requesting a missing tile returns a fallback instead of throwing
        assertDoesNotThrow(() -> {
            GameImage fallbackTile = imageService.getTileImage("non-existent-tile");
            assertNotNull(fallbackTile);
            assertTrue(fallbackTile.isValid());
        });
    }
    
    @Test
    void testExistingImageLoads() {
        // Test that existing images load properly
        assertDoesNotThrow(() -> {
            GameImage bgMenu = imageService.getGameImage(ImageConstants.IMGID_BG_MENU);
            assertNotNull(bgMenu);
            assertTrue(bgMenu.isValid());
        });
    }
    
    @Test
    void testDefeatImageFallback() {
        // Test that the missing defeat image (ID 17) returns a fallback
        assertDoesNotThrow(() -> {
            GameImage defeatImage = imageService.getGameImage(ImageConstants.IMGID_MENU_DEFEAT);
            assertNotNull(defeatImage);
            assertTrue(defeatImage.isValid());
        });
    }
    
    @Test
    void testVictoryImageLoads() {
        // Test that the victory image loads (should exist)
        assertDoesNotThrow(() -> {
            GameImage victoryImage = imageService.getGameImage(ImageConstants.IMGID_MENU_VICTORY);
            assertNotNull(victoryImage);
            assertTrue(victoryImage.isValid());
        });
    }
} 