package com.codegans.ai.cup2016.action;

import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.GameMap;
import model.Move;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 10:28
 */
public class MoveAction extends BaseAction {
    private final GameMap map;
    private final Point target;
    private final double speed;
    private final double strafe;
    private final double turn;

    public MoveAction(int score, GameMap map, Point target, double speed, double strafe, double turn) {
        super(score);
        this.map = map;
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

    @Override
    public void apply(Move move) {
        move.setSpeed(map.limitSpeed(speed));
        move.setStrafeSpeed(map.limitStrafe(strafe));
        move.setTurn(map.limitAngle(turn));
    }

    @Override
    public String toString() {
        return String.format("%s(%.4f;%.4f)[%.4f][%.4f]<%.4f>", super.toString(), target.x, target.y, speed, strafe, turn);
    }
}
