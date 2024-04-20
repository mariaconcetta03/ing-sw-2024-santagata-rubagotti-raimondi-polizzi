package utils;

import java.io.Serializable;

public class Event implements Serializable {
    public enum EventType {
        EVENT_1,
        EVENT_2,
        EVENT_3
    };

    public Event(EventType event){
        this.event = event;
    }

    EventType event;
    public int code = 1;

    public void printEvent() {
        if (this.event.equals(EventType.EVENT_1)) {
            System.out.println("è venuto l'evento 1");
        }
        if (this.event.equals(EventType.EVENT_2)) {
            System.out.println("è venuto l'evento 2");
        }
        if (this.event.equals(EventType.EVENT_3)) {
            System.out.println("è venuto l'evento 3");
        }
        System.out.println("Questo è il code: " + code);
    }

}
