package jaci.openrio.toast.lib.math;

/**
 * A simple class representing a 2D Vector. This class is capable of all the relevant mathematical operations for
 * a 2D vector.
 *
 * @author Jaci
 */
public class Vec2D {

    private double x = 0, y = 0;

    public Vec2D() { }

    /**
     * Create a new 2D Vector with the given x and y components
     */
    public Vec2D(double x, double y) {
        this.x = x; this.y = y;
    }

    /**
     * Clone a 2D Vector Instance
     */
    public Vec2D(Vec2D clone) {
        this(clone.x(), clone.y());
    }

    // -- STATICS -- //

    /**
     * Create a 2D Vector from an angle relative to the positive x-axis and a magnitude
     * @param angleInDegrees    The angle relative to the positive x-axis in Degrees
     * @param magnitude         The magnitude of the vector
     */
    public static Vec2D fromDegrees(double angleInDegrees, double magnitude) {
        double x = magnitude * Math.cos(angleInDegrees * Math.PI / 180);
        double y = magnitude * Math.sin(angleInDegrees * Math.PI / 180);
        return new Vec2D(x, y);
    }

    /**
     * Create a 2D Vector from an angle relative to the positive y-axis and a magnitude
     * @param angleInDegrees    The angle relative to the positive y-axis in Degrees (Clockwise on a Clock)
     * @param magnitude         The magnitude of the vector
     */
    public static Vec2D fromDegrees2(double angleInDegrees, double magnitude) {
        double x = magnitude * Math.sin(angleInDegrees * Math.PI / 180);
        double y = magnitude * Math.cos(angleInDegrees * Math.PI / 180);
        return new Vec2D(x, y);
    }

    /**
     * Create a 2D Vector from an angle relative to the positive x-axis and a magnitude
     * @param angleInRad        The angle relative to the positive x-axis in Radians
     * @param magnitude         The magnitude of the vector
     */
    public static Vec2D fromRadians(double angleInRad, double magnitude) {
        double x = magnitude * Math.cos(angleInRad);
        double y = magnitude * Math.sin(angleInRad);
        return new Vec2D(x, y);
    }

    /**
     * Create a 2D Vector from an angle relative to the positive y-axis and a magnitude
     * @param angleInRad        The angle relative to the positive y-axis in Radians (Clockwise on a Clock)
     * @param magnitude         The magnitude of the vector
     */
    public static Vec2D fromRadians2(double angleInRad, double magnitude) {
        double x = magnitude * Math.sin(angleInRad);
        double y = magnitude * Math.cos(angleInRad);
        return new Vec2D(x, y);
    }

    // -- INSTANCE -- //

    /**
     * @return The x component of the 2D Vector
     */
    public double x() {
        return x;
    }

    /**
     * @return The y component of the 2D Vector
     */
    public double y() {
        return y;
    }

    /**
     * Set the x-component of the 2D Vector
     * @param newX The new x component of the 2D Vector
     */
    public void setX(double newX) {
        this.x = newX;
    }

    /**
     * Set the y-component of the 2D Vector
     * @param newY The new y component of the 2D Vector
     */
    public void setY(double newY) {
        this.y = newY;
    }

    /**
     * @return The magnitude of the 2D Vector
     */
    public double magnitude() {
        return Math.sqrt(x() * x() + y() * y());
    }

    /**
     * @return The heading relative to the positive x-axis in the form of Degrees.
     */
    public double heading() {
        return Math.atan2(y(), x()) * 180 / Math.PI;
    }

    /**
     * @return The heading relative to the positive x-axis in the form of Radians.
     */
    public double headingRad() {
        return Math.atan2(y(), x());
    }

    /**
     * @return The heading relative to the positive y-axis in the form of Degrees (Clockwise on a Clock)
     */
    public double heading2() {
        return Math.atan2(x(), y()) * 180 / Math.PI;
    }

    /**
     * @return The heading relative to the positive y-axis in the form of Degrees (Clockwise on a Clock)
     */
    public double headingRad2() {
        return Math.atan2(x(), y());
    }

    /**
     * @return A 2D Vector of magnitude 1 and heading of the original 2D Vector
     */
    public Vec2D toUnitVector() {
        double mag = magnitude();
        return new Vec2D(x() / mag, y() / mag);
    }

    /**
     * Multiply this 2D Vector by a Scalar value
     * @param scalar The scalar value to multiply by
     */
    public Vec2D multiply(double scalar) {
        return new Vec2D(x() * scalar, y() * scalar);
    }

    /**
     * Subtract another 2D Vector from this 2D Vector
     * @param otherVector The vector to subtract from this vector
     */
    public Vec2D subtract(Vec2D otherVector) {
        return new Vec2D(x() - otherVector.x(), y() - otherVector.y());
    }

    /**
     * Add this 2D Vector to another 2D Vector
     * @param otherVector The vector to add to this vector
     */
    public Vec2D add(Vec2D otherVector) {
        return new Vec2D(x() + otherVector.x(), y() + otherVector.y());
    }

    /**
     * Calculate the Dot Product of this 2D Vector
     * @param otherVector The vector to dot product against
     * @return The scalar Dot Product of the Vectors
     */
    public double dot(Vec2D otherVector) {
        return x() * otherVector.x() + y() * otherVector.y();
    }

    /**
     * Calculate the Vector Projection of this Vector onto another Vector
     * @param otherVector The vector to project onto
     */
    public Vec2D projectOnto(Vec2D otherVector) {
        return otherVector.multiply(dot(otherVector) / otherVector.dot(otherVector));
    }

    /**
     * Calculate the Scalar Projection of this Vector onto another Vector
     * @param otherVector The vector to project onto
     */
    public double scalarProjectOnto(Vec2D otherVector) {
        return dot(otherVector) / otherVector.magnitude();
    }

    /**
     * Calculate this vector relative to another vector
     * @param otherVector The vector 'viewing' this vector
     */
    public Vec2D relativeTo(Vec2D otherVector) {
        return subtract(otherVector);
    }

    /**
     * Represent a 2D Vector in String form
     */
    public String toString() {
        return String.format("(%.2f, %.2f)", x(), y());         // 2 Decimal Places
    }
}
