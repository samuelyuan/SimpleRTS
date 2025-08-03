/**
 * Handles game time management and day progression.
 */
public class GameTimer {
    public static final int SPAWN_HOUR = 12;
    public static final int ENEMY_ATTACK_HOUR = 6;
    public static final int FLAG_RESET_INTERVAL = 6;
    
    // Time-related fields from GameTime
    private int day;
    private int hour;
    private long startTime;
    private long prevRunningTime;
    
    private final GameFlagManager flagManager;
    
    public GameTimer(int day, int hour) {
        this.day = day;
        this.hour = hour;
        this.startTime = 0;
        this.prevRunningTime = 0;
        this.flagManager = null; // Will be set by GameLoop
    }
    
    public GameTimer(int day, int hour, GameFlagManager flagManager) {
        this.day = day;
        this.hour = hour;
        this.startTime = 0;
        this.prevRunningTime = 0;
        this.flagManager = flagManager;
    }
    
    public void update() {
        // Get the current time
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }

        // Get the update time
        int numSeconds = 4;
        long runningTime = (System.currentTimeMillis() - startTime) / (1000 * numSeconds);
        if (runningTime - prevRunningTime >= 1) {
            addHour(); // every 4 seconds in real life takes an hour in game
        }
        prevRunningTime = runningTime;

        // A day consists of 24 hours
        if (hour >= 24) {
            addDay();
        }
        
        // Recalculate the flag counts at 0:00, 6:00, 12:00 and 18:00
        if (flagManager != null && getHour() % FLAG_RESET_INTERVAL == 0) {
            flagManager.reset();
        }
    }
    
    public int getHour() {
        return hour % 24;
    }
    
    public int getDay() {
        return day;
    }
    
    public void addHour() {
        hour++;
    }
    
    public void addDay() {
        hour = 0;
        day++;
    }
    
    public boolean isNight() {
        int currentHour = getHour();
        return currentHour < 6 || currentHour > 21;
    }
    
    public boolean isSpawnTime() {
        return getHour() == SPAWN_HOUR;
    }
    
    public boolean isEnemyAttackTime() {
        return getHour() == ENEMY_ATTACK_HOUR;
    }
} 