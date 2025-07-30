/**
 * Handles game time management and day progression.
 */
public class GameTimer {
    public static final int SPAWN_HOUR = 12;
    public static final int ENEMY_ATTACK_HOUR = 6;
    public static final int FLAG_RESET_INTERVAL = 6;
    
    private final GameTime gameTime;
    private final GameUnitManager unitManager;
    
    public GameTimer(int day, int hour) {
        this.gameTime = new GameTime(day, hour);
        this.unitManager = null; // Will be set by GameLoop
    }
    
    public GameTimer(int day, int hour, GameUnitManager unitManager) {
        this.gameTime = new GameTime(day, hour);
        this.unitManager = unitManager;
    }
    
    public void update() {
        gameTime.update();
        
        // Recalculate the flag counts at 0:00, 6:00, 12:00 and 18:00
        if (unitManager != null && gameTime.getHour() % FLAG_RESET_INTERVAL == 0) {
            unitManager.getFlagManager().reset();
        }
    }
    
    public int getHour() {
        return gameTime.getHour();
    }
    
    public int getDay() {
        return gameTime.getDay();
    }
    
    public GameTime getGameTime() {
        return gameTime;
    }
    
    public boolean isSpawnTime() {
        return gameTime.getHour() == SPAWN_HOUR;
    }
    
    public boolean isEnemyAttackTime() {
        return gameTime.getHour() == ENEMY_ATTACK_HOUR;
    }
} 