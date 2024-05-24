package CODEX.distributed.messages;

import CODEX.utils.ClientMessage;
import CODEX.utils.Event;
import CODEX.utils.executableMessages.serverMessages.ServerMessage;

import java.io.Serializable;
import java.util.*;

/**
 * This class defines a message that is used to pass information through the socket in a bidirectional way
 */
public class SCKMessage extends Message implements Serializable{ //qui dovremmo aggiungere attributi e costruttori che collegano il messaggio a chi l'ha generato
    private ClientMessage clientMessage;
    private ServerMessage serverMessage;
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

    public SCKMessage(ClientMessage msg){  //o si vuol mandare un ClientMessage o si vuole mandare un ServerMessage
        super(); //qui abbiamo un this.event=
        this.clientMessage=msg; //letto lato server
    }
    public SCKMessage(ServerMessage msg){ //o si vuol mandare un ClientMessage o si vuole mandare un ServerMessage
        super(); //qui abbiamo un this.event=null;
        this.serverMessage=msg; //letto lato client
    }
}
