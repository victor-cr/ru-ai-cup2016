package com.codegans.ai.cup2016.action;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 10:28
 */
public abstract class BaseAction implements Action {
    private final int score;

    public BaseAction(int score) {
        this.score = score;
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
        return getClass().getSimpleName() + '[' + score + ']';
    }
}
