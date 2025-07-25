import input.GameMouseEvent;

public abstract class StateMachine {
	public abstract void run();

	public abstract void handleMouseCommand(GameMouseEvent e);
}
