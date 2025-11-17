import java.util.ArrayList;
import java.util.List;

import entities.GameFlag;
import entities.GameUnit;
import entities.GameUnitManager;
import managers.GameTimer;
import graphics.Color;
import graphics.DrawingInstruction;
import graphics.IGraphics;
import graphics.Point;
import graphics.Rect;
import utils.Constants;
import utils.TileCoordinateConverter;
import graphics.GameImage;

public class GraphicsMain {
	private static boolean isNight;
	private GameFogWar fogWar;
	private GameStateManager stateManager;
	private CameraManager cameraManager;
	private RendererUnit rendererUnit;
	private RendererHUD rendererHud;

	public GraphicsMain(GameStateManager stateManager, GameFogWar fogWar, CameraManager cameraManager) {
		this.stateManager = stateManager;
		this.fogWar = fogWar;
		this.cameraManager = cameraManager;
		this.rendererUnit = new RendererUnit(this);
		this.rendererHud = new RendererHUD(stateManager.getImageService());
	}

	/**
	 * Gets the camera X position from CameraManager.
	 * 
	 * @return The camera X position
	 */
	public int getCameraX() {
		return cameraManager.getCameraX();
	}

	/**
	 * Gets the camera Y position from CameraManager.
	 * 
	 * @return The camera Y position
	 */
	public int getCameraY() {
		return cameraManager.getCameraY();
	}

