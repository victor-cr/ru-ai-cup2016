package com.codegans.ai.cup2016.action;

import model.ActionType;
import model.Move;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 18/11/2016 17:15
 */
public class CastAction extends BaseAction {
    private static final int N_A = -1;

    private final ActionType type;
    private final long targetId;
    private final double angle;
    private final double min;
    private final double max;

    public static CastAction staff(int score) {
        return new CastAction(score, ActionType.STAFF, N_A, 0, N_A, N_A);
    }

    public static CastAction missile(int score, double angle) {
        return new CastAction(score, ActionType.MAGIC_MISSILE, N_A, angle, N_A, N_A);
    }

    public static CastAction missile(int score, double angle, double min, double max) {
        return new CastAction(score, ActionType.MAGIC_MISSILE, N_A, angle, min, max);
    }

    public static CastAction fireball(int score, double angle, double min, double max) {
        return new CastAction(score, ActionType.FIREBALL, N_A, angle, min, max);
    }

    public static CastAction frostbolt(int score, double angle, double min, double max) {
        return new CastAction(score, ActionType.FROST_BOLT, N_A, angle, min, max);
    }

    public static CastAction haste(int score, long targetId) {
        return new CastAction(score, ActionType.HASTE, targetId, 0, N_A, N_A);
    }

    public static CastAction shield(int score, long targetId) {
        return new CastAction(score, ActionType.SHIELD, targetId, 0, N_A, N_A);
    }

    private CastAction(int score, ActionType type, long targetId, double angle, double min, double max) {
        super(score);

        this.type = type;
        this.targetId = targetId;
        this.angle = angle;
        this.min = min;
        this.max = max;
    }

    @Override
    public void apply(Move move) {
        move.setAction(type);
        move.setCastAngle(angle);

        if (min != N_A) {
            move.setMinCastDistance(min);
        }

        if (max != N_A) {
            move.setMaxCastDistance(max);
        }

        if (targetId != N_A) {
            move.setStatusTargetId(targetId);
        }
    }

    @Override
    public String toString() {
        return String.format("%s[%s]<%.4f>", super.toString(), type, angle);
    }
}
