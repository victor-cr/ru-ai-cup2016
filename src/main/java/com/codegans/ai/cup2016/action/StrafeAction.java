package com.codegans.ai.cup2016.action;

import model.Move;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 18/11/2016 17:15
 */
public class StrafeAction extends BaseAction {
    private final double speed;

    public StrafeAction(int score, double speed) {
        super(score);

        this.speed = speed;
    }

    @Override
    public void apply(Move move) {
        move.setStrafeSpeed(speed);
    }

    @Override
    public String toString() {
        return String.format("%s[%.4f]", super.toString(), speed);
    }
}
