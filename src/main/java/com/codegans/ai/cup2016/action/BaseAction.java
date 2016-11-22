package com.codegans.ai.cup2016.action;

import com.codegans.ai.cup2016.decision.Decision;

import java.util.Arrays;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 10:28
 */
public abstract class BaseAction implements Action {
    private final int score;
    private final Class<? extends Decision> decision;

    public BaseAction(int score) {
        this.score = score;
        this.decision = trace();
    }

    @Override
    public int score() {
        return score;
    }

    @Override
    public int compareTo(Action o) {
        return Integer.compare(score(), o.score());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() ^ score;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && getClass() == obj.getClass() && score() == ((BaseAction) obj).score();
    }

    @Override
    public String toString() {
        return decision.getSimpleName() + ": " + getClass().getSimpleName() + '[' + score + ']';
    }

    private static Class<? extends Decision> trace() {
        String className = Arrays.stream(Thread.currentThread().getStackTrace())
                .map(StackTraceElement::getClassName)
                .filter(e -> e.contains("Decision"))
                .filter(e -> !e.contains("Abstract"))
                .limit(1).findFirst().orElse(null);

        if (className == null) {
            throw new IllegalStateException("Huynya");
        }

        try {
            return (Class<? extends Decision>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
