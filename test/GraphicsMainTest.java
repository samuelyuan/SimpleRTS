import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import graphics.Color;
import graphics.Point;
import graphics.Rect;

public class GraphicsMainTest {
    @Test
    public void testHealthBarColor() {
        GraphicsMain graphicsMain = new GraphicsMain(null); // fogWar not needed for this test
        DrawingInstruction instr = graphicsMain.getHealthBarInstruction(40, new Point(10, 10));
        assertNotNull(instr, "Instruction should not be null for health > 0");
        assertEquals(Color.ORANGE, instr.color, "Expected color for health 40 is ORANGE");
    }

    @Test
    public void testMouseSelectionInstruction() {
        Mouse.selectX1 = 120;
        Mouse.selectY1 = 80;
        Mouse.selectX2 = 200;
        Mouse.selectY2 = 160;
        Mouse.isPressed = true;

        GraphicsMain graphicsMain = new GraphicsMain(null); // fogWar not needed for this test
        DrawingInstruction instr = graphicsMain.getMouseSelectionInstruction();

        assertNotNull(instr);
        assertEquals(Color.BLACK, instr.color);
        assertFalse(instr.fill);

        Rect expected = new Rect(120, 80, 80, 80);
        assertEquals(expected, instr.rect);
    }

    @Test
    public void testFogInstructions() {
        String[][] map = {
            {"Land", "Land"},
            {"Wall", "Flag"}
        };

        GameFogWar fogWar = new GameFogWar(2, 2);
        fogWar.getVisibleData()[0][0] = false;
        fogWar.getVisibleData()[0][1] = true;
        fogWar.getVisibleData()[1][0] = false;
        fogWar.getVisibleData()[1][1] = false;

        int count = 0;
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                if (!fogWar.isTileVisible(x, y)) {
                    count++;
                }
            }
        }
        assertEquals(3, count);
    }

    @Test
    public void testCalculateDirection() {
        // East
        assertEquals(GameUnit.DIR_EAST, GraphicsMain.calculateDirection(new Point(0, 0), new Point(10, 0)));
        // West
        assertEquals(GameUnit.DIR_WEST, GraphicsMain.calculateDirection(new Point(10, 0), new Point(0, 0)));
        // South
        assertEquals(GameUnit.DIR_SOUTH, GraphicsMain.calculateDirection(new Point(0, 0), new Point(0, 10)));
        // North
        assertEquals(GameUnit.DIR_NORTH, GraphicsMain.calculateDirection(new Point(0, 10), new Point(0, 0)));
        // Diagonal (East wins if deltaX == deltaY)
        assertEquals(GameUnit.DIR_EAST, GraphicsMain.calculateDirection(new Point(0, 0), new Point(10, 10)));
        // Diagonal (West wins if deltaX == -deltaY)
        assertEquals(GameUnit.DIR_WEST, GraphicsMain.calculateDirection(new Point(10, 10), new Point(0, 0)));
    }

    @Test
    public void testGetTileImageKey() {
        assertEquals("Land", GraphicsMain.getTileImageKey("Land"));
        assertEquals("Wall", GraphicsMain.getTileImageKey("Wall"));
        assertEquals("Flag", GraphicsMain.getTileImageKey("Flag"));
        assertEquals("Land", GraphicsMain.getTileImageKey("SomethingElse"));
        assertEquals("Land", GraphicsMain.getTileImageKey(""));
    }

    @Test
    public void testRectEquality() {
        Rect expected = new Rect(120, 80, 80, 80);
        Rect actual = new Rect(120, 80, 80, 80);
        assertEquals(expected, actual, "Rects should be equal");
    }
}
