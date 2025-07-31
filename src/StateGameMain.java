import java.util.ArrayList;
import java.util.Iterator;

import graphics.Point;
import input.GameMouseEvent;

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
		graphicsMain.drawGraphics(g, gameTimer, unitManager);

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
		playerUnit.setPlayerSelected(stateManager.getSelectionManager().isPlayerSelect(
			playerUnit.getCurrentPosition(), 
			playerUnit.isClickedOn(),
			graphicsMain.getCameraX(), 
			graphicsMain.getCameraY()
		));

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
			enemyUnit.setDestination(new Point(
				TileCoordinateConverter.mapToScreen(playerFlag.getMapX() - 1, playerFlag.getMapY()).x,
				TileCoordinateConverter.mapToScreen(playerFlag.getMapX() - 1, playerFlag.getMapY()).y
			));
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
			if (isRightClick(e) && player.isPlayerSelected()) {
				handleUnitMovement(e, player);
			}

			// Update unit selection state
			player.setClickedOn(stateManager.getSelectionManager().isClickOnUnit(
				e, 
				player.getCurrentPosition(), 
				graphicsMain.getCameraX(), 
				graphicsMain.getCameraY()
			));
		}
	}

	private boolean isRightClick(GameMouseEvent e) {
		return e.button == 3; // Right mouse button
	}

	private void handleUnitMovement(GameMouseEvent e, GameUnit player) {
		// Get all selected units
		ArrayList<GameUnit> selectedUnits = getSelectedUnits();
		
		if (selectedUnits.size() == 1) {
			// Single unit - direct movement
			handleSingleUnitMovement(e, player);
		} else {
			// Multiple units - formation movement
			handleFormationMovement(e, selectedUnits);
		}
	}
	
	/**
	 * Handles movement for a single unit
	 */
	private void handleSingleUnitMovement(GameMouseEvent e, GameUnit player) {
		// Convert screen coordinates to world coordinates with camera offset
		player.setDestination(TileCoordinateConverter.screenToMapWithCamera(
			new Point(e.x, e.y), 
			graphicsMain.getCameraX(), 
			graphicsMain.getCameraY()
		));
		// Convert back to screen coordinates for the destination
		player.setDestination(TileCoordinateConverter.mapToScreen(player.getDestination()));

		// Player should start moving
		player.startMoving();
	}
	
	/**
	 * Handles movement for multiple units using formation
	 */
	private void handleFormationMovement(GameMouseEvent e, ArrayList<GameUnit> selectedUnits) {
		// Get the target destination (center of formation)
		Point targetDestination = TileCoordinateConverter.screenToMapWithCamera(
			new Point(e.x, e.y), 
			graphicsMain.getCameraX(), 
			graphicsMain.getCameraY()
		);
		
		// Calculate formation positions
		ArrayList<Point> formationPositions = calculateFormationPositions(targetDestination, selectedUnits.size());
		
		// Assign destinations to each unit
		for (int i = 0; i < selectedUnits.size() && i < formationPositions.size(); i++) {
			GameUnit unit = selectedUnits.get(i);
			Point formationPos = formationPositions.get(i);
			
			// Convert formation position to screen coordinates
			unit.setDestination(TileCoordinateConverter.mapToScreen(formationPos));
			unit.startMoving();
		}
	}
	
	/**
	 * Gets all currently selected units
	 */
	private ArrayList<GameUnit> getSelectedUnits() {
		ArrayList<GameUnit> selectedUnits = new ArrayList<>();
		for (GameUnit unit : unitManager.getPlayerList()) {
			if (unit.isPlayerSelected()) {
				selectedUnits.add(unit);
			}
		}
		return selectedUnits;
	}
	
	/**
	 * Calculates formation positions around a target destination
	 * Creates a formation that spreads units out in a logical pattern
	 */
	private ArrayList<Point> calculateFormationPositions(Point targetDestination, int unitCount) {
		ArrayList<Point> positions = new ArrayList<>();
		
		if (unitCount <= 1) {
			positions.add(targetDestination);
			return positions;
		}
		
		// Calculate formation size based on unit count
		int formationRadius = calculateFormationRadius(unitCount);
		
		// Create formation positions
		for (int i = 0; i < unitCount; i++) {
			Point formationPos = calculateFormationPosition(targetDestination, i, unitCount, formationRadius);
			positions.add(formationPos);
		}
		
		return positions;
	}
	
	/**
	 * Calculates the radius of the formation based on unit count
	 */
	private int calculateFormationRadius(int unitCount) {
		// Larger formations need more space
		if (unitCount <= 4) return 1;
		if (unitCount <= 9) return 2;
		if (unitCount <= 16) return 3;
		return 4; // For very large groups
	}
	
	/**
	 * Calculates a specific position in the formation
	 */
	private Point calculateFormationPosition(Point center, int unitIndex, int totalUnits, int radius) {
		// Create a spiral pattern starting from the center
		if (unitIndex == 0) {
			return center; // First unit goes to center
		}
		
		// Calculate position in a grid pattern around the center
		int gridSize = radius * 2 + 1;
		int unitsPerRing = 8; // 8 directions around the center
		
		int ring = (unitIndex - 1) / unitsPerRing + 1;
		int positionInRing = (unitIndex - 1) % unitsPerRing;
		
		if (ring > radius) {
			// Fallback: spread units in a larger area
			ring = radius;
		}
		
		// Calculate offset from center
		int offsetX = 0, offsetY = 0;
		
		switch (positionInRing) {
			case 0: offsetX = ring; offsetY = 0; break;      // East
			case 1: offsetX = ring; offsetY = -ring; break;   // Northeast
			case 2: offsetX = 0; offsetY = -ring; break;      // North
			case 3: offsetX = -ring; offsetY = -ring; break;  // Northwest
			case 4: offsetX = -ring; offsetY = 0; break;      // West
			case 5: offsetX = -ring; offsetY = ring; break;   // Southwest
			case 6: offsetX = 0; offsetY = ring; break;       // South
			case 7: offsetX = ring; offsetY = ring; break;    // Southeast
		}
		
		return new Point(center.x + offsetX, center.y + offsetY);
	}
}
