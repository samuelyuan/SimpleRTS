
public class MapNode {
	private int x, y;
	private int fScore, gScore, hScore;
	private MapNode parentNode;

	public MapNode(int x, int y, int g, int h, MapNode parent) {
		this.x = x;
		this.y = y;
		gScore = g;
		hScore = h;
		fScore = g + h;
		parentNode = parent;
	}

	public String toString() {
		String s = "";
		s += "\n(" + x;
		s += ", " + y + ")";
		s += "\nf: " + fScore;
		s += " g: " + gScore;
		s += " h: " + hScore;
		if (parentNode != null)
			s += "\nParent: (" + parentNode.getX() + "," + parentNode.getY() + ")";

		return s;
	}

	public static int findH(int nodeX, int nodeY, int finalX, int finalY) {
		return (10 * Math.abs(nodeX - finalX) + 10 * Math.abs(nodeY - finalY));
	}

	public static int findG(int nodeX1, int nodeY1, int nodeX2, int nodeY2) {
		int dx = Math.abs(nodeX1 - nodeX2);
		int dy = Math.abs(nodeY1 - nodeY2);
		if (dx == 1 && dy == 0 || dx == 0 && dy == 1) {
			return 10; // straight movement
		} else if (dx == 1 && dy == 1) {
			return 14; // diagonal movement
		}
		return -1; // invalid movement
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getF() {
		return fScore;
	}

	public int getG() {
		return gScore;
	}

	public int getH() {
		return hScore;
	}

	public MapNode getParent() {
		return parentNode;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setF(int f) {
		this.fScore = f;
	}

	public void setG(int g) {
		this.gScore = g;
	}

	public void setH(int h) {
		this.hScore = h;
	}

	public void setParent(MapNode parent) {
		this.parentNode = parent;
	}

}
