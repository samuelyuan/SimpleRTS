package pathfinding;

import graphics.Point;

/**
 * Handles movement physics calculations including steering behavior,
 * velocity updates, and stuck detection.
 */
public class MovementPhysics {
    // Physics state - using primitive doubles for better performance
    private double currentX, currentY;
    private double velocityX, velocityY;
    private double maxVelocity, maxForce;
    private double mass;

    /**
     * Creates a new MovementPhysics instance with default physics properties.
     * @param startX Initial X position
     * @param startY Initial Y position
     */
    public MovementPhysics(double startX, double startY) {
        this.currentX = startX;
        this.currentY = startY;
        
        // Default physics properties
        this.velocityX = 1.0;
        this.velocityY = 1.0;
        this.maxVelocity = 1.5;
        this.maxForce = 1.0;
        this.mass = 1.0;
    }

    /**
     * Creates a new MovementPhysics instance with custom physics properties.
     * @param startX Initial X position
     * @param startY Initial Y position
     * @param maxVelocity Maximum velocity
     * @param maxForce Maximum steering force
     * @param mass Mass of the object
     */
    public MovementPhysics(double startX, double startY, double maxVelocity, double maxForce, double mass) {
        this(startX, startY);
        this.maxVelocity = maxVelocity;
        this.maxForce = maxForce;
        this.mass = mass;
    }

    /**
     * Updates the position based on steering behavior towards a target.
     * @param targetX Target X position
     * @param targetY Target Y position
     */
    public void updatePosition(double targetX, double targetY) {
        // Calculate desired velocity
        double desiredX = targetX - currentX;
        double desiredY = targetY - currentY;
        
        // Limit desired velocity
        double desiredMagnitude = Math.sqrt(desiredX * desiredX + desiredY * desiredY);
        if (desiredMagnitude > maxVelocity) {
            desiredX = (desiredX / desiredMagnitude) * maxVelocity;
            desiredY = (desiredY / desiredMagnitude) * maxVelocity;
        }
        
        // Calculate steering force
        double steeringX = desiredX - velocityX;
        double steeringY = desiredY - velocityY;
        
        // Limit steering force
        double steeringMagnitude = Math.sqrt(steeringX * steeringX + steeringY * steeringY);
        if (steeringMagnitude > maxForce) {
            steeringX = (steeringX / steeringMagnitude) * maxForce;
            steeringY = (steeringY / steeringMagnitude) * maxForce;
        }
        
        // Apply acceleration (F = ma --> a = F / m)
        double accelerationX = steeringX / mass;
        double accelerationY = steeringY / mass;
        
        // Update velocity
        velocityX += accelerationX;
        velocityY += accelerationY;
        
        // Limit velocity
        double velocityMagnitude = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        if (velocityMagnitude > maxVelocity) {
            velocityX = (velocityX / velocityMagnitude) * maxVelocity;
            velocityY = (velocityY / velocityMagnitude) * maxVelocity;
        }
        
        // Update position
        currentX += velocityX;
        currentY += velocityY;
    }



    /**
     * Gets the current position as a Point.
     * @return Current position
     */
    public Point getCurrentPosition() {
        return new Point((int) currentX, (int) currentY);
    }

    /**
     * Gets the current X position.
     * @return Current X position
     */
    public double getCurrentX() {
        return currentX;
    }

    /**
     * Gets the current Y position.
     * @return Current Y position
     */
    public double getCurrentY() {
        return currentY;
    }

    /**
     * Sets the current position.
     * @param x X position
     * @param y Y position
     */
    public void setPosition(double x, double y) {
        this.currentX = x;
        this.currentY = y;
    }

    /**
     * Sets the physics properties.
     * @param maxVelocity Maximum velocity
     * @param maxForce Maximum steering force
     * @param mass Mass of the object
     */
    public void setPhysicsProperties(double maxVelocity, double maxForce, double mass) {
        this.maxVelocity = maxVelocity;
        this.maxForce = maxForce;
        this.mass = mass;
    }

    /**
     * Gets the maximum velocity.
     * @return Maximum velocity
     */
    public double getMaxVelocity() {
        return maxVelocity;
    }

    /**
     * Gets the maximum force.
     * @return Maximum force
     */
    public double getMaxForce() {
        return maxForce;
    }

    /**
     * Gets the mass.
     * @return Mass
     */
    public double getMass() {
        return mass;
    }

    /**
     * Calculates the distance between two points.
     * @param x1 First point X
     * @param y1 First point Y
     * @param x2 Second point X
     * @param y2 Second point Y
     * @return Distance between the points
     */
    public static double getDistance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
