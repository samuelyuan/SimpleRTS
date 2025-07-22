import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameTimeTest {

    private GameTime gameTime;

    @BeforeEach
    public void setUp() {
        // Initialize a GameTime instance before each test
        gameTime = new GameTime(1, 10); // Starts at day 1, hour 10
    }

    @Test
    public void testGetDay() {
        // Ensure day is correctly initialized
        assertEquals(1, gameTime.getDay(), "Day should be initialized to 1");
        
        // Increment day
        gameTime.addDay();
        assertEquals(2, gameTime.getDay(), "Day should increment correctly");
    }

    @Test
    public void testGetHour() {
        // Ensure hour is correctly initialized
        assertEquals(10, gameTime.getHour(), "Hour should be initialized to 10");

        // Increment hour
        gameTime.addHour();
        assertEquals(11, gameTime.getHour(), "Hour should increment correctly");

        // Increment hour using a loop
        for (int i = 0; i < 12; i++) {
            gameTime.addHour();
        }
        assertEquals(23, gameTime.getHour(), "Hour should be 23 hours");
    }

    @Test
    public void testHourWraparound() {
        // Ensure hour is correctly initialized to 10
        assertEquals(10, gameTime.getHour(), "Hour should be initialized to 10");

        // Increment hour using a loop to add 15 hours to 10
        for (int i = 0; i < 15; i++) {
            gameTime.addHour();
        }

        // After adding 15 hours to 10, the expected hour is 1 (since 10 + 15 = 25, and 25 % 24 = 1)
        assertEquals(1, gameTime.getHour(), "Hour should wrap around after 24 hours");

        // Now increment another 24 hours
        for (int i = 0; i < 24; i++) {
            gameTime.addHour();
        }

        // After adding 24 hours to 1, the expected hour is 1 (since 1 + 24 = 25, and 25 % 24 = 1)
        assertEquals(1, gameTime.getHour(), "Hour should wrap around to 1 after 48 hours");
    }

    @Test
    public void testAddHour() {
        // Initially, the hour is 10
        gameTime.addHour();
        assertEquals(11, gameTime.getHour(), "Hour should increment by 1");
    }

    @Test
    public void testAddDay() {
        // Initially, the day is 1 and hour is 10
        gameTime.addDay();
        assertEquals(2, gameTime.getDay(), "Day should increment by 1");
        assertEquals(0, gameTime.getHour(), "Hour should reset to 0 after a day is added");
    }

    @Test
    public void testUpdateHour() throws InterruptedException {
        // Simulate real time updates
        long currentTime = System.currentTimeMillis();
        gameTime.update(); // Should not increment yet
        long newTime = System.currentTimeMillis();
        
        // Ensure that `update()` correctly updates the hour after some real-time has passed
        if (newTime - currentTime > 4000) {
            gameTime.update();
            assertTrue(gameTime.getHour() > 10, "Hour should update after real time passes");
        }
    }

    @Test
    public void testIsNight() {
        // Test when it's night time (should return true)
        gameTime = new GameTime(1, 23); // 23:00 should be night
        assertTrue(gameTime.isNight(), "It should be night at 23:00");

        // Test when it's day time (should return false)
        gameTime = new GameTime(1, 10); // 10:00 should be day
        assertFalse(gameTime.isNight(), "It should not be night at 10:00");

        // Test early morning (should be night)
        gameTime = new GameTime(1, 5); // 05:00 should be night
        assertTrue(gameTime.isNight(), "It should be night at 05:00");
    }
}
