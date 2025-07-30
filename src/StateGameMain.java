import java.util.ArrayList;
import java.util.Iterator;

import graphics.IGraphics;
import graphics.Point;
import input.GameMouseEvent;
import map.TileConverter;

/**
 * Main game state that handles the game loop and input.
 */
public class StateGameMain extends StateMachine {
	private final GameStateManager stateManager;
	private final GameUnitManager unitManager;
	private final GameFogWar fogWar;
	private final GraphicsMain graphicsMain;
	private final GameTimer gameTimer;
	private final UnitSpawner unitSpawner;

	public StateGameMain(GameStateManager stateManager, GameUnitManager unitManager, GameFogWar fogWar,
			GraphicsMain graphicsMain) {
		this.stateManager = stateManager;
		this.unitManager = unitManager;
		this.fogWar = fogWar;
		this.graphicsMain = graphicsMain;
		this.gameTimer = new GameTimer(1, 0, unitManager);
		this.unitSpawner = new UnitSpawner(unitManager);
	}

	@Override
	public void run(graphics.IGraphics g) {
		int[][] map = stateManager.getGameMap().getMapData();

		// Initialize components if needed
		initializeComponentsIfNeeded(map);

		// Render graphics
		graphicsMain.drawGraphics(g, gameTimer.getGameTime(), unitManager);

		// Handle flag spawning
		handleFlagSpawning(map);

		// Run faction logic
		runFaction(map, GameFlag.FACTION_PLAYER);
		runFaction(map, GameFlag.FACTION_ENEMY);

		// Update game timer
		gameTimer.update();
	}

	private void initializeComponentsIfNeeded(int[][] map) {
		if (fogWar == null || graphicsMain == null) {
			// This should ideally be handled in the constructor, but keeping for compatibility
		}
	}

	private void handleFlagSpawning(int[][] map) {
		Iterator<GameFlag> itrFlag = unitManager.getFlagManager().getFlagList();
		while (itrFlag.hasNext()) {
			GameFlag flag = itrFlag.next();

			// Spawn units at 12:00 hours
			if (gameTimer.getHour() == GameTimer.SPAWN_HOUR && !unitSpawner.isSpawned()) {
				unitSpawner.spawnUnitsNearFlag(map, flag);
			}

			flag.runLogic();
		}

		// Update spawn state
		unitSpawner.updateSpawnState(gameTimer.getHour());
	}

	// Retrieve the unit list depending on which faction it is
	private ArrayList<GameUnit> getUnitList(int factionId) {
		if (factionId == GameFlag.FACTION_PLAYER) {
			return unitManager.getPlayerList();
		} else if (factionId == GameFlag.FACTION_ENEMY) {
			return unitManager.getEnemyList();
		}
		return new ArrayList<>();
	}

	private void runFaction(int[][] map, int factionId) {
		ArrayList<GameUnit> unitList = getUnitList(factionId);

		// Loop through all units
		for (int i = 0; i < unitList.size(); i++) {
			GameUnit unit = unitList.get(i);

			if (factionId == GameFlag.FACTION_PLAYER) {
				runPlayerLogic(map, unit);
			} else if (factionId == GameFlag.FACTION_ENEMY) {
				runEnemyLogic(map, unit);
			}

			// Determine whether the unit is near the flag
			unitManager.checkFlagStates(unit, factionId);

			// Remove dead units
			if (!unit.isAlive()) {
				unitManager.removeDeadUnits(map, unitList, i);
			}
		}

		// Check terminating conditions
		checkTerminatingConditions(factionId);
	}

	private void runPlayerLogic(int[][] map, GameUnit playerUnit) {
		// Select and move units
		playerUnit.isPlayerSelected = stateManager.getSelectionManager().isPlayerSelect(
			playerUnit.getCurrentPoint(), 
			playerUnit.isClickedOn,
			stateManager.getCameraX(), 
			stateManager.getCameraY()
		);

		playerUnit.findPath(map, unitManager.getPlayerList());

		// Handle battles
		playerUnit.interactWithEnemy(map, unitManager.getEnemyList());
	}

	private void runEnemyLogic(int[][] map, GameUnit enemyUnit) {
		// Send enemy units to attack the flag every day at around 06:00 hours
		if (gameTimer.isEnemyAttackTime()) {
			GameFlag playerFlag = unitManager.getFlagManager().getPlayerFlag();
			if (playerFlag == null) {
				System.out.println("No player flag found!");
				return;
			}
			enemyUnit.destination = new Point(
				TileCoordinateConverter.mapToScreen(playerFlag.getMapX() - 1, playerFlag.getMapY()).x,
				TileCoordinateConverter.mapToScreen(playerFlag.getMapX() - 1, playerFlag.getMapY()).y
			);
			enemyUnit.startMoving();
		}

		// Follow the path towards the flag
		enemyUnit.findPath(map, unitManager.getEnemyList());
	}

	private void checkTerminatingConditions(int factionId) {
		if (unitManager.isFlagsListEmpty(factionId)) {
			if (factionId == GameFlag.FACTION_PLAYER) {
				stateManager.setNewState(GameState.STATE_GAMEOVER); // Player loses all flags
			} else if (factionId == GameFlag.FACTION_ENEMY) {
				stateManager.setNewState(GameState.STATE_NEXTLVL); // Enemy loses all flags
			}
		}
	}

	@Override
	public void handleMouseCommand(GameMouseEvent e) {
		for (int i = 0; i < unitManager.getPlayerList().size(); i++) {
			GameUnit player = unitManager.getPlayerList().get(i);

			// Right mouse click dictates player position
			if (isRightClick(e) && player.isPlayerSelected) {
				handleUnitMovement(e, player);
			}

			// Update unit selection state
			player.isClickedOn = stateManager.getSelectionManager().isClickOnUnit(
				e, 
				player.getCurrentPoint(), 
				stateManager.getCameraX(), 
				stateManager.getCameraY()
			);
		}
	}

	private boolean isRightClick(GameMouseEvent e) {
		return e.button == 3; // Right mouse button
	}

	private void handleUnitMovement(GameMouseEvent e, GameUnit player) {
		// Convert screen coordinates to world coordinates with camera offset
		player.destination = TileCoordinateConverter.screenToMapWithCamera(
			new Point(e.x, e.y), 
			stateManager.getCameraX(), 
			stateManager.getCameraY()
		);
		// Convert back to screen coordinates for the destination
		player.destination = TileCoordinateConverter.mapToScreen(player.destination);

		// Player should start moving
		player.startMoving();
	}
}
