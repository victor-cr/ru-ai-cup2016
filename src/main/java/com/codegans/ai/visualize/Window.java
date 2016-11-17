package com.codegans.ai.visualize;

import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.PathFinder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.Bonus;
import model.Building;
import model.BuildingType;
import model.Faction;
import model.Message;
import model.Minion;
import model.Player;
import model.Projectile;
import model.SkillType;
import model.Status;
import model.Tree;
import model.Wizard;
import model.World;

import java.util.Collection;

import static javafx.scene.paint.Color.*;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 20.11.2015 17:02
 */
public class Window extends Application {
    private static final Logger LOG = LoggerFactory.getLogger();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();

        Rectangle field = new Rectangle(4000, 4000);

        root.setCursor(Cursor.CROSSHAIR);
        root.getChildren().add(field);
        root.setAutoSizeChildren(true);
        root.setOnMouseClicked(event -> {
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();

            switch (event.getButton()) {
                case PRIMARY:
                    Circle tree = new Circle(mouseX, mouseY, 20.0D + Math.random() * 30);

                    tree.setFill(GREEN);
                    tree.setOnMouseClicked(e -> {
                        root.getChildren().remove(tree);
                        e.consume();
                    });

                    root.getChildren().add(tree);
                    break;
                case SECONDARY:
                    Circle tower = new Circle(mouseX, mouseY, 50);

                    tower.setFill(GRAY);
                    tower.setOnMouseClicked(e -> {
                        root.getChildren().remove(tower);
                        e.consume();
                    });

                    root.getChildren().add(tower);
                    break;
                case MIDDLE:
                    Circle me = new Circle(mouseX, mouseY, 35);

                    me.setFill(RED);

                    root.getChildren().add(me);

                    Canvas canvas = new Canvas(4000, 4000);

                    root.getChildren().add(canvas);

                    Thread calc = new Thread(() -> {
                        Wizard wizard = setupWizard(me);

                        World world = setupWorld(wizard, root.getChildren());

                        long time = System.currentTimeMillis();
                        LOG.printf("Started...%n");

                        Collection<Point> points = PathFinder.aStar().traverse(world, new Point(wizard), new Point(world.getBuildings()[0]), me.getRadius(), e -> {
                            Circle point = new Circle(e.x, e.y, 1);

                            point.setFill(DARKBLUE);

                            Platform.runLater(() -> canvas.getGraphicsContext2D().getPixelWriter().setColor((int) e.x, (int) e.y, DARKBLUE));
                        });

                        LOG.printf("Completed: %d ms%n", System.currentTimeMillis() - time);

                        points.stream().map(e -> new Circle(e.x, e.y, 2)).peek(e -> e.setFill(YELLOW)).forEach(e -> Platform.runLater(() -> root.getChildren().add(e)));
                    });

                    calc.setDaemon(true);
                    calc.start();

                    break;
                default:
                    break;
            }
        });

        primaryStage.setScene(new Scene(
                root,
                1000,
                1000,
                Color.WHITE
        ));
        primaryStage.show();

//        World world = setupWorld();
//
//        Game game = setupGame(world);

    }

    private World setupWorld(Wizard me, Collection<? extends Node> nodes) {
        Tree[] trees = nodes.stream()
                .filter(e -> e instanceof Circle)
                .map(e -> (Circle) e)
                .filter(e -> e.getFill() == GREEN)
                .map(e -> new Tree(0, e.getCenterX(), e.getCenterY(), 0, 0, 0, Faction.OTHER, e.getRadius(), 100, 100, new Status[0]))
                .toArray(Tree[]::new);
        Building[] buildings = nodes.stream()
                .filter(e -> e instanceof Circle)
                .map(e -> (Circle) e)
                .filter(e -> e.getFill() == GRAY)
                .map(e -> new Building(0, e.getCenterX(), e.getCenterY(), 0, 0, 0, Faction.RENEGADES, e.getRadius(), 100, 100, new Status[0], BuildingType.GUARDIAN_TOWER, 100, 100, 100, 1, 1))
                .toArray(Building[]::new);
        Wizard[] wizards = new Wizard[]{me};

        return new World(0, 0, 4000, 4000, new Player[0], wizards, new Minion[0], new Projectile[0], new Bonus[0], buildings, trees);
    }

    private Wizard setupWizard(Circle wizard) {
        return new Wizard(0, wizard.getCenterX(), wizard.getCenterY(), 0, 0, 0, Faction.ACADEMY, wizard.getRadius(), 100, 100, new Status[0], 0, true, 100, 100, 100, 100, 1, 1, new SkillType[0], 1, new int[0], true, new Message[0]);
    }

//    private Game setupGame(World world) {
//        return new Game(0, 0, world.getWidth(), world.getHeight(), tileSize, tileSize / 12,
//                0, 0, 0, 0, new int[0], 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//                0, 0, 0, 0, 0, 0, 0, 0, 0);
//    }

    public static void main(String[] args) {
        launch(args);
    }
}
