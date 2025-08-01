import graphics.Color;
import graphics.Rect;
import utils.Constants;
import utils.TileCoordinateConverter;

public class GameFlag {
	private int mapX = 0;
	private int mapY = 0;
	private int health = 1;
	private Color flagColor; // Color for the flag
	private Rect boundingBox; // Bounding box for health bar display

	static final int FACTION_ENEMY = -1;
	static final int FACTION_NEUTRAL = 0;
	static final int FACTION_PLAYER = 1;
	private int controlFaction = FACTION_NEUTRAL;

	final int FLAG_RADIUS = 3;

	public GameFlag() {
		this(0, 0, GameFlag.FACTION_NEUTRAL);
	}

	public GameFlag(int x, int y, int factionId) {
		this.mapX = x;
		this.mapY = y;
		this.controlFaction = factionId;

		if (factionId == GameFlag.FACTION_PLAYER)
			this.health = 100;
		else if (factionId == GameFlag.FACTION_ENEMY)
			this.health = -100;
		else
			this.health = 0;
	}

	public int getControlFaction() {
		return controlFaction;
	}

	public int getMapX() {
		return mapX;
	}

	public int getMapY() {
		return mapY;
	}

	public void setMapX(int x) {
		mapX = x;
	}

	public void setMapY(int y) {
		mapY = y;
	}

	public boolean isFactionPlayer() {
		return controlFaction == FACTION_PLAYER;
	}

	public boolean isFactionEnemy() {
		return controlFaction == FACTION_ENEMY;
	}

	public int getHealth() {
		return health;
	}

	public Color getFlagColor() {
		return flagColor;
	}

	public Rect getBoundingBox() {
		return boundingBox;
	}

	public void runLogic() {
		handleControl();
	}

	public void shiftToFaction(int unitX, int unitY, int factionId) {
		if (Math.abs(unitX - this.mapX) + Math.abs(unitY - this.mapY) <= FLAG_RADIUS) {
			// Adjust the health based on the faction's proximity
			health += (factionId * 2);

			// Ensure the health stays within bounds (-100 to 100)
			if (health > 100) {
				health = 100; // Cap health at 100 for player control
			} else if (health < -100) {
				health = -100; // Cap health at -100 for enemy control
			}

			// After adjusting health, call handleControl to check and update the faction
			// control
			handleControl();
		}
	}

	public void handleControl() {
		// Switch flag control to the enemy
		if (health <= -100 && controlFaction != GameFlag.FACTION_ENEMY) {
			health = -100;
			controlFaction = FACTION_ENEMY;
		}

		// Switch flag control to the player
		if (health >= 100 && controlFaction != GameFlag.FACTION_PLAYER) {
			health = 100;
			controlFaction = FACTION_PLAYER;
		}

		// Switch flag control to neutral
		if ((health < 0 && controlFaction == GameFlag.FACTION_PLAYER)
				|| (health > 0 && controlFaction == GameFlag.FACTION_ENEMY)) {
			controlFaction = FACTION_NEUTRAL;
		}
	}

	public Color getColorForFaction() {
		// Return the flag's color based on the control faction
		switch (controlFaction) {
			case FACTION_PLAYER:
				return Color.BLUE;
			case FACTION_NEUTRAL:
				return Color.GRAY;
			case FACTION_ENEMY:
				return Color.RED;
			default:
				return Color.BLACK; // Default color if needed
		}
	}

	public Rect getBoundingBoxForState(int cameraX, int cameraY) {
		// Calculate the bounding box for the health bar
		int width = (int) ((double) (Constants.TILE_WIDTH - 2) / 100.0 * Math.abs(health));
		int height = Constants.TILE_HEIGHT / 8;
		int x = TileCoordinateConverter.mapToScreen(mapX, mapY).x + 2;
		int y = TileCoordinateConverter.mapToScreen(mapX, mapY).y + Constants.TILE_HEIGHT / 8;

		return new Rect(x, y, width, height);
	}

	/**
	 * Returns a DrawingInstruction for rendering this flag.
	 * This method encapsulates the drawing logic and makes it testable.
	 * 
	 * @param cameraX The camera X offset
	 * @param cameraY The camera Y offset
	 * @return A DrawingInstruction for rendering the flag
	 */
	public DrawingInstruction getDrawingInstruction(int cameraX, int cameraY) {
		Rect boundingBox = getBoundingBoxForState(cameraX, cameraY);
		Color flagColor = getColorForFaction();
		return new DrawingInstruction(boundingBox, flagColor, true); // Fill the flag
	}
}
