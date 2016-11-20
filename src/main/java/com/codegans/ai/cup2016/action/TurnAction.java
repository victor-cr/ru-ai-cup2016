package com.codegans.ai.cup2016.action;

import model.Move;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 18/11/2016 17:15
 */
public class TurnAction extends BaseAction {
    private final double angle;

    public TurnAction(int score, double angle) {
        super(score);

        this.angle = angle;
    }

    @Override
    public void apply(Move move) {
        move.setTurn(angle);
    }

    @Override
    public String toString() {
        return String.format("%s<%.4f>", super.toString(), angle);
    }
}
