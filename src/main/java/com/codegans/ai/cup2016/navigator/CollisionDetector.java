package com.codegans.ai.cup2016.navigator;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 20.11.2016 10:38
 */

import com.codegans.ai.cup2016.model.Point;
import model.Building;
import model.LivingUnit;
import model.Minion;
import model.Tree;
import model.Wizard;

import java.util.stream.Stream;

public interface CollisionDetector {
    double width();

    double height();

    boolean isNear(double x, double y, double radius, LivingUnit unit);

    boolean canPass(Point from, Point to, double radius);

    boolean overlaps(Point from, Point to, double radius, LivingUnit unit);

    boolean contains(Point from, Point to, double radius, Point target);

    boolean available(double x, double y, double radius);

    LivingUnit unitAt(double x, double y);

    Stream<LivingUnit> unitsAt(double x, double y, double radius);

    Stream<Minion> minionsAt(double x, double y, double radius);

    Stream<Wizard> wizardsAt(double x, double y, double radius);

    Stream<Building> towersAt(double x, double y, double radius);

    Stream<Tree> treesAt(double x, double y, double radius);
}
