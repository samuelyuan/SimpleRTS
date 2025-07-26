import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graphics.Point;
import map.MapParseResult;
import map.TileConverter;

public class GameMapTest {
    @Test
    public void testFormatMapDescriptionWrapsAndMatchesLevel() {
        String rawLine = "#2a This is a simple test to check line wrapping";
        GameMap gameMap = new GameMap();
        ArrayList<String> result = gameMap.formatMapDescription(rawLine, 2, 20);

        assertNotNull(result);
        assertTrue(result.size() > 1); // should wrap
        assertTrue(result.get(0).startsWith("This")); // confirm it's formatting content
    }
}
