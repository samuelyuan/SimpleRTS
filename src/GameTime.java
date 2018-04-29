
public class GameTime 
{
	private int day;
	private int hour;
	
	private long startTime;
	private long prevRunningTime;
	
	public GameTime(int day, int hour)
	{
		this.day = day;
		this.hour = hour;
		
		startTime = 0;
		prevRunningTime = 0;
	}
	
	public int getDay() { return day; }
	public int getHour() { return hour % 24; }
	public void addHour() { hour++; }
	public void addDay()
	{
		hour = 0;
		day++;
	}
	
	public void update()
	{
		//Get the current time
		if (startTime == 0)
		{
			startTime = System.currentTimeMillis();
		}
		
		//Get the update time
		int numSeconds = 4;
		long runningTime = (System.currentTimeMillis() - startTime) / (1000 * numSeconds);
		if (runningTime - prevRunningTime >= 1)
		{
			addHour(); //every 4 seconds in real life takes an hour in game
		}
		prevRunningTime = runningTime;
		
		//A day consists of 24 hours
		if (hour >= 24)
		{
			addDay();
		}
	}
	
	public boolean isNight()
	{
		int currentHour = getHour();
		if (currentHour >= 6 && currentHour <= 21)
			return false;
		else
			return true;
	}
}
