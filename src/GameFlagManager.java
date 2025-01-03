import java.util.ArrayList;
import java.util.Iterator;

public class GameFlagManager {
	private int numFlagsPlayer;
	private int numFlagsEnemy;
	private ArrayList<GameFlag> flags;

	public GameFlagManager() {
		numFlagsPlayer = 0;
		numFlagsEnemy = 0;
		flags = new ArrayList<GameFlag>();
	}

	public void addPlayerFlag(int x, int y) {
		flags.add(new GameFlag(x, y, GameFlag.FACTION_PLAYER));
		numFlagsPlayer++;
	}

	public void addEnemyFlag(int x, int y) {
		flags.add(new GameFlag(x, y, GameFlag.FACTION_ENEMY));
		numFlagsEnemy++;
	}

	public GameFlag getPlayerFlag() {
		for (int i = 0; i < flags.size(); i++) {
			if (flags.get(i).isFactionPlayer()) {
				return flags.get(i);
			}
		}

		return null;
	}

	public Iterator<GameFlag> getFlagList() {
		return flags.iterator();
	}

	public boolean isPlayerFlagsEmpty() {
		if (numFlagsPlayer == 0) {
			flags.clear();
			return true;
		} else {
			return false;
		}
	}

	public boolean isEnemyFlagsEmpty() {
		if (numFlagsEnemy == 0) {
			flags.clear();
			return true;
		} else {
			return false;
		}
	}

	public void reset() {
		numFlagsPlayer = 0;
		numFlagsEnemy = 0;
		for (int i = 0; i < flags.size(); i++) {
			if (flags.get(i).isFactionPlayer()) {
				numFlagsPlayer++;
			} else if (flags.get(i).isFactionEnemy()) {
				numFlagsEnemy++;
			}
		}
	}

	public void checkFlagState(int x, int y, int factionId) {
		for (int j = 0; j < flags.size(); j++) {
			flags.get(j).shiftToFaction(x, y, factionId);
		}
	}
}
