package com.codegans.ai.cup2016.model;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 25/11/2016 15:40
 */
public final class MoveHistory {
    private final Point target;
    private final double speed;
    private final double strafe;
    private final double turn;

    public MoveHistory(Point target, double speed, double strafe, double turn) {
        this.target = target;
        this.speed = speed;
        this.strafe = strafe;
        this.turn = turn;
    }

    public Point target() {
        return target;
    }

    public double speed() {
        return speed;
    }

    public double strafe() {
        return strafe;
    }

    public double turn() {
        return turn;
    }
}
