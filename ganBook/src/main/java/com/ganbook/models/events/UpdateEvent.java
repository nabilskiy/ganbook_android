package com.ganbook.models.events;

import com.ganbook.models.answers.EventsAnswer;

/**
 * Created by dmytro_vodnik on 7/21/16.
 * working on ganbook1 project
 */
public class UpdateEvent {

    private EventsAnswer eventsAnswer;

    public int getPos() {
        return pos;
    }

    private final int pos;

    public UpdateEvent(EventsAnswer eventsAnswer, int pos) {
        this.eventsAnswer = eventsAnswer;
        this.pos = pos;
    }

    public EventsAnswer getEventsAnswer() {
        return eventsAnswer;
    }

    @Override
    public String toString() {
        return "UpdateEvent{" +
                "eventsAnswer=" + eventsAnswer +
                ", pos=" + pos +
                '}';
    }
}
