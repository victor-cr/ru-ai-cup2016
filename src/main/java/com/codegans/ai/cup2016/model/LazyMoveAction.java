package com.codegans.ai.cup2016.model;

import model.Move;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 25/11/2016 15:30
 */
public interface LazyMoveAction {
    Point execute(Move move);
}
