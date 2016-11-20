package com.codegans.ai.cup2016.navigator;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 20.11.2016 10:38
 */

public interface CollisionDetectorFactory {
    CollisionDetector full();

    CollisionDetector staticOnly();
}
