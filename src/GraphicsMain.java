import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import graphics.Color;
import graphics.GameFont;
import graphics.IGraphics;
import graphics.ImageUtils;
import graphics.Point;
import graphics.Rect;
import map.TileConverter;
import ui.UIComponent;
import ui.UILabel;
import graphics.GameImage;

public class GraphicsMain {
	private static boolean isNight;
	private GameFogWar fogWar;
	private GameStateManager stateManager;

	// HUD UI components
	private UIComponent hudRoot;
	private UILabel playerCountLabel;
	private UILabel enemyCountLabel;
	private UILabel timerDayLabel;
	private UILabel timerHourLabel;

	// Add a constructor to inject fogWar
	public GraphicsMain(GameStateManager stateManager, GameFogWar fogWar) {
		this.stateManager = stateManager;
		this.fogWar = fogWar;
		setupHUD();
	}

	private void setupHUD() {
		hudRoot = new UIComponent(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT) {
			@Override
			protected void draw(graphics.IGraphics g) {
			}
		};
		// Player panel
		playerCountLabel = new UILabel(Constants.SCREEN_WIDTH / 2 - 100, Constants.SCREEN_HEIGHT - 100 + 65, "");
		playerCountLabel.setFont(new GameFont("Comic Sans", GameFont.PLAIN, 32));
		playerCountLabel.setColor(Color.WHITE);
		hudRoot.addChild(playerCountLabel);
		// Enemy panel
		enemyCountLabel = new UILabel(Constants.SCREEN_WIDTH / 2 + 150, Constants.SCREEN_HEIGHT - 100 + 65, "");
		enemyCountLabel.setFont(new GameFont("Comic Sans", GameFont.PLAIN, 32));
		enemyCountLabel.setColor(Color.WHITE);
		hudRoot.addChild(enemyCountLabel);
		// Timer panel (day and hour as two labels)
		timerDayLabel = new UILabel(Constants.SCREEN_WIDTH / 2 - 25, Constants.SCREEN_HEIGHT - 100 + 25, "");
		timerDayLabel.setFont(new GameFont("Comic Sans", GameFont.PLAIN, 20));
		timerDayLabel.setColor(Color.WHITE);
		hudRoot.addChild(timerDayLabel);
		timerHourLabel = new UILabel(Constants.SCREEN_WIDTH / 2 - 25, Constants.SCREEN_HEIGHT - 100 + 50, "");
		timerHourLabel.setFont(new GameFont("Comic Sans", GameFont.PLAIN, 20));
		timerHourLabel.setColor(Color.WHITE);
		hudRoot.addChild(timerHourLabel);
	}

	// helper functions

	public void drawImageOnScreen(IGraphics g, java.awt.Image img, int x, int y, int width, int height) {
		g.drawImage(new GameImage(img), x - stateManager.getCameraX(), y - stateManager.getCameraY(), width, height);
	}

	public void drawRectOnScreen(IGraphics g, int x, int y, int width, int height, boolean fill) {
		if (fill) {
			g.fillRect(x - stateManager.getCameraX(), y - stateManager.getCameraY(), width, height);
		} else {
			g.drawRect(x - stateManager.getCameraX(), y - stateManager.getCameraY(), width, height);
		}
	}

	public void drawInstruction(IGraphics g, DrawingInstruction instr) {
		Rect r = instr.rect;
		g.setColor(instr.color);
		if (instr.fill) {
			g.fillRect(r.x - stateManager.getCameraX(), r.y - stateManager.getCameraY(), r.width, r.height);
		} else {
			g.drawRect(r.x - stateManager.getCameraX(), r.y - stateManager.getCameraY(), r.width, r.height);
		}
	}

	public void drawGraphics(IGraphics g, GameTime gameTimer, GameUnitManager unitManager) {
		// draw according to a day/night cycle
		isNight = gameTimer.isNight();

		fogWar.calculateFogOfWar(unitManager.getPlayerList(), stateManager.getGameMap().mapdata);

		// Draw the map first
		drawMapTiles(g, stateManager.getGameMap().getDrawData());

		// Draw the units
		for (int i = 0; i < unitManager.getPlayerList().size(); i++) {
			GameUnit playerUnit = unitManager.getPlayerList().get(i);
			drawUnit(g, playerUnit);
		}
		for (int i = 0; i < unitManager.getEnemyList().size(); i++) {
			GameUnit enemyUnit = unitManager.getEnemyList().get(i);
			drawUnit(g, enemyUnit);
		}

		// Draw the flags
		java.util.Iterator<GameFlag> flagIter = unitManager.getFlagManager().getFlagList();
		while (flagIter.hasNext()) {
			GameFlag flag = flagIter.next();
			drawFlag(g, flag);
		}

		// draw fog (although this should be done before drawing units, not after
		renderFog(g, stateManager.getGameMap().getDrawData());

		// Draw everything else
		drawMouseSelectionBox(g);
		drawMinimap(g);
		// Update HUD labels
		playerCountLabel.setText("" + unitManager.getPlayerList().size());
		enemyCountLabel.setText("");
		timerDayLabel.setText("Day: " + gameTimer.getDay());
		timerHourLabel.setText(gameTimer.getHour() + ":00");
		// Draw player/enemy panel backgrounds and timer image
		g.drawImage(GameImageManager.getImage(ImageConstants.IMGID_SUPPLY_PLAYER),
				Constants.SCREEN_WIDTH / 2 - 250, Constants.SCREEN_HEIGHT - 100, 200, 100);
		g.drawImage(GameImageManager.getImage(ImageConstants.IMGID_SUPPLY_ENEMY),
				Constants.SCREEN_WIDTH / 2 + 100, Constants.SCREEN_HEIGHT - 100, 200, 100);
		g.drawImage(GameImageManager.getImage(ImageConstants.IMGID_GAME_TIMER),
				Constants.SCREEN_WIDTH / 2 - 50, Constants.SCREEN_HEIGHT - 100, 150, 100);
		// Render the HUD
		hudRoot.render(g);
	}

