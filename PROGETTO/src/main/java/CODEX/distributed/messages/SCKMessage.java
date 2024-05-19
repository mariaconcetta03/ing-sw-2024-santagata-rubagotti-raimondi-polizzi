package CODEX.distributed.messages;

import CODEX.utils.Event;

import java.io.Serializable;
import java.util.*;

/**
 * This class defines a message that is used to pass information through the socket in a bidirectional way
 */
public class SCKMessage extends Message implements Serializable{ //qui dovremmo aggiungere attributi e costruttori che collegano il messaggio a chi l'ha generato

    /**
     * Constructor method
     * @param obj the single generic object
     * @param event the action we are performing
     */
    public SCKMessage(Object obj, Event event) {
        super(obj, event);
    }

    /**
     * Constructor method
     * @param obj List of generic objects
     * @param event the action we are performing
     */
    public SCKMessage(List<Object> obj, Event event) {
        super(obj, event);
    }
}
