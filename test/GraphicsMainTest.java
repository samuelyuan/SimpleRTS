import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import graphics.Color;
import graphics.Point;
import graphics.Rect;
import graphics.IGraphics;


public class GraphicsMainTest {
    @Test
    public void testHealthBarColor() {
        // Create a mock GameStateManager for GraphicsMain
        GameStateManager mockStateManager = mock(GameStateManager.class);
        CameraManager mockCameraManager = mock(CameraManager.class);
        when(mockCameraManager.getCameraX()).thenReturn(0);
        when(mockCameraManager.getCameraY()).thenReturn(0);
        
        GraphicsMain graphicsMain = new GraphicsMain(mockStateManager, null, mockCameraManager);
        RendererUnit unitRenderer = new RendererUnit(graphicsMain);
        
        // Test health color logic
        assertEquals(Color.ORANGE, unitRenderer.getHealthColor(40), "Expected color for health 40 is ORANGE");
        assertEquals(Color.GREEN, unitRenderer.getHealthColor(80), "Expected color for health 80 is GREEN");
        assertEquals(Color.YELLOW, unitRenderer.getHealthColor(60), "Expected color for health 60 is YELLOW");
        assertEquals(Color.RED, unitRenderer.getHealthColor(20), "Expected color for health 20 is RED");
        assertNull(unitRenderer.getHealthColor(0), "Expected null for health 0");
    }

    @Test
    public void testMouseSelectionInstruction() {
        // Create a mock GameStateManager that returns 0 for camera coordinates
        GameStateManager mockStateManager = mock(GameStateManager.class);
        CameraManager mockCameraManager = mock(CameraManager.class);
        when(mockCameraManager.getCameraX()).thenReturn(0);
        when(mockCameraManager.getCameraY()).thenReturn(0);

        // Create a mock SelectionManager
        SelectionManager mockSelectionManager = mock(SelectionManager.class);
        when(mockSelectionManager.isSelectionActive()).thenReturn(true);
        
        SelectionManager.SelectionBox mockSelectionBox = new SelectionManager.SelectionBox(120, 80, 200, 160);
        when(mockSelectionManager.getSelectionBox()).thenReturn(mockSelectionBox);
        
        when(mockStateManager.getSelectionManager()).thenReturn(mockSelectionManager);

        GraphicsMain graphicsMain = new GraphicsMain(mockStateManager, null, mockCameraManager);
        DrawingInstruction instr = graphicsMain.getMouseSelectionInstruction();

        assertNotNull(instr);
        assertEquals(new Color(0, 150, 255), instr.color); // Blue color
        assertFalse(instr.fill); // Outline instead of fill

        Rect expected = new Rect(120, 80, 80, 80);
        assertEquals(expected, instr.rect);
    }

    @Test
    public void testFogInstructions() {
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
        assertEquals(Constants.DIR_EAST, GraphicsMain.calculateDirection(new Point(0, 0), new Point(10, 0)));
        // West
        assertEquals(Constants.DIR_WEST, GraphicsMain.calculateDirection(new Point(10, 0), new Point(0, 0)));
        // South
        assertEquals(Constants.DIR_SOUTH, GraphicsMain.calculateDirection(new Point(0, 0), new Point(0, 10)));
        // North
        assertEquals(Constants.DIR_NORTH, GraphicsMain.calculateDirection(new Point(0, 10), new Point(0, 0)));
        // Diagonal (East wins if deltaX == deltaY)
        assertEquals(Constants.DIR_EAST, GraphicsMain.calculateDirection(new Point(0, 0), new Point(10, 10)));
        // Diagonal (West wins if deltaX == -deltaY)
        assertEquals(Constants.DIR_WEST, GraphicsMain.calculateDirection(new Point(10, 10), new Point(0, 0)));
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

    @Test
    public void testMinimapRendering() {
        // Create a mock GameStateManager with necessary components
        GameStateManager mockStateManager = mock(GameStateManager.class);
        CameraManager mockCameraManager = mock(CameraManager.class);
        when(mockCameraManager.getCameraX()).thenReturn(0);
        when(mockCameraManager.getCameraY()).thenReturn(0);

        // Create a mock GameMap
        GameMap mockGameMap = mock(GameMap.class);
        int[][] mockMapData = new int[5][5]; // 5x5 map
        when(mockGameMap.getMapData()).thenReturn(mockMapData);
        when(mockStateManager.getGameMap()).thenReturn(mockGameMap);

        // Create a mock GameUnitManager
        GameUnitManager mockUnitManager = mock(GameUnitManager.class);
        when(mockStateManager.getUnitManager()).thenReturn(mockUnitManager);

        // Create a mock GameFlagManager
        GameFlagManager mockFlagManager = mock(GameFlagManager.class);
        when(mockUnitManager.getFlagManager()).thenReturn(mockFlagManager);

        // Create empty lists for units and flags
        when(mockUnitManager.getPlayerList()).thenReturn(new ArrayList<>());
        when(mockUnitManager.getEnemyList()).thenReturn(new ArrayList<>());
        when(mockFlagManager.getFlagList()).thenReturn(new ArrayList<GameFlag>().iterator());

        // Create GameFogWar
        GameFogWar fogWar = new GameFogWar(5, 5);

        // Create GraphicsMain
        GraphicsMain graphicsMain = new GraphicsMain(mockStateManager, fogWar, mockCameraManager);

        // Create mock graphics
        IGraphics mockGraphics = mock(IGraphics.class);

        // Test that drawMinimap doesn't throw exceptions
        assertDoesNotThrow(() -> {
            graphicsMain.drawMinimap(mockGraphics);
        }, "Minimap rendering should not throw exceptions");
    }
}
