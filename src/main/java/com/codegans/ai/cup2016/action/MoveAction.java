package com.codegans.ai.cup2016.action;

import com.codegans.ai.cup2016.model.Point;
import model.Move;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 10:28
 */
public class MoveAction extends BaseAction {
    private final Point target;
    private final double speed;
    private final double strafe;
    private final double turn;

    public MoveAction(int score, Point target, double speed, double strafe, double turn) {
        super(score);
        this.target = target;
        this.speed = speed;
        this.strafe = strafe;
        this.turn = turn;
    }

    public Point target() {
        return target;
    }

    @Override
    public void apply(Move move) {
        move.setSpeed(speed);
        move.setStrafeSpeed(strafe);
        move.setTurn(turn);
    }

    @Override
    public String toString() {
        return String.format("%s(%.4f;%.4f)[%.4f][%.4f]<%.4f>", super.toString(), target.x, target.y, speed, strafe, turn);
    }
}
