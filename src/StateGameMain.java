import java.util.ArrayList;
import java.util.Iterator;

import graphics.Point;
import input.GameMouseEvent;
import map.TileConverter;

public class StateGameMain extends StateMachine {
	private GameTime gameTimer = new GameTime(1, 0);
	// private GameFlagManager flagManager = new GameFlagManager();
	private GraphicsMain graphicsMain;
	private GameFogWar fogWar;

	private boolean isSpawned = false; // only spawn once per day
	private GameUnitManager unitManager;
	private GameStateManager stateManager;

	public StateGameMain(GameStateManager stateManager, GameUnitManager unitManager, GameFogWar fogWar,
			GraphicsMain graphicsMain) {
		this.stateManager = stateManager;
		this.unitManager = unitManager;
		this.fogWar = fogWar;
		this.graphicsMain = graphicsMain;
	}

	public void run(graphics.IGraphics g) {
		int[][] map = this.stateManager.getGameMap().getMapData();

		// If fogWar or graphicsMain not initialized (e.g., map loaded after
		// constructor)
		if (fogWar == null || graphicsMain == null) {
			fogWar = new GameFogWar(map.length, map[0].length);
			graphicsMain = new GraphicsMain(stateManager, fogWar);
		}

		graphicsMain.drawGraphics(g, gameTimer, unitManager);

		// Add capture the flag system
		Iterator<GameFlag> itrFlag = unitManager.getFlagManager().getFlagList();
		while (itrFlag.hasNext()) {
			GameFlag flag = itrFlag.next();

			// The spawn should be based off of a timer, meaning that every few days or
			// weeks of a battle,
			// reinforcements appear near the flag
			// Spawn units at 12:00 hours
			if (gameTimer.getHour() == 12 && isSpawned == false) {
				spawnUnitsNearFlag(map, flag);
			}

			flag.runLogic();
		}

		// set to true, so that it doesn't repeatedly spawn
		if (gameTimer.getHour() == 12 && isSpawned == false) {
			isSpawned = true;
		}

		// set to false after the hour has passed
		if (gameTimer.getHour() > 12) {
			isSpawned = false;
		}

		// handle player
		runFaction(map, GameFlag.FACTION_PLAYER);

		// handle enemy
		runFaction(map, GameFlag.FACTION_ENEMY);

		updateDayTimer();
	}

	// Retrieve the unit list depending on which faction it is
	public ArrayList<GameUnit> getUnitList(int factionId) {
		if (factionId == GameFlag.FACTION_PLAYER) {
			return unitManager.getPlayerList();
		} else if (factionId == GameFlag.FACTION_ENEMY) {
			return unitManager.getEnemyList();
		}
		return new ArrayList<>();
	}

	public void runFaction(int[][] map, int factionId) {
		ArrayList<GameUnit> unitList = getUnitList(factionId);

		// loop through all the units
		for (int i = 0; i < unitList.size(); i++) {
			GameUnit unit = unitList.get(i);

			if (factionId == GameFlag.FACTION_PLAYER) {
				runPlayerLogic(map, unit);
			} else if (factionId == GameFlag.FACTION_ENEMY) {
				runEnemyLogic(map, unit);
			}

			// Determine whether the unit is near the flag
			unitManager.checkFlagStates(unit, factionId);

			// remove dead units
			if (unit.isAlive() == false) {
				unitManager.removeDeadUnits(map, unitList, i);
			}
		}

		// terminating condition
		if (unitManager.isFlagsListEmpty(factionId)) {
			if (factionId == GameFlag.FACTION_PLAYER) {
				stateManager.setNewState(GameState.STATE_GAMEOVER); // player loses all flags, game over
			} else if (factionId == GameFlag.FACTION_ENEMY) {
				stateManager.setNewState(GameState.STATE_NEXTLVL); // enemy loses all flags, win
			}
		}
	}

	public void runPlayerLogic(int[][] map, GameUnit playerUnit) {
		// select and move units
		playerUnit.isPlayerSelected = Mouse.isPlayerSelect(playerUnit.getCurrentPoint(), playerUnit.isClickedOn,
				stateManager.getCameraX(), stateManager.getCameraY());

		playerUnit.findPath(map, unitManager.getPlayerList());

		// check collisions with other players once
		// for (int j = 0; j < i; j++)
		// UnitManager.pHandleOverlap(playerList.get(i), playerList.get(j));

		// handle battles
		playerUnit.interactWithEnemy(map, unitManager.getEnemyList());
	}