	public GameStateManager getStateManager() {
		return stateManager;
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

	public void drawImageOnScreen(IGraphics g, GameImage img, int x, int y, int width, int height) {
		g.drawImage(img, x - getCameraX(), y - getCameraY(), width, height);
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

	public void drawGraphics(IGraphics g, GameTimer gameTimer, GameUnitManager unitManager) {
		// draw according to a day/night cycle
		isNight = gameTimer.isNight();

		fogWar.calculateFogOfWar(unitManager.getPlayerList(), stateManager.getGameMap().getMapData());

		// Draw the map first
		drawMapTiles(g, stateManager.getGameMap().getDrawData());

		// Draw the units using the unit renderer
		renderAllUnits(g, unitManager);

		// Draw the flags
		renderAllFlags(g, unitManager);

		// Draw combat effects (on top of units)
		renderCombatEffects(g);

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
		java.util.Iterator<GameFlag> flagIter = stateManager.getFlagManager().getFlagList();
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
				tempImg = (java.awt.Image) stateManager.getImageService().getTileImage(getTileImageKey(tileStr), isNight)
						.getBackendImage();

				Point screenPos = TileCoordinateConverter.mapToScreen(x, y);
				drawImageOnScreen(g, new GameImage(tempImg), screenPos.x, screenPos.y,
						Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
			}
		}
	}

	public DrawingInstruction getMouseSelectionInstruction() {
		SelectionManager selectionManager = stateManager.getSelectionManager();
		if (!selectionManager.isSelectionActive())
			return null;

		SelectionManager.SelectionBox selectionBox = selectionManager.getSelectionBox();

		Rect rect = new Rect(
				selectionBox.x1 + getCameraX(),
				selectionBox.y1 + getCameraY(),
				selectionBox.getWidth(),
				selectionBox.getHeight());

		// Return a selection box outline so units inside remain visible
		return new DrawingInstruction(rect, new Color(0, 150, 255), false); // Blue outline, not filled
	}

	public void drawMouseSelectionBox(IGraphics g) {
		DrawingInstruction instr = this.getMouseSelectionInstruction();
		if (instr != null) {
			// Draw the filled selection box
			drawInstruction(g, instr);
			
			// Draw the border outline
			SelectionManager selectionManager = stateManager.getSelectionManager();
			SelectionManager.SelectionBox selectionBox = selectionManager.getSelectionBox();
			
			Rect borderRect = new Rect(
					selectionBox.x1 + getCameraX(),
					selectionBox.y1 + getCameraY(),
					selectionBox.getWidth(),
					selectionBox.getHeight());
			
			DrawingInstruction borderInstr = new DrawingInstruction(borderRect, new Color(0, 200, 255), false);
			drawInstruction(g, borderInstr);
		}
	}

	void drawMinimap(IGraphics g) {
		MinimapRenderer minimapRenderer = new MinimapRenderer(stateManager, fogWar);
		minimapRenderer.render(g, getCameraX(), getCameraY());
	}

	/**
	 * Helper class to break up the large drawMinimap method into smaller, focused methods
	 */
	private class MinimapRenderer {
		private final GameStateManager stateManager;
		private final GameFogWar fogWar;
		private final int minimapWidth = 200;
		private final int minimapHeight = 200;
		private final int minimapX = 10;
		private final int minimapY = Constants.SCREEN_HEIGHT - minimapHeight - 10;
		private int tileWidth, tileHeight;

		public MinimapRenderer(GameStateManager stateManager, GameFogWar fogWar) {
			this.stateManager = stateManager;
			this.fogWar = fogWar;
		}

		public void render(IGraphics g, int cameraX, int cameraY) {
			calculateTileDimensions();
			drawBackground(g);
			drawVisitedAreas(g);
			drawTerrainFeatures(g);
			drawFlags(g);
			drawPlayerUnits(g);
			drawEnemyUnits(g);
			drawCameraViewport(g, cameraX, cameraY);
		}

		private void calculateTileDimensions() {
			int mapCols = stateManager.getGameMap().getMapData()[0].length;
			int mapRows = stateManager.getGameMap().getMapData().length;
			tileWidth = minimapWidth / mapCols;
			tileHeight = minimapHeight / mapRows;
		}

		private void drawBackground(IGraphics g) {
			// Draw background
			g.setColor(new Color(50, 50, 50));
			g.fillRect(minimapX, minimapY, minimapWidth, minimapHeight);
			
			// Draw border
			g.setColor(new Color(200, 200, 200));
			g.drawRect(minimapX, minimapY, minimapWidth, minimapHeight);
		}

		private void drawVisitedAreas(IGraphics g) {
			int mapCols = stateManager.getGameMap().getMapData()[0].length;
			int mapRows = stateManager.getGameMap().getMapData().length;

			for (int y = 0; y < mapRows; y++) {
				for (int x = 0; x < mapCols; x++) {
					int drawX = minimapX + x * tileWidth;
					int drawY = minimapY + y * tileHeight;
					
					// Check if this tile has been visited (explored)
					if (fogWar.isTileVisited(x, y)) {
						// Draw visited area with a neutral gray color
						g.setColor(new Color(140, 140, 140)); // Neutral gray for visited areas
						g.fillRect(drawX, drawY, tileWidth, tileHeight);
					}
				}
			}
		}

		private void drawTerrainFeatures(IGraphics g) {
			int mapCols = stateManager.getGameMap().getMapData()[0].length;
			int mapRows = stateManager.getGameMap().getMapData().length;

			for (int y = 0; y < mapRows; y++) {
				for (int x = 0; x < mapCols; x++) {
					if (fogWar.isTileVisited(x, y)) {
						int tile = stateManager.getGameMap().getMapData()[y][x];
						int drawX = minimapX + x * tileWidth;
						int drawY = minimapY + y * tileHeight;
						
						// Draw walls
						if (tile == 1) { // Wall
							g.setColor(new Color(80, 80, 80)); // Dark gray for walls
							g.fillRect(drawX, drawY, tileWidth, tileHeight);
						}
					}
				}
			}
		}

		private void drawFlags(IGraphics g) {
			java.util.Iterator<GameFlag> flagIter = stateManager.getFlagManager().getFlagList();
			while (flagIter.hasNext()) {
				GameFlag flag = flagIter.next();
				int flagX = flag.getMapX();
				int flagY = flag.getMapY();
				
				int drawX = minimapX + flagX * tileWidth;
				int drawY = minimapY + flagY * tileHeight;
				
				// Always show flags on minimap for strategic planning
				Color flagColor = flag.getColorForFaction();
				g.setColor(flagColor);
				g.fillRect(drawX, drawY, tileWidth, tileHeight);
				
				// Draw flag border
				g.setColor(new Color(255, 255, 255));
				g.drawRect(drawX, drawY, tileWidth, tileHeight);
			}
		}

		private void drawPlayerUnits(IGraphics g) {
			for (GameUnit playerUnit : stateManager.getUnitManager().getPlayerList()) {
				if (playerUnit.isAlive()) {
					Point unitPos = playerUnit.getCurrentPosition();
					Point mapPos = playerUnit.getMapPoint(unitPos);
					
					// Only show units in visited areas
					if (fogWar.isTileVisited(mapPos.x, mapPos.y)) {
						int drawX = minimapX + mapPos.x * tileWidth;
						int drawY = minimapY + mapPos.y * tileHeight;
						
						// Draw player unit as blue dot
						g.setColor(new Color(0, 100, 255)); // Blue for player units
						g.fillRect(drawX + 1, drawY + 1, tileWidth - 2, tileHeight - 2);
					}
				}
			}
		}

		private void drawEnemyUnits(IGraphics g) {
			for (GameUnit enemyUnit : stateManager.getUnitManager().getEnemyList()) {
				if (enemyUnit.isAlive()) {
					Point unitPos = enemyUnit.getCurrentPosition();
					Point mapPos = enemyUnit.getMapPoint(unitPos);
					
					// Only show enemy units if they're currently visible (in fog of war range)
					if (fogWar.isTileVisible(mapPos.x, mapPos.y)) {
						int drawX = minimapX + mapPos.x * tileWidth;
						int drawY = minimapY + mapPos.y * tileHeight;
						
						// Draw enemy unit as red dot
						g.setColor(new Color(255, 100, 100)); // Red for enemy units
						g.fillRect(drawX + 1, drawY + 1, tileWidth - 2, tileHeight - 2);
					}
				}
			}
		}

		private void drawCameraViewport(IGraphics g, int cameraX, int cameraY) {
			int screenWidth = Constants.SCREEN_WIDTH;
			int screenHeight = Constants.SCREEN_HEIGHT;
			
			// Calculate viewport bounds in minimap coordinates
			int viewportX = minimapX + (cameraX / Constants.TILE_WIDTH) * tileWidth;
			int viewportY = minimapY + (cameraY / Constants.TILE_HEIGHT) * tileHeight;
			int viewportWidth = (screenWidth / Constants.TILE_WIDTH) * tileWidth;
			int viewportHeight = (screenHeight / Constants.TILE_HEIGHT) * tileHeight;
			
			// Constrain viewport to minimap bounds
			viewportX = Math.max(minimapX, Math.min(minimapX + minimapWidth - viewportWidth, viewportX));
			viewportY = Math.max(minimapY, Math.min(minimapY + minimapHeight - viewportHeight, viewportY));
			viewportWidth = Math.min(viewportWidth, minimapWidth);
			viewportHeight = Math.min(viewportHeight, minimapHeight);
			
			// Draw viewport outline
			g.setColor(new Color(255, 255, 0)); // Yellow for viewport
			g.drawRect(viewportX, viewportY, viewportWidth, viewportHeight);
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
		// Draw the flag capture radius indicator
		drawFlagRadius(g, flag);
		
		// Get drawing instruction from flag and execute it
		DrawingInstruction instr = flag.getDrawingInstruction(getCameraX(), getCameraY());
		drawInstruction(g, instr);
	}
	
	/**
	 * Draws the flag capture radius indicator
	 */
	private void drawFlagRadius(IGraphics g, GameFlag flag) {
		Point flagScreenPos = TileCoordinateConverter.mapToScreen(flag.getMapX(), flag.getMapY());
		int flagX = flagScreenPos.x - getCameraX();
		int flagY = flagScreenPos.y - getCameraY();
		
		// Calculate radius in screen coordinates
		int radiusPixels = GameFlag.FLAG_RADIUS * Constants.TILE_WIDTH;
		
		// Draw radius indicator based on flag control
		Color radiusColor;
		boolean isCapturing = flag.isBeingCaptured();
		
		switch (flag.getControlFaction()) {
			case GameFlag.FACTION_PLAYER:
				radiusColor = new Color(0, 100, 255, isCapturing ? 60 : 30); // Brighter when capturing
				break;
			case GameFlag.FACTION_ENEMY:
				radiusColor = new Color(255, 100, 100, isCapturing ? 60 : 30); // Brighter when capturing
				break;
			default: // Neutral
				radiusColor = new Color(150, 150, 150, isCapturing ? 60 : 30); // Brighter when capturing
				break;
		}
		
		// Draw filled circle for capture zone
		g.setColor(radiusColor);
		drawCircle(g, flagX + Constants.TILE_WIDTH/2, flagY + Constants.TILE_HEIGHT/2, radiusPixels, true);
		
		// Draw border for better visibility
		Color borderColor = new Color(radiusColor.r, radiusColor.g, radiusColor.b, 150);
		g.setColor(borderColor);
		drawCircle(g, flagX + Constants.TILE_WIDTH/2, flagY + Constants.TILE_HEIGHT/2, radiusPixels, false);
	}
	
	/**
	 * Draws a circle using polygon approximation
	 */
	private void drawCircle(IGraphics g, int centerX, int centerY, int radius, boolean fill) {
		int segments = 32; // Number of line segments to approximate circle
		int[] xPoints = new int[segments];
		int[] yPoints = new int[segments];
		
		for (int i = 0; i < segments; i++) {
			double angle = 2 * Math.PI * i / segments;
			xPoints[i] = centerX + (int)(radius * Math.cos(angle));
			yPoints[i] = centerY + (int)(radius * Math.sin(angle));
		}
		
		if (fill) {
			g.fillPolygon(xPoints, yPoints, segments);
		} else {
			g.drawPolygon(xPoints, yPoints, segments);
		}
	}
	
	/**
	 * Renders all combat effects including damage numbers, attack animations,
	 * death animations, and particles.
	 */
	private void renderCombatEffects(IGraphics g) {
		CombatEffectManager effectManager = stateManager.getCombatEffectManager();
		effectManager.render(g, getCameraX(), getCameraY());
	}
}
