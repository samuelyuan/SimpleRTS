import java.awt.Color;
import java.awt.Rectangle;

public class GameFlag {
	private int mapX = 0;
	private int mapY = 0;
	private int health = 1;
	private Color flagColor; // Color for the flag
	private Rectangle boundingBox; // Bounding box for health bar display

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

	public Rectangle getBoundingBox() {
		return boundingBox;
	}

	public void runLogic() {
		handleControl();
		draw();
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

	public void draw() {
		// Make sure the color and bounding box are updated before drawing
		updateColor();
		updateBoundingBox();

		// Use the updated color
		SimpleRTS.offscr.setColor(flagColor);

		// Draw the health bar with the updated bounding box
		SimpleRTS.offscr.fillRect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
	}

	public void updateColor() {
		// Update the flag's color based on the control faction
		switch (controlFaction) {
			case FACTION_PLAYER:
				this.flagColor = Color.BLUE;
				break;
			case FACTION_NEUTRAL:
				this.flagColor = Color.GRAY;
				break;
			case FACTION_ENEMY:
				this.flagColor = Color.RED;
				break;
			default:
				this.flagColor = Color.BLACK; // Default color if needed
				break;
		}
	}

	public void updateBoundingBox() {
		// Calculate the bounding box for the health bar
		int width = (int) ((double) (GameMap.TILE_WIDTH - 2) / 100.0 * Math.abs(health));
		int height = GameMap.TILE_HEIGHT / 8;
		int x = mapX * GameMap.TILE_WIDTH - SimpleRTS.cameraX + 2;
		int y = mapY * GameMap.TILE_HEIGHT + GameMap.TILE_HEIGHT / 8 - SimpleRTS.cameraY;

		this.boundingBox = new Rectangle(x, y, width, height);
	}
}
