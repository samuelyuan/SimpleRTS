import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import graphics.Color;
import graphics.IGraphics;
import graphics.Point;
import graphics.Rect;
import map.TileConverter;
import graphics.GameImage;

public class GraphicsMain {
	private static boolean isNight;
	private GameFogWar fogWar;
	private GameStateManager stateManager;
	private RendererUnit rendererUnit;
	private RendererHUD rendererHud;

	public GraphicsMain(GameStateManager stateManager, GameFogWar fogWar) {
		this.stateManager = stateManager;
		this.fogWar = fogWar;
		this.rendererUnit = new RendererUnit(this);
		this.rendererHud = new RendererHUD();
	}

	/**
	 * Gets the camera X position from GameStateManager.
	 * 
	 * @return The camera X position
	 */
	public int getCameraX() {
		return stateManager.getCameraX();
	}

	/**
	 * Gets the camera Y position from GameStateManager.
	 * 
	 * @return The camera Y position
	 */
	public int getCameraY() {
		return stateManager.getCameraY();
	}

	/**
	 * Gets the current day/night state.
	 * 
	 * @return true if it's night, false if it's day
	 */
	public boolean isNight() {
		return isNight;
	}

	// helper functions

	public void drawImageOnScreen(IGraphics g, java.awt.Image img, int x, int y, int width, int height) {
		g.drawImage(new GameImage(img), x - getCameraX(), y - getCameraY(), width, height);
	}

	public void drawRectOnScreen(IGraphics g, int x, int y, int width, int height, boolean fill) {
		if (fill) {
			g.fillRect(x - getCameraX(), y - getCameraY(), width, height);
		} else {
			g.drawRect(x - getCameraX(), y - getCameraY(), width, height);
		}
	}

	public void drawInstruction(IGraphics g, DrawingInstruction instr) {
		Rect r = instr.rect;
		g.setColor(instr.color);
		if (instr.fill) {
			g.fillRect(r.x - getCameraX(), r.y - getCameraY(), r.width, r.height);
		} else {
			g.drawRect(r.x - getCameraX(), r.y - getCameraY(), r.width, r.height);
		}
	}

	public void drawGraphics(IGraphics g, GameTime gameTimer, GameUnitManager unitManager) {
		// draw according to a day/night cycle
		isNight = gameTimer.isNight();

		fogWar.calculateFogOfWar(unitManager.getPlayerList(), stateManager.getGameMap().getMapData());

		// Draw the map first
		drawMapTiles(g, stateManager.getGameMap().getDrawData());

		// Draw the units using the unit renderer
		renderAllUnits(g, unitManager);

		// Draw the flags
		renderAllFlags(g, unitManager);

		// draw fog (although this should be done before drawing units, not after
		renderFog(g, stateManager.getGameMap().getDrawData());

		// Draw everything else
		drawMouseSelectionBox(g);
		drawMinimap(g);

		// Render the HUD using the HUD renderer
		rendererHud.renderHUD(g, unitManager, gameTimer);
	}

	private void renderAllUnits(IGraphics g, GameUnitManager unitManager) {
		for (GameUnit playerUnit : unitManager.getPlayerList()) {
			rendererUnit.renderUnit(g, playerUnit);
		}
		for (GameUnit enemyUnit : unitManager.getEnemyList()) {
			rendererUnit.renderUnit(g, enemyUnit);
		}
	}

	private void renderAllFlags(IGraphics g, GameUnitManager unitManager) {
		java.util.Iterator<GameFlag> flagIter = unitManager.getFlagManager().getFlagList();
		while (flagIter.hasNext()) {
			GameFlag flag = flagIter.next();
			drawFlag(g, flag);
		}
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

	public DrawingInstruction getMouseSelectionInstruction() {
		if (!Mouse.isPressed)
			return null;

		Mouse.sortSelectionCoordinates(); // ensures coordinates are ordered

		Rect rect = new Rect(
				Mouse.boxX1 + getCameraX(),
				Mouse.boxY1 + getCameraY(),
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

		int mapCols = stateManager.getGameMap().getMapData()[0].length;
		int mapRows = stateManager.getGameMap().getMapData().length;

		int tileWidth = minimapWidth / mapCols;
		int tileHeight = minimapHeight / mapRows;

		int minimapX = 10;
		int minimapY = Constants.SCREEN_HEIGHT - minimapHeight - 10;

		for (int y = 0; y < mapRows; y++) {
			for (int x = 0; x < mapCols; x++) {
				int tile = stateManager.getGameMap().getMapData()[y][x];
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
		Rect boundingBox = flag.getBoundingBoxForState(getCameraX(), getCameraY());
		g.setColor(flagColor);
		g.fillRect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
	}
}