	public List<DrawingInstruction> getFogInstructions(String[][] mapData) {
		List<DrawingInstruction> fogRects = new ArrayList<>();

		for (int y = 0; y < mapData.length; y++) {
			for (int x = 0; x < mapData[y].length; x++) {
				if (!fogWar.isTileVisible(x, y)) {
					Rect rect = new Rect(
							x * Constants.TILE_WIDTH,
							y * Constants.TILE_HEIGHT,
							Constants.TILE_WIDTH,
							Constants.TILE_HEIGHT);
					fogRects.add(new DrawingInstruction(rect, new Color(226, 226, 226), true));
				}
			}
		}

		return fogRects;
	}

	public void renderFog(IGraphics g, String[][] mapData) {
		for (DrawingInstruction instr : getFogInstructions(mapData)) {
			drawInstruction(g, instr);
		}
	}

	public void drawUnit(IGraphics g, GameUnit unit) {
		// draw square and black outline
		// highlight selected units
		if (unit.isPlayerSelected) {
			g.setColor(Color.BLACK);
			this.drawRectOnScreen(g, unit.getCurrentPoint().x, unit.getCurrentPoint().y, Constants.TILE_WIDTH,
					Constants.TILE_HEIGHT, false);

			// draw destination point on screen if there is a path created
			if (unit.isPathCreated() && unit.isAlive()) {
				g.setColor(Color.GREEN);
				Point mapDest = unit.getMapPoint(unit.destination);
				this.drawRectOnScreen(g, mapDest.x * Constants.TILE_WIDTH, mapDest.y * Constants.TILE_HEIGHT,
						Constants.TILE_WIDTH, Constants.TILE_HEIGHT, false);
			}
		}

		// change direction for all units, not just player units!
		if (unit.isPathCreated() && unit.isAlive()) {
			Point mapDest = unit.getMapPoint(unit.destination);
			unit.direction = calculateDirection(unit.getCurrentPoint(),
					new Point(mapDest.x * Constants.TILE_WIDTH, mapDest.y * Constants.TILE_HEIGHT));
		}

		// draw the sprite
		this.drawUnitSprite(g, unit.getCurrentPoint(), unit.getClassType(), unit.getIsPlayerUnit(),
				unit.direction);

		// Health Bar
		this.drawHealthBar(g, unit.getHealth(), unit.getCurrentPoint());
	}

