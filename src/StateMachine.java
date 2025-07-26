import input.GameMouseEvent;
import graphics.IGraphics;
import ui.UIComponent;

public abstract class StateMachine {
	public abstract void run(IGraphics g);

	public abstract void handleMouseCommand(GameMouseEvent e);

	public UIComponent getRoot() { return null; }
}
