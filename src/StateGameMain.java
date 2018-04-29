import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class StateGameMain extends StateMachine
{
	private GameTime gameTimer = new GameTime(1, 0);
	//private GameFlagManager flagManager = new GameFlagManager();
	private GraphicsMain graphicsMain = new GraphicsMain();
	
	private boolean isSpawned = false; //only spawn once per day
	
	public void run() 
	{
		int[][] map = GameMap.mapdata;
		
		graphicsMain.drawGraphics(gameTimer);
		
		//Add capture the flag system
		Iterator<GameFlag> itrFlag = GameUnitManager.getFlagManager().getFlagList();
		while (itrFlag.hasNext())
		{
			GameFlag flag = itrFlag.next();
			
			//The spawn should be based off of a timer, meaning that every few days or weeks of a battle,
			//reinforcements appear near the flag
			//Spawn units at 12:00 hours
			if (gameTimer.getHour() == 12 && isSpawned == false)
			{
				spawnUnitsNearFlag(map, flag);
			}
			
			flag.runLogic();
		}
		
		//set to true, so that it doesn't repeatedly spawn
		if (gameTimer.getHour() == 12 && isSpawned == false)
			isSpawned = true;
		
		//set to false after the hour has passed
		if (gameTimer.getHour() > 12)
			isSpawned = false;
		
		//handle player
		runFaction(map, GameFlag.FACTION_PLAYER);

		//handle enemy
		runFaction(map, GameFlag.FACTION_ENEMY);			
	
		updateDayTimer();		
	}
	
	//Retrieve the unit list depending on which faction it is
	public ArrayList<GameUnit> getUnitList(int factionId)
	{
		ArrayList<GameUnit> unitList = new ArrayList<GameUnit>();
		if (factionId == GameFlag.FACTION_PLAYER)
		{
			unitList = SimpleRTS.playerList;
		}
		else if (factionId == GameFlag.FACTION_ENEMY)
		{
			unitList = SimpleRTS.enemyList;
		}
		
		return unitList;
	}
	
	public void runFaction(int[][] map, int factionId)
	{
		ArrayList<GameUnit> unitList = getUnitList(factionId);
			
		//loop through all the units
		for (int i = 0; i < unitList.size(); i++)
		{
			GameUnit unit = unitList.get(i);

			if (factionId == GameFlag.FACTION_PLAYER)
				runPlayerLogic(map, unit);
			else if (factionId == GameFlag.FACTION_ENEMY)
				runEnemyLogic(map, unit);
			
			//Determine whether the unit is near the flag
			GameUnitManager.checkFlagStates(unit, factionId);
			
			//remove dead units
			if (unit.isAlive() == false)
			{
				GameUnitManager.removeDeadUnits(map, unitList, i);
			}
		}
		
		//terminating condition
		if (GameUnitManager.isFlagsListEmpty(factionId))
		{
			if (factionId == GameFlag.FACTION_PLAYER)
				SimpleRTS.setNewState(SimpleRTS.GameState.STATE_GAMEOVER); //player loses all flags, game over
			else if (factionId == GameFlag.FACTION_ENEMY)
				SimpleRTS.setNewState(SimpleRTS.GameState.STATE_NEXTLVL); //enemy loses all flags, win
		}
	}

	public void runPlayerLogic(int[][] map, GameUnit playerUnit)
	{
		//select and move units
		playerUnit.isPlayerSelected = Mouse.isPlayerSelect(playerUnit.getCurrentPoint(), playerUnit.isClickedOn);
		
		playerUnit.findPath(map);
		
		//check collisions with other players once
		//for (int j = 0; j < i; j++)
		//	UnitManager.pHandleOverlap(SimpleRTS.playerList.get(i), SimpleRTS.playerList.get(j));
		
		//handle battles
		playerUnit.interactWithEnemy(map, SimpleRTS.enemyList);
	}
	
	public void runEnemyLogic(int[][] map, GameUnit enemyUnit)
	{
		//send the enemy units to attack the flag every day at around 06:00 hours
		if (gameTimer.getHour() == 6)
		{
			GameFlag playerFlag = GameUnitManager.getFlagManager().getPlayerFlag();
			enemyUnit.destination = new Point((playerFlag.getMapX()-1) * GameMap.TILE_WIDTH, 
					playerFlag.getMapY() * GameMap.TILE_HEIGHT);
			enemyUnit.startMoving();
		}
		
		//Follow the path towards the flag
		enemyUnit.findPath(map);
	}
	
	public void updateDayTimer()
	{
		gameTimer.update();

//		if (minute >= 60)
//			hour++;
		
		//Recalculate the flag counts at 0:00, 6:00, 12:00 and 18:00
		if (gameTimer.getHour() % 6 == 0)
		{
			GameUnitManager.getFlagManager().reset();
		}
	}
	
	public void spawnUnitsNearFlag(int[][] map, GameFlag flag)
	{		
		//Spawn four units around the flag (N, S, E, W)
		//Add each unit to the master player list
		for (int i = 0; i < 4; i++)
		{
			int flagFactionId = flag.getControlFaction();
			
			//WEST
			if (isTileAvailable(map, flag.getMapX() - 1, flag.getMapY(), flagFactionId))
			{
				addUnitToMap(map, flag.getMapX() - 1, flag.getMapY(), flagFactionId);
			}
			//EAST
			else if (isTileAvailable(map, flag.getMapX() + 1, flag.getMapY(), flagFactionId))
			{
				addUnitToMap(map, flag.getMapX() + 1, flag.getMapY(), flagFactionId);
			}
			//NORTH
			else if (isTileAvailable(map, flag.getMapX(), flag.getMapY() - 1, flagFactionId))
			{
				addUnitToMap(map, flag.getMapX(), flag.getMapY() - 1, flagFactionId);
			}
			//SOUTH
			else if (isTileAvailable(map, flag.getMapX(), flag.getMapY() + 1, flagFactionId))
			{
				addUnitToMap(map, flag.getMapX(), flag.getMapY() + 1, flagFactionId);
			}
			else
				break;
		}		
	}
	
	public boolean isTileAvailable(int[][] map, int x, int y, int factionId)
	{
		ArrayList<GameUnit> unitList = getUnitList(factionId);
			
		//if there's a wall, then it's occupied
		if (map[y][x] == GameMap.TILE_WALL)
			return false;
		
		//If a unit is standing on the desired tile, the tile is considered to be occupied
		for (int i = 0; i < unitList.size(); i++)
		{
			GameUnit unit = unitList.get(i);
			if (unit.isOnTile(map, x, y))
				return false;
		}
		
		return true;
	}
	
	public void addUnitToMap(int[][] map, int x, int y, int factionId)
	{
		ArrayList<GameUnit> unitList = getUnitList(factionId);
		
		//Create new unit
		GameUnit newUnit = new GameUnit(x * GameMap.TILE_WIDTH, y * GameMap.TILE_HEIGHT, 
				(factionId == GameFlag.FACTION_PLAYER), GameUnit.UNIT_ID_LIGHT);
		//newUnit.setSpeed(2);
		newUnit.spawn(map, new Point(x, y), factionId);
		
		//Add unit to list
		unitList.add(newUnit);
		
//		if (factionId== GameFlag.FACTION_PLAYER)
//			GameUnitManager.addPlayerUnit(newUnit, SimpleRTS.playerList);
//		else if (factionId == GameFlag.FACTION_ENEMY)
//			GameUnitManager.addEnemyUnit(newUnit, SimpleRTS.enemyList);
	}
	
	//Use the mouse to send player units to various locations.
	public void handleMouseCommand(MouseEvent e) 
	{
		for (int i = 0; i < SimpleRTS.playerList.size(); i++)
		{
			GameUnit player = SimpleRTS.playerList.get(i);
			
			//right mouse click dictates player position
			if (e.getButton() == MouseEvent.BUTTON3 && player.isPlayerSelected == true)
			{
				//offset for scrolling camera
				player.destination = new Point(e.getX() + SimpleRTS.cameraX, e.getY() + SimpleRTS.cameraY);
				
				//destX, destY must be multiples of tile_width and tile_height to simplify pathfinder calculations
				//destinationX -= destinationX % TILE_WIDTH;
				//destinationY -= destinationY % TILE_HEIGHT;
				
				//player should better start moving
				player.startMoving();
			}	
			
			player.isClickedOn = Mouse.isClickOnUnit(e, player.getCurrentPoint());
		}
	}
}
