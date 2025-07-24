import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import graphics.AwtGraphicsAdapter;
import graphics.Color;
import graphics.Point;
import graphics.Rect;
import map.TileConverter;
import ui.UIComponent;
import ui.UILabel;


public class GraphicsMain {
	private static boolean isNight;
	private GameFogWar fogWar;

	// HUD UI components
	private UIComponent hudRoot;
	private UILabel playerCountLabel;
	private UILabel enemyCountLabel;
	private UILabel timerDayLabel;
	private UILabel timerHourLabel;

	// Add a constructor to inject fogWar
	public GraphicsMain(GameFogWar fogWar) {
		this.fogWar = fogWar;
		setupHUD();
	}

	private void setupHUD() {
		hudRoot = new UIComponent(0, 0, SimpleRTS.screenWidth, SimpleRTS.screenHeight) {
			@Override
			protected void draw(graphics.IGraphics g) {}
		};
		// Player panel
		playerCountLabel = new UILabel(SimpleRTS.screenWidth / 2 - 100, SimpleRTS.screenHeight - 100 + 65, "");
		playerCountLabel.setFont(new Font("Comic Sans", Font.PLAIN, 32));
		playerCountLabel.setColor(Color.WHITE);
		hudRoot.addChild(playerCountLabel);
		// Enemy panel
		enemyCountLabel = new UILabel(SimpleRTS.screenWidth / 2 + 150, SimpleRTS.screenHeight - 100 + 65, "");
		enemyCountLabel.setFont(new Font("Comic Sans", Font.PLAIN, 32));
		enemyCountLabel.setColor(Color.WHITE);
		hudRoot.addChild(enemyCountLabel);
		// Timer panel (day and hour as two labels)
		timerDayLabel = new UILabel(SimpleRTS.screenWidth / 2 - 25, SimpleRTS.screenHeight - 100 + 25, "");
		timerDayLabel.setFont(new Font("Comic Sans", Font.PLAIN, 20));
		timerDayLabel.setColor(Color.WHITE);
		hudRoot.addChild(timerDayLabel);
		timerHourLabel = new UILabel(SimpleRTS.screenWidth / 2 - 25, SimpleRTS.screenHeight - 100 + 50, "");
		timerHourLabel.setFont(new Font("Comic Sans", Font.PLAIN, 20));
		timerHourLabel.setColor(Color.WHITE);
		hudRoot.addChild(timerHourLabel);
	}

	// helper functions
	
	public void drawImageOnScreen(Graphics g, Image img, int x, int y, int width, int height) {
		g.drawImage(img, x - SimpleRTS.cameraX, y - SimpleRTS.cameraY, width, height, null);
	}

	
	public void drawRectOnScreen(Graphics g, int x, int y, int width, int height, boolean fill) {
		if (fill)
			g.fillRect(x - SimpleRTS.cameraX, y - SimpleRTS.cameraY, width, height);
		else
			g.drawRect(x - SimpleRTS.cameraX, y - SimpleRTS.cameraY, width, height);
	}

	
	public void drawInstruction(Graphics g, DrawingInstruction instr) {
		Rect r = instr.rect;
		g.setColor(instr.color.toAwtColor());
		if (instr.fill) {
			g.fillRect(r.x - SimpleRTS.cameraX, r.y - SimpleRTS.cameraY, r.width, r.height);
		} else {
			g.drawRect(r.x - SimpleRTS.cameraX, r.y - SimpleRTS.cameraY, r.width, r.height);
		}
	}

