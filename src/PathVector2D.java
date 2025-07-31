
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
		if (magnitude > 0) {
			x /= magnitude;
			y /= magnitude;
		}
		// If magnitude is 0, leave the vector as is (zero vector)
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
		double magnitude1 = v1.getMagnitude();
		double magnitude2 = v2.getMagnitude();
		
		// Handle zero vectors
		if (magnitude1 == 0 || magnitude2 == 0) {
			return 0.0; // Return 0 for angle between zero vectors
		}
		
		double dotProduct = v1.getDotProduct(v2);
		double denominator = magnitude1 * magnitude2;
		
		// Clamp the value to [-1, 1] to avoid numerical precision issues
		double cosAngle = dotProduct / denominator;
		if (cosAngle > 1.0) cosAngle = 1.0;
		if (cosAngle < -1.0) cosAngle = -1.0;
		
		return Math.acos(cosAngle);
	}

	public static double getDistance(PathVector2D v1, PathVector2D v2) {
		double xDist = (v1.x - v2.x) * (v1.x - v2.x);
		double yDist = (v1.y - v2.y) * (v1.y - v2.y);
		return Math.sqrt(xDist + yDist);
	}

	public static PathVector2D getNormalPoint(PathVector2D p, PathVector2D a, PathVector2D b) {
		PathVector2D ap = p.subtract(a);
		PathVector2D ab = b.subtract(a);
		
		// Handle case where a and b are the same point
		if (ab.getMagnitude() == 0) {
			return a; // Return point a if line segment is degenerate
		}
		
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
}
