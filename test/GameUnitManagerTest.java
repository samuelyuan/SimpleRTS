import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import graphics.Point;

public class GameUnitManagerTest {
    private GameUnitManager unitManager;
    private GameFlagManager flagManager;

    @BeforeEach
    public void setUp() {
        flagManager = new GameFlagManager();
        unitManager = new GameUnitManager(flagManager);
    }

    @Test
    public void testClearUnits() {
        // Add dummy units
        unitManager.getPlayerList().add(new GameUnit(0, 0, true, Constants.UNIT_ID_LIGHT));
        unitManager.getEnemyList().add(new GameUnit(1, 1, false, Constants.UNIT_ID_MEDIUM));
        assertFalse(unitManager.getPlayerList().isEmpty());
        assertFalse(unitManager.getEnemyList().isEmpty());
        unitManager.clearUnits();
        assertTrue(unitManager.getPlayerList().isEmpty());
        assertTrue(unitManager.getEnemyList().isEmpty());
    }

    @Test
    public void testLoadPlayerUnits() {
        Map<Point, Integer> allyUnits = new HashMap<>();
        allyUnits.put(new Point(2, 3), Constants.UNIT_ID_LIGHT);
        allyUnits.put(new Point(4, 5), Constants.UNIT_ID_HEAVY);
        unitManager.loadPlayerUnits(allyUnits);
        assertEquals(2, unitManager.getPlayerList().size());
        assertEquals(TileCoordinateConverter.mapToScreen(2, 3), unitManager.getPlayerList().get(0).getCurrentPosition());
        assertEquals(Constants.UNIT_ID_LIGHT, unitManager.getPlayerList().get(0).getClassType());
    }

    @Test
    public void testLoadEnemyUnits() {
        Map<Point, Integer> enemyUnits = new HashMap<>();
        enemyUnits.put(new Point(1, 1), Constants.UNIT_ID_MEDIUM);
        unitManager.loadEnemyUnits(enemyUnits);
        assertEquals(1, unitManager.getEnemyList().size());
        assertEquals(TileCoordinateConverter.mapToScreen(1, 1), unitManager.getEnemyList().get(0).getCurrentPosition());
        assertEquals(Constants.UNIT_ID_MEDIUM, unitManager.getEnemyList().get(0).getClassType());
    }

    @Test
    public void testLoadFlag() {
        Map<Point, Integer> flagPositions = new HashMap<>();
        flagPositions.put(new Point(3, 3), GameFlag.FACTION_PLAYER);
        flagPositions.put(new Point(5, 5), GameFlag.FACTION_ENEMY);
        unitManager.loadFlag(flagPositions);
        assertEquals(1, flagManager.getNumFlagsPlayer());
        assertEquals(1, flagManager.getNumFlagsEnemy());
    }

    @Test
    public void testRemoveDeadUnits() {
        GameUnit unit1 = new GameUnit(0, 0, true, Constants.UNIT_ID_LIGHT);
        GameUnit unit2 = new GameUnit(1, 1, true, Constants.UNIT_ID_MEDIUM);
        ArrayList<GameUnit> playerList = unitManager.getPlayerList();
        playerList.add(unit1);
        playerList.add(unit2);
        int[][] map = new int[2][2];
        unitManager.removeDeadUnits(map, playerList, 0);
        assertEquals(1, playerList.size());
        assertEquals(unit2, playerList.get(0));
    }

    @Test
    public void testInitWithTestData() {
        Map<Point, Integer> allyUnits = new HashMap<>();
        allyUnits.put(new Point(1, 2), Constants.UNIT_ID_LIGHT);
        Map<Point, Integer> enemyUnits = new HashMap<>();
        enemyUnits.put(new Point(3, 4), Constants.UNIT_ID_HEAVY);
        Map<Point, Integer> flagPositions = new HashMap<>();
        flagPositions.put(new Point(5, 6), GameFlag.FACTION_PLAYER);
        unitManager.init(allyUnits, enemyUnits, flagPositions);
        assertEquals(1, unitManager.getPlayerList().size());
        assertEquals(1, unitManager.getEnemyList().size());
        assertEquals(1, flagManager.getNumFlagsPlayer());
    }
} 