	public void drawGraphics(Graphics g, GameTime gameTimer, GameUnitManager unitManager) {
		// draw according to a day/night cycle
		isNight = gameTimer.isNight();

		fogWar.calculateFogOfWar(unitManager.getPlayerList(), GameMap.mapdata);

		// Draw the map first
		drawMapTiles(g, GameMap.getDrawData());

		// Draw the units
		for (int i = 0; i < unitManager.getPlayerList().size(); i++) {
			GameUnit playerUnit = unitManager.getPlayerList().get(i);
			drawUnit(g, playerUnit);
		}
		for (int i = 0; i < unitManager.getEnemyList().size(); i++) {
			GameUnit enemyUnit = unitManager.getEnemyList().get(i);
			drawUnit(g, enemyUnit);
		}

		// draw fog (although this should be done before drawing units, not after
		renderFog(g, GameMap.getDrawData());

		// Draw everything else
		drawMouseSelectionBox(g);
		drawMinimap(g);
		// Update HUD labels
		playerCountLabel.setText("" + unitManager.getPlayerList().size());
		enemyCountLabel.setText("");
		timerDayLabel.setText("Day: " + gameTimer.getDay());
		timerHourLabel.setText(gameTimer.getHour() + ":00");
		// Draw player/enemy panel backgrounds and timer image
		g.drawImage(GameImage.getImage(ImageConstants.IMGID_SUPPLY_PLAYER),
			SimpleRTS.screenWidth / 2 - 250, SimpleRTS.screenHeight - 100, 200, 100, null);
		g.drawImage(GameImage.getImage(ImageConstants.IMGID_SUPPLY_ENEMY),
			SimpleRTS.screenWidth / 2 + 100, SimpleRTS.screenHeight - 100, 200, 100, null);
		g.drawImage(GameImage.getImage(ImageConstants.IMGID_GAME_TIMER),
			SimpleRTS.screenWidth / 2 - 50, SimpleRTS.screenHeight - 100, 150, 100, null);
		// Render the HUD
		hudRoot.render(new AwtGraphicsAdapter(g));
	}

	public List<DrawingInstruction> getFogInstructions(String[][] mapData) {
		List<DrawingInstruction> fogRects = new ArrayList<>();

		for (int y = 0; y < mapData.length; y++) {
			for (int x = 0; x < mapData[y].length; x++) {
				if (!fogWar.isTileVisible(x, y)) {
					Rect rect = new Rect(
						x * GameMap.TILE_WIDTH,
						y * GameMap.TILE_HEIGHT,
						GameMap.TILE_WIDTH,
						GameMap.TILE_HEIGHT
					);
					fogRects.add(new DrawingInstruction(rect, new Color(226, 226, 226), true));
				}
			}
		}

		return fogRects;
	}

	public void renderFog(Graphics g, String[][] mapData) {
		for (DrawingInstruction instr : getFogInstructions(mapData)) {
			drawInstruction(g, instr);
		}
	}

