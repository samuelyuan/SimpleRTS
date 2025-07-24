import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import graphics.Point;

public class GameFogWarTest {

    private GameFogWar fog;

    @BeforeEach
    public void setUp() {
        fog = new GameFogWar(10, 10);
    }

    @Test
    public void testResetClearsVisibility() {
        fog.getVisibleData()[2][2] = true;
        fog.reset(10, 10);
        assertFalse(fog.isTileVisible(2, 2));
    }

    @Test
    public void testIsTileVisibleFalseByDefault() {
        assertFalse(fog.isTileVisible(3, 3));
    }

    @Test
    public void testCalculateFogOfWarSetsVisibleTilesInRange() {
        GameUnit mockUnit = mock(GameUnit.class);
        when(mockUnit.getCurrentPoint()).thenReturn(new Point(5, 5));
        when(mockUnit.getMapPoint(new Point(5, 5))).thenReturn(new Point(5, 5));

        fog.calculateFogOfWar(List.of(mockUnit), new int[10][10]);

        assertTrue(fog.isTileVisible(5, 5)); // center
        assertTrue(fog.isTileVisible(0, 5)); // left edge
        assertTrue(fog.isTileVisible(5, 0)); // top edge
        assertTrue(fog.isTileVisible(9, 5)); // right edge
        assertTrue(fog.isTileVisible(5, 9)); // bottom edge
    }

    @Test
    public void testCalculateFogOfWarDoesNotThrowOnEdgeUnits() {
        GameUnit mockUnit = mock(GameUnit.class);
        when(mockUnit.getCurrentPoint()).thenReturn(new Point(0, 0));
        when(mockUnit.getMapPoint(new Point(0, 0))).thenReturn(new Point(0, 0));

        assertDoesNotThrow(() -> fog.calculateFogOfWar(List.of(mockUnit), new int[10][10]));
    }

    @Test
    public void testMultipleUnitsUpdateVisibility() {
        GameUnit unit1 = mock(GameUnit.class);
        when(unit1.getCurrentPoint()).thenReturn(new Point(1, 1));
        when(unit1.getMapPoint(new Point(1, 1))).thenReturn(new Point(1, 1));

        GameUnit unit2 = mock(GameUnit.class);
        when(unit2.getCurrentPoint()).thenReturn(new Point(8, 8));
        when(unit2.getMapPoint(new Point(8, 8))).thenReturn(new Point(8, 8));

        fog.calculateFogOfWar(List.of(unit1, unit2), new int[10][10]);

        assertTrue(fog.isTileVisible(1, 1));
        assertTrue(fog.isTileVisible(8, 8));
    }
}
