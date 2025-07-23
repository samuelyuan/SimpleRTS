import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

public class GraphicsMainTest {
    @Test
    public void testHealthBarColor() {
        DrawingInstruction instr = GraphicsMain.getHealthBarInstruction(40, new Point(10, 10));
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

        DrawingInstruction instr = GraphicsMain.getMouseSelectionInstruction();

        assertNotNull(instr);
        assertEquals(Color.BLACK, instr.color);
        assertFalse(instr.fill);

        Rectangle expected = new Rectangle(120, 80, 80, 80);
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
}
