package map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class MapDescriptionLoaderTest {

    @Test
    public void testGetAvailableLevels_ContainsLevel1() {
        List<Integer> levels = MapDescriptionLoader.getAvailableLevels();
        assertNotNull(levels);
        assertTrue(levels.contains(1));
    }
    
    @Test
    public void testGetAvailableLevels_ContainsLevel2() {
        List<Integer> levels = MapDescriptionLoader.getAvailableLevels();
        assertNotNull(levels);
        assertTrue(levels.contains(2));
    }
    
    @Test
    public void testGetAvailableLevels_Size() {
        List<Integer> levels = MapDescriptionLoader.getAvailableLevels();
        assertNotNull(levels);
        assertTrue(levels.size() >= 2);
    }
    
    @Test
    public void testFormatDescription_NotNull() {
        String testText = "This is a test description that should be formatted to fit within a reasonable line width for display purposes.";
        List<String> formatted = MapDescriptionLoader.formatDescription(testText, 40);
        assertNotNull(formatted);
    }
    
    @Test
    public void testFormatDescription_NotEmpty() {
        String testText = "This is a test description that should be formatted to fit within a reasonable line width for display purposes.";
        List<String> formatted = MapDescriptionLoader.formatDescription(testText, 40);
        assertFalse(formatted.isEmpty());
    }
    
    @Test
    public void testFormatDescription_LineWidthLimit() {
        String testText = "This is a test description that should be formatted to fit within a reasonable line width for display purposes.";
        List<String> formatted = MapDescriptionLoader.formatDescription(testText, 40);
        
        for (String line : formatted) {
            assertTrue(line.length() <= 40, "Line exceeds max width: " + line);
        }
    }
    
    @Test
    public void testFormatDescription_EmptyInput() {
        List<String> formatted = MapDescriptionLoader.formatDescription("", 40);
        assertNotNull(formatted);
        assertTrue(formatted.isEmpty());
    }
    
    @Test
    public void testFormatDescription_NullInput() {
        List<String> formatted = MapDescriptionLoader.formatDescription(null, 40);
        assertNotNull(formatted);
        assertTrue(formatted.isEmpty());
    }
    
    @Test
    public void testGetDescription_NonExistentLevel() {
        String desc = MapDescriptionLoader.getDescription(999, true);
        assertNull(desc);
    }
    
    @Test
    public void testGetDescription_NonExistentLevelEnd() {
        String desc = MapDescriptionLoader.getDescription(999, false);
        assertNull(desc);
    }
} 