	/*
	 * Draw all the snow, walls, units, etc...
	 */
	public void drawMapTiles(IGraphics g, String[][] mapData) {
		for (int y = 0; y < mapData.length; y++) {
			for (int x = 0; x < mapData[y].length; x++) {
				java.awt.Image tempImg;
				String tileStr = mapData[y][x];
				tempImg = (java.awt.Image) GameImageManager.getImage(getTileImageKey(tileStr), isNight)
						.getBackendImage();

				drawImageOnScreen(g, tempImg, x * Constants.TILE_WIDTH, y * Constants.TILE_HEIGHT,
						Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
			}
		}
	}

	public void drawUnitSprite(IGraphics g, Point current, int classType, boolean isPlayerUnit, int direction) {
		// display unit based off of class type
		// also, add a distinct team color to units
		g.setColor(Color.BLACK);
		BufferedImage newImg = null;
		if (classType == Constants.UNIT_ID_LIGHT) {
			if (isPlayerUnit) {
				newImg = (BufferedImage) GameImageManager.getImage(TileConverter.STR_UNIT_LIGHT_PLAYER, isNight)
						.getBackendImage();
			} else {
				newImg = (BufferedImage) GameImageManager.getImage(TileConverter.STR_UNIT_LIGHT_ENEMY, isNight)
						.getBackendImage();
			}
			// calculate direction
			newImg = newImg.getSubimage(Constants.TILE_WIDTH * direction, 0, Constants.TILE_WIDTH,
					Constants.TILE_HEIGHT);
		} else if (classType == Constants.UNIT_ID_MEDIUM) {
			newImg = (BufferedImage) ImageUtils
					.addTeamColorToUnit(GameImageManager.getImage(TileConverter.STR_UNIT_MEDIUM), isPlayerUnit)
					.getBackendImage();
		} else if (classType == Constants.UNIT_ID_HEAVY) {
			if (isPlayerUnit) {
				newImg = (BufferedImage) GameImageManager.getImage(TileConverter.STR_UNIT_HEAVY_PLAYER, isNight)
						.getBackendImage();
			} else {
				newImg = (BufferedImage) GameImageManager.getImage(TileConverter.STR_UNIT_HEAVY_ENEMY, isNight)
						.getBackendImage();
			}
			// calculate direction
			newImg = newImg.getSubimage(Constants.TILE_WIDTH * direction, 0, Constants.TILE_WIDTH,
					Constants.TILE_HEIGHT);
		}
		this.drawImageOnScreen(g, newImg, current.x, current.y, Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
	}

	public DrawingInstruction getHealthBarInstruction(int health, Point current) {
		Color healthColor;
		if (health > 75) {
			healthColor = Color.GREEN;
		} else if (health > 50) {
			healthColor = Color.YELLOW;
		} else if (health > 25) {
			healthColor = Color.ORANGE;
		} else if (health > 0) {
			healthColor = Color.RED;
		} else {
			return null; // Do not draw the health bar
		}

		Rect rect = new Rect(
				current.x + 2,
				current.y + Constants.TILE_HEIGHT / 8,
				(int) ((double) (Constants.TILE_WIDTH - 2) / 100.0 * health),
				Constants.TILE_HEIGHT / 8);

		return new DrawingInstruction(rect, healthColor, true);
	}

	public void drawHealthBar(IGraphics g, int health, Point current) {
		DrawingInstruction instr = this.getHealthBarInstruction(health, current);
		if (instr != null) {
			drawInstruction(g, instr);
		}
	}

	public DrawingInstruction getMouseSelectionInstruction() {
		if (!Mouse.isPressed)
			return null;

		Mouse.sortSelectionCoordinates(); // ensures coordinates are ordered

		Rect rect = new Rect(
				Mouse.boxX1 + stateManager.getCameraX(),
				Mouse.boxY1 + stateManager.getCameraY(),
				Mouse.boxX2 - Mouse.boxX1,
				Mouse.boxY2 - Mouse.boxY1);

		return new DrawingInstruction(rect, Color.BLACK, false); // Not filled, it's an outline
	}

	public void drawMouseSelectionBox(IGraphics g) {
		DrawingInstruction instr = this.getMouseSelectionInstruction();
		if (instr != null) {
			drawInstruction(g, instr);
		}
	}

	private void drawMinimap(IGraphics g) {
		int minimapWidth = 200;
		int minimapHeight = 200;

		int mapCols = stateManager.getGameMap().mapdata[0].length;
		int mapRows = stateManager.getGameMap().mapdata.length;

		int tileWidth = minimapWidth / mapCols;
		int tileHeight = minimapHeight / mapRows;

		int minimapX = 10;
		int minimapY = Constants.SCREEN_HEIGHT - minimapHeight - 10;

		for (int y = 0; y < mapRows; y++) {
			for (int x = 0; x < mapCols; x++) {
				int tile = stateManager.getGameMap().mapdata[y][x];
				String tileStr = TileConverter.tileIntToStr(tile);
				Image tileImg = (java.awt.Image) GameImageManager.getImage(tileStr).getBackendImage();

				int drawX = minimapX + x * tileWidth;
				int drawY = minimapY + y * tileHeight;

				g.drawImage(new GameImage(tileImg), drawX, drawY, tileWidth, tileHeight);

				Color flagColor;
				switch (tile) {
					case 8:
						flagColor = Color.BLUE;
						break;
					case 9:
						flagColor = Color.RED;
						break;
					default:
						flagColor = Color.GRAY;
						break;
				}

				g.setColor(flagColor);
				g.fillRect(drawX, drawY, tileWidth, tileHeight);
			}
		}
	}

	public void resetFogOfWar(int mapHeight, int mapWidth) {
		if (fogWar == null) {
			fogWar = new GameFogWar(mapHeight, mapWidth);
		} else {
			fogWar.reset(mapHeight, mapWidth);
		}
	}

	public static int calculateDirection(Point current, Point destination) {
		int deltaX = destination.x - current.x;
		int deltaY = destination.y - current.y;
		if (Math.abs(deltaX) >= Math.abs(deltaY)) {
			return (deltaX > 0) ? Constants.DIR_EAST : Constants.DIR_WEST;
		} else {
			return (deltaY > 0) ? Constants.DIR_SOUTH : Constants.DIR_NORTH;
		}
	}

	public static String getTileImageKey(String tileStr) {
		if (tileStr.contains("Land") || tileStr.contains("Wall") || tileStr.contains("Flag")) {
			return tileStr.substring(0, 4);
		} else {
			return "Land";
		}
	}

	public void drawFlag(IGraphics g, GameFlag flag) {
		// Set color based on faction
		Color flagColor = flag.getColorForFaction();
		Rect boundingBox = flag.getBoundingBoxForState(stateManager.getCameraX(), stateManager.getCameraY());
		g.setColor(flagColor);
		g.fillRect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
	}
}
