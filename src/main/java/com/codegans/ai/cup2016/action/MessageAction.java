package com.codegans.ai.cup2016.action;

import model.Message;
import model.Move;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 18/11/2016 17:15
 */
public class MessageAction extends BaseAction {
    private final Message[] messages;

    public MessageAction(int score, Message[] messages) {
        super(score);

        this.messages = messages;
    }

    @Override
    public void apply(Move move) {
        move.setMessages(messages);
    }
}
