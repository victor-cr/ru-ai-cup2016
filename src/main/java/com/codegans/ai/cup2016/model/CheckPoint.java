package com.codegans.ai.cup2016.model;

import model.LaneType;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2016 20:21
 */
public final class CheckPoint {
    public final Point checkpoint;
    public final LaneType lane;

    public CheckPoint(Point checkpoint, LaneType lane) {
        this.checkpoint = checkpoint;
        this.lane = lane;
    }

    @Override
    public int hashCode() {
        return checkpoint.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof CheckPoint && ((CheckPoint) obj).checkpoint.equals(checkpoint);
    }

    @Override
    public String toString() {
        return String.format("%s->%s", checkpoint, lane);
    }

}
