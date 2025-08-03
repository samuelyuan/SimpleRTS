import java.util.ArrayList;
import java.util.Iterator;

import graphics.Point;
import input.GameMouseEvent;
import utils.TileCoordinateConverter;

/**
 * Main game state that handles the game loop and input.
 */
public class StateGameMain extends StateMachine {
	private final GameStateManager stateManager;
	private final GameUnitManager unitManager;
	private final GameFogWar fogWar;
	private final GraphicsMain graphicsMain;
	private final GameTimer gameTimer;

	public StateGameMain(GameStateManager stateManager, GameUnitManager unitManager, GameFogWar fogWar,
			GraphicsMain graphicsMain) {
		this.stateManager = stateManager;
		this.unitManager = unitManager;
		this.fogWar = fogWar;
		this.graphicsMain = graphicsMain;
		this.gameTimer = new GameTimer(1, 0, stateManager.getFlagManager());
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
		Iterator<GameFlag> itrFlag = stateManager.getFlagManager().getFlagList();
		while (itrFlag.hasNext()) {
			GameFlag flag = itrFlag.next();

			// Spawn units at 12:00 hours
			if (gameTimer.getHour() == GameTimer.SPAWN_HOUR && !unitManager.isSpawned()) {
				unitManager.spawnUnitsNearFlag(map, flag);
			}

			flag.runLogic();
		}

		// Update spawn state
		unitManager.updateSpawnState(gameTimer.getHour());
		
		// Update combat effects
		stateManager.getCombatEffectManager().update();
	}



	private void runFaction(int[][] map, int factionId) {
		ArrayList<GameUnit> unitList = unitManager.getUnitList(factionId);

		// Loop through all units
		for (int i = 0; i < unitList.size(); i++) {
			GameUnit unit = unitList.get(i);

			if (factionId == GameFlag.FACTION_PLAYER) {
				runPlayerLogic(map, unit);
			} else if (factionId == GameFlag.FACTION_ENEMY) {
				runEnemyLogic(map, unit);
			}

			// Determine whether the unit is near the flag
			stateManager.checkFlagStates(unit, factionId);

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
		
		// Handle combat effects
		handleCombatEffects(playerUnit);
	}

	private void runEnemyLogic(int[][] map, GameUnit enemyUnit) {
		// Send enemy units to attack the flag every day at around 06:00 hours
		if (gameTimer.isEnemyAttackTime()) {
			GameFlag playerFlag = stateManager.getFlagManager().getPlayerFlag();
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
		
		// Handle combat effects
		handleCombatEffects(enemyUnit);
	}

	private void checkTerminatingConditions(int factionId) {
		if (stateManager.isFlagsListEmpty(factionId)) {
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
				unitManager.handleUnitMovement(e, graphicsMain.getCameraX(), graphicsMain.getCameraY());
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
	
	/**
	 * Handles combat effects for a unit including damage numbers, attack animations,
	 * death animations, and particles.
	 */
	private void handleCombatEffects(GameUnit unit) {
		CombatEffectManager effectManager = stateManager.getCombatEffectManager();
		
		// Create attack animation if unit is attacking
		if (unit.isAttacking()) {
			effectManager.createAttackAnimation(unit);
			
			// Create combat particles
			effectManager.createCombatParticles(unit.getCurrentPosition(), 5);
		}
		
		// Temporarily disable all damage numbers to reduce distraction
		// if (unit.getLastDamageDealt() > 0) {
		// 	if (unit.wasLastHitCritical()) {
		// 		effectManager.createDamageNumber(
		// 			unit.getCurrentPosition(), 
		// 			unit.getLastDamageDealt(), 
		// 			unit.wasLastHitCritical()
		// 		);
		// 	}
		// 	unit.clearCombatEffects();
		// }
		
		// Clear combat effects without showing damage numbers
		if (unit.getLastDamageDealt() > 0) {
			unit.clearCombatEffects();
		}
		
		// Create death animation if unit just died
		if (!unit.isAlive()) {
			effectManager.createDeathAnimation(unit);
		}
	}
}
