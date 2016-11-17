package com.codegans.ai.cup2016.log;

import com.codegans.ai.cup2016.Navigator;
import com.codegans.ai.cup2016.action.Action;
import model.Wizard;
import model.World;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 11:46
 */
public interface Logger {
    void print(Object message);

    void printf(String pattern, Object... params);

    void action(Action<?> action);

    void wizard(Wizard car, Navigator navigator);

    void others(World world);
}