	public void drawUnit(Graphics g, GameUnit unit) {
		// draw square and black outline
		// highlight selected units
		if (unit.isPlayerSelected) {
			g.setColor(Color.BLACK.toAwtColor());
			this.drawRectOnScreen(g, unit.getCurrentPoint().x, unit.getCurrentPoint().y, GameMap.TILE_WIDTH,
					GameMap.TILE_HEIGHT, false);

			// draw destination point on screen if there is a path created
			if (unit.isPathCreated() && unit.isAlive()) {
				g.setColor(Color.GREEN.toAwtColor());
				Point mapDest = unit.getMapPoint(unit.destination);
				this.drawRectOnScreen(g, mapDest.x * GameMap.TILE_WIDTH, mapDest.y * GameMap.TILE_HEIGHT,
						GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT, false);
			}
		}

		// change direction for all units, not just player units!
		if (unit.isPathCreated() && unit.isAlive()) {
			Point mapDest = unit.getMapPoint(unit.destination);
			unit.direction = calculateDirection(unit.getCurrentPoint(), new Point(mapDest.x * GameMap.TILE_WIDTH, mapDest.y * GameMap.TILE_HEIGHT));
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
	public void drawMapTiles(Graphics g, String[][] mapData) {
		for (int y = 0; y < mapData.length; y++) {
			for (int x = 0; x < mapData[y].length; x++) {
				Image tempImg;
				String tileStr = mapData[y][x];
				tempImg = GameImage.getImage(getTileImageKey(tileStr), isNight);

				drawImageOnScreen(g, tempImg, x * GameMap.TILE_WIDTH, y * GameMap.TILE_HEIGHT,
						GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT);
			}
		}
	}

	
	public void drawUnitSprite(Graphics g, Point current, int classType, boolean isPlayerUnit, int direction) {
		// display unit based off of class type
		// also, add a distinct team color to units
		g.setColor(Color.BLACK.toAwtColor());
		BufferedImage newImg = null;
		if (classType == GameUnit.UNIT_ID_LIGHT) {
			if (isPlayerUnit) {
				newImg = (BufferedImage) GameImage.getImage(TileConverter.STR_UNIT_LIGHT_PLAYER, isNight);
			} else {
				newImg = (BufferedImage) GameImage.getImage(TileConverter.STR_UNIT_LIGHT_ENEMY, isNight);
			}
			// calculate direction
			newImg = newImg.getSubimage(GameMap.TILE_WIDTH * direction, 0, GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT);
		} else if (classType == GameUnit.UNIT_ID_MEDIUM) {
			newImg = addTeamColorToUnit(GameImage.getImage(TileConverter.STR_UNIT_MEDIUM), isPlayerUnit);
		} else if (classType == GameUnit.UNIT_ID_HEAVY) {
			if (isPlayerUnit) {
				newImg = (BufferedImage) GameImage.getImage(TileConverter.STR_UNIT_HEAVY_PLAYER, isNight);
			} else {
				newImg = (BufferedImage) GameImage.getImage(TileConverter.STR_UNIT_HEAVY_ENEMY, isNight);
			}
			// calculate direction
			newImg = newImg.getSubimage(GameMap.TILE_WIDTH * direction, 0, GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT);
		}
		this.drawImageOnScreen(g, newImg, current.x, current.y, GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT);
	}

	public static BufferedImage addTeamColorToUnit(Image img, boolean isPlayerUnit) {
		BufferedImage bImage = new BufferedImage(GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);

		Graphics g = bImage.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();

		for (int y = 0; y < bImage.getHeight(); y++) {
			for (int x = 0; x < bImage.getWidth(); x++) {
				if (bImage.getRGB(x, y) != Color.WHITE.toAwtColor().getRGB()) {
					if (isPlayerUnit) {
						bImage.setRGB(x, y, Color.BLUE.toAwtColor().getRGB());
					} else {
						bImage.setRGB(x, y, Color.RED.toAwtColor().getRGB());
					}
				}
			}
		}

		return bImage;
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
				current.y + GameMap.TILE_HEIGHT / 8,
				(int) ((double) (GameMap.TILE_WIDTH - 2) / 100.0 * health),
				GameMap.TILE_HEIGHT / 8);

		return new DrawingInstruction(rect, healthColor, true);
	}

	
	public void drawHealthBar(Graphics g, int health, Point current) {
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
				Mouse.boxX1 + SimpleRTS.cameraX,
				Mouse.boxY1 + SimpleRTS.cameraY,
				Mouse.boxX2 - Mouse.boxX1,
				Mouse.boxY2 - Mouse.boxY1);

		return new DrawingInstruction(rect, Color.BLACK, false); // Not filled, it's an outline
	}

	public void drawMouseSelectionBox(Graphics g) {
		DrawingInstruction instr = this.getMouseSelectionInstruction();
		if (instr != null) {
			drawInstruction(g, instr);
		}
	}

	private void drawMinimap(Graphics g) {
		int minimapWidth = 200;
		int minimapHeight = 200;

		int mapCols = GameMap.mapdata[0].length;
		int mapRows = GameMap.mapdata.length;

		int tileWidth = minimapWidth / mapCols;
		int tileHeight = minimapHeight / mapRows;

		int minimapX = 10;
		int minimapY = SimpleRTS.screenHeight - minimapHeight - 10;

		for (int y = 0; y < mapRows; y++) {
			for (int x = 0; x < mapCols; x++) {
				int tile = GameMap.mapdata[y][x];
				String tileStr = TileConverter.tileIntToStr(tile);
				Image tileImg = GameImage.getImage(tileStr);

				int drawX = minimapX + x * tileWidth;
				int drawY = minimapY + y * tileHeight;

				g.drawImage(tileImg, drawX, drawY, tileWidth, tileHeight, null);

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

				g.setColor(flagColor.toAwtColor());
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
			return (deltaX > 0) ? GameUnit.DIR_EAST : GameUnit.DIR_WEST;
		} else {
			return (deltaY > 0) ? GameUnit.DIR_SOUTH : GameUnit.DIR_NORTH;
		}
	}

	public static String getTileImageKey(String tileStr) {
		if (tileStr.contains("Land") || tileStr.contains("Wall") || tileStr.contains("Flag")) {
			return tileStr.substring(0, 4);
		} else {
			return "Land";
		}
	}
}
