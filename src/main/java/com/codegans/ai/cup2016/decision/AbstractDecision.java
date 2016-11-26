package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 20.11.2016 21:40
 */
public abstract class AbstractDecision implements Decision {
    protected static final Logger LOG = LoggerFactory.getLogger();

    protected final int priority;

    public AbstractDecision(int priority) {
        this.priority = priority;
    }
}
