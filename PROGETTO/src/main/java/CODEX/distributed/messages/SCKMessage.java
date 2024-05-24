package CODEX.distributed.messages;


import CODEX.utils.executableMessages.clientMessages.ClientMessage;
import CODEX.utils.executableMessages.events.Event;
import CODEX.utils.executableMessages.serverMessages.ServerMessage;

import java.io.Serializable;
import java.util.*;

/**
 * This class defines a message that is used to pass information through the socket in a bidirectional way
 */
public class SCKMessage implements Serializable{ //qui dovremmo aggiungere attributi e costruttori che collegano il messaggio a chi l'ha generato
    private final ClientMessage clientMessage;
    private final ServerMessage serverMessage;
    private final Event event;





    public SCKMessage(ClientMessage msg){  //o si vuol mandare un ClientMessage o si vuole mandare un ServerMessage
        this.event=null;
        this.serverMessage=null;
        this.clientMessage=msg; //letto lato server
    }
    public SCKMessage(ServerMessage msg){ //o si vuol mandare un ClientMessage o si vuole mandare un ServerMessage
        this.event=null;
        this.clientMessage=null;
        this.serverMessage=msg; //letto lato client
    }
    public SCKMessage(Event event){
        this.clientMessage=null;
        this.serverMessage=null;
        this.event=event;

    }



    public Event getEvent(){
        return this.event;
    }
    public ServerMessage getServerMessage(){
        return this.serverMessage;
    }
    public ClientMessage getClientMessage(){
        return this.clientMessage;
    }
}