	public void runEnemyLogic(int[][] map, GameUnit enemyUnit) {
		// send the enemy units to attack the flag every day at around 06:00 hours
		if (gameTimer.getHour() == 6) {
			GameFlag playerFlag = unitManager.getFlagManager().getPlayerFlag();
			if (playerFlag == null) {
				System.out.println("No player flag found!");
				return;
			}
			enemyUnit.destination = new Point((playerFlag.getMapX() - 1) * Constants.TILE_WIDTH,
					playerFlag.getMapY() * Constants.TILE_HEIGHT);
			enemyUnit.startMoving();
		}

		// Follow the path towards the flag
		enemyUnit.findPath(map, unitManager.getEnemyList());
	}

	public void updateDayTimer() {
		gameTimer.update();

		// if (minute >= 60)
		// hour++;

		// Recalculate the flag counts at 0:00, 6:00, 12:00 and 18:00
		if (gameTimer.getHour() % 6 == 0) {
			unitManager.getFlagManager().reset();
		}
	}

	public void spawnUnitsNearFlag(int[][] map, GameFlag flag) {
		// Spawn four units around the flag (N, S, E, W)
		// Add each unit to the master player list
		for (int i = 0; i < 4; i++) {
			int flagFactionId = flag.getControlFaction();

			// WEST
			if (isTileAvailable(map, flag.getMapX() - 1, flag.getMapY(), flagFactionId)) {
				addUnitToMap(map, flag.getMapX() - 1, flag.getMapY(), flagFactionId);
			}
			// EAST
			else if (isTileAvailable(map, flag.getMapX() + 1, flag.getMapY(), flagFactionId)) {
				addUnitToMap(map, flag.getMapX() + 1, flag.getMapY(), flagFactionId);
			}
			// NORTH
			else if (isTileAvailable(map, flag.getMapX(), flag.getMapY() - 1, flagFactionId)) {
				addUnitToMap(map, flag.getMapX(), flag.getMapY() - 1, flagFactionId);
			}
			// SOUTH
			else if (isTileAvailable(map, flag.getMapX(), flag.getMapY() + 1, flagFactionId)) {
				addUnitToMap(map, flag.getMapX(), flag.getMapY() + 1, flagFactionId);
			} else {
				break;
			}
		}
	}

	public boolean isTileAvailable(int[][] map, int x, int y, int factionId) {
		ArrayList<GameUnit> unitList = getUnitList(factionId);

		// if there's a wall, then it's occupied
		if (map[y][x] == TileConverter.TILE_WALL) {
			return false;
		}

		// If a unit is standing on the desired tile, the tile is considered to be
		// occupied
		for (GameUnit unit : unitList) {
			if (unit.isOnTile(map, x, y)) {
				return false;
			}
		}

		return true;
	}

	public void addUnitToMap(int[][] map, int x, int y, int factionId) {
		ArrayList<GameUnit> unitList = getUnitList(factionId);

		// Create new unit
		GameUnit newUnit = new GameUnit(x * Constants.TILE_WIDTH, y * Constants.TILE_HEIGHT,
				(factionId == GameFlag.FACTION_PLAYER), Constants.UNIT_ID_LIGHT);
		// newUnit.setSpeed(2);
		newUnit.spawn(map, new Point(x, y), factionId);

		// Add unit to list
		unitList.add(newUnit);
	}

	// Use the mouse to send player units to various locations.
	public void handleMouseCommand(GameMouseEvent e) {
		for (int i = 0; i < unitManager.getPlayerList().size(); i++) {
			GameUnit player = unitManager.getPlayerList().get(i);

			// right mouse click dictates player position
			if (e.button == 3 && player.isPlayerSelected) {
				// offset for scrolling camera
				player.destination = new Point(e.x + stateManager.getCameraX(), e.y + stateManager.getCameraY());

				// destX, destY must be multiples of tile_width and tile_height to simplify
				// pathfinder calculations
				// destinationX -= destinationX % TILE_WIDTH;
				// destinationY -= destinationY % TILE_HEIGHT;

				// player should better start moving
				player.startMoving();
			}

			player.isClickedOn = Mouse.isClickOnUnit(e, player.getCurrentPoint(), stateManager.getCameraX(), stateManager.getCameraY());
		}
	}
}
