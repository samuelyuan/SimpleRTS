package pathfinding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PathVector2DTest {

    private PathVector2D vector1;
    private PathVector2D vector2;
    private PathVector2D zeroVector;

    @BeforeEach
    public void setUp() {
        vector1 = new PathVector2D(3.0, 4.0);
        vector2 = new PathVector2D(1.0, 2.0);
        zeroVector = new PathVector2D(0.0, 0.0);
    }

    // Constructor tests
    @Test
    public void testDefaultConstructor() {
        PathVector2D defaultVector = new PathVector2D();
        assertEquals(0.0, defaultVector.getX(), 0.001, "Default constructor should create zero vector");
        assertEquals(0.0, defaultVector.getY(), 0.001, "Default constructor should create zero vector");
    }

    @Test
    public void testParameterizedConstructor() {
        PathVector2D testVector = new PathVector2D(5.5, -3.2);
        assertEquals(5.5, testVector.getX(), 0.001, "X component should be set correctly");
        assertEquals(-3.2, testVector.getY(), 0.001, "Y component should be set correctly");
    }

    // Getter tests
    @Test
    public void testGetX() {
        assertEquals(3.0, vector1.getX(), 0.001, "getX should return correct X component");
    }

    @Test
    public void testGetY() {
        assertEquals(4.0, vector1.getY(), 0.001, "getY should return correct Y component");
    }

    // Basic operation tests
    @Test
    public void testAdd() {
        PathVector2D result = vector1.add(vector2);
        assertEquals(4.0, result.getX(), 0.001, "Addition X component should be correct");
        assertEquals(6.0, result.getY(), 0.001, "Addition Y component should be correct");
        
        // Test that original vectors are not modified
        assertEquals(3.0, vector1.getX(), 0.001, "Original vector should not be modified");
        assertEquals(4.0, vector1.getY(), 0.001, "Original vector should not be modified");
    }

    @Test
    public void testAddWithZeroVector() {
        PathVector2D result = vector1.add(zeroVector);
        assertEquals(vector1.getX(), result.getX(), 0.001, "Adding zero vector should return original vector");
        assertEquals(vector1.getY(), result.getY(), 0.001, "Adding zero vector should return original vector");
    }

    @Test
    public void testSubtract() {
        PathVector2D result = vector1.subtract(vector2);
        assertEquals(2.0, result.getX(), 0.001, "Subtraction X component should be correct");
        assertEquals(2.0, result.getY(), 0.001, "Subtraction Y component should be correct");
        
        // Test that original vectors are not modified
        assertEquals(3.0, vector1.getX(), 0.001, "Original vector should not be modified");
        assertEquals(4.0, vector1.getY(), 0.001, "Original vector should not be modified");
    }

    @Test
    public void testSubtractWithZeroVector() {
        PathVector2D result = vector1.subtract(zeroVector);
        assertEquals(vector1.getX(), result.getX(), 0.001, "Subtracting zero vector should return original vector");
        assertEquals(vector1.getY(), result.getY(), 0.001, "Subtracting zero vector should return original vector");
    }

    // Mathematical operation tests
    @Test
    public void testGetDotProduct() {
        double dotProduct = vector1.getDotProduct(vector2);
        // (3*1) + (4*2) = 3 + 8 = 11
        assertEquals(11.0, dotProduct, 0.001, "Dot product should be calculated correctly");
    }

    @Test
    public void testGetDotProductWithZeroVector() {
        double dotProduct = vector1.getDotProduct(zeroVector);
        assertEquals(0.0, dotProduct, 0.001, "Dot product with zero vector should be zero");
    }

    @Test
    public void testGetDotProductWithSelf() {
        double dotProduct = vector1.getDotProduct(vector1);
        // (3*3) + (4*4) = 9 + 16 = 25
        assertEquals(25.0, dotProduct, 0.001, "Dot product with self should equal magnitude squared");
    }

    @Test
    public void testGetMagnitude() {
        double magnitude = vector1.getMagnitude();
        // sqrt(3^2 + 4^2) = sqrt(9 + 16) = sqrt(25) = 5
        assertEquals(5.0, magnitude, 0.001, "Magnitude should be calculated correctly");
    }

    @Test
    public void testGetMagnitudeOfZeroVector() {
        double magnitude = zeroVector.getMagnitude();
        assertEquals(0.0, magnitude, 0.001, "Zero vector magnitude should be zero");
    }

    @Test
    public void testGetMagnitudeOfUnitVector() {
        PathVector2D unitVector = new PathVector2D(1.0, 0.0);
        double magnitude = unitVector.getMagnitude();
        assertEquals(1.0, magnitude, 0.001, "Unit vector magnitude should be one");
    }

    @Test
    public void testNormalize() {
        vector1.normalize();
        assertEquals(0.6, vector1.getX(), 0.001, "Normalized X component should be 3/5 = 0.6");
        assertEquals(0.8, vector1.getY(), 0.001, "Normalized Y component should be 4/5 = 0.8");
        
        // Check that magnitude is now 1
        assertEquals(1.0, vector1.getMagnitude(), 0.001, "Normalized vector should have magnitude 1");
    }

    @Test
    public void testNormalizeAlreadyNormalized() {
        vector1.normalize();
        double originalX = vector1.getX();
        double originalY = vector1.getY();
        
        vector1.normalize(); // Normalize again
        assertEquals(originalX, vector1.getX(), 0.001, "Double normalization should not change vector");
        assertEquals(originalY, vector1.getY(), 0.001, "Double normalization should not change vector");
    }

    @Test
    public void testScale() {
        vector1.scale(2.0);
        assertEquals(6.0, vector1.getX(), 0.001, "Scaled X component should be doubled");
        assertEquals(8.0, vector1.getY(), 0.001, "Scaled Y component should be doubled");
    }

    @Test
    public void testScaleByZero() {
        vector1.scale(0.0);
        assertEquals(0.0, vector1.getX(), 0.001, "Scaling by zero should result in zero vector");
        assertEquals(0.0, vector1.getY(), 0.001, "Scaling by zero should result in zero vector");
    }

    @Test
    public void testScaleByNegative() {
        vector1.scale(-1.0);
        assertEquals(-3.0, vector1.getX(), 0.001, "Scaling by -1 should negate X component");
        assertEquals(-4.0, vector1.getY(), 0.001, "Scaling by -1 should negate Y component");
    }

    @Test
    public void testLimit() {
        PathVector2D longVector = new PathVector2D(10.0, 10.0);
        longVector.limit(5.0);
        
        double magnitude = longVector.getMagnitude();
        assertEquals(5.0, magnitude, 0.001, "Limited vector should have specified magnitude");
    }

    @Test
    public void testLimitAlreadyUnderLimit() {
        PathVector2D shortVector = new PathVector2D(1.0, 1.0);
        double originalMagnitude = shortVector.getMagnitude();
        
        shortVector.limit(5.0);
        assertEquals(originalMagnitude, shortVector.getMagnitude(), 0.001, 
                    "Vector under limit should not be changed");
    }

    @Test
    public void testRotate() {
        PathVector2D testVector = new PathVector2D(1.0, 0.0); // Unit vector along x-axis
        testVector.rotate(Math.PI / 2); // Rotate 90 degrees
        
        assertEquals(0.0, testVector.getX(), 0.001, "90-degree rotation should result in x=0");
        assertEquals(1.0, testVector.getY(), 0.001, "90-degree rotation should result in y=1");
    }

    @Test
    public void testRotateZeroVector() {
        zeroVector.rotate(Math.PI / 4);
        assertEquals(0.0, zeroVector.getX(), 0.001, "Rotating zero vector should remain zero");
        assertEquals(0.0, zeroVector.getY(), 0.001, "Rotating zero vector should remain zero");
    }

    // Static method tests
    @Test
    public void testGetAngleInBetween() {
        PathVector2D v1 = new PathVector2D(1.0, 0.0);
        PathVector2D v2 = new PathVector2D(0.0, 1.0);
        
        double angle = PathVector2D.getAngleInBetween(v1, v2);
        assertEquals(Math.PI / 2, angle, 0.001, "Angle between perpendicular vectors should be π/2");
    }

    @Test
    public void testGetAngleInBetweenParallel() {
        PathVector2D v1 = new PathVector2D(1.0, 0.0);
        PathVector2D v2 = new PathVector2D(2.0, 0.0);
        
        double angle = PathVector2D.getAngleInBetween(v1, v2);
        assertEquals(0.0, angle, 0.001, "Angle between parallel vectors should be 0");
    }

    @Test
    public void testGetAngleInBetweenAntiParallel() {
        PathVector2D v1 = new PathVector2D(1.0, 0.0);
        PathVector2D v2 = new PathVector2D(-1.0, 0.0);
        
        double angle = PathVector2D.getAngleInBetween(v1, v2);
        assertEquals(Math.PI, angle, 0.001, "Angle between anti-parallel vectors should be π");
    }

    @Test
    public void testGetDistance() {
        PathVector2D v1 = new PathVector2D(0.0, 0.0);
        PathVector2D v2 = new PathVector2D(3.0, 4.0);
        
        double distance = PathVector2D.getDistance(v1, v2);
        assertEquals(5.0, distance, 0.001, "Distance should be calculated correctly");
    }

    @Test
    public void testGetDistanceSamePoint() {
        double distance = PathVector2D.getDistance(vector1, vector1);
        assertEquals(0.0, distance, 0.001, "Distance to same point should be zero");
    }

    @Test
    public void testGetNormalPoint() {
        PathVector2D p = new PathVector2D(2.0, 2.0);
        PathVector2D a = new PathVector2D(0.0, 0.0);
        PathVector2D b = new PathVector2D(4.0, 0.0);
        
        PathVector2D normalPoint = PathVector2D.getNormalPoint(p, a, b);
        assertEquals(2.0, normalPoint.getX(), 0.001, "Normal point X should be correct");
        assertEquals(0.0, normalPoint.getY(), 0.001, "Normal point Y should be correct");
    }

    @Test
    public void testGetNormalPointPointOnLine() {
        PathVector2D p = new PathVector2D(2.0, 0.0);
        PathVector2D a = new PathVector2D(0.0, 0.0);
        PathVector2D b = new PathVector2D(4.0, 0.0);
        
        PathVector2D normalPoint = PathVector2D.getNormalPoint(p, a, b);
        assertEquals(2.0, normalPoint.getX(), 0.001, "Normal point should be the point itself when on line");
        assertEquals(0.0, normalPoint.getY(), 0.001, "Normal point should be the point itself when on line");
    }

    // Utility method tests
    @Test
    public void testToString() {
        String result = vector1.toString();
        assertEquals("3.0i + 4.0j", result, "toString should format vector correctly");
    }

    @Test
    public void testToStringWithNegativeComponents() {
        PathVector2D negativeVector = new PathVector2D(-1.5, -2.5);
        String result = negativeVector.toString();
        assertEquals("-1.5i + -2.5j", result, "toString should handle negative components");
    }

    // Edge case tests
    @Test
    public void testNormalizeZeroVector() {
        // Zero vector should remain zero when normalized
        zeroVector.normalize();
        assertEquals(0.0, zeroVector.getX(), 0.001, "Normalizing zero vector should keep it zero");
        assertEquals(0.0, zeroVector.getY(), 0.001, "Normalizing zero vector should keep it zero");
    }

    @Test
    public void testGetAngleInBetweenZeroVectors() {
        // Angle between zero vector and any vector should be 0
        double angle = PathVector2D.getAngleInBetween(zeroVector, vector1);
        assertEquals(0.0, angle, 0.001, "Angle between zero vector and any vector should be 0");
        
        // Angle between two zero vectors should be 0
        double angle2 = PathVector2D.getAngleInBetween(zeroVector, new PathVector2D(0.0, 0.0));
        assertEquals(0.0, angle2, 0.001, "Angle between two zero vectors should be 0");
    }
    
    @Test
    public void testGetNormalPointDegenerateLine() {
        // Test with degenerate line segment (a and b are the same point)
        PathVector2D p = new PathVector2D(2.0, 2.0);
        PathVector2D a = new PathVector2D(1.0, 1.0);
        PathVector2D b = new PathVector2D(1.0, 1.0); // Same as a
        
        PathVector2D normalPoint = PathVector2D.getNormalPoint(p, a, b);
        assertEquals(a.getX(), normalPoint.getX(), 0.001, "Normal point should be point a for degenerate line");
        assertEquals(a.getY(), normalPoint.getY(), 0.001, "Normal point should be point a for degenerate line");
    }
} 