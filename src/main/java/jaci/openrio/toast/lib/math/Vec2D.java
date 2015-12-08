package jaci.openrio.toast.lib.math;

public class Vec2D {

    private double x = 0, y = 0;

    public Vec2D() { }

    public Vec2D(double x, double y) {
        this.x = x; this.y = y;
    }

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

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public void setX(double newX) {
        this.x = newX;
    }

    public void setY(double newY) {
        this.y = newY;
    }

    public double magnitude() {
        return Math.sqrt(x() * x() + y() * y());
    }

    /**
     * Get the heading relative to the positive x-axis in the form of Degrees.
     */
    public double heading() {
        return Math.atan2(y(), x()) * 180 / Math.PI;
    }

    /**
     * Get the heading relative to the positive x-axis in the form of Radians.
     */
    public double headingRad() {
        return Math.atan2(y(), x());
    }

    /**
     * Get the heading relative to the positive y-axis in the form of Degrees (Clockwise on a Clock)
     */
    public double heading2() {
        return Math.atan2(x(), y()) * 180 / Math.PI;
    }

    /**
     * Get the heading relative to the positive y-axis in the form of Degrees (Clockwise on a Clock)
     */
    public double headingRad2() {
        return Math.atan2(x(), y());
    }

    public Vec2D toUnitVector() {
        double mag = magnitude();
        return new Vec2D(x() / mag, y() / mag);
    }

    public Vec2D multiply(double scalar) {
        return new Vec2D(x() * scalar, y() * scalar);
    }

    public Vec2D subtract(Vec2D otherVector) {
        return new Vec2D(x() - otherVector.x(), y() - otherVector.y());
    }

    public Vec2D add(Vec2D otherVector) {
        return new Vec2D(x() + otherVector.x(), y() + otherVector.y());
    }

    public double dot(Vec2D otherVector) {
        return x() * otherVector.x() + y() * otherVector.y();
    }

    public Vec2D projectOnto(Vec2D otherVector) {
        return otherVector.multiply(dot(otherVector) / otherVector.dot(otherVector));
    }

    public double scalarProjectOnto(Vec2D otherVector) {
        return dot(otherVector) / otherVector.magnitude();
    }

    public Vec2D rotate(double angleInDegrees) {
        double angle = angleInDegrees * Math.PI / 180;
        double x = x() * Math.cos(angle) - y() * Math.sin(angle);
        double y = y() * Math.sin(angle) - x() * Math.cos(angle);
        return new Vec2D(x, y);
    }

    public Vec2D rotateRad(double angleInRadians) {
        double x = x() * Math.cos(angleInRadians) - y() * Math.sin(angleInRadians);
        double y = y() * Math.sin(angleInRadians) - x() * Math.cos(angleInRadians);
        return new Vec2D(x, y);
    }

    public String toString() {
        return String.format("(%.2f, %.2f)", x(), y());         // 2 Decimal Places
    }
}
