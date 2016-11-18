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
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.DARKBLUE;
import static javafx.scene.paint.Color.GRAY;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.ROSYBROWN;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 20.11.2015 17:02
 */
public class Window extends Application {
    private static final double SCALE = 1.1;
    private static final Logger LOG = LoggerFactory.getLogger();
    private static final File CANVAS_FILE = new File(System.getProperty("user.home"), "field.ai-cup-2016");


    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();
        Canvas canvas = new Canvas(4000, 4000);

        root.setCursor(Cursor.CROSSHAIR);
        root.getChildren().add(canvas);
        root.setScaleX(0.25);
        root.setScaleY(0.25);
        root.setTranslateX(0 - canvas.getWidth() * (1 - 0.25) / 2);
        root.setTranslateY(0 - canvas.getHeight() * (1 - 0.25) / 2);

        canvas.getGraphicsContext2D().setStroke(ROSYBROWN);
        canvas.getGraphicsContext2D().setLineWidth(10);
        canvas.getGraphicsContext2D().strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());

        root.setOnScroll(event -> {
            event.consume();

            if (event.getDeltaY() == 0) {
                return;
            }

            double scaleFactor = (event.getDeltaY() > 0) ? SCALE : 1 / SCALE;

            root.setScaleX(root.getScaleX() * scaleFactor);
            root.setScaleY(root.getScaleY() * scaleFactor);
            root.setTranslateX(0 - canvas.getWidth() * (1 - root.getScaleX()) / 2);
            root.setTranslateY(0 - canvas.getHeight() * (1 - root.getScaleY()) / 2);
        });
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

                    execute(root, canvas);

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

        primaryStage.getScene().setOnKeyPressed(event -> {
            if (event.isControlDown()) {
                switch (event.getCode()) {
                    case C:
                        store(root);
                        break;
                    case V:
                        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

                        root.getChildren().clear();
                        root.getChildren().add(canvas);

                        load(root);

                        execute(root, canvas);
                        break;
                }
            }
        });
    }

    private World setupWorld(Collection<? extends Node> nodes) {
        Tree[] trees = nodes.stream()
                .filter(e -> e instanceof Circle)
                .map(e -> (Circle) e)
                .filter(e -> e.getFill().equals(GREEN))
                .map(e -> new Tree(0, e.getCenterX(), e.getCenterY(), 0, 0, 0, Faction.OTHER, e.getRadius(), 100, 100, new Status[0]))
                .toArray(Tree[]::new);
        Building[] buildings = nodes.stream()
                .filter(e -> e instanceof Circle)
                .map(e -> (Circle) e)
                .filter(e -> e.getFill().equals(GRAY))
                .map(e -> new Building(0, e.getCenterX(), e.getCenterY(), 0, 0, 0, Faction.RENEGADES, e.getRadius(), 100, 100, new Status[0], BuildingType.GUARDIAN_TOWER, 100, 100, 100, 1, 1))
                .toArray(Building[]::new);
        Wizard[] wizards = nodes.stream()
                .filter(e -> e instanceof Circle)
                .map(e -> (Circle) e)
                .filter(e -> e.getFill().equals(RED))
                .map(this::setupWizard)
                .toArray(Wizard[]::new);

        return new World(0, 0, 4000, 4000, new Player[0], wizards, new Minion[0], new Projectile[0], new Bonus[0], buildings, trees);
    }

    private Wizard setupWizard(Circle wizard) {
        return new Wizard(0, wizard.getCenterX(), wizard.getCenterY(), 0, 0, 0, Faction.ACADEMY, wizard.getRadius(), 100, 100, new Status[0], 0, true, 100, 100, 100, 100, 1, 1, new SkillType[0], 1, new int[0], true, new Message[0]);
    }

    private void execute(Group root, Canvas canvas) {
        Thread calc = new Thread(() -> {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            World world = setupWorld(root.getChildren());

            Wizard me = world.getWizards()[0];

            long time = System.currentTimeMillis();
            LOG.printf("Started...%n");

            Collection<Point> points = PathFinder.aStar().traverse(world, new Point(me), new Point(world.getBuildings()[0]), me.getRadius(), e -> Platform.runLater(() -> gc.getPixelWriter().setColor((int) e.x, (int) e.y, DARKBLUE)));

            LOG.printf("Completed: %d ms%n", System.currentTimeMillis() - time);

            Platform.runLater(() -> {
                gc.setFill(BLACK);
                gc.setLineWidth(5);
                gc.strokePolyline(
                        points.stream().mapToDouble(e -> e.x).toArray(),
                        points.stream().mapToDouble(e -> e.y).toArray(),
                        points.size()
                );
            });
        });

        calc.setDaemon(true);
        calc.start();
    }

    private void store(Group root) {
        try (PrintWriter out = new PrintWriter(new FileWriter(CANVAS_FILE, false))) {
            root.getChildren().stream()
                    .filter(e -> e instanceof Circle)
                    .map(e -> (Circle) e)
                    .map(e -> String.valueOf(e.getFill()) + ';' + e.getRadius() + ';' + e.getCenterX() + ';' + e.getCenterY() + '\n')
                    .forEach(out::write);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void load(Group root) {
        try (BufferedReader in = new BufferedReader(new FileReader(CANVAS_FILE))) {
            in.lines().map(e -> e.split(";")).filter(e -> e[0].length() != 0).forEach(e -> {
                Circle circle = new Circle(Double.parseDouble(e[2]), Double.parseDouble(e[3]), Double.parseDouble(e[1]));

                circle.setFill(Color.valueOf(e[0]));

                root.getChildren().add(circle);
            });
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
