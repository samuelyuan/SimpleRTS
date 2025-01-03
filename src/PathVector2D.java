
public final class PathVector2D {
	private double x, y;

	public PathVector2D(double xComponent, double yComponent) {
		x = xComponent;
		y = yComponent;
	}

	public PathVector2D() {
		this(0, 0);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public PathVector2D add(PathVector2D otherVector) {
		PathVector2D newVec = new PathVector2D();
		newVec.x = this.x + otherVector.x;
		newVec.y = this.y + otherVector.y;
		return newVec;
	}

	public PathVector2D subtract(PathVector2D otherVector) {
		PathVector2D newVec = new PathVector2D();
		newVec.x = this.x - otherVector.x;
		newVec.y = this.y - otherVector.y;
		return newVec;
	}

	public double getDotProduct(PathVector2D otherVector) {
		// System.out.println("x1 = " + x + " x2 = " + otherVector.x);
		// System.out.println("y1 = " + y + " y2 = " + otherVector.y);
		return x * otherVector.x + y * otherVector.y;
	}

	public double getMagnitude() {
		return Math.sqrt(x * x + y * y);
	}

	public void normalize() {
		double magnitude = getMagnitude();
		x /= magnitude;
		y /= magnitude;
	}

	public void scale(double scaleFactor) {
		x *= scaleFactor;
		y *= scaleFactor;
	}

	public void limit(double maximum) {
		// only limit if magnitude is too large
		if (this.getMagnitude() > maximum) {
			normalize();
			scale(maximum);
		}
	}

	public void rotate(double angle) {
		double length = getMagnitude();
		x = Math.cos(angle) * length;
		y = Math.sin(angle) * length;
	}

	public static double getAngleInBetween(PathVector2D v1, PathVector2D v2) {
		double dotProduct = v1.getDotProduct(v2);
		return Math.acos(dotProduct / (v1.getMagnitude() * v2.getMagnitude()));
	}

	public static double getDistance(PathVector2D v1, PathVector2D v2) {
		double xDist = (v1.x - v2.x) * (v1.x - v2.x);
		double yDist = (v1.y - v2.y) * (v1.y - v2.y);
		return Math.sqrt(xDist + yDist);
	}

	public static PathVector2D getNormalPoint(PathVector2D p, PathVector2D a, PathVector2D b) {
		PathVector2D ap = p.subtract(a);
		PathVector2D ab = b.subtract(a);
		ab.normalize();
		ab.scale(ap.getDotProduct(ab));

		PathVector2D normalPoint = a.add(ab);
		return normalPoint;
	}

	public String toString() {
		String s = "";
		s += (this.x + "i + " + this.y + "j");
		return s;
	}

	/*
	 * public static void main(String[] args)
	 * {
	 * PathVector2D v1 = new PathVector2D(3, 4);
	 * PathVector2D v2 = new PathVector2D(4, 50);
	 * 
	 * PathVector2D v3 = v1.add(v2);
	 * PathVector2D v4 = v1.subtract(v2);
	 * 
	 * System.out.println("v1: " + v1 + " magnitude: " + v1.getMagnitude());
	 * v1.normalize();
	 * System.out.println("v1 normalized: " + v1);
	 * v1.scale(5);
	 * System.out.println("v1 scaled: " + v1);
	 * System.out.println("v2: " + v2);
	 * System.out.println("v3: " + v3);
	 * System.out.println("v4: " + v4);
	 * System.out.println("dot product: " + v1.getDotProduct(v2));
	 * 
	 * PathVector2D v5 = new PathVector2D(0, 10);
	 * PathVector2D v6 = new PathVector2D(20, 0);
	 * 
	 * System.out.println("angle in between: " + getAngleInBetween(v5, v6));
	 * }
	 */
}
