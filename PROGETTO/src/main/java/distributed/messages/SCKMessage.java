package distributed.messages;

import utils.Event;

import java.io.Serializable;

public class SCKMessage extends Message implements Serializable{ //qui dovremmo aggiungere attributi e costruttori che collegano il messaggio a chi l'ha generato

    /**
     * @param obj
     * @param event
     */
    public SCKMessage(Object obj, Event event) {
        super(obj, event);
    }
}
