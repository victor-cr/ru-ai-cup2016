package com.codegans.ai.cup2016.log;

import com.codegans.ai.cup2016.Navigator;
import com.codegans.ai.cup2016.action.Action;
import model.Wizard;
import model.World;

import java.util.Arrays;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 19:56
 */
public class Slf4jLogger implements Logger {
    private static final String CLASS_NAME = Slf4jLogger.class.getName();

    private org.slf4j.Logger logger() {
        String className = Arrays.stream(Thread.currentThread().getStackTrace())
                .skip(1)
                .map(StackTraceElement::getClassName)
                .filter(e -> !CLASS_NAME.equals(e))
                .limit(1)
                .findAny()
                .orElse("");

        return org.slf4j.LoggerFactory.getLogger(className);
    }

    @Override
    public void print(Object message) {
        logger().info(String.valueOf(message));
    }

    @Override
    public void printf(String pattern, Object... params) {
        print(String.format(pattern, params));
    }

    @Override
    public void action(Action<?> action) {
        printf("Perform action: %s%n", action);
    }

    @Override
    public void wizard(Wizard wizard, Navigator navigator) {
        printf("%s%n", wizard);
    }

    @Override
    public void others(World world) {
        printf("All wizards: %s%n", Arrays.asList(world.getWizards()));
    }
